package be.solxa.peopleapi.service;

import be.solxa.peopleapi.exception.PersonValidationException;
import be.solxa.peopleapi.model.Person;
import be.solxa.peopleapi.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonService personService;

    private Person testPerson;
    private UUID testId;

    @BeforeEach
    public void setUp() {
        testId = UUID.randomUUID();
        testPerson = new Person();
        testPerson.setId(testId);
        testPerson.setFirstName("John");
        testPerson.setLastName("Doe");
    }

    @Test
    public void testSearchNoFilters() {
        List<Person> personList = List.of(testPerson);
        when(personRepository.findAll()).thenReturn(personList);

        List<Person> result = personService.search(null, null);

        assertEquals(1, result.size());
        assertEquals("John", result.getFirst().getFirstName());
        verify(personRepository).findAll();
        verify(personRepository, never()).search(any(), any());
    }

    @Test
    public void testSearchWithFilters() {
        List<Person> filteredList = List.of(testPerson);
        when(personRepository.search("Jo", null)).thenReturn(filteredList);

        List<Person> result = personService.search("Jo", null);

        assertEquals(1, result.size());
        assertEquals("John", result.getFirst().getFirstName());
        verify(personRepository).search("Jo", null);
        verify(personRepository, never()).findAll();
    }

    @Test
    public void testGetPersonById() {
        when(personRepository.findById(testId)).thenReturn(Optional.of(testPerson));

        Optional<Person> result = personService.getPersonById(testId);

        assertTrue(result.isPresent());
        assertEquals(testId, result.get().getId());
        assertEquals("John", result.get().getFirstName());
    }

    @Test
    public void testGetPersonByIdNotFound() {
        when(personRepository.findById(testId)).thenReturn(Optional.empty());

        Optional<Person> result = personService.getPersonById(testId);

        assertFalse(result.isPresent());
    }

    @Test
    public void testCreatePerson() {
        Person newPerson = new Person();
        newPerson.setFirstName("Jane");
        newPerson.setLastName("Smith");

        when(personRepository.save(any(Person.class))).thenAnswer(invocation -> {
            Person savedPerson = invocation.getArgument(0);
            assertNotNull(savedPerson.getId()); // ID should be generated
            return savedPerson;
        });

        Person result = personService.createPerson(newPerson);

        assertNotNull(result.getId());
        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        verify(personRepository).save(any(Person.class));
    }

    @Test
    public void testCreatePersonWithEmptyFirstName() {
        Person invalidPerson = new Person();
        invalidPerson.setFirstName("");
        invalidPerson.setLastName("Smith");

        assertThrows(PersonValidationException.class, () -> {
            personService.createPerson(invalidPerson);
        });

        verify(personRepository, never()).save(any(Person.class));
    }

    @Test
    public void testCreatePersonWithEmptyLastName() {
        Person invalidPerson = new Person();
        invalidPerson.setFirstName("Jane");
        invalidPerson.setLastName("");

        assertThrows(PersonValidationException.class, () -> {
            personService.createPerson(invalidPerson);
        });

        verify(personRepository, never()).save(any(Person.class));
    }

    @Test
    public void testUpdatePerson() {
        Person updatedDetails = new Person();
        updatedDetails.setFirstName("John");
        updatedDetails.setLastName("Updated");

        when(personRepository.findById(testId)).thenReturn(Optional.of(testPerson));
        when(personRepository.save(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Person> result = personService.updatePerson(testId, updatedDetails);

        assertTrue(result.isPresent());
        assertEquals(testId, result.get().getId());
        assertEquals("John", result.get().getFirstName());
        assertEquals("Updated", result.get().getLastName());
        verify(personRepository).save(testPerson);
    }

    @Test
    public void testUpdatePersonNotFound() {
        Person updatedDetails = new Person();
        updatedDetails.setFirstName("John");
        updatedDetails.setLastName("Updated");

        when(personRepository.findById(testId)).thenReturn(Optional.empty());

        Optional<Person> result = personService.updatePerson(testId, updatedDetails);

        assertFalse(result.isPresent());
        verify(personRepository, never()).save(any(Person.class));
    }

    @Test
    public void testDeletePerson() {
        when(personRepository.findById(testId)).thenReturn(Optional.of(testPerson));
        doNothing().when(personRepository).delete(testPerson);

        boolean result = personService.deletePerson(testId);

        assertTrue(result);
        verify(personRepository).delete(testPerson);
    }

    @Test
    public void testDeletePersonNotFound() {
        when(personRepository.findById(testId)).thenReturn(Optional.empty());

        boolean result = personService.deletePerson(testId);

        assertFalse(result);
        verify(personRepository, never()).delete(any(Person.class));
    }
}