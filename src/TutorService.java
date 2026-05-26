package com.petshop.service;

import com.petshop.domain.dto.TutorDTO;
import com.petshop.domain.entity.Tutor;
import com.petshop.exception.EntityNotFoundException;
import com.petshop.repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TutorService {

    private final TutorRepository tutorRepository;

    public Tutor createTutor(TutorDTO dto) {
        Tutor tutor = Tutor.builder()
                .name(dto.getName())
                .cpf(dto.getCpf())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .build();
        return tutorRepository.save(tutor);
    }

    public Tutor updateTutor(Long id, TutorDTO dto) {
        Tutor tutor = getTutorById(id);
        tutor.setName(dto.getName());
        tutor.setPhone(dto.getPhone());
        if (dto.getEmail() != null) {
            tutor.setEmail(dto.getEmail());
        }
        return tutorRepository.save(tutor);
    }

    @Transactional(readOnly = true)
    public Tutor getTutorById(Long id) {
        return tutorRepository.findById(id)
                .orElseThrow(() -> EntityNotFoundException.tutorNotFound(id));
    }

    @Transactional(readOnly = true)
    public Tutor getTutorByCpf(String cpf) {
        return tutorRepository.findByCpf(cpf)
                .orElseThrow(() -> new EntityNotFoundException("Tutor não encontrado com CPF: " + cpf));
    }

    @Transactional(readOnly = true)
    public List<Tutor> getAllTutors() {
        return tutorRepository.findAll();
    }

    public void deleteTutor(Long id) {
        Tutor tutor = getTutorById(id);
        tutorRepository.delete(tutor);
    }

    @Transactional(readOnly = true)
    public List<TutorDTO> getAllTutorsDTO() {
        return tutorRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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
