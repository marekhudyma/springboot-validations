package com.marekhudyma.springboot.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class CustomValidator implements ConstraintValidator<CustomConstraint, String> {

  private static final String HEADER_EXPECTED_VALUE = "custom";
  private static final String HEADER_NAME = "Request-Context";

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    String header = getHeaderValue(HEADER_NAME);
    return (HEADER_EXPECTED_VALUE.equals(header) && value != null && value.startsWith("ABC") && value.endsWith("Z"));
  }

  private String getHeaderValue(String headerName) {
    var servletAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
    if (servletAttributes != null) {
      return servletAttributes.getRequest().getHeader(headerName);
    } else {
      return null;
    }
  }
}
