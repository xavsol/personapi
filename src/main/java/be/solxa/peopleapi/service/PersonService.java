package be.solxa.peopleapi.service;

import be.solxa.peopleapi.exception.PersonValidationException;
import be.solxa.peopleapi.model.Person;
import be.solxa.peopleapi.repository.PersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    public List<Person> search(String firstName, String lastName) {
        // If both filters are null, return all persons
        if (firstName == null && lastName == null) {
            return personRepository.findAll();
        }

        return personRepository.search(firstName, lastName);
    }

    public Optional<Person> getPersonById(UUID id) {
        return personRepository.findById(id);
    }

    public Person createPerson(Person person) {
        validatePerson(person);
        person.generateId();
        return personRepository.save(person);
    }

    public Optional<Person> updatePerson(UUID id, Person personDetails) {
        validatePerson(personDetails);

        return personRepository.findById(id)
                .map(existingPerson -> {
                    existingPerson.setFirstName(personDetails.getFirstName());
                    existingPerson.setLastName(personDetails.getLastName());
                    return personRepository.save(existingPerson);
                });
    }

    public boolean deletePerson(UUID id) {
        return personRepository.findById(id)
                .map(person -> {
                    personRepository.delete(person);
                    return true;
                })
                .orElse(false);
    }

    private void validatePerson(Person person) {
        if (person.getFirstName() == null || person.getFirstName().trim().isEmpty()) {
            throw new PersonValidationException("First name cannot be empty");
        }

        if (person.getLastName() == null || person.getLastName().trim().isEmpty()) {
            throw new PersonValidationException("Last name cannot be empty");
        }
    }
}
