package be.solxa.peopleapi.repository;

import be.solxa.peopleapi.model.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class PersonRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PersonRepository personRepository;

    @Test
    public void testFindByFirstNameAndLastNameBothNull() {
        // Given
        Person person1 = new Person();
        person1.setFirstName("John");
        person1.setLastName("Doe");
        entityManager.persist(person1);

        Person person2 = new Person();
        person2.setFirstName("Jane");
        person2.setLastName("Smith");
        entityManager.persist(person2);

        entityManager.flush();

        // When
        List<Person> result = personRepository.search(null, null);

        // Then
        assertEquals(2, result.size());
    }

    @Test
    public void testFindByFirstNameStartsWith() {
        // Given
        Person person1 = new Person();
        person1.setFirstName("John");
        person1.setLastName("Doe");
        entityManager.persist(person1);

        Person person2 = new Person();
        person2.setFirstName("Jonathan");
        person2.setLastName("Smith");
        entityManager.persist(person2);

        Person person3 = new Person();
        person3.setFirstName("Alice");
        person3.setLastName("Johnson");
        entityManager.persist(person3);

        entityManager.flush();

        // When
        List<Person> result = personRepository.search("Jo", null);

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(p -> p.getFirstName().equals("John")));
        assertTrue(result.stream().anyMatch(p -> p.getFirstName().equals("Jonathan")));
    }

    @Test
    public void testFindByFirstNameEndsWith() {
        // Given
        Person person1 = new Person();
        person1.setFirstName("Sebastian");
        person1.setLastName("Doe");
        entityManager.persist(person1);

        Person person2 = new Person();
        person2.setFirstName("Fabian");
        person2.setLastName("Smith");
        entityManager.persist(person2);

        Person person3 = new Person();
        person3.setFirstName("Alice");
        person3.setLastName("Johnson");
        entityManager.persist(person3);

        entityManager.flush();

        // When
        List<Person> result = personRepository.search("ian", null);

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(p -> p.getFirstName().equals("Sebastian")));
        assertTrue(result.stream().anyMatch(p -> p.getFirstName().equals("Fabian")));
    }

    @Test
    public void testFindByLastNameCaseInsensitive() {
        // Given
        Person person1 = new Person();
        person1.setFirstName("John");
        person1.setLastName("Smith");
        entityManager.persist(person1);

        Person person2 = new Person();
        person2.setFirstName("Jane");
        person2.setLastName("SMITH");
        entityManager.persist(person2);

        Person person3 = new Person();
        person3.setFirstName("Alice");
        person3.setLastName("Johnson");
        entityManager.persist(person3);

        entityManager.flush();

        // When
        List<Person> result = personRepository.search(null, "smith");

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(p -> p.getLastName().equals("Smith")));
        assertTrue(result.stream().anyMatch(p -> p.getLastName().equals("SMITH")));
    }

    @Test
    public void testFindByFirstNameAndLastName() {
        // Given
        Person person1 = new Person();
        person1.setFirstName("John");
        person1.setLastName("Smith");
        entityManager.persist(person1);

        Person person2 = new Person();
        person2.setFirstName("John");
        person2.setLastName("Doe");
        entityManager.persist(person2);

        entityManager.flush();

        // When
        List<Person> result = personRepository.search("Jo", "Sm");

        // Then
        assertEquals(1, result.size());
        assertEquals("John", result.getFirst().getFirstName());
        assertEquals("Smith", result.getFirst().getLastName());
    }
}