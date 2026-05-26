# 🚀 GUIA RÁPIDO - Sistema Petshop

## ⚡ Quick Start (5 Minutos)

### 1. Compilar o Projeto
```bash
cd c:\Users\Geisa\Desktop\PETSHOP\Petshop-master
mvn clean install
```

### 2. Executar a Aplicação
```bash
mvn spring-boot:run
```

A aplicação iniciará em: **http://localhost:8080/api**

### 3. Dados Iniciais (Automáticos)
Ao iniciar, o sistema cria:

**Funcionários:**
- `banho_user` / `senha123` - Setor Banho
- `tosa_user` / `senha123` - Setor Tosa  
- `vet_user` / `senha123` - Veterinário
- `admin` / `admin123` - Administrador

**Clientes + Animais:**
- Ana Silva (CPF: 12345678901) com Rex (Cão)
- Bruno Costa (CPF: 98765432100) com Mimi (Gato)

---

## 📁 Estrutura de Arquivos

```
PETSHOP/
├── pom.xml                          # Maven - Dependências e build
├── README.md                        # Documentação principal
├── ARQUITETURA_SOLID.md             # Padrões e princípios aplicados
├── GUIA_RAPIDO.md                   # Este arquivo
├── Postman_Collection.json          # Testes de API (importar no Postman)
├── application.yml                  # Configurações da aplicação
├── PetshopApplication.java          # Classe principal Spring Boot
│
├── src/main/java/com/petshop/
│   ├── domain/
│   │   ├── entity/
│   │   │   ├── Tutor.java           # Entidade tutor
│   │   │   ├── Animal.java          # Entidade animal
│   │   │   ├── Appointment.java     # Entidade agendamento
│   │   │   ├── ServiceRecord.java   # Histórico de serviços
│   │   │   └── Employee.java        # Entidade funcionário
│   │   ├── enum/
│   │   │   ├── Species.java         # Espécies de animais
│   │   │   ├── ServiceType.java     # Tipos de serviços
│   │   │   ├── PaymentMethod.java   # Formas de pagamento
│   │   │   └── AppointmentStatus.java # Status agendamento
│   │   └── dto/
│   │       ├── TutorDTO.java
│   │       ├── AnimalDTO.java
│   │       ├── AppointmentRequestDTO.java
│   │       ├── AppointmentResponseDTO.java
│   │       ├── PaymentDTO.java
│   │       └── CancellationRequestDTO.java
│   │
│   ├── repository/
│   │   ├── TutorRepository.java
│   │   ├── AnimalRepository.java
│   │   ├── AppointmentRepository.java
│   │   ├── ServiceRecordRepository.java
│   │   └── EmployeeRepository.java
│   │
│   ├── service/
│   │   ├── TutorService.java
│   │   ├── AnimalService.java
│   │   ├── AppointmentService.java
│   │   ├── AppointmentAvailabilityService.java
│   │   ├── PaymentService.java
│   │   └── ServiceRecordService.java
│   │
│   ├── controller/
│   │   ├── TutorPublicController.java          # API Pública
│   │   ├── AnimalPublicController.java
│   │   ├── AppointmentPublicController.java
│   │   ├── PaymentPublicController.java
│   │   ├── AppointmentEmployeeController.java  # API Funcionários
│   │   └── AdminEmployeeController.java
│   │
│   ├── exception/
│   │   ├── EntityNotFoundException.java
│   │   ├── InvalidAppointmentException.java
│   │   ├── CancellationNotAllowedException.java
│   │   ├── ErrorResponse.java
│   │   └── GlobalExceptionHandler.java
│   │
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   └── DataLoader.java
│   │
│   └── PetshopApplication.java      # Classe main
│
└── .gitignore                       # Arquivos ignorados pelo git
```

---

## 🧪 Testando a API

### Opção 1: Postman (Recomendado)
1. Abra o **Postman**
2. Clique em **Import**
3. Selecione o arquivo `Postman_Collection.json`
4. Todos os endpoints ficarão disponíveis

### Opção 2: cURL (Terminal)

**Registrar Tutor:**
```bash
curl -X POST http://localhost:8080/api/public/tutors/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Maria Santos",
    "cpf": "55566677788",
    "phone": "11987654321",
    "email": "maria@email.com"
  }'
```

**Listar Agendamentos (com autenticação):**
```bash
curl -X GET http://localhost:8080/api/employee/appointments/today/department/BANHO \
  -u banho_user:senha123
```

### Opção 3: VS Code REST Client

Crie arquivo `test.http`:
```http
### Registrar Tutor
POST http://localhost:8080/api/public/tutors/register
Content-Type: application/json

{
  "name": "Lucas Oliveira",
  "cpf": "99988877766",
  "phone": "11912345678",
  "email": "lucas@email.com"
}

### Listar Tutores (Admin)
GET http://localhost:8080/api/employee/admin/tutors
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

---

## 🔍 Explorando o H2 Database

1. Acesse: **http://localhost:8080/api/h2-console**
2. Credenciais:
   - URL: `jdbc:h2:mem:petshopdb`
   - User: `sa`
   - Password: (deixe em branco)

Consultas úteis:
```sql
-- Ver todos os tutores
SELECT * FROM tutors;

-- Ver todos os agendamentos
SELECT * FROM appointments;

-- Ver histórico de serviços
SELECT * FROM service_records;

-- Ver funcionários
SELECT * FROM employees;

-- Agendamentos do dia
SELECT * FROM appointments 
WHERE DATE(scheduled_at) = CURRENT_DATE 
AND status IN ('SCHEDULED', 'CONFIRMED');
```

---

## 🛠️ Customizações Comuns

### Mudar Porta
Edite `application.yml`:
```yaml
server:
  port: 8888
```

### Mudar Intervalo Mínimo de Agendamentos
Edite `AppointmentAvailabilityService.java`:
```java
private static final int APPOINTMENT_INTERVAL_HOURS = 3;  // Mudar de 2 para 3
```

### Mudar Taxa de Cancelamento
Edite `AppointmentService.java`:
```java
private static final BigDecimal CANCELLATION_FEE_PERCENT = new BigDecimal("75.00");  // Mudar de 50 para 75%
```

### Mudar Limiar VIP
Edite `PaymentService.java`:
```java
BigDecimal vipThreshold = new BigDecimal("2000.00");  // Mudar de 1000 para 2000
```

---

## 📊 Fluxo Básico de Uso

```
1. CLIENTE registra (tutor)
   ↓
2. CLIENTE cadastra animal
   ↓
3. CLIENTE visualiza serviços disponíveis
   ↓
4. CLIENTE verifica horários disponíveis
   ↓
5. CLIENTE agenda serviço
   ↓
6. FUNCIONÁRIO visualiza agendamento (dashboard)
   ↓
7. FUNCIONÁRIO inicia atendimento
   ↓
8. FUNCIONÁRIO conclui atendimento
   ↓
9. CLIENTE processa pagamento
   ↓
10. HISTÓRICO registrado para analytics
```

---

## 🐛 Troubleshooting

### Erro: "Porta 8080 já em uso"
```bash
# Matar processo na porta 8080
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Erro: "Maven não encontrado"
```bash
# Usar Maven Wrapper
./mvnw clean install
```

### Banco de Dados não carregando dados iniciais
- Verifique se `DataLoader.java` está no classpath
- Confirme que `@SpringBootApplication` está na classe principal

---

## ✨ Funcionalidades Principais

### ✅ Implementadas
- [x] CRUD de tutores e animais
- [x] Agendamento com validação de disponibilidade (2h intervalo)
- [x] Cancelamento com taxa de 50% (< 2h)
- [x] Dashboard por departamento/setor
- [x] Processamento de pagamentos
- [x] Histórico de serviços
- [x] Identificação de clientes VIP
- [x] Autenticação básica para funcionários
- [x] Tratamento global de erros
- [x] Validações com Bean Validation

### 🎯 Sugestões de Melhorias Futuras
- [ ] JWT em vez de Basic Auth
- [ ] Notificações por email/SMS
- [ ] Dashboard web com gráficos
- [ ] Integração com gateway de pagamento real
- [ ] Sistema de avaliação e reviews
- [ ] Agendamento recorrente
- [ ] Backup automático do banco
- [ ] Relatórios em PDF
- [ ] Mobile app para clientes

---

## 📚 Documentação Relacionada

- `README.md` - Documentação completa
- `ARQUITETURA_SOLID.md` - Detalhes de arquitetura e padrões
- `pom.xml` - Dependências do projeto

---

## 🤝 Contribuindo

Para melhorias:
1. Crie uma branch: `git checkout -b feature/melhoria`
2. Commit: `git commit -am 'Add melhoria'`
3. Push: `git push origin feature/melhoria`
4. Abra um Pull Request

---

## 📞 Suporte

Dúvidas? Verifique:
1. Logs da aplicação (console)
2. H2 Console para inspecionar dados
3. Documentação SOLID
4. Postman Collection para exemplos

---

**Divirta-se desenvolvendo! 🚀** 🐾
