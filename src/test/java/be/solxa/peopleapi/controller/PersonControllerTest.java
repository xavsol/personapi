package be.solxa.peopleapi.controller;

import be.solxa.peopleapi.model.Person;
import be.solxa.peopleapi.service.PersonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;

@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PersonService personService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllPersons() throws Exception {
        Person person1 = new Person();
        person1.setId(UUID.randomUUID());
        person1.setFirstName("John");
        person1.setLastName("Doe");

        Person person2 = new Person();
        person2.setId(UUID.randomUUID());
        person2.setFirstName("Jane");
        person2.setLastName("Smith");

        when(personService.search(null, null)).thenReturn(Arrays.asList(person1, person2));

        mockMvc.perform(get("/api/persons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));
    }

    @Test
    public void testGetPersonsWithFilters() throws Exception {
        Person person = new Person();
        person.setId(UUID.randomUUID());
        person.setFirstName("John");
        person.setLastName("Doe");

        when(personService.search("Jo", null)).thenReturn(List.of(person));

        mockMvc.perform(get("/api/persons").param("firstName", "Jo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    public void testGetPersonById() throws Exception {
        UUID id = UUID.randomUUID();
        Person person = new Person();
        person.setId(id);
        person.setFirstName("John");
        person.setLastName("Doe");

        when(personService.getPersonById(id)).thenReturn(Optional.of(person));

        mockMvc.perform(get("/api/persons/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    public void testGetPersonByIdNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(personService.getPersonById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/persons/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreatePerson() throws Exception {
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");

        Person savedPerson = new Person();
        savedPerson.setId(UUID.randomUUID());
        savedPerson.setFirstName("John");
        savedPerson.setLastName("Doe");

        when(personService.createPerson(any(Person.class))).thenReturn(savedPerson);

        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(person)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    public void testUpdatePerson() throws Exception {
        UUID id = UUID.randomUUID();
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Updated");

        Person updatedPerson = new Person();
        updatedPerson.setId(id);
        updatedPerson.setFirstName("John");
        updatedPerson.setLastName("Updated");

        when(personService.updatePerson(eq(id), any(Person.class))).thenReturn(Optional.of(updatedPerson));

        mockMvc.perform(put("/api/persons/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(person)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Updated"));
    }

    @Test
    public void testUpdatePersonNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");

        when(personService.updatePerson(eq(id), any(Person.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/persons/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(person)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeletePerson() throws Exception {
        UUID id = UUID.randomUUID();
        when(personService.deletePerson(id)).thenReturn(true);

        mockMvc.perform(delete("/api/persons/{id}", id))
                .andExpect(status().isNoContent());

        verify(personService).deletePerson(id);
    }

    @Test
    public void testDeletePersonNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(personService.deletePerson(id)).thenReturn(false);

        mockMvc.perform(delete("/api/persons/{id}", id))
                .andExpect(status().isNotFound());

        verify(personService).deletePerson(id);
    }
    }