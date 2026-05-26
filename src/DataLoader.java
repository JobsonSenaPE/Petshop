package com.petshop.config;

import com.petshop.domain.entity.Employee;
import com.petshop.domain.entity.Tutor;
import com.petshop.domain.entity.Animal;
import com.petshop.domain.enum.Species;
import com.petshop.repository.EmployeeRepository;
import com.petshop.repository.TutorRepository;
import com.petshop.repository.AnimalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final TutorRepository tutorRepository;
    private final AnimalRepository animalRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (employeeRepository.count() == 0) {
            createDefaultEmployees();
        }

        if (tutorRepository.count() == 0) {
            createSampleTutorsAndAnimals();
        }
    }

    private void createDefaultEmployees() {
        Employee bathEmployee = Employee.builder()
                .username("banho_user")
                .email("banho@petshop.com")
                .password(passwordEncoder.encode("senha123"))
                .fullName("João - Setor de Banho")
                .department("BANHO")
                .enabled(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .accountNonExpired(true)
                .build();

        Employee groomingEmployee = Employee.builder()
                .username("tosa_user")
                .email("tosa@petshop.com")
                .password(passwordEncoder.encode("senha123"))
                .fullName("Maria - Setor de Tosa")
                .department("TOSA")
                .enabled(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .accountNonExpired(true)
                .build();

        Employee vetEmployee = Employee.builder()
                .username("vet_user")
                .email("vet@petshop.com")
                .password(passwordEncoder.encode("senha123"))
                .fullName("Dr. Carlos - Veterinário")
                .department("VETERINÁRIO")
                .enabled(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .accountNonExpired(true)
                .build();

        Employee adminEmployee = Employee.builder()
                .username("admin")
                .email("admin@petshop.com")
                .password(passwordEncoder.encode("admin123"))
                .fullName("Administrador")
                .department("ADMIN")
                .enabled(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .accountNonExpired(true)
                .build();

        employeeRepository.save(bathEmployee);
        employeeRepository.save(groomingEmployee);
        employeeRepository.save(vetEmployee);
        employeeRepository.save(adminEmployee);
    }

    private void createSampleTutorsAndAnimals() {
        Tutor tutor1 = Tutor.builder()
                .name("Ana Silva")
                .cpf("12345678901")
                .phone("11999999999")
                .email("ana@email.com")
                .build();

        Tutor tutor2 = Tutor.builder()
                .name("Bruno Costa")
                .cpf("98765432100")
                .phone("11988888888")
                .email("bruno@email.com")
                .build();

        tutorRepository.save(tutor1);
        tutorRepository.save(tutor2);

        Animal animal1 = Animal.builder()
                .name("Rex")
                .species(Species.DOG)
                .breed("Vira-lata")
                .age(4)
                .weight(12.5)
                .tutor(tutor1)
                .build();

        Animal animal2 = Animal.builder()
                .name("Mimi")
                .species(Species.CAT)
                .breed("Siamês")
                .age(2)
                .weight(4.2)
                .tutor(tutor2)
                .build();

        animalRepository.save(animal1);
        animalRepository.save(animal2);
    }
}
