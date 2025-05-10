package ru.rtln.userservice.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.rtln.userservice.entity.Gender;

@Converter(autoApply = true)
public class GenderConverter implements AttributeConverter<Gender, Long> {

    @Override
    public Long convertToDatabaseColumn(Gender attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getId();
    }

    @Override
    public Gender convertToEntityAttribute(Long dbData) {
        return Gender.getValueFromId(dbData);
    }

}
