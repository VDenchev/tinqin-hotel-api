package com.tinqinacademy.hotel.core.processors.searchvisitors;

import com.tinqinacademy.hotel.core.processors.base.BaseOperationProcessor;
import com.tinqinacademy.hotel.api.errors.ErrorOutput;
import com.tinqinacademy.hotel.api.models.input.VisitorDetailsInput;
import com.tinqinacademy.hotel.api.models.output.VisitorDetailsOutput;
import com.tinqinacademy.hotel.api.operations.searchvisitors.input.SearchVisitorsInput;
import com.tinqinacademy.hotel.api.operations.searchvisitors.operation.SearchVisitorsOperation;
import com.tinqinacademy.hotel.api.operations.searchvisitors.output.SearchVisitorsOutput;
import com.tinqinacademy.hotel.persistence.models.output.VisitorSearchResult;
import com.tinqinacademy.hotel.persistence.repositories.CustomGuestRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import jakarta.validation.Validator;

import java.util.List;

import static io.vavr.API.Match;

@Service
@Slf4j
public class SearchVisitorsOperationProcessor extends BaseOperationProcessor implements SearchVisitorsOperation {

  private final CustomGuestRepository customGuestRepository;

  public SearchVisitorsOperationProcessor(
      ConversionService conversionService,
      Validator validator, CustomGuestRepository customGuestRepository
  ) {
    super(conversionService, validator);
    this.customGuestRepository = customGuestRepository;
  }

  @Override
  public Either<ErrorOutput, SearchVisitorsOutput> process(SearchVisitorsInput input) {
    return validateInput(input)
        .flatMap(validInput ->
            Try.of(() -> {
                  log.info("Start searchVisitors input: {}", validInput);

                  VisitorDetailsInput visitorDetailsInput = validInput.getVisitorDetailsInput();

                  List<VisitorSearchResult> results = customGuestRepository.searchVisitors(
                      visitorDetailsInput.getStartDate(),
                      visitorDetailsInput.getEndDate(),
                      visitorDetailsInput.getFirstName(),
                      visitorDetailsInput.getLastName(),
                      visitorDetailsInput.getBirthDate(),
                      validInput.getUserIds(),
                      visitorDetailsInput.getIdCardNo(),
                      visitorDetailsInput.getIdCardIssueAuthority(),
                      input.getRoomNo()
                  );

                  List<VisitorDetailsOutput> visitors = convertSearchResultListToVistorDetailsOutputList(results);

                  SearchVisitorsOutput output = createOutput(visitors);

                  log.info("End searchVisitors output: {}", output);
                  return output;
                })
                .toEither()
                .mapLeft(t -> Match(t).of(
                    defaultCase(t)
                ))
        );
  }

  private List<VisitorDetailsOutput> convertSearchResultListToVistorDetailsOutputList(List<VisitorSearchResult> results) {
    return results.stream().map(r ->
            VisitorDetailsOutput.builder()
                .startDate(r.getStartDate())
                .endDate(r.getEndDate())
                .firstName(r.getFirstName())
                .birthDate(r.getBirthDate())
                .lastName(r.getLastName())
                .userId(r.getUserId())
                .idCardNo(r.getIdCardNumber())
                .idCardValidity(r.getIdCardValidity())
                .idCardIssueAuthority(r.getIdCardIssueAuthority())
                .idCardIssueDate(r.getIdCardIssueDate())
                .build()
        )
        .toList();
  }

  private SearchVisitorsOutput createOutput(List<VisitorDetailsOutput> visitors) {
    return SearchVisitorsOutput.builder()
        .visitors(visitors)
        .build();
  }
}
