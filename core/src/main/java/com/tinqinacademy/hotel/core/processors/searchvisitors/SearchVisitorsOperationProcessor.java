package com.tinqinacademy.hotel.core.processors.searchvisitors;

import com.tinqinacademy.hotel.api.models.input.VisitorDetailsInput;
import com.tinqinacademy.hotel.api.models.output.VisitorDetailsOutput;
import com.tinqinacademy.hotel.api.operations.searchvisitors.input.SearchVisitorsInput;
import com.tinqinacademy.hotel.api.operations.searchvisitors.operation.SearchVisitorsOperation;
import com.tinqinacademy.hotel.api.operations.searchvisitors.output.SearchVisitorsOutput;
import com.tinqinacademy.hotel.persistence.models.output.VisitorSearchResult;
import com.tinqinacademy.hotel.persistence.repositories.CustomGuestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchVisitorsOperationProcessor implements SearchVisitorsOperation {

  private final CustomGuestRepository customGuestRepository;

  @Override
  public SearchVisitorsOutput process(SearchVisitorsInput input) {
    log.info("Start searchVisitors input: {}", input);

    VisitorDetailsInput visitorDetailsInput = input.getVisitorDetailsInput();

    List<VisitorSearchResult> results = customGuestRepository.searchVisitors(
        visitorDetailsInput.getStartDate(),
        visitorDetailsInput.getEndDate(),
        visitorDetailsInput.getFirstName(),
        visitorDetailsInput.getLastName(),
        visitorDetailsInput.getBirthDate(),
        visitorDetailsInput.getPhoneNo(),
        visitorDetailsInput.getIdCardNo(),
        visitorDetailsInput.getIdCardIssueAuthority(),
        input.getRoomNo()
    );

    List<VisitorDetailsOutput> visitors = convertSearchResultListToVistorDetailsOutputList(results);

    SearchVisitorsOutput output = createOutput(visitors);

    log.info("End searchVisitors output: {}", output);
    return output;
  }

  private List<VisitorDetailsOutput> convertSearchResultListToVistorDetailsOutputList(List<VisitorSearchResult> results) {
    return results.stream().map(r ->
        VisitorDetailsOutput.builder()
            .startDate(r.getStartDate())
            .endDate(r.getEndDate())
            .firstName(r.getFirstName())
            .birthDate(r.getBirthDate())
            .lastName(r.getLastName())
            .idCardNo(r.getIdCardNumber())
            .idCardValidity(r.getIdCardValidity())
            .idCardIssueAuthority(r.getIdCardIssueAuthority())
            .idCardIssueDate(r.getIdCardIssueDate())
            .build()).toList();
  }

  private SearchVisitorsOutput createOutput(List<VisitorDetailsOutput> visitors) {
    return SearchVisitorsOutput.builder()
        .visitors(visitors)
        .build();
  }
}
