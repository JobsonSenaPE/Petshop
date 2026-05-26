package com.petshop.controller;

import com.petshop.domain.dto.TutorDTO;
import com.petshop.domain.entity.Tutor;
import com.petshop.service.TutorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/tutors")
@RequiredArgsConstructor
public class TutorPublicController {

    private final TutorService tutorService;

    @PostMapping("/register")
    public ResponseEntity<TutorDTO> registerTutor(@Valid @RequestBody TutorDTO tutorDTO) {
        Tutor tutor = tutorService.createTutor(tutorDTO);
        TutorDTO response = convertToDTO(tutor);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TutorDTO> getTutorById(@PathVariable Long id) {
        Tutor tutor = tutorService.getTutorById(id);
        return ResponseEntity.ok(convertToDTO(tutor));
    }

    @GetMapping("/search/cpf/{cpf}")
    public ResponseEntity<TutorDTO> getTutorByCpf(@PathVariable String cpf) {
        Tutor tutor = tutorService.getTutorByCpf(cpf);
        return ResponseEntity.ok(convertToDTO(tutor));
    }

    private TutorDTO convertToDTO(Tutor tutor) {
        return TutorDTO.builder()
                .id(tutor.getId())
                .name(tutor.getName())
                .cpf(tutor.getCpf())
                .phone(tutor.getPhone())
                .email(tutor.getEmail())
                .build();
    }
}
