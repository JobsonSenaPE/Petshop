# 🏗️ Diagrama de Arquitetura - Petshop System

## Arquitetura em Camadas

```
┌─────────────────────────────────────────────────────────────────┐
│                    CLIENTE (REST API)                           │
│  ├─ Navegador Web / Postman / Mobile App / cURL                │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│              CONTROLLER LAYER (REST Endpoints)                  │
│  ├─ TutorPublicController (POST, GET)                          │
│  ├─ AnimalPublicController (POST, GET)                         │
│  ├─ AppointmentPublicController (POST, GET, CANCEL)            │
│  ├─ PaymentPublicController (POST, GET)                        │
│  ├─ AppointmentEmployeeController (GET, PUT) [AUTH]           │
│  └─ AdminEmployeeController (CRUD) [AUTH]                     │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│               GLOBAL EXCEPTION HANDLER                          │
│  ├─ EntityNotFoundException                                     │
│  ├─ InvalidAppointmentException                                │
│  ├─ CancellationNotAllowedException                            │
│  └─ Validation Exception Handler                              │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│              SERVICE LAYER (Business Logic)                     │
│  ├─ TutorService (CRUD)                                        │
│  ├─ AnimalService (CRUD)                                       │
│  ├─ AppointmentService (Schedule, Cancel)                      │
│  ├─ AppointmentAvailabilityService (Validate Time)            │
│  ├─ PaymentService (Process Payment, VIP Status)              │
│  └─ ServiceRecordService (History)                            │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│             REPOSITORY LAYER (Data Access)                      │
│  ├─ TutorRepository (Spring Data JPA)                          │
│  ├─ AnimalRepository (Spring Data JPA)                         │
│  ├─ AppointmentRepository (Custom Queries)                     │
│  ├─ ServiceRecordRepository (Custom Queries)                   │
│  └─ EmployeeRepository (Custom Queries)                        │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│             DATABASE LAYER (H2 / PostgreSQL)                    │
│  ├─ tutors                                                      │
│  ├─ animals                                                     │
│  ├─ appointments                                                │
│  ├─ service_records                                             │
│  └─ employees                                                   │
└─────────────────────────────────────────────────────────────────┘
```

---

## Fluxo de Dados - Agendamento

```
┌─────────────┐
│   CLIENTE   │
└──────┬──────┘
       │ Acessa: POST /api/public/appointments/schedule
       ▼
┌──────────────────────────┐
│ AppointmentRequest DTO   │
│ - animalId               │
│ - tutorId                │
│ - scheduledDateTime      │
│ - serviceType            │
└──────┬───────────────────┘
       │ Validação automática (Bean Validation)
       ▼
┌──────────────────────────────┐
│ AppointmentPublicController  │
│ - validações de negócio      │
└──────┬───────────────────────┘
       │
       ▼
┌────────────────────────────────────┐
│ AppointmentService                 │
│ - getCalls: AnimalService          │
│ - getCalls: TutorService           │
│ - getCalls: AvailabilityService    │
└──────┬─────────────────────────────┘
       │
       ▼
┌──────────────────────────────┐
│ AppointmentAvailability      │
│ - validateBusinessHours()    │  ✅ 08:00-20:00?
│ - validateLunchBreak()       │  ✅ NOT 12:00-13:00?
│ - validateInterval()         │  ✅ 2h gap?
└──────┬───────────────────────┘
       │
       ▼
┌──────────────────────────────┐
│ AppointmentRepository        │
│ - save(appointment)          │
└──────┬───────────────────────┘
       │
       ▼
┌──────────────────────────────┐
│ H2 DATABASE                  │
│ INSERT INTO appointments ... │
└──────┬───────────────────────┘
       │
       ▼
┌──────────────────────────────┐
│ AppointmentResponse DTO      │
│ - id                         │
│ - status: SCHEDULED          │
│ - createdAt                  │
└──────┬───────────────────────┘
       │
       ▼
┌─────────────┐
│   CLIENTE   │ ← JSON 201 CREATED
└─────────────┘
```

---

## Fluxo de Dados - Cancelamento com Taxa

```
┌──────────────────┐
│ CLIENTE REQUEST  │
│ POST /api/public/appointments/1/cancel
│ { "reason": "..." }
└────────┬─────────┘
         │
         ▼
┌──────────────────────────────┐
│ AppointmentPublicController  │
└────────┬─────────────────────┘
         │
         ▼
┌──────────────────────────────┐
│ AppointmentService           │
│ - getAppointmentById(1)      │
│ - Check Status (SCHEDULED?)  │
└────────┬─────────────────────┘
         │
         ▼
┌────────────────────────────────┐
│ Calculate Minutes Until...     │
│ ← 2 horas = 120 minutos?       │
└────────┬───────────────────────┘
         │
    ┌────┴────┐
    │          │
    ▼          ▼
┌─────────┐  ┌──────────────────────────┐
│ SIM     │  │ NÃO (< 120 min)         │
│ (Sem    │  │ → CancelationNotAllowed │
│ Taxa)   │  │ → THROW Exception       │
└────┬────┘  │ → Fee = 50%              │
     │       └────┬───────────────────┘
     │            │
     ▼            ▼
 UPDATE      ┌─────────────────┐
 Status =    │ HTTP 403        │
 CANCELLED   │ Forbidden       │
             │ + Fee info      │
             └─────────────────┘
```

---

## Fluxo de Autenticação

```
┌──────────────────┐
│ FUNCIONÁRIO      │
│ Username: banho_user
│ Password: senha123
└────────┬─────────┘
         │
         ▼
┌──────────────────────────┐
│ HTTP Basic Auth          │
│ Authorization: Basic ... │
└────────┬─────────────────┘
         │
         ▼
┌──────────────────────────┐
│ SecurityConfig           │
│ - OncePerRequestFilter   │
│ - Decode Base64          │
└────────┬─────────────────┘
         │
         ▼
┌──────────────────────────┐
│ EmployeeRepository       │
│ - findByUsername()       │
└────────┬─────────────────┘
         │
         ▼
┌──────────────────────────┐
│ BCrypt Password Check    │
│ matches(input, stored)?  │
└────────┬─────────────────┘
    ┌────┴────┐
    │          │
    ▼          ▼
  ✅           ❌
  Login      Reject
  Success    401 Unauthorized
  │
  ▼
┌──────────────────────────┐
│ RoleCheck: ROLE_EMPLOYEE │
└────────┬─────────────────┘
    ┌────┴────┐
    │          │
    ▼          ▼
  ✅           ❌
  Granted     403
  Access      Forbidden
```

---

## Relação de Entidades

```
                    ┌──────────────┐
                    │   Tutor      │
                    │   (PK: id)   │
                    └──────┬───────┘
                           │
         ┌─────────────────┼──────────────────┐
         │                 │                  │
         ▼                 ▼                  ▼
    ┌────────────┐  ┌─────────────┐  ┌──────────────┐
    │  Animal    │  │ Appointment │  │ ServiceRecord│
    │ (FK: tutor)│  │(FK: tutor)  │  │(FK: tutor)   │
    └────────────┘  │(FK: animal) │  │(FK: animal)  │
         │          └─────────────┘  │(FK: appt)    │
         │                 │          └──────────────┘
         ▼                 ▼                 │
    ┌────────────┐  ┌─────────────┐         │
    │   N:N      │  │  Appointment│◄────────┘
    │ Service    │  │   Status    │
    │  Records   │  │  (ENUM)     │
    └────────────┘  └─────────────┘

Employee (Separado)
┌────────────┐
│  Employee  │
│(Autenticação)
└────────────┘
```

---

## Fluxo de Validação de Disponibilidade

```
REQUEST: Agendar em 2025-06-15 10:00:00

    │
    ▼
┌─────────────────────────────┐
│ validateBusinessHours()     │
│ hour = 10 (within 8-20)     │  ✅ PASS
└────────────┬────────────────┘
             │
             ▼
┌─────────────────────────────┐
│ validateLunchBreak()        │
│ hour = 10 (NOT 12-13)       │  ✅ PASS
└────────────┬────────────────┘
             │
             ▼
┌──────────────────────────────────────┐
│ validateIntervalBetweenAppointments()│
│ Buscar: 10:00 ± 2 horas (08:00-12:00│
│ Encontrar conflitos                  │
└────────────┬───────────────────────────┘
             │
    ┌────────┴────────┐
    │                 │
    ▼                 ▼
 ✅ NENHUM        ❌ ENCONTRADO
 Agendamento    Lançar exceção
 válido         "Já existe 
 Seguir adiante  agendamento"
```

---

## Estrutura de Packages (Visual)

```
com.petshop
├── 📦 config
│   ├── SecurityConfig.java
│   └── DataLoader.java
│
├── 📦 domain
│   ├── 📂 entity
│   │   ├── Tutor.java
│   │   ├── Animal.java
│   │   ├── Appointment.java
│   │   ├── ServiceRecord.java
│   │   └── Employee.java
│   ├── 📂 enum
│   │   ├── Species.java
│   │   ├── ServiceType.java
│   │   ├── PaymentMethod.java
│   │   └── AppointmentStatus.java
│   └── 📂 dto
│       ├── TutorDTO.java
│       ├── AnimalDTO.java
│       ├── AppointmentRequestDTO.java
│       ├── AppointmentResponseDTO.java
│       ├── PaymentDTO.java
│       └── CancellationRequestDTO.java
│
├── 📦 repository
│   ├── TutorRepository.java
│   ├── AnimalRepository.java
│   ├── AppointmentRepository.java
│   ├── ServiceRecordRepository.java
│   └── EmployeeRepository.java
│
├── 📦 service
│   ├── TutorService.java
│   ├── AnimalService.java
│   ├── AppointmentService.java
│   ├── AppointmentAvailabilityService.java
│   ├── PaymentService.java
│   └── ServiceRecordService.java
│
├── 📦 controller
│   ├── TutorPublicController.java
│   ├── AnimalPublicController.java
│   ├── AppointmentPublicController.java
│   ├── PaymentPublicController.java
│   ├── AppointmentEmployeeController.java
│   └── AdminEmployeeController.java
│
├── 📦 exception
│   ├── EntityNotFoundException.java
│   ├── InvalidAppointmentException.java
│   ├── CancellationNotAllowedException.java
│   ├── ErrorResponse.java
│   └── GlobalExceptionHandler.java
│
└── PetshopApplication.java
```

---

## Fluxo de Pagamento

```
┌──────────────────┐
│  CLIENTE PAGA    │
│  Agendamento: 1  │
│  Valor: R$45     │
└────────┬─────────┘
         │
         ▼
┌──────────────────────────┐
│ PaymentPublicController  │
│ - paymentMethod: PIX     │
│ - amount: 45.00          │
│ - discount: 0            │
└────────┬─────────────────┘
         │
         ▼
┌──────────────────────────┐
│ PaymentService           │
│ - calculateFee()         │
│ - checkCancellation()    │
└────────┬─────────────────┘
         │
    ┌────┴─────┐
    │           │
    ▼           ▼
┌────────┐  ┌────────────────┐
│Normal  │  │Cancelamento    │
│ R$45   │  │Dentro 2h:      │
│        │  │Fee = R$22.50   │
└───┬────┘  │(50%)           │
    │       └────┬───────────┘
    │            │
    ▼            ▼
┌────────────────────────────┐
│ ServiceRecordRepository    │
│ - save(record)             │
│ - amount_paid              │
│ - cancellation_fee         │
│ - payment_method           │
└────────┬───────────────────┘
         │
         ▼
┌────────────────────────────┐
│ Database (H2)              │
│ INSERT service_records     │
└────────┬───────────────────┘
         │
         ▼
┌────────────────────────────┐
│ PaymentResponse            │
│ - id: 1                    │
│ - status: COMPLETED        │
│ - timestamp: 2025-06-10... │
└──────────┬─────────────────┘
           │
           ▼
      ✅ 201 CREATED
```

---

## Fluxo de Status VIP

```
┌─────────────────────┐
│ Cliente (Tutor ID 1)│
└──────────┬──────────┘
           │
           ▼
┌──────────────────────────────┐
│ PaymentService               │
│ - calculateTotalSpentByTutor │
│   (tutorId: 1)              │
└──────────┬───────────────────┘
           │
           ▼
┌──────────────────────────────┐
│ ServiceRecordRepository      │
│ - findByTutorId(1)          │
│ - Soma todos os payments    │
│   SELECT SUM(amount_paid)   │
└──────────┬───────────────────┘
           │
           ▼
┌──────────────────────────────┐
│ Total Gasto: R$1.250        │
└──────────┬───────────────────┘
           │
    ┌──────┴───────┐
    │              │
    ▼              ▼
┌─────────┐    ┌──────────┐
│ >= 1000 │    │ < 1000   │
│   VIP! │    │ Regular  │
│   ✅   │    │   ❌     │
└─────────┘    └──────────┘
```

---

## Segurança de Dados

```
CLIENT REQUEST
    │
    ▼
HTTPS (em produção)
    │
    ▼
BasicAuth Header
    │
    ▼
SecurityFilterChain
    │
    ▼
UserDetailsService
    │
    ▼
BCrypt Password Compare
    │
    ▼
RoleCheck (ROLE_EMPLOYEE)
    │
    ▼
Authorization Filter
    │
    ▼
Controller
    │
    ▼
Service (Validações)
    │
    ▼
Repository (JPA)
    │
    ▼
Prepared Statement (SQL Injection prevention)
    │
    ▼
Database (H2/PostgreSQL)
```

---

Esta arquitetura segue as melhores práticas e é escalável! 🚀
