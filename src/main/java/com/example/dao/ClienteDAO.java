package com.example.dao;

import com.example.entity.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class ClienteDAO implements DAO<Cliente> {

    @Override
    public Cliente get(Long id) {
        Cliente cliente = null;
        String sql = "SELECT * FROM clientes WHERE id = ?";
        try (Connection connection = ConnectionDb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    cliente = createClienteFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cliente;
    }

    @Override
    public int save(Cliente cliente) {
        String sql = "INSERT INTO clientes (nome_de_usuario, email, senha) VALUES (?, ?, ?)";
        try (Connection connection = ConnectionDb.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cliente.getNomeDeUsuario());
            statement.setString(2, cliente.getEmail());
            statement.setString(3, cliente.getSenha());
            statement.execute();
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public boolean update(Cliente cliente, String[] params) {
        String sql = "UPDATE clientes SET nome_de_usuario = ?, email = ?, senha = ?, codigo = ?, is_adm = ? WHERE id = ?";
        try (Connection connection = ConnectionDb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cliente.getNomeDeUsuario());
            statement.setString(2, cliente.getEmail());
            statement.setString(3, cliente.getSenha());
            statement.setString(4, cliente.getCode());
            statement.setInt(5, cliente.isAdm());
            statement.setLong(6, cliente.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCodigo(Cliente cliente) {
        String sql = "UPDATE clientes SET codigo = ? WHERE id = ?";
        try (Connection connection = ConnectionDb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cliente.getCode());
            statement.setLong(2, cliente.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM clientes WHERE id = ?";
        try (Connection connection = ConnectionDb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean verificarNomeDeUsuarioExistente(String nomeDeUsuario) {
        String sql = "SELECT COUNT(*) FROM clientes WHERE nome_de_usuario = ?";
        try (Connection connection = ConnectionDb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nomeDeUsuario);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean verificarEmailExistente(String email) {
        String sql = "SELECT COUNT(*) FROM clientes WHERE email = ?";
        try (Connection connection = ConnectionDb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    return true ;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Integer verificarLogin(String email, String senha) {
        //PRECISA FAZER SOBRECARGA NO destinoID,

        String sql = "SELECT id FROM clientes WHERE email = ? AND senha = ?";
        try (Connection connection = ConnectionDb.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.setString(2, senha);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                } else {
                    return null;
                }
            } catch (Exception e){
                return null;
            }
        } catch(Exception e){
            return null;
        }
    }

    public Integer destinoCodigo(String email, String sql, String columnLabel) {
        try (Connection connection = ConnectionDb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, email);
                try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(columnLabel);
                } else {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public boolean verificarAdm(int userId) {
        String sql = "SELECT is_adm FROM clientes WHERE id = ?";
        try (Connection connection = ConnectionDb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("is_adm") == 1;
                } else {
                    return false;
                }
            } catch (Exception e){
                return false;
            }
        } catch (Exception e){
            return false;
        }
    }

    public List<Cliente> getAllClients() {
        List<Cliente> clientList = new ArrayList<>();
        String sql = "SELECT id, nome_de_usuario, email, is_adm FROM clientes"; // Seleciona apenas as colunas necessárias
        try (Connection connection = ConnectionDb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                int isAdm = resultSet.getInt("is_adm");
                String nomeDeUsuario = resultSet.getString("nome_de_usuario");
                String email = resultSet.getString("email");
                Cliente cliente = new Cliente();
                cliente.setNomeDeUsuario(nomeDeUsuario);
                cliente.setEmail(email);
                cliente.setId(id);
                cliente.setAdm(isAdm);
                clientList.add(cliente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientList;
    }

    // Métodos auxiliares
    private Cliente createClienteFromResultSet(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id");
        String nomeDeUsuario = resultSet.getString("nome_de_usuario");
        String email = resultSet.getString("email");
        String senha = resultSet.getString("senha");
        String codigo = resultSet.getString("codigo");
        int isAdm = resultSet.getInt("is_adm");
        Cliente cliente = new Cliente(nomeDeUsuario, email, senha);
        cliente.setId(id);
        cliente.setCode(codigo);
        cliente.setAdm(isAdm);
        return cliente;
    }
}
