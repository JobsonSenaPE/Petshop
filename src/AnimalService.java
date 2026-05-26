package com.petshop.service;

import com.petshop.domain.dto.AnimalDTO;
import com.petshop.domain.entity.Animal;
import com.petshop.domain.entity.Tutor;
import com.petshop.exception.EntityNotFoundException;
import com.petshop.repository.AnimalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final TutorService tutorService;

    public Animal createAnimal(AnimalDTO dto) {
        Tutor tutor = tutorService.getTutorById(dto.getTutorId());
        
        Animal animal = Animal.builder()
                .name(dto.getName())
                .species(dto.getSpecies())
                .breed(dto.getBreed())
                .age(dto.getAge())
                .weight(dto.getWeight())
                .observations(dto.getObservations())
                .tutor(tutor)
                .build();
        return animalRepository.save(animal);
    }

    public Animal updateAnimal(Long id, AnimalDTO dto) {
        Animal animal = getAnimalById(id);
        animal.setName(dto.getName());
        animal.setSpecies(dto.getSpecies());
        animal.setBreed(dto.getBreed());
        animal.setAge(dto.getAge());
        animal.setWeight(dto.getWeight());
        animal.setObservations(dto.getObservations());
        return animalRepository.save(animal);
    }

    @Transactional(readOnly = true)
    public Animal getAnimalById(Long id) {
        return animalRepository.findById(id)
                .orElseThrow(() -> EntityNotFoundException.animalNotFound(id));
    }

    @Transactional(readOnly = true)
    public List<Animal> getAnimalsByTutor(Long tutorId) {
        tutorService.getTutorById(tutorId);
        return animalRepository.findByTutorId(tutorId);
    }

    @Transactional(readOnly = true)
    public List<Animal> getAllAnimals() {
        return animalRepository.findAll();
    }

    public void deleteAnimal(Long id) {
        Animal animal = getAnimalById(id);
        animalRepository.delete(animal);
    }

    @Transactional(readOnly = true)
    public List<AnimalDTO> getAllAnimalsDTO() {
        return animalRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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
