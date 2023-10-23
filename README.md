# DaivuStore

Welcome to the Perfume Store repository, a project developed in JavaFX, MySQL, and IntelliJ IDEA. This perfume store system allows you to manage products and customers in a virtual store environment.

## Execution Instructions

To run the code of this project, follow the steps below:

1. Clone this repository to your local machine:

```bash
git clone https://github.com/lucasrealdev/DaivuStore.git
```

2. Open the project in your IntelliJ IDEA development environment or convert it to your preferred IDE.

3. In the ConnectionDb class (located in the dao package), you should configure the connection information to your MySQL database. Make sure to change the following variables to match your MySQL setup:

```java
private static final String URL_BANCO = "jdbc:mysql://localhost:3306/daivu";
private static final String LOGIN_BANCO = "seu-usuario";
private static final String SENHA_BANCO = "sua-senha";
```

4. Ensure that you have MySQL installed on your machine and have created the daivu database. You can use the following SQL command to create the database structure:

```sql
CREATE DATABASE IF NOT EXISTS `daivu`;
USE `daivu`;

CREATE TABLE `clientes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome_de_usuario` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `senha` varchar(50) NOT NULL,
  `codigo` int DEFAULT NULL,
  `is_adm` int DEFAULT '0',
  PRIMARY KEY (`id`)
);

CREATE TABLE `produtos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `marca` varchar(30) NOT NULL,
  `descricao` varchar(100) NOT NULL,
  `ml` varchar(7) NOT NULL,
  `preco` double NOT NULL,
  `link_imagem` varchar(300) NOT NULL,
  PRIMARY KEY (`id`)
);
```

5. To manage and add products and customers you have to create a user manually in the database with the is_adm property with the value '1'

```sql
  INSERT INTO clientes (nome_de_usuario, email, senha, is_adm) VALUES ("AdministradorTeste", "Teste@gmail.com", "Teste@001", 1);
```

6. Run the Main class to start the Perfume Store application.

## Project Resources

The Perfume Store project utilizes the following technologies and libraries:

* **JavaFX**: Framework for creating graphical interfaces.
* **MySQL**: Relational database for storing product and customer information.
* **SceneBuilder**: Graphic tool for creating user interfaces.
* **IntelliJ IDEA**: Integrated Development Environment (IDE).

## Contributions

This is an open-source project, and you are welcome to contribute. Feel free to make improvements, fix issues, or add new features.

If you wish to contribute, follow these steps:

1. Fork this repository.
2. Create a branch for your feature: git checkout -b feature-my-feature.
3. Commit your changes: git commit -m 'Add my feature'.
4. Push the branch: git push origin feature-my-feature.
5. Open a Pull Request.

## Tipo e descrição 🦄

O commit semântico possui os elementos estruturais abaixo (tipos), que informam a intenção do seu commit ao utilizador(a) de seu código.

- `feat`- Commits do tipo feat indicam que seu trecho de código está incluindo um **novo recurso** (se relaciona com o MINOR do versionamento semântico).

- `fix` - Commits do tipo fix indicam que seu trecho de código commitado está **solucionando um problema** (bug fix), (se relaciona com o PATCH do versionamento semântico).

- `docs` - Commits do tipo docs indicam que houveram **mudanças na documentação**, como por exemplo no Readme do seu repositório. (Não inclui alterações em código).

- `test` - Commits do tipo test são utilizados quando são realizadas **alterações em testes**, seja criando, alterando ou excluindo testes unitários. (Não inclui alterações em código)

- `build` - Commits do tipo build são utilizados quando são realizadas modificações em **arquivos de build e dependências**.

- `perf` - Commits do tipo perf servem para identificar quaisquer alterações de código que estejam relacionadas a **performance**.

- `style` - Commits do tipo style indicam que houveram alterações referentes a **formatações de código**, semicolons, trailing spaces, lint... (Não inclui alterações em código).

- `refactor` - Commits do tipo refactor referem-se a mudanças devido a **refatorações que não alterem sua funcionalidade**, como por exemplo, uma alteração no formato como é processada determinada parte da tela, mas que manteve a mesma funcionalidade, ou melhorias de performance devido a um code review.

- `chore` - Commits do tipo chore indicam **atualizações de tarefas** de build, configurações de administrador, pacotes... como por exemplo adicionar um pacote no gitignore. (Não inclui alterações em código)

- `ci` - Commits do tipo ci indicam mudanças relacionadas a **integração contínua** (_continuous integration_).

- `raw` - Commits to tipo raw indicam mudanças relacionadas a arquivos de configurações, dados, features, parametros.
  
## implementations to be made

&#x2610; Login With Google Functionality<br>
&#x2610; Forgot Password Functionality<br>
&#x2610; Update User GUI Functionality<br>
&#x2610; Change cart to BD for each Client<br>
&#x2610; Account Configuration Functionality<br>
&#x2610; Implement purchasing system<br>
&#x2610; improve security<br>
&#x2610; Optimize the code
