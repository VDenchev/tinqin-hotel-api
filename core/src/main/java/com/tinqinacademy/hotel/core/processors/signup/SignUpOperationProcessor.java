package com.tinqinacademy.hotel.core.processors.signup;

import com.tinqinacademy.hotel.api.base.BaseOperationProcessor;
import com.tinqinacademy.hotel.api.errors.ErrorOutput;
import com.tinqinacademy.hotel.api.exceptions.EntityAlreadyExistsException;
import com.tinqinacademy.hotel.api.operations.signup.input.SignUpInput;
import com.tinqinacademy.hotel.api.operations.signup.operation.SignUpOperation;
import com.tinqinacademy.hotel.api.operations.signup.output.SignUpOutput;
import com.tinqinacademy.hotel.persistence.entities.user.User;
import com.tinqinacademy.hotel.persistence.repositories.UserRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import jakarta.validation.Validator;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

import static io.vavr.API.Match;

@Service
@Slf4j
public class SignUpOperationProcessor extends BaseOperationProcessor implements SignUpOperation {

  private final UserRepository userRepository;

  public SignUpOperationProcessor(ConversionService conversionService, Validator validator, UserRepository userRepository) {
    super(conversionService, validator);
    this.userRepository = userRepository;
  }

  @Override
  public Either<ErrorOutput, SignUpOutput> process(SignUpInput input) {
    return validateInput(input)
        .flatMap(validInput ->
            Try.of(() -> {
                  log.info("Start signUp input: {}", validInput);

                  ensureUserWithSameEmailDoesNotExist(validInput.getEmail());
                  ensureUserWithSamePhoneNoDoesNotExist(validInput.getPhoneNo());


                  String hashedPassword = hashPassword(validInput.getPassword());

                  User user = convertSignUpInputToUser(validInput, hashedPassword);
                  userRepository.save(user);

                  SignUpOutput output = createOutput(user);
                  log.info("End signUp output: {}", output);
                  return output;
                })
                .toEither()
                .mapLeft(t -> Match(t).of(
                    customStatusCase(t, EntityAlreadyExistsException.class, HttpStatus.BAD_REQUEST),
                    customStatusCase(t, GeneralSecurityException.class, HttpStatus.BAD_REQUEST)
                ))
        );
  }

  private void ensureUserWithSameEmailDoesNotExist(String email) {
    Optional<User> userMaybe = userRepository.findByEmail(email);

    if (userMaybe.isPresent()) {
      throw new EntityAlreadyExistsException("User with the provided email already exists");
    }
  }

  private void ensureUserWithSamePhoneNoDoesNotExist(String phoneNo) {
    Optional<User> userMaybe = userRepository.findByPhoneNumber(phoneNo);

    if (userMaybe.isPresent()) {
      throw new EntityAlreadyExistsException("User with the provided phone number already exists");
    }
  }

  private User convertSignUpInputToUser(SignUpInput input, String hashedPassword) {
    User user = conversionService.convert(input, User.class);
    user.setPassword(hashedPassword);
    return user;
  }

  private SignUpOutput createOutput(User user) {
    return SignUpOutput.builder()
        .id(user.getId())
        .build();
  }

  private String hashPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
    int iterations = 50_000;
    char[] chars = password.toCharArray();
    byte[] salt = generateSalt();

    PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
    SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

    byte[] hash = skf.generateSecret(spec).getEncoded();
    return iterations + ":" + toHex(salt) + ":" + toHex(hash);
  }

  private String toHex(byte[] array) {
    BigInteger bi = new BigInteger(1, array);
    String hex = bi.toString(16);
    int paddingLength = (array.length * 2) - hex.length();
    if (paddingLength > 0) {
      return String.format("%0" + paddingLength + "d", 0) + hex;
    } else {
      return hex;
    }
  }

  private byte[] generateSalt() throws NoSuchAlgorithmException {
    SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
    byte[] salt = new byte[16];
    sr.nextBytes(salt);
    return salt;
  }
}
