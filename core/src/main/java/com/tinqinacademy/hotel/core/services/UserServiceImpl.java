package com.tinqinacademy.hotel.core.services;

import com.tinqinacademy.hotel.api.exceptions.EntityAlreadyExistsException;
import com.tinqinacademy.hotel.api.operations.signup.input.SignUpInput;
import com.tinqinacademy.hotel.api.operations.signup.output.SignUpOutput;
import com.tinqinacademy.hotel.api.services.contracts.UserService;
import com.tinqinacademy.hotel.core.mappers.UserMapper;
import com.tinqinacademy.hotel.persistence.entities.user.User;
import com.tinqinacademy.hotel.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  public SignUpOutput signUp(SignUpInput input) {

    log.info("Start signUp input: {}", input);

    Optional<User> userOptional = userRepository.findByEmail(input.getEmail());

    if(userOptional.isPresent()) {
      throw new EntityAlreadyExistsException("User with the provided email already exists");
    }

    userOptional = userRepository.findByPhoneNumber(input.getPhoneNumber());

    if(userOptional.isPresent()) {
      throw new EntityAlreadyExistsException("User with the provided phone number already exists");
    }

    String hashedPassword;
    try {
      hashedPassword = hashPassword(input.getPassword());
    } catch (GeneralSecurityException ex) {
      throw new RuntimeException("Error while hashing password");
    }

    User user = userMapper.fromSignUpInputToUser(input, hashedPassword);
    userRepository.save(user);

    log.info("user: {}", user);

    SignUpOutput output = userMapper.fromUserToSignUpOutput(user);
    log.info("End signUp output: {}", output);
    return output;
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
