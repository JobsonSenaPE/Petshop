package com.petshop.controller;

import com.petshop.domain.dto.TutorDTO;
import com.petshop.domain.dto.AnimalDTO;
import com.petshop.domain.entity.Tutor;
import com.petshop.domain.entity.Animal;
import com.petshop.service.TutorService;
import com.petshop.service.AnimalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/employee/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('EMPLOYEE')")
public class AdminEmployeeController {

    private final TutorService tutorService;
    private final AnimalService animalService;

    @GetMapping("/tutors")
    public ResponseEntity<List<TutorDTO>> getAllTutors() {
        List<TutorDTO> tutors = tutorService.getAllTutorsDTO();
        return ResponseEntity.ok(tutors);
    }

    @GetMapping("/tutors/{id}")
    public ResponseEntity<TutorDTO> getTutorById(@PathVariable Long id) {
        Tutor tutor = tutorService.getTutorById(id);
        return ResponseEntity.ok(convertTutorToDTO(tutor));
    }

    @PutMapping("/tutors/{id}")
    public ResponseEntity<TutorDTO> updateTutor(@PathVariable Long id, @Valid @RequestBody TutorDTO dto) {
        Tutor tutor = tutorService.updateTutor(id, dto);
        return ResponseEntity.ok(convertTutorToDTO(tutor));
    }

    @DeleteMapping("/tutors/{id}")
    public ResponseEntity<Void> deleteTutor(@PathVariable Long id) {
        tutorService.deleteTutor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/animals")
    public ResponseEntity<List<AnimalDTO>> getAllAnimals() {
        List<AnimalDTO> animals = animalService.getAllAnimalsDTO();
        return ResponseEntity.ok(animals);
    }

    @GetMapping("/animals/{id}")
    public ResponseEntity<AnimalDTO> getAnimalById(@PathVariable Long id) {
        Animal animal = animalService.getAnimalById(id);
        return ResponseEntity.ok(convertAnimalToDTO(animal));
    }

    @GetMapping("/tutors/{tutorId}/animals")
    public ResponseEntity<List<AnimalDTO>> getAnimalsByTutor(@PathVariable Long tutorId) {
        List<Animal> animals = animalService.getAnimalsByTutor(tutorId);
        List<AnimalDTO> dtos = animals.stream().map(this::convertAnimalToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/animals/{id}")
    public ResponseEntity<AnimalDTO> updateAnimal(@PathVariable Long id, @Valid @RequestBody AnimalDTO dto) {
        Animal animal = animalService.updateAnimal(id, dto);
        return ResponseEntity.ok(convertAnimalToDTO(animal));
    }

    @DeleteMapping("/animals/{id}")
    public ResponseEntity<Void> deleteAnimal(@PathVariable Long id) {
        animalService.deleteAnimal(id);
        return ResponseEntity.noContent().build();
    }

    private TutorDTO convertTutorToDTO(Tutor tutor) {
        return TutorDTO.builder()
                .id(tutor.getId())
                .name(tutor.getName())
                .cpf(tutor.getCpf())
                .phone(tutor.getPhone())
                .email(tutor.getEmail())
                .build();
    }

    private AnimalDTO convertAnimalToDTO(Animal animal) {
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
