package be.solxa.peopleapi.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PersonTest {

    @Test
    public void testGenerateId() {
        Person person = new Person();
        assertNull(person.getId());

        person.generateId();
        assertNotNull(person.getId());
    }

    @Test
    public void testGettersAndSetters() {
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");

        assertEquals("John", person.getFirstName());
        assertEquals("Doe", person.getLastName());
    }
}