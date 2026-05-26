# 🐾 Sistema de Agendamento Petshop - Spring Boot

Um sistema completo de gerenciamento de agendamentos para petshop, desenvolvido com Spring Boot 3, seguindo os padrões SOLID, Clean Code e melhores práticas.

## 📋 Índice

- [Características](#características)
- [Tecnologias](#tecnologias)
- [Arquitetura](#arquitetura)
- [Como Instalar](#como-instalar)
- [Como Usar](#como-usar)
- [API Endpoints](#api-endpoints)
- [Autenticação](#autenticação)
- [Regras de Negócio](#regras-de-negócio)

---

## ✨ Características

### Para Clientes (Acesso Público)
- ✅ Registrar tutor e cadastrar animais
- ✅ Agendar serviços (individuais ou combinados)
- ✅ Visualizar agendamentos
- ✅ Cancelar agendamentos com taxa de 50% (dentro de 2h do horário marcado)
- ✅ Processar pagamentos (PIX, Cartão, Dinheiro, Débito)
- ✅ Visualizar histórico de serviços
- ✅ Consultar status VIP (clientes com mais de R$1000 gastos)

### Para Funcionários (Acesso Autenticado)
- ✅ Dashboard por setor (Banho, Tosa, Veterinário)
- ✅ Visualizar agendamentos do dia por departamento
- ✅ Gerenciar status de agendamentos (iniciar, concluir)
- ✅ Gerenciar tutores e animais (CRUD)
- ✅ Relatórios de clientes VIP

### Gestão de Disponibilidade
- ✅ Intervalos obrigatórios de 2 horas entre agendamentos
- ✅ Bloqueio de horários: 20h-08h (fora de funcionamento)
- ✅ Intervalo de almoço: 12h-13h (bloqueado)
- ✅ Múltiplos clientes podem agendar diferentes serviços no mesmo horário

---

## 🔧 Tecnologias

- **Framework**: Spring Boot 3.2.0
- **Banco de Dados**: H2 (em desenvolvimento), PostgreSQL/MySQL (produção)
- **JPA/Hibernate**: Acesso a dados
- **Spring Security**: Autenticação e autorização
- **Validation**: Bean Validation (Jakarta)
- **Build**: Maven
- **Lombok**: Redução de boilerplate
- **Java**: 17+

---

## 🏗️ Arquitetura

### Estrutura de Pacotes

```
com.petshop
├── config/                    # Configurações (Security, Data Loader)
├── domain/
│   ├── entity/               # Entidades JPA
│   ├── enum/                 # Enums (Species, ServiceType, PaymentMethod, AppointmentStatus)
│   └── dto/                  # Data Transfer Objects
├── repository/               # Spring Data Repositories
├── service/                  # Lógica de negócio
├── controller/               # REST Controllers
├── exception/                # Exceções personalizadas e Global Exception Handler
└── PetshopApplication.java   # Classe principal
```

### Princípios SOLID Aplicados

1. **Single Responsibility**: Cada classe tem uma única responsabilidade
   - `TutorService`: apenas gerencia tutores
   - `AnimalService`: apenas gerencia animais
   - `AppointmentAvailabilityService`: apenas valida disponibilidade

2. **Open/Closed**: Extensível sem modificar código existente
   - Enums para serviços e status
   - Interfaces de Repository do Spring Data

3. **Liskov Substitution**: Contrato consistente entre serviços
   - Todas as entidades implementam padrão JPA

4. **Interface Segregation**: Interfaces focadas
   - `AppointmentRepository` com métodos específicos
   - `EmployeeRepository` com queries customizadas

5. **Dependency Inversion**: Injeção de dependências
   - `@RequiredArgsConstructor` com Lombok
   - Spring gerencia o ciclo de vida

---

## 🚀 Como Instalar

### Pré-requisitos
- Java 17 ou superior
- Maven 3.6+
- Git

### Passos

1. **Clone o repositório**
```bash
git clone https://github.com/seu-usuario/petshop-system.git
cd petshop-system
```

2. **Compile o projeto**
```bash
mvn clean install
```

3. **Execute a aplicação**
```bash
mvn spring-boot:run
```

A aplicação iniciará em `http://localhost:8080/api`

4. **Acesse o banco de dados H2** (desenvolvimento)
```
http://localhost:8080/api/h2-console
```

Credenciais H2:
- **URL**: `jdbc:h2:mem:petshopdb`
- **Usuário**: `sa`
- **Senha**: (deixe em branco)

---

## 📖 Como Usar

### Dados Iniciais

Ao iniciar, o sistema cria automaticamente:

**Funcionários (para autenticação):**
- `username: banho_user` / `password: senha123` - Setor Banho
- `username: tosa_user` / `password: senha123` - Setor Tosa
- `username: vet_user` / `password: senha123` - Veterinário
- `username: admin` / `password: admin123` - Administrador

**Tutores e Animais de Exemplo:**
- Tutor: Ana Silva (CPF: 12345678901)
- Animal: Rex (Cão)
- Tutor: Bruno Costa (CPF: 98765432100)
- Animal: Mimi (Gato)

---

## 🔌 API Endpoints

### 📱 Endpoints Públicos (sem autenticação)

#### Tutores
```
POST   /api/public/tutors/register              - Registrar novo tutor
GET    /api/public/tutors/{id}                  - Obter tutor por ID
GET    /api/public/tutors/search/cpf/{cpf}     - Buscar tutor por CPF
```

#### Animais
```
POST   /api/public/animals                      - Cadastrar novo animal
GET    /api/public/animals/{id}                 - Obter animal por ID
GET    /api/public/animals/tutor/{tutorId}     - Listar animais do tutor
```

#### Agendamentos
```
GET    /api/public/appointments/services        - Listar serviços disponíveis
POST   /api/public/appointments/schedule        - Agendar novo serviço
GET    /api/public/appointments/{id}            - Obter detalhes do agendamento
GET    /api/public/appointments/tutor/{tutorId} - Listar agendamentos do tutor
POST   /api/public/appointments/{id}/cancel     - Cancelar agendamento
GET    /api/public/appointments/available-slots - Listar horários disponíveis
```

#### Pagamentos
```
POST   /api/public/payments/process                          - Processar pagamento
GET    /api/public/payments/tutor/{tutorId}/total-spent      - Total gasto pelo tutor
GET    /api/public/payments/tutor/{tutorId}/history          - Histórico de pagamentos
```

---

### 🔐 Endpoints para Funcionários (autenticação: Basic Auth)

#### Agendamentos
```
GET    /api/employee/appointments/today/department/{department}           - Agendamentos do dia
GET    /api/employee/appointments/date/{date}/department/{department}     - Agendamentos por data
GET    /api/employee/appointments/departments                             - Departamentos disponíveis
PUT    /api/employee/appointments/{id}/start                              - Iniciar atendimento
PUT    /api/employee/appointments/{id}/complete                           - Concluir atendimento
```

#### Administração
```
GET    /api/employee/admin/tutors                      - Listar todos os tutores
GET    /api/employee/admin/tutors/{id}                 - Obter tutor
PUT    /api/employee/admin/tutors/{id}                 - Atualizar tutor
DELETE /api/employee/admin/tutors/{id}                 - Deletar tutor

GET    /api/employee/admin/animals                     - Listar todos os animais
GET    /api/employee/admin/animals/{id}                - Obter animal
GET    /api/employee/admin/tutors/{id}/animals         - Animais do tutor
PUT    /api/employee/admin/animals/{id}                - Atualizar animal
DELETE /api/employee/admin/animals/{id}                - Deletar animal
```

---

## 🔐 Autenticação

A aplicação usa **HTTP Basic Authentication**. Inclua o header:

```
Authorization: Basic dXNlcm5hbWU6cGFzc3dvcmQ=
```

Onde `dXNlcm5hbWU6cGFzc3dvcmQ=` é a codificação Base64 de `username:password`.

**Exemplo com cURL:**
```bash
curl -u banho_user:senha123 http://localhost:8080/api/employee/appointments/today/department/BANHO
```

---

## 📋 Regras de Negócio

### Horário de Funcionamento
- **Abertura**: 08:00
- **Fechamento**: 20:00
- **Intervalo de Almoço**: 12:00 - 13:00 (bloqueado)
- **Intervalo Mínimo entre Agendamentos**: 2 horas

### Cancelamento de Agendamentos
- **Até 2h antes**: ✅ Sem taxa
- **Menos de 2h antes**: ⚠️ Taxa de 50% será aplicada

### Status de Clientes VIP
- **Critério**: Total gasto ≥ R$1.000,00
- **Benefícios**: Elegível para promoções e descontos

### Serviços Disponíveis

| Código | Serviço | Preço | Categoria |
|--------|---------|-------|-----------|
| BATH | Banho | R$45,00 | Individual |
| GROOMING | Tosa | R$60,00 | Individual |
| VETERINARY | Consulta Veterinária | R$90,00 | Individual |
| BATH_GROOMING | Banho + Tosa | R$95,00 | Combo |
| BATH_VETERINARY | Banho + Consulta | R$125,00 | Combo |
| GROOMING_VETERINARY | Tosa + Consulta | R$140,00 | Combo |
| FULL_SERVICE | Banho + Tosa + Consulta | R$180,00 | Combo |

### Formas de Pagamento
- Dinheiro
- PIX
- Cartão de Crédito
- Cartão de Débito

---

## 📝 Exemplos de Requisições

### Registrar Tutor
```json
POST /api/public/tutors/register
Content-Type: application/json

{
  "name": "Pedro Silva",
  "cpf": "12345678900",
  "phone": "11987654321",
  "email": "pedro@email.com"
}
```

### Cadastrar Animal
```json
POST /api/public/animals
Content-Type: application/json

{
  "name": "Belinha",
  "species": "DOG",
  "breed": "Poodle",
  "age": 3,
  "weight": 8.5,
  "observations": "Gosta de brincar",
  "tutorId": 1
}
```

### Agendar Serviço
```json
POST /api/public/appointments/schedule
Content-Type: application/json

{
  "animalId": 1,
  "tutorId": 1,
  "scheduledDateTime": "2025-06-15T10:00:00",
  "serviceType": "BATH",
  "notes": "Animal hiperativo, cuidado"
}
```

### Processar Pagamento
```json
POST /api/public/payments/process
Content-Type: application/json

{
  "appointmentId": 1,
  "paymentMethod": "PIX",
  "amount": 45.00,
  "discount": 0,
  "notes": "Pagamento realizado"
}
```

### Cancelar Agendamento
```json
POST /api/public/appointments/1/cancel
Content-Type: application/json

{
  "reason": "Cliente mudou de horário"
}
```

---

## 🧪 Tratamento de Erros

A aplicação retorna erros estruturados com o seguinte formato:

```json
{
  "status": 400,
  "message": "Descrição do erro",
  "error": "Tipo de erro",
  "timestamp": "2025-06-10T10:30:00",
  "path": "/api/public/appointments/schedule"
}
```

### Códigos de Status Comum
- `200 OK`: Sucesso
- `201 CREATED`: Criado com sucesso
- `400 BAD REQUEST`: Validação falhou
- `403 FORBIDDEN`: Cancelamento dentro de 2h (taxa 50%)
- `404 NOT FOUND`: Recurso não encontrado
- `409 CONFLICT`: Horário indisponível
- `422 UNPROCESSABLE ENTITY`: Operação não permitida neste estado
- `500 INTERNAL SERVER ERROR`: Erro do servidor

---

## 📊 Modelo de Dados

### Entidades Principais

**Tutor**
```
- id (PK)
- name (NOT NULL)
- cpf (UNIQUE, NOT NULL)
- phone (NOT NULL)
- email (UNIQUE)
- createdAt / updatedAt
```

**Animal**
```
- id (PK)
- name (NOT NULL)
- species (ENUM: DOG, CAT, BIRD, RABBIT, HAMSTER, OTHER)
- breed (NOT NULL)
- age (NOT NULL)
- weight
- observations
- tutor_id (FK)
- createdAt / updatedAt
```

**Appointment**
```
- id (PK)
- animal_id (FK)
- tutor_id (FK)
- scheduled_at (NOT NULL, UNIQUE with animal_id)
- service_type (ENUM)
- status (ENUM: SCHEDULED, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED, NO_SHOW)
- price (NOT NULL)
- notes
- cancellation_fee_applied
- cancelled_at
- cancellation_reason
- createdAt / updatedAt
```

**ServiceRecord (Histórico)**
```
- id (PK)
- appointment_id (FK)
- animal_id (FK)
- tutor_id (FK)
- service_type (ENUM)
- payment_method (ENUM)
- amount_paid (NOT NULL)
- discount_applied
- cancellation_fee
- employee_id
- notes
- recorded_at (NOT NULL)
```

**Employee**
```
- id (PK)
- username (UNIQUE, NOT NULL)
- email (UNIQUE, NOT NULL)
- password (NOT NULL)
- fullName (NOT NULL)
- department (NOT NULL)
- enabled / accountNonLocked / credentialsNonExpired / accountNonExpired
```

---

## 🔄 Fluxo de Agendamento

1. **Cliente registra**: Cadastra tutor e animal
2. **Cliente agenda**: Seleciona data/hora (validação de disponibilidade)
3. **Sistema confirma**: Verifica intervalo de 2h e horário de funcionamento
4. **Agendamento criado**: Status = SCHEDULED
5. **Funcionário confirma**: Altera status para CONFIRMED
6. **Funcionário inicia**: Altera status para IN_PROGRESS
7. **Funcionário conclui**: Altera status para COMPLETED
8. **Cliente paga**: ProcessPayment cria ServiceRecord
9. **Histórico registrado**: Dados persistem para analytics

---

## 🚨 Tratamento de Cancelamento

```
Agendamento criado (SCHEDULED)
        ↓
Cliente solicita cancelamento
        ↓
    [2h antes? (Validar)]
        ↓
    ├─ SIM → Cancelamento permitido (sem taxa)
    │           ↓
    │     Status = CANCELLED
    │
    └─ NÃO → Cancelamento com taxa (50%)
                ↓
            CancellationNotAllowedException
                ↓
            Retorna status 403 com fee calculado
```

---

## 🎯 Próximos Passos para Produção

1. **Configurar banco PostgreSQL/MySQL**
   - Alterar `application.yml` com connection string
   - Mudar `ddl-auto` para `validate`

2. **Implementar JWT** (recomendado para produção)
   - Adicionar dependência: `spring-boot-starter-security`
   - Criar `JwtTokenProvider`
   - Atualizar `SecurityConfig`

3. **Adicionar Logging**
   - SLF4J + Logback configurado

4. **Testes Unitários e Integração**
   - Adicionar `spring-boot-starter-test`
   - Criar testes para serviços críticos

5. **Deploy**
   - Docker
   - CI/CD (GitHub Actions, GitLab CI)
   - Cloud (AWS, Azure, GCP)

---

## 📞 Suporte e Contato

Para dúvidas ou sugestões, abra uma issue no repositório.

---

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

---

**Desenvolvido com ❤️ para facilitar a gestão de petshops** 🐾
