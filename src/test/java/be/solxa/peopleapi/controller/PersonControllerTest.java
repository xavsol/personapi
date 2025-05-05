package be.solxa.peopleapi.controller;

import be.solxa.peopleapi.dto.PersonDTO;
import be.solxa.peopleapi.mapper.PersonMapper;
import be.solxa.peopleapi.model.Person;
import be.solxa.peopleapi.service.PersonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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

    @MockBean
    private PersonService personService;

    @MockBean
    private PersonMapper personMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllPersons() throws Exception {
        // Créer des entités Person
        Person person1 = new Person();
        person1.setId(UUID.randomUUID());
        person1.setFirstName("John");
        person1.setLastName("Doe");

        Person person2 = new Person();
        person2.setId(UUID.randomUUID());
        person2.setFirstName("Jane");
        person2.setLastName("Smith");

        PersonDTO dto1 = new PersonDTO();
        dto1.setId(person1.getId());
        dto1.setFirstName("John");
        dto1.setLastName("Doe");

        PersonDTO dto2 = new PersonDTO();
        dto2.setId(person2.getId());
        dto2.setFirstName("Jane");
        dto2.setLastName("Smith");

        // Configurer les mocks
        when(personService.search(null, null)).thenReturn(Arrays.asList(person1, person2));
        when(personMapper.toDTO(person1)).thenReturn(dto1);
        when(personMapper.toDTO(person2)).thenReturn(dto2);

        mockMvc.perform(get("/api/persons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));
    }

    @Test
    public void testGetPersonsWithFilters() throws Exception {
        // Créer une entité Person
        Person person = new Person();
        person.setId(UUID.randomUUID());
        person.setFirstName("John");
        person.setLastName("Doe");

        // Créer le DTO correspondant
        PersonDTO dto = new PersonDTO();
        dto.setId(person.getId());
        dto.setFirstName("John");
        dto.setLastName("Doe");

        // Configurer les mocks
        when(personService.search("Jo", null)).thenReturn(List.of(person));
        when(personMapper.toDTO(person)).thenReturn(dto);

        mockMvc.perform(get("/api/persons").param("firstName", "Jo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    public void testGetPersonById() throws Exception {
        UUID id = UUID.randomUUID();

        // Créer une entité Person
        Person person = new Person();
        person.setId(id);
        person.setFirstName("John");
        person.setLastName("Doe");

        // Créer le DTO correspondant
        PersonDTO dto = new PersonDTO();
        dto.setId(id);
        dto.setFirstName("John");
        dto.setLastName("Doe");

        // Configurer les mocks
        when(personService.getPersonById(id)).thenReturn(Optional.of(person));
        when(personMapper.toDTO(person)).thenReturn(dto);

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
        UUID id = UUID.randomUUID();

        // Créer le DTO d'entrée
        PersonDTO inputDto = new PersonDTO();
        inputDto.setFirstName("John");
        inputDto.setLastName("Doe");

        // Créer l'entité qui sera retournée par le mapper
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");

        // Créer l'entité sauvegardée
        Person savedPerson = new Person();
        savedPerson.setId(id);
        savedPerson.setFirstName("John");
        savedPerson.setLastName("Doe");

        // Créer le DTO de sortie
        PersonDTO outputDto = new PersonDTO();
        outputDto.setId(id);
        outputDto.setFirstName("John");
        outputDto.setLastName("Doe");

        // Configurer les mocks
        when(personMapper.toEntity(any(PersonDTO.class))).thenReturn(person);
        when(personService.createPerson(any(Person.class))).thenReturn(savedPerson);
        when(personMapper.toDTO(savedPerson)).thenReturn(outputDto);

        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    public void testUpdatePerson() throws Exception {
        UUID id = UUID.randomUUID();

        // Créer le DTO d'entrée
        PersonDTO inputDto = new PersonDTO();
        inputDto.setFirstName("John");
        inputDto.setLastName("Updated");

        // Créer l'entité qui sera retournée par le mapper
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Updated");

        // Créer l'entité mise à jour
        Person updatedPerson = new Person();
        updatedPerson.setId(id);
        updatedPerson.setFirstName("John");
        updatedPerson.setLastName("Updated");

        // Créer le DTO de sortie
        PersonDTO outputDto = new PersonDTO();
        outputDto.setId(id);
        outputDto.setFirstName("John");
        outputDto.setLastName("Updated");

        // Configurer les mocks
        when(personMapper.toEntity(any(PersonDTO.class))).thenReturn(person);
        when(personService.updatePerson(eq(id), any(Person.class))).thenReturn(Optional.of(updatedPerson));
        when(personMapper.toDTO(updatedPerson)).thenReturn(outputDto);

        mockMvc.perform(put("/api/persons/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Updated"));
    }

    @Test
    public void testUpdatePersonNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        // Créer le DTO d'entrée
        PersonDTO inputDto = new PersonDTO();
        inputDto.setFirstName("John");
        inputDto.setLastName("Doe");

        // Créer l'entité qui sera retournée par le mapper
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");

        // Configurer les mocks
        when(personMapper.toEntity(any(PersonDTO.class))).thenReturn(person);
        when(personService.updatePerson(eq(id), any(Person.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/persons/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
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