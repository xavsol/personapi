package be.solxa.peopleapi.mapper;

import be.solxa.peopleapi.dto.PersonDTO;
import be.solxa.peopleapi.model.Person;
import org.springframework.stereotype.Component;

@Component
public class PersonMapper {

    public PersonDTO toDTO(Person person) {
        if (person == null) {
            return null;
        }

        return new PersonDTO(
                person.getId(),
                person.getFirstName(),
                person.getLastName()
        );
    }

    public Person toEntity(PersonDTO dto) {
        if (dto == null) {
            return null;
        }

        Person person = new Person();
        person.setId(dto.getId());
        person.setFirstName(dto.getFirstName());
        person.setLastName(dto.getLastName());

        return person;
    }
}
