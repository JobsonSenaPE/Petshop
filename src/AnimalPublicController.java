package com.petshop.controller;

import com.petshop.domain.dto.AnimalDTO;
import com.petshop.domain.entity.Animal;
import com.petshop.service.AnimalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/public/animals")
@RequiredArgsConstructor
public class AnimalPublicController {

    private final AnimalService animalService;

    @PostMapping
    public ResponseEntity<AnimalDTO> createAnimal(@Valid @RequestBody AnimalDTO animalDTO) {
        Animal animal = animalService.createAnimal(animalDTO);
        return new ResponseEntity<>(convertToDTO(animal), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimalDTO> getAnimalById(@PathVariable Long id) {
        Animal animal = animalService.getAnimalById(id);
        return ResponseEntity.ok(convertToDTO(animal));
    }

    @GetMapping("/tutor/{tutorId}")
    public ResponseEntity<List<AnimalDTO>> getAnimalsByTutor(@PathVariable Long tutorId) {
        List<Animal> animals = animalService.getAnimalsByTutor(tutorId);
        List<AnimalDTO> dtos = animals.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    private AnimalDTO convertToDTO(Animal animal) {
        return AnimalDTO.builder()
                .id(animal.getId())
                .name(animal.getName())
                .species(animal.getSpecies())
                .breed(animal.getBreed())
                .age(animal.getAge())
                .weight(animal.getWeight())
                .observations(animal.getObservations())
                .tutorId(animal.getTutor().getId())
                .build();
    }
}
