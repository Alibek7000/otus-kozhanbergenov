package kz.dao;

import kz.entity.Item;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemsDao {

    private final DataSource dataSource = DataSource.getInstance();

    public Item save(Item item) {
        String sql = "INSERT INTO item (name, price) VALUES (?, ?)";
        String generatedColumns[] = {"id"};

        try (PreparedStatement stmt = dataSource.getConnection().prepareStatement(sql, generatedColumns)) {
            stmt.setString(1, item.getName());
            stmt.setBigDecimal(2, item.getPrice());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    item.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Saving item failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save item", e);
        }
        return item;
    }

    public List<Item> findAll() {
        String sql = "SELECT id, name, price FROM item";
        List<Item> items = new ArrayList<>();
        try (PreparedStatement stmt = dataSource.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Item item = new Item();
                item.setId(rs.getLong("id"));
                item.setName(rs.getString("name"));
                item.setPrice(rs.getBigDecimal("price"));
                items.add(item);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find items", e);
        }
        return items;
    }

    public void update(Item item) {
        String sql = "UPDATE item SET price = ? WHERE id = ?";
        try (PreparedStatement stmt = dataSource.getConnection().prepareStatement(sql)) {
            stmt.setBigDecimal(1, item.getPrice());
            stmt.setLong(2, item.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update item", e);
        }
    }

    public void delete(Item item) {
        String sql = "DELETE FROM item WHERE id = ?";
        try (PreparedStatement stmt = dataSource.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, item.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete item", e);
        }
    }
}
