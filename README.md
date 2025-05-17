Ram.all Server
=============

Este é um projeto de backend para um sistema de gerenciamento de ramais (extensões) desenvolvido com Spring Boot. Ele tem como objetivo gerenciar autenticação de usuários, login/logout de ramais e disponibilizar APIs para consultar e gerenciar ramais.

Tecnologias Utilizadas
----------------------

-   **Spring Boot**: Framework principal utilizado para desenvolver o backend.

-   **Spring Security**: Para autenticação e autorização de usuários.

-   **Spring Data JPA**: Para interação com banco de dados utilizando JPA.

-   **MySQL**: Banco de dados utilizado.

-   **JWT**: Utilizado para gerar e validar tokens de autenticação.

-   **Lombok**: Para reduzir a quantidade de código boilerplate (como getters, setters e construtores).

-   **SpringDoc OpenAPI**: Para documentar as APIs REST.

Estrutura do Projeto
--------------------

A estrutura do projeto segue o padrão do Spring Boot. Aqui estão algumas pastas importantes:

-   `controllers/`: Contém os controladores responsáveis pelas rotas da API.

-   `domain/`: Contém as entidades do sistema.

-   `dto/`: Contém os objetos de transferência de dados (DTOs).

-   `repositories/`: Contém os repositórios JPA.

-   `infra/security/`: Contém classes de segurança, como o serviço para gerar e validar tokens JWT.

-   `infra/service/`: Contém classes de serviços, como o serviço para envio de email e o de range para os ramais.

Como Rodar o Projeto
--------------------

### 1\. Clonar o repositório

`git clone https://github.com/Migzviana/Ram.all--Server`

`cd Ramal_back`

### 2\. Instalar as dependências

Caso você não tenha o Maven instalado, instale o [Maven](https://maven.apache.org/install.html) primeiro. Depois, execute o comando:

`mvn install`

### 3\. Configurar o Banco de Dados

Configure o banco de dados MySQL, criando um banco de dados com o nome desejado (por exemplo, `ramal_db`) e edite as configurações de acesso no arquivo `application.properties`:

`spring.datasource.url=jdbc:mysql://localhost:3306/ramal_db
spring.datasource.username=root
spring.datasource.password=senha`

### 4\. Rodar o Projeto

Para rodar o projeto, execute o comando:

`mvn spring-boot:run`

Agora, a aplicação estará rodando localmente em `http://localhost:8080`.

Rotas da API
------------

A seguir, são descritas as principais rotas disponíveis no backend.

### Autenticação

#### **POST** `/auth/login`

Realiza o login de um usuário.

**Body**:

`{
  "email": "usuario@dominio.com",
  "password": "senha"
}`

**Resposta de sucesso**:

`{
  "name": "Nome do Usuário",
  "email": "usuario@dominio.com",
  "token": "token_jwt_aqui"
}`

**Resposta de erro**:

`{
  "message": "Usuário não encontrado." // ou "Senha incorreta"
}`

#### **POST** `/auth/register`

Realiza o registro de um novo usuário.

**Body**:

`{
  "name": "Nome do Usuário",
  "email": "usuario@dominio.com",
  "password": "senha"
}`

**Resposta de sucesso**:

`{
  "name": "Nome do Usuário",
  "email": "usuario@dominio.com",
  "token": "token_jwt_aqui"
}`

**Resposta de erro**:

`{
  "message": "Usuário já existe."
}`

### Ramais (Extensions)

#### **GET** `/extensions/all`

Obtém todos os ramais registrados.

**Resposta de sucesso**:

`[
  {
    "extensionNumber": "1001",
    "loggedUser": "usuario@dominio.com"
  },
  {
    "extensionNumber": "1002",
    "loggedUser": null
  }
]`

#### **GET** `/extensions/available`

Obtém todos os ramais disponíveis (não estão em uso).

**Resposta de sucesso**:

`[
  {
    "extensionNumber": "1002",
    "loggedUser": null
  }
]`

#### **POST** `/extensions/login`

Realiza o login de um usuário em um ramal.

**Body**:

`{
  "extension": "1001"
}`

**Resposta de sucesso**:

`{
  "message": "Login realizado com sucesso"
}`

**Resposta de erro**:

`{
  "message": "Ramal não encontrado." // ou "Token inválido."
}`

#### **DELETE** `/extensions/logout/{extensionNumber}`

Desloga o usuário do ramal especificado.

**Resposta de sucesso**:

`{
  "message": "Logout realizado com sucesso."
}`

**Resposta de erro**:

`{
  "message": "Esse ramal não está em uso." // ou "Você não tem permissão para deslogar esse ramal."
}`

#### **GET** `/extensions/range?inicio=1001&fim=1005`

Obtém uma lista de ramais dentro de um intervalo de números.

**Resposta de sucesso**:

`[
  {
    "extensionNumber": "1001",
    "loggedUser": "usuario@dominio.com"
  },
  {
    "extensionNumber": "1002",
    "loggedUser": null
  }
]`

Testes
------

Para rodar os testes da aplicação, use o comando:

`mvn test`
