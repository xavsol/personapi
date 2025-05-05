package be.solxa.peopleapi.controller;

import be.solxa.peopleapi.dto.PersonDTO;
import be.solxa.peopleapi.model.Person;
import be.solxa.peopleapi.repository.PersonRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1"
})
public class PersonControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PersonRepository personRepository;

    private Person testPerson;
    private UUID testId;

    @BeforeEach
    void setUp() {
        personRepository.deleteAll();

        testPerson = new Person();
        testPerson.setFirstName("John");
        testPerson.setLastName("Doe");
        testPerson = personRepository.save(testPerson);
        testId = testPerson.getId();
    }

    @Test
    void getAllPersons_ShouldReturnAllPersons() throws Exception {
        mockMvc.perform(get("/api/persons"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(testId.toString()))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"));
    }

    @Test
    void getAllPersons_WithFilters_ShouldReturnFilteredPersons() throws Exception {
        Person anotherPerson = new Person();
        anotherPerson.setFirstName("Jane");
        anotherPerson.setLastName("Smith");
        personRepository.save(anotherPerson);

        mockMvc.perform(get("/api/persons").param("firstName", "Jo"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"));

        mockMvc.perform(get("/api/persons").param("lastName", "Sm"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Jane"));
    }

    @Test
    void getPersonById_ExistingId_ShouldReturnPerson() throws Exception {
        mockMvc.perform(get("/api/persons/{id}", testId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void getPersonById_NonExistingId_ShouldReturnNotFound() throws Exception {
        UUID nonExistingId = UUID.randomUUID();
        mockMvc.perform(get("/api/persons/{id}", nonExistingId))
                .andExpect(status().isNotFound());
    }

    @Test
    void createPerson_ValidPerson_ShouldCreateAndReturnPerson() throws Exception {
        PersonDTO personDTO = new PersonDTO();
        personDTO.setFirstName("Alice");
        personDTO.setLastName("Johnson");

        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Johnson"));
    }

    @Test
    void createPerson_InvalidPerson_ShouldReturnBadRequest() throws Exception {
        PersonDTO invalidPerson = new PersonDTO();
        invalidPerson.setFirstName("");
        invalidPerson.setLastName("Johnson");

        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPerson)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePerson_ExistingId_ShouldUpdateAndReturnPerson() throws Exception {
        PersonDTO updatedPerson = new PersonDTO();
        updatedPerson.setFirstName("John");
        updatedPerson.setLastName("Updated");

        mockMvc.perform(put("/api/persons/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPerson)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Updated"));
    }

    @Test
    void updatePerson_NonExistingId_ShouldReturnNotFound() throws Exception {
        UUID nonExistingId = UUID.randomUUID();
        PersonDTO updatedPerson = new PersonDTO();
        updatedPerson.setFirstName("John");
        updatedPerson.setLastName("Updated");

        mockMvc.perform(put("/api/persons/{id}", nonExistingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPerson)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePerson_ExistingId_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/persons/{id}", testId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePerson_NonExistingId_ShouldReturnNotFound() throws Exception {
        UUID nonExistingId = UUID.randomUUID();
        mockMvc.perform(delete("/api/persons/{id}", nonExistingId))
                .andExpect(status().isNotFound());
    }
}