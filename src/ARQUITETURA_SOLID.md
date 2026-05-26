# 📐 Arquitetura SOLID do Petshop System

## Visão Geral

Este documento detalha como os cinco princípios SOLID foram aplicados no desenvolvimento do Sistema de Agendamento Petshop.

---

## 1️⃣ Single Responsibility Principle (SRP)

**Definição**: Uma classe deve ter uma única razão para mudar, ou seja, uma única responsabilidade.

### Implementação no Projeto

#### ✅ Bom Exemplo: `AppointmentAvailabilityService`
```java
@Service
public class AppointmentAvailabilityService {
    // ÚNICA RESPONSABILIDADE: Validar e gerenciar disponibilidade de agendamentos
    - validateAppointmentDateTime()
    - validateBusinessHours()
    - validateLunchBreak()
    - validateIntervalBetweenAppointments()
    - generateAvailableSlots()
}
```

**Por que é bom?**
- A classe tem uma única razão para mudar: se as regras de disponibilidade mudarem
- Fácil de testar unitariamente
- Fácil de reutilizar em outros contextos

#### ✅ Separação de Serviços

| Serviço | Responsabilidade |
|---------|------------------|
| `TutorService` | CRUD e validações de tutores |
| `AnimalService` | CRUD e validações de animais |
| `AppointmentService` | Ciclo de vida de agendamentos |
| `AppointmentAvailabilityService` | Validação de disponibilidade |
| `PaymentService` | Processamento de pagamentos |
| `ServiceRecordService` | Histórico e analytics |

#### ❌ Evitado: Classe Monolítica
```java
// ❌ NÃO FAZER - Múltiplas responsabilidades
public class PetshopManager {
    public void createTutor() { ... }          // Responsabilidade 1
    public void validateAvailability() { ... } // Responsabilidade 2
    public void processPayment() { ... }       // Responsabilidade 3
    public void generateReport() { ... }       // Responsabilidade 4
}
```

---

## 2️⃣ Open/Closed Principle (OCP)

**Definição**: Uma classe deve ser aberta para extensão, mas fechada para modificação.

### Implementação no Projeto

#### ✅ Bom Exemplo: Enums Extensíveis

**ServiceType Enum:**
```java
public enum ServiceType {
    BATH("Banho", new BigDecimal("45.00")),
    GROOMING("Tosa", new BigDecimal("60.00")),
    VETERINARY("Consulta Veterinária", new BigDecimal("90.00")),
    // ... combos
}
```

**Por que é bom?**
- Para adicionar novo serviço: apenas adicione novo enum (NÃO modifique código existente)
- O método `getDepartment()` funciona com novo serviço automaticamente
- Extensível sem quebrar código existente

#### ✅ Estratégia: Template Method Pattern

```java
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(...) { ... }
    
    @ExceptionHandler(InvalidAppointmentException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAppointment(...) { ... }
    
    // Extensível: adicione novos @ExceptionHandler sem modificar handlers existentes
}
```

#### ❌ Evitado: Condicional Rígido
```java
// ❌ NÃO FAZER - Fechado para extensão
public BigDecimal getPrice(String serviceCode) {
    if (serviceCode.equals("BATH")) return 45.00;
    if (serviceCode.equals("GROOMING")) return 60.00;
    // ... para cada novo serviço, modifique o método
}
```

---

## 3️⃣ Liskov Substitution Principle (LSP)

**Definição**: Objetos de uma classe derivada devem ser substituíveis por objetos da classe base sem quebrar a aplicação.

### Implementação no Projeto

#### ✅ Bom Exemplo: Contrato Consistente

**Entity Base:**
```java
@Entity
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
}
```

**Implementações (Tutor, Animal, Appointment):**
```java
@Entity
public class Tutor extends BaseEntity { ... }

@Entity
public class Animal extends BaseEntity { ... }

@Entity
public class Appointment extends BaseEntity { ... }
```

**Por que é bom?**
- Qualquer código esperando uma entidade com `id` e `createdAt` funciona com todas
- Contratos claros e previsíveis

#### ✅ Exemplo: UserDetails Contract
```java
@Entity
public class Employee implements UserDetails {
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { ... }
    
    @Override
    public boolean isAccountNonExpired() { ... }
    
    // ... implementação do contrato UserDetails
}
```

**Spring Security aceita qualquer `UserDetails` sem modificação**

#### ❌ Evitado: Violação de Contrato
```java
// ❌ NÃO FAZER - Quebra o contrato
public class SpecialAnimal extends Animal {
    @Override
    public String getName() {
        return null; // Viola o contrato que Animal.getName() nunca retorna null
    }
}
```

---

## 4️⃣ Interface Segregation Principle (ISP)

**Definição**: Clientes não devem ser forçados a depender de interfaces que não usam.

### Implementação no Projeto

#### ✅ Bom Exemplo: Repositories Segregados

**TutorRepository:**
```java
@Repository
public interface TutorRepository extends JpaRepository<Tutor, Long> {
    Optional<Tutor> findByCpf(String cpf);
    Optional<Tutor> findByEmail(String email);
    // Apenas métodos relevantes para Tutor
}
```

**AppointmentRepository:**
```java
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByAnimalIdAndStatusIn(Long animalId, List<AppointmentStatus> statuses);
    
    @Query("SELECT a FROM Appointment a WHERE ...")
    List<Appointment> findAvailableSlots(LocalDateTime startDateTime, LocalDateTime endDateTime);
    // Apenas métodos relevantes para Appointment
}
```

**Por que é bom?**
- `TutorController` não precisa conhecer sobre `AppointmentRepository`
- Cada serviço depende apenas do que precisa
- Interfaces focadas e coesas

#### ❌ Evitado: Interface Gorda
```java
// ❌ NÃO FAZER - Interface muito grande
public interface PetshopRepository {
    // Métodos para Tutor
    Tutor findTutorById(Long id);
    void saveTutor(Tutor tutor);
    
    // Métodos para Animal
    Animal findAnimalById(Long id);
    void saveAnimal(Animal animal);
    
    // Métodos para Appointment
    Appointment findAppointmentById(Long id);
    void saveAppointment(Appointment appointment);
    
    // Qualquer cliente é forçado a depender de TUDO
}
```

---

## 5️⃣ Dependency Inversion Principle (DIP)

**Definição**: Módulos de alto nível não devem depender de módulos de baixo nível. Ambos devem depender de abstrações.

### Implementação no Projeto

#### ✅ Bom Exemplo: Injeção de Dependências

**AppointmentService (Alto Nível):**
```java
@Service
@RequiredArgsConstructor  // Injeção via Lombok
@Transactional
public class AppointmentService {
    // Depende de ABSTRAÇÕES (interfaces)
    private final AppointmentRepository appointmentRepository;
    private final AppointmentAvailabilityService availabilityService;
    private final AnimalService animalService;
    private final TutorService tutorService;
    
    // Não depende de implementações concretas
}
```

**Spring Container gerencia a criação:**
```java
// ✅ Spring cria e injeta automaticamente
AppointmentService appointmentService = new AppointmentService(
    appointmentRepository,
    availabilityService,
    animalService,
    tutorService
);
```

**Por que é bom?**
- Fácil trocar implementação (ex: `AppointmentRepository` por mock em testes)
- Código desacoplado e testável
- Responsabilidade do Spring gerenciar ciclo de vida

#### ✅ Exemplo: Abstração de Persistência

```java
// Abstração: Repository
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> { ... }

// Implementação: Spring Data fornece
// AppointmentRepositoryImpl extends SimpleJpaRepository { ... }

// Serviço depende da abstração
@Service
public class AppointmentService {
    private final AppointmentRepository repo;  // Depende de interface
    
    public Appointment getById(Long id) {
        return repo.findById(id).orElseThrow(...);  // Funciona com qualquer implementação
    }
}
```

#### ❌ Evitado: Dependência Direta
```java
// ❌ NÃO FAZER - Dependência direta de implementação
public class AppointmentService {
    private AppointmentRepositoryImpl repo = new AppointmentRepositoryImpl();  // Acoplamento!
    
    public Appointment getById(Long id) {
        return repo.findById(id);
    }
}

// Impossível testar com mock, impossível trocar implementação
```

---

## 🎯 Resumo de Benefícios SOLID

| Princípio | Benefício | Aplicado Em |
|-----------|-----------|------------|
| **SRP** | Código mais fácil de manter e testar | Services separados por responsabilidade |
| **OCP** | Fácil adicionar novos features | Enums extensíveis, Exception handlers |
| **LSP** | Código previsível e reutilizável | Contratos consistentes em entities |
| **ISP** | Interfaces focadas | Repositories especializados |
| **DIP** | Código testável e desacoplado | Injeção de dependências via Spring |

---

## 🧪 Como Testar (Exemplo)

### Teste Unitário com Mock (Possível devido SOLID)

```java
@ExtendWith(MockitoExtension.class)
public class AppointmentAvailabilityServiceTest {
    
    @Mock
    private AppointmentRepository appointmentRepository;
    
    @InjectMocks
    private AppointmentAvailabilityService service;
    
    @Test
    void testValidateBusinessHours_ShouldThrowException_WhenOutsideBusinessHours() {
        LocalDateTime invalidTime = LocalDateTime.of(2025, 6, 15, 21, 0, 0);
        
        assertThrows(InvalidAppointmentException.class, () -> {
            service.validateAppointmentDateTime(invalidTime, 1L);
        });
    }
}
```

**Por que funciona?**
- `AppointmentAvailabilityService` foi implementado com SRP
- Dependências injetáveis (DIP)
- Mock de `AppointmentRepository` não afeta o teste

---

## 📚 Referências

- **Clean Code**: Robert C. Martin (Uncle Bob)
- **Design Patterns**: Gang of Four
- **Spring Framework Documentation**
- **SOLID Principles**: Wikipedia

---

**Desenvolvido com foco em qualidade e manutenibilidade** 🚀
