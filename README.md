# Arquitetura Hexagonal (Ports & Adapters)

Este projeto demonstra a refatoração de um serviço simples de e-commerce (iniciado em MVC) para o modelo de Arquitetura Hexagonal (Ports and Adapters), protegendo as regras de negócio no núcleo do sistema (Domínio) e isolando integrações externas por meio de adaptadores.

A atividade a seguir deve ser realizada pelos alunos a partir desta base. Leia todo o README antes de começar.


## Visão Geral da Arquitetura Hexagonal

A Arquitetura Hexagonal separa o coração do sistema (Domínio) das preocupações externas (UI/REST, Banco de Dados, Mensageria, etc.) por meio de portas (ports) e adaptadores (adapters).

- Domínio (Core):
  - Onde vivem as Regras de Negócio e o Modelo de Domínio Rico (sem anotações de frameworks).
  - Não conhece detalhes de frameworks, HTTP, JPA, etc.
- Camada de Aplicação (Application):
  - Orquestra casos de uso. Expõe "Portos de Entrada" (interfaces dirigidas pelo caso de uso) e depende de "Portos de Saída" (contratos de serviços externos necessários).
  - Depende do Domínio (para instanciar/usar BOs) e de Portos (interfaces). Não depende de adapters concretos.
- Infraestrutura (Infrastructure):
  - Adaptadores de Entrada (ex.: REST Controller) convertem o mundo externo em chamadas de caso de uso.
  - Adaptadores de Saída (ex.: JPA Repository) implementam os Portos de Saída com tecnologias específicas.

Fluxo típico (entrada → domínio → saída):
1. Request chega ao Adapter de Entrada (ex.: `Controller`).
2. DTO de entrada é mapeado para um `BO` (Business Object) do Domínio.
3. A `Service` da camada de Aplicação orquestra o caso de uso, valida pelo Domínio e chama uma Porta de Saída.
4. O Adapter de Saída converte o `BO` em `Entity` e persiste via tecnologia (ex.: Spring Data JPA).
5. A resposta pode ser mapeada para um DTO de saída.


## Estrutura do Projeto (packages principais)

Base do pacote: `com.fag.lucasmartins.arquitetura_software`

- core
  - domain
    - bo
      - `ProdutoBO.java` — Modelo de Domínio Rico do produto (ex.: validações de regra de negócio do produto)
    - exceptions
      - `DomainException.java` — Exceção de domínio para violação de regras
- application
  - ports
    - in
      - service
        - `ProdutoServicePort.java` — Porto de Entrada (contrato do caso de uso para produto)
    - out
      - persistence
        - h2
          - `ProdutoRepositoryPort.java` — Porto de Saída (contrato de persistência)
  - services
    - `ProdutoService.java` — Orquestra o caso de uso de produto. Depende de `ProdutoRepositoryPort` e do Domínio
- infrastructure
  - adapters
    - in
      - rest
        - controller
          - `ProdutoControllerAdapter.java` — Controller REST (Adapter de Entrada)
        - dto
          - `ProdutoDTO.java` — DTO de entrada/saída
        - mapper
          - `ProdutoDTOMapper.java` — Mapeamento entre DTO e BO
    - out
      - persistence
        - h2
          - entity
            - `ProdutoEntity.java` — Entidade JPA (mapeamento para a tabela)
          - jpa
            - `ProdutoJpaRepository.java` — Interface Spring Data
          - mapper
            - `ProdutoMapper.java` — Mapeamento entre BO e Entity
          - repository
            - `ProdutoRepositoryPortAdapter.java` — Implementação do Porto de Saída

Aplicação Spring Boot: `ArquiteturaHexagonalApplication.java`


## Por que Hexagonal aqui?

- Protege a Regra de Negócio: o Domínio não é poluído com anotações ou lógicas técnicas.
- Facilita testes: Portos permitem mocks/fakes claros para simular infraestrutura.
- Evolução segura: adicionar mensageria, novos bancos, ou novas interfaces (ex.: CLI) sem tocar nas regras de negócio.


## Atividade da Sprint — Módulo Pessoa (Cliente)

Como vimos em aula, um e-commerce não vive apenas de produtos. Precisamos conhecer quem compra. O terreno já está pronto (código refatorado para Arquitetura Hexagonal). Agora é a sua vez: implemente o módulo de Pessoa (Cliente), mantendo o núcleo de negócio protegido no Domínio.

### História
Implementar a funcionalidade de Cadastro de Pessoa dentro da estrutura hexagonal já existente, focando na proteção da regra de negócio dentro do Coração do Sistema (Domínio).

### Atributos obrigatórios de Pessoa
- `id` (UUID)
- `nomeCompleto` (String)
- `cpf` (String; exatamente 11 caracteres)
- `dataNascimento` (LocalDate)
- `email` (String; deve conter "@")
- `telefone` (String; exatamente 11 caracteres; sem parênteses ou traços)

### Regras de Negócio (no Domínio)
- Maioridade: idade >= 18 anos.
- CPF: obrigatório e com tamanho exatamente 11.
- Telefone: exatamente 11 caracteres (apenas dígitos).
- E-mail: obrigatório e com formato básico (conter "@").

Estas validações devem estar encapsuladas na entidade de domínio (Modelo Rico) e lançar `DomainException` (ou exceção de domínio equivalente) quando houver violação.


## O que você deve criar (respeitando os pacotes)

1) Camada de Domínio (`core.domain`)
- `PessoaBO` (Modelo de Domínio Rico)
  - Construtor/factory com validações de maioridade, CPF, telefone e e-mail.
  - Métodos/funções de negócio conforme necessário (ex.: normalizar telefone).
  - Sem anotações de Spring/JPA. Sem DTOs.

2) Camada de Aplicação (`application`)
- Portos de Entrada
  - `PessoaServicePort` (ex.: `application.ports.in.service`) — descreve o caso de uso de cadastro/busca etc.
- Portos de Saída
  - `PessoaRepositoryPort` (ex.: `application.ports.out.persistence`) — contrato de persistência de Pessoa via BO.
- Serviço de Aplicação
  - `PessoaService` (ex.: `application.services`) — orquestra o fluxo: recebe dados, instancia/valida `PessoaBO`, chama `PessoaRepositoryPort`.

3) Camada de Infraestrutura (`infrastructure`)
- Adaptador de Entrada (REST)
  - `PessoaControllerAdapter` — recebe requisições HTTP.
  - DTOs de entrada/saída (ex.: `PessoaRequestDTO`, `PessoaResponseDTO`).
  - Mapper (ex.: `PessoaDTOMapper`) — mapeia DTO ↔ BO.
- Adaptador de Saída (Persistência)
  - `PessoaEntity` (em `...out.persistence.<db>.entity`) — anotações JPA (`@Entity`, `@Table`, etc.).
  - `PessoaJpaRepository` — interface Spring Data.
  - `PessoaMapper` — mapeia BO ↔ Entity.
  - `PessoaRepositoryPortAdapter` — implementa `PessoaRepositoryPort` com Spring Data.

Observação: siga o padrão já existente para `Produto` como referência de nomes e pacotes.


## Contratos e Fluxo de Dados (guia prático)

- Controller (Adapter IN) recebe `PessoaRequestDTO` → mapeia para `PessoaBO`.
- `PessoaService` recebe `PessoaBO` → chama validações (no próprio BO) → aciona `PessoaRepositoryPort.save(bo)`.
- Adapter OUT converte `PessoaBO` → `PessoaEntity` → persiste com `PessoaJpaRepository` → retorna `PessoaBO` ou ID.
- Controller mapeia `PessoaBO` → `PessoaResponseDTO` e retorna.

Erros de validação do Domínio devem resultar em HTTP 400 (Bad Request) com uma mensagem clara. Utilize `DomainException` conforme o padrão do projeto.


## Endpoints sugeridos (exemplo)

```
POST   /api/v1/pessoas
GET    /api/v1/pessoas/{id}
GET    /api/v1/pessoas
```

Exemplo de payload (POST `/api/v1/pessoas`):

```json
{ 
  "nomeCompleto": "Maria da Silva",
  "cpf": "12345678901",
  "dataNascimento": "1990-05-20",
  "email": "maria.silva@example.com",
  "telefone": "41988887777"
}
```

Respostas de erro (exemplos):
- 400 — "Idade mínima de 18 anos não atendida"
- 400 — "CPF deve conter 11 dígitos"
- 400 — "E-mail inválido"
- 400 — "Telefone deve conter 11 dígitos"


## Critérios de Avaliação

1) Modelo Rico vs Anêmico
- A entidade `PessoaBO` encapsula as regras (idade >= 18, CPF=11, e-mail com "@", telefone=11)?
- Há vazamento de regra de negócio para Controller/Service/Adapter?

2) Inversão de Dependência (DIP)
- `PessoaService` depende de `PessoaRepositoryPort` (interface), e não diretamente do Spring Data?
- A injeção respeita os Portos (sem acoplar service a adapter concreto)?

3) Fronteiras e Mapeamento de Dados
- Controller trabalha com DTO e converte para BO antes de chamar o Service?
- Service lida apenas com BO (não com DTO/Entity)?
- Adapter de repositório converte BO ↔ Entity corretamente?

4) Evidências de Validação
- Foi anexado print do Postman/Insomnia com cadastro válido (sucesso)?
- Foi anexado print mostrando bloqueio quando uma regra é violada (erro)?


## Entrega (passo a passo)

1. Crie um fork deste repositório no seu GitHub.
2. Crie uma branch: `feature/hexagonal`.
3. Implemente o módulo Pessoa conforme descrito (Domínio, Aplicação, Infraestrutura).
4. Faça commits e pushes normalmente para a branch `feature/hexagonal`.
5. Ao concluir, abra um Pull Request da sua `feature/hexagonal` para a sua `master` (no seu fork).
6. Envie o link do PR aberto no Classroom.
7. No Classroom, anexe pelo menos 2 prints do Postman/Insomnia: um de sucesso e um de erro de validação.


## Como executar o projeto localmente

Pré-requisitos:
- Java 17+
- Maven 3.8+

Passos:

> **Importante:** o `pom.xml` fica dentro da pasta `t03n-arquitetura-hexagonal-master`.  
> Se você executar Maven na raiz do repositório (`TrabalhoTer-a`), aparecerá o erro: *"there is no POM in this directory"*.

```bash
# Opção 1 (entrando na pasta do projeto)
cd t03n-arquitetura-hexagonal-master
./mvnw spring-boot:run

# Windows (PowerShell/CMD)
cd t03n-arquitetura-hexagonal-master
.\mvnw.cmd spring-boot:run

# Opção 2 (sem trocar de pasta)
mvn -f t03n-arquitetura-hexagonal-master/pom.xml spring-boot:run
```

Atalhos na raiz do repositório:

```bash
# Linux/macOS
./run-app.sh

# Windows
.\run-app.cmd
```

Configurações:
- Arquivo `src/main/resources/application.properties` já contém configurações básicas (ex.: H2). Ajuste conforme necessário.

A API atual de Produto (exemplo) segue o mesmo padrão e pode servir de referência para o módulo Pessoa.


## Boas práticas recomendadas

- Escreva testes unitários para o Domínio (validando as regras de negócio sem infraestrutura).
- Mantenha construtores/factories do Domínio coesos e com invariantes fortes.
- Evite anotações de framework no pacote `core.domain`.
- Mantenha os mappers simples e determinísticos.
- Nomes claros de Portos e Adapters ajudam os revisores.


## Referências rápidas

- Ports & Adapters (Hexagonal Architecture) — Alistair Cockburn
- Domain-Driven Design (DDD) — Eric Evans

---

Bons estudos e mãos à obra! O importante é proteger o Coração do Sistema e deixar que os detalhes técnicos orbitem ao redor, como verdadeiros adaptadores.

## Sprint Atual — Consumo assíncrono de Pedido via SQS (SNS -> SQS FIFO)

Foi adicionado um **adapter de entrada de mensageria** para criação de pedidos: `SqsPedidoAdapter`.

### Fluxo implementado
<<<<<<< ours
<<<<<<< ours
<<<<<<< ours
<<<<<<< ours
<<<<<<< ours
1. O `SqsPedidoAdapter` escuta a fila configurada em `aws.sqs.queue.pedidos`.
=======
1. O `SqsPedidoAdapter` escuta a fila configurada em `queue.order-events`.
>>>>>>> theirs
=======
1. O `SqsPedidoAdapter` escuta a fila configurada em `queue.order-events`.
>>>>>>> theirs
=======
1. O `SqsPedidoAdapter` escuta a fila configurada em `queue.order-events`.
>>>>>>> theirs
=======
1. O `SqsPedidoAdapter` escuta a fila configurada em `queue.order-events`.
>>>>>>> theirs
=======
1. O `SqsPedidoAdapter` escuta a fila configurada em `queue.order-events`.
>>>>>>> theirs
2. O JSON do evento é desserializado para `PedidoEventDTO`.
3. O mapper `PedidoEventDTOMapper` traduz o contrato externo para o `PedidoBO` da aplicação:
   - `zipCode` -> `cep`
   - `customerId` -> `pessoa.id`
   - `orderItems[].sku` -> `itens[].produto.id`
   - `orderItems[].amount` -> `itens[].quantidade`
4. O adapter chama `PedidoServicePort.criarPedido(...)` reutilizando o caso de uso já existente.

### Variáveis de ambiente obrigatórias
Para deixar o projeto pronto para rodar (faltando apenas suas chaves), configure na IDE/terminal:

```bash
<<<<<<< ours
<<<<<<< ours
<<<<<<< ours
<<<<<<< ours
<<<<<<< ours
AWS_REGION=us-east-1
AWS_ACCESS_KEY_ID=<SUA_ACCESS_KEY>
AWS_SECRET_ACCESS_KEY=<SUA_SECRET_KEY>
AWS_SQS_PEDIDOS_QUEUE=<T0XN_NOME_COMPLETO.fifo>
<<<<<<< ours
<<<<<<< ours
=======
APP_SQS_PEDIDO_ENABLED=true
>>>>>>> theirs
=======
APP_SQS_PEDIDO_ENABLED=true
>>>>>>> theirs
=======
=======
>>>>>>> theirs
=======
>>>>>>> theirs
=======
>>>>>>> theirs
=======
>>>>>>> theirs
AWS_ACCESS_KEY_ID=<SUA_ACCESS_KEY>
AWS_SECRET_ACCESS_KEY=<SUA_SECRET_KEY>
QUEUE_ORDER_EVENTS=<T0XN_NOME_COMPLETO.fifo>
APP_SQS_PEDIDO_ENABLED=true
<<<<<<< ours
<<<<<<< ours
<<<<<<< ours
<<<<<<< ours
>>>>>>> theirs
=======
>>>>>>> theirs
=======
>>>>>>> theirs
=======
>>>>>>> theirs
=======
>>>>>>> theirs
```

> Observação: a fila deve estar inscrita no tópico SNS do professor com **Raw Message Delivery** ativado.

<<<<<<< ours
<<<<<<< ours
<<<<<<< ours
<<<<<<< ours
<<<<<<< ours
<<<<<<< ours
<<<<<<< ours
=======
> Por padrão, o consumer SQS inicia **desabilitado** (`APP_SQS_PEDIDO_ENABLED=false`) para a aplicação não cair ao subir sem AWS configurada.
> Ao configurar suas keys e fila, ative com `APP_SQS_PEDIDO_ENABLED=true`.

>>>>>>> theirs
=======
> Por padrão, o consumer SQS inicia **desabilitado** (`APP_SQS_PEDIDO_ENABLED=false`) para a aplicação não cair ao subir sem AWS configurada.
> Ao configurar suas keys e fila, ative com `APP_SQS_PEDIDO_ENABLED=true`.

>>>>>>> theirs
=======
> Por padrão, o consumer SQS inicia **desabilitado** (`APP_SQS_PEDIDO_ENABLED=false`) para a aplicação não cair ao subir sem AWS configurada.
> Ao configurar suas keys e fila, ative com `APP_SQS_PEDIDO_ENABLED=true`.

>>>>>>> theirs
=======
> Por padrão, o consumer SQS inicia **desabilitado** (`APP_SQS_PEDIDO_ENABLED=false`) para a aplicação não cair ao subir sem AWS configurada.
> Ao configurar suas keys e fila, ative com `APP_SQS_PEDIDO_ENABLED=true`.

>>>>>>> theirs
=======
> Por padrão, o consumer SQS inicia **desabilitado** (`APP_SQS_PEDIDO_ENABLED=false`) para a aplicação não cair ao subir sem AWS configurada.
> Ao configurar suas keys e fila, ative com `APP_SQS_PEDIDO_ENABLED=true`.

>>>>>>> theirs
=======
> Por padrão, o consumer SQS inicia **desabilitado** (`APP_SQS_PEDIDO_ENABLED=false`) para a aplicação não cair ao subir sem AWS configurada.
> Ao configurar suas keys e fila, ative com `APP_SQS_PEDIDO_ENABLED=true`.

>>>>>>> theirs
=======
> Por padrão, o consumer SQS inicia **desabilitado** (`APP_SQS_PEDIDO_ENABLED=false`) para a aplicação não cair ao subir sem AWS configurada.
> Ao configurar suas keys e fila, ative com `APP_SQS_PEDIDO_ENABLED=true`.

>>>>>>> theirs
### Contrato do evento esperado
```json
{
  "zipCode": "80010000",
  "customerId": 1,
  "orderItems": [
    { "sku": 1, "amount": 5 },
    { "sku": 2, "amount": 3 }
  ],
  "origin": "SQS_QUEUE",
  "occurredAt": "2024-05-20T14:30:00Z"
}
```
<<<<<<< ours
<<<<<<< ours
<<<<<<< ours
<<<<<<< ours
<<<<<<< ours
=======
=======
>>>>>>> theirs
=======
>>>>>>> theirs
=======
>>>>>>> theirs
=======
>>>>>>> theirs


## Configurando variáveis na IDE

### IntelliJ IDEA
No `Run/Debug Configurations`, preencha **Environment variables**:

```
AWS_ACCESS_KEY_ID=sua_access_key;AWS_SECRET_ACCESS_KEY=sua_secret_key;QUEUE_ORDER_EVENTS=T0XN_NOME_COMPLETO.fifo;APP_SQS_PEDIDO_ENABLED=true
```

### VS Code (`launch.json`)
Adicione o bloco `env` na configuração Java:

```json
"env": {
  "AWS_ACCESS_KEY_ID": "COLE_SUA_ACCESS_KEY_AQUI",
  "AWS_SECRET_ACCESS_KEY": "COLE_SUA_SECRET_KEY_AQUI",
  "QUEUE_ORDER_EVENTS": "T0XN_NOME_COMPLETO.fifo",
  "APP_SQS_PEDIDO_ENABLED": "true"
}
```

> Segurança: nunca commitar Access Key ou Secret Key em arquivo versionado.
<<<<<<< ours
<<<<<<< ours
<<<<<<< ours
<<<<<<< ours
>>>>>>> theirs
=======
>>>>>>> theirs
=======
>>>>>>> theirs
=======
>>>>>>> theirs
=======


### Troubleshooting (erro `Application finished with exit code: 1`)
Se der erro no `spring-boot:run`, valide esta ordem:

1. Para subir **sem AWS/SQS**, mantenha:
   - `APP_SQS_PEDIDO_ENABLED=false`
2. Para subir **com SQS**, configure também:
   - `AWS_ACCESS_KEY_ID`
   - `AWS_SECRET_ACCESS_KEY`
   - `QUEUE_ORDER_EVENTS` (ex.: `T0XN_SEU_NOME_PEDIDOS.fifo`)

> Dica: `queue.order-events` já possui valor padrão de fallback no `application.properties`, mas em ambiente real use sempre sua fila individual.
>>>>>>> theirs
