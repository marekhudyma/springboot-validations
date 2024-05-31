package com.marekhudyma.springboot.validations;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Validated
public class MyController {

  private final Validator validator;

  public MyController(Validator validator) {
    this.validator = validator;
  }

  @PostMapping(value = "/endpoint-basic-validation", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity endpointBasicValidation(@RequestBody @Validated(BasicValidation.class) final Body request) {
    return ResponseEntity.ok().build();
  }

  @PostMapping(value = "/endpoint-extended-validation", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity endpointExtendedValidation(
      @RequestBody @Validated(ExtendedValidation.class) final Body request) {
    return ResponseEntity.ok().build();
  }

  @PostMapping(value = "/endpoint-mixed-validation", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity endpointMixedValidation(@RequestHeader(value = "Request-Context") String requestContext,
                                                @RequestBody final Body request) {

    if ("basic".equals(requestContext)) {
      validate(request, BasicValidation.class);
      return ResponseEntity.ok().build();
    } else if ("extended".equals(requestContext)) {
      validate(request, ExtendedValidation.class);
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.badRequest().build();
    }
  }

  @PostMapping(value = "/endpoint-limited-view", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity endpointLimitedView(@JsonView(BodyLimited.class) @RequestBody final Body request) {

    if (request.integerProperty() != null) {
      return ResponseEntity.badRequest().build();
    }
    return ResponseEntity.ok().build();
  }

  @PostMapping(value = "/endpoint-custom-validation", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity endpointCustomValidation(
      @Validated(CustomValidation.class) @RequestBody final Body request) {


    return ResponseEntity.ok().build();
  }

  private void validate(Body request, Class clazz) {
    Set<ConstraintViolation<Body>> validationErrors = validator.validate(request, clazz);
    if (!validationErrors.isEmpty()) {
      throw new ValidationException(
          validationErrors.stream()
              .map(v -> v.getPropertyPath() + " : " + v.getMessage())
              .collect(Collectors.joining(", "))
      );
    }
  }


  record Body(
      @NotNull(groups = {BasicValidation.class})
      @Email(groups = {ExtendedValidation.class})
      @CustomConstraint(groups = {CustomValidation.class})
      @JsonProperty("textProperty") String textProperty,

      @Min(value = 1, groups = {BasicValidation.class})
      @Max(value = 100, groups = {ExtendedValidation.class})
      @JsonProperty("integerProperty") Integer integerProperty) {
  }

  record BodyLimited(
      @NotNull(groups = {BasicValidation.class})
      @Email(groups = {ExtendedValidation.class})
      @CustomConstraint(groups = {CustomValidation.class})
      @JsonProperty("textProperty") String textProperty) {
  }

  interface BasicValidation {
  }

  interface ExtendedValidation {
  }

  interface CustomValidation {
  }
}
