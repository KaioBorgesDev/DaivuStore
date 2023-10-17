package com.example.dao;

import com.example.entity.Produto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO implements DAO<Produto> {

    @Override
    public Produto get(Long id) {
        String sql = "SELECT * FROM produtos WHERE id = ?";
        try (Connection connection = ConnectionDb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return createProdutoFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int save(Produto produto) {
        String sql = "INSERT INTO produtos (marca, descricao, ml, preco, link_imagem) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = ConnectionDb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, produto.getMarca());
            statement.setString(2, produto.getDescricao());
            statement.setString(3, produto.getMl());
            statement.setDouble(4, produto.getPreco());
            statement.setString(5, produto.getLinkImagem());
            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public boolean update(Produto produto, String[] params) {
        String sql = "UPDATE produtos SET marca = ?, descricao = ?, ml = ?, preco = ?, link_imagem = ? WHERE id = ?";
        try (Connection connection = ConnectionDb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, produto.getMarca());
            statement.setString(2, produto.getDescricao());
            statement.setString(3, produto.getMl());
            statement.setDouble(4, produto.getPreco());
            statement.setString(5, produto.getLinkImagem());
            statement.setLong(6, produto.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM produtos WHERE id = ?";
        try (Connection connection = ConnectionDb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Produto> getAllProdutos() {
        List<Produto> productList = new ArrayList<>();
        String sql = "SELECT * FROM produtos";
        try (Connection connection = ConnectionDb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                productList.add(createProdutoFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }

    public ArrayList<String> getAllUniqueBrands() {
        ArrayList<String> uniqueBrands = new ArrayList<>();
        String sql = "SELECT DISTINCT marca FROM produtos";

        try (Connection connection = ConnectionDb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String marca = resultSet.getString("marca");
                uniqueBrands.add(marca);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return uniqueBrands;
    }

    private Produto createProdutoFromResultSet(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id");
        String marca = resultSet.getString("marca");
        String descricao = resultSet.getString("descricao");
        String ml = resultSet.getString("ml");
        Double preco = resultSet.getDouble("preco");
        String linkImagem = resultSet.getString("link_imagem");
        Produto produto = new Produto(marca, descricao, ml, preco, linkImagem);
        produto.setId(id);
        return produto;
    }
}
