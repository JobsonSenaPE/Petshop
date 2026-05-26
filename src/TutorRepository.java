package com.petshop.repository;

import com.petshop.domain.entity.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TutorRepository extends JpaRepository<Tutor, Long> {
    Optional<Tutor> findByCpf(String cpf);
    Optional<Tutor> findByEmail(String email);
    Optional<Tutor> findByPhone(String phone);
}
