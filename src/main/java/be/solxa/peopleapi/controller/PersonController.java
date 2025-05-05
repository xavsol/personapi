package be.solxa.peopleapi.controller;

import be.solxa.peopleapi.dto.PersonDTO;
import be.solxa.peopleapi.mapper.PersonMapper;
import be.solxa.peopleapi.model.Person;
import be.solxa.peopleapi.service.PersonService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/persons")
@AllArgsConstructor
public class PersonController {

    private final PersonService personService;
    private final PersonMapper personMapper;

    @GetMapping
    @Operation(summary = "Get all persons with optional filtering",
            description = "Retrieve a list of persons with optional filtering by first or last name (case insensitive, partial match)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Persons retrieved successfully")
    })
    public ResponseEntity<List<PersonDTO>> search(
            @Parameter(description = "Filter by first name (case insensitive, partial match)")
            @RequestParam(required = false) String firstName,
            @Parameter(description = "Filter by last name (case insensitive, partial match)")
            @RequestParam(required = false) String lastName) {

        List<Person> persons = personService.search(firstName, lastName);
        List<PersonDTO> personDTOs = persons.stream()
                .map(personMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(personDTOs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a person by ID", description = "Retrieve a specific person by their UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Person found",
                    content = @Content(schema = @Schema(implementation = PersonDTO.class))),
            @ApiResponse(responseCode = "404", description = "Person not found")
    })
    public ResponseEntity<PersonDTO> getPersonById(
            @Parameter(description = "Person UUID", required = true)
            @PathVariable UUID id) {
        return personService.getPersonById(id)
                .map(personMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new person", description = "Create a new person with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Person created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<PersonDTO> createPerson(
            @Parameter(description = "Person details", required = true)
            @Valid @RequestBody PersonDTO personDTO) {
        Person person = personMapper.toEntity(personDTO);
        Person createdPerson = personService.createPerson(person);
        return ResponseEntity.status(HttpStatus.CREATED).body(personMapper.toDTO(createdPerson));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a person", description = "Update an existing person with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Person updated successfully"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<PersonDTO> updatePerson(
            @Parameter(description = "Person UUID", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Updated person details", required = true)
            @Valid @RequestBody PersonDTO personDTO) {
        Person personDetails = personMapper.toEntity(personDTO);
        return personService.updatePerson(id, personDetails)
                .map(personMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a person", description = "Delete a person by their UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Person deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Person not found")
    })
    public ResponseEntity<Void> deletePerson(
            @Parameter(description = "Person UUID", required = true)
            @PathVariable UUID id) {
        boolean deleted = personService.deletePerson(id);

        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}