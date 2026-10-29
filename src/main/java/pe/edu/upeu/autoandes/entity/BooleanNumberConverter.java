package pe.edu.upeu.autoandes.entity;

import jakarta.persistence.*;

@Converter
public class BooleanNumberConverter implements AttributeConverter<Boolean, Integer> {
  public Integer convertToDatabaseColumn(Boolean v) {
    return Boolean.TRUE.equals(v) ? 1 : 0;
  }

  public Boolean convertToEntityAttribute(Integer v) {
    return v != null && v == 1;
  }
}
