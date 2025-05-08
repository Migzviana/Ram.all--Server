Ramal Backend
Este é um projeto de backend para um sistema de gerenciamento de ramais (extensões) desenvolvido com Spring Boot. Ele tem como objetivo gerenciar autenticação de usuários, login/logout de ramais e disponibilizar APIs para consultar e gerenciar ramais.

Tecnologias Utilizadas
Spring Boot: Framework principal utilizado para desenvolver o backend.

Spring Security: Para autenticação e autorização de usuários.

Spring Data JPA: Para interação com banco de dados utilizando JPA.

MySQL: Banco de dados utilizado.

JWT: Utilizado para gerar e validar tokens de autenticação.

Lombok: Para reduzir a quantidade de código boilerplate (como getters, setters e construtores).

SpringDoc OpenAPI: Para documentar as APIs REST.

Como Rodar o Projeto
1. Clonar o repositório
bash
Copiar
Editar
git clone <URL_DO_REPOSITORIO>
cd Ramal_back
2. Instalar as dependências
Caso você não tenha o Maven instalado, instale o Maven primeiro. Depois, execute o comando:

bash
Copiar
Editar
mvn install
3. Configurar o Banco de Dados
Configure o banco de dados MySQL, criando um banco de dados com o nome desejado (por exemplo, ramal_db) e edite as configurações de acesso no arquivo application.properties:

properties
Copiar
Editar
spring.datasource.url=jdbc:mysql://localhost:3306/ramal_db
spring.datasource.username=root
spring.datasource.password=senha
4. Rodar o Projeto
Para rodar o projeto, execute o comando:

bash
Copiar
Editar
mvn spring-boot:run
Agora, a aplicação estará rodando localmente em http://localhost:8080.

Rotas da API
A seguir, são descritas as principais rotas disponíveis no backend.

Autenticação
POST /auth/login
Realiza o login de um usuário.

Body:

json
Copiar
Editar
{
  "email": "usuario@dominio.com",
  "password": "senha"
}
Resposta de sucesso:

json
Copiar
Editar
{
  "name": "Nome do Usuário",
  "email": "usuario@dominio.com",
  "token": "token_jwt_aqui"
}
Resposta de erro:

json
Copiar
Editar
{
  "message": "Usuário não encontrado." // ou "Senha incorreta"
}
POST /auth/register
Realiza o registro de um novo usuário.

Body:

json
Copiar
Editar
{
  "name": "Nome do Usuário",
  "email": "usuario@dominio.com",
  "password": "senha"
}
Resposta de sucesso:

json
Copiar
Editar
{
  "name": "Nome do Usuário",
  "email": "usuario@dominio.com",
  "token": "token_jwt_aqui"
}
Resposta de erro:

json
Copiar
Editar
{
  "message": "Usuário já existe."
}
Ramais (Extensions)
GET /extensions/all
Obtém todos os ramais registrados.

Resposta de sucesso:

json
Copiar
Editar
[
  {
    "extensionNumber": "1001",
    "loggedUser": "usuario@dominio.com"
  },
  {
    "extensionNumber": "1002",
    "loggedUser": null
  }
]
GET /extensions/available
Obtém todos os ramais disponíveis (não estão em uso).

Resposta de sucesso:

json
Copiar
Editar
[
  {
    "extensionNumber": "1002",
    "loggedUser": null
  }
]
POST /extensions/login
Realiza o login de um usuário em um ramal.

Body:

json
Copiar
Editar
{
  "extension": "1001"
}
Resposta de sucesso:

json
Copiar
Editar
{
  "message": "Login realizado com sucesso"
}
Resposta de erro:

json
Copiar
Editar
{
  "message": "Ramal não encontrado." // ou "Token inválido."
}
DELETE /extensions/logout/{extensionNumber}
Desloga o usuário do ramal especificado.

Resposta de sucesso:

json
Copiar
Editar
{
  "message": "Logout realizado com sucesso."
}
Resposta de erro:

json
Copiar
Editar
{
  "message": "Esse ramal não está em uso." // ou "Você não tem permissão para deslogar esse ramal."
}
GET /extensions/range?inicio=1001&fim=1005
Obtém uma lista de ramais dentro de um intervalo de números.

Resposta de sucesso:

json
Copiar
Editar
[
  {
    "extensionNumber": "1001",
    "loggedUser": "usuario@dominio.com"
  },
  {
    "extensionNumber": "1002",
    "loggedUser": null
  }
]
Testes
Para rodar os testes da aplicação, use o comando:

bash
Copiar
Editar
mvn test
Estrutura do Projeto
A estrutura do projeto segue o padrão do Spring Boot. Aqui estão algumas pastas importantes:

controllers/: Contém os controladores responsáveis pelas rotas da API.

domain/: Contém as entidades do sistema.

dto/: Contém os objetos de transferência de dados (DTOs).

repositories/: Contém os repositórios JPA.

infra/security/: Contém classes de segurança, como o serviço para gerar e validar tokens JWT.

Dependências no pom.xml
Aqui estão as principais dependências configuradas no pom.xml:

Spring Boot Starter Data JPA: Para integração com o banco de dados.

Spring Boot Starter Security: Para autenticação e autorização.

Spring Boot Starter Web: Para expor as APIs REST.

Spring Boot Starter Validation: Para validações de entrada nas APIs.

Spring Security Test: Para realizar testes de segurança.

Java JWT: Para criar e verificar tokens JWT.

MySQL Connector: Para conectar-se ao banco de dados MySQL.

