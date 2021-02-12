package dao;

import models.User;

import java.sql.*;
import java.util.Optional;

public class UserDao {
    private final Connection connection;

    public UserDao(Connection connection) {
        this.connection = connection;
    }

    public void delete(int id) {
        final String template = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(template)) {
            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows != 1) {
                throw new IllegalArgumentException(
                        "Affected rows on delete: " + affectedRows);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    public Optional<User> getUserById(int id) {
        try (Statement statement = connection.createStatement()) {
            ResultSet cursor = statement.executeQuery(
                    "SELECT * FROM users WHERE id = " + id);
            if (!cursor.next()) {
                return Optional.empty();
            }

            return Optional.of(createUserFromCursorIfPossible(cursor));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch one user", e);
        }
    }

    private User createUserFromCursorIfPossible(ResultSet cursor) throws SQLException {
        final User user = new User();
        user.id = cursor.getInt("id");
        user.name = cursor.getString("name");
        return user;
    }

    public int insertUser(User user) {
        final String insertTemplate =
                "INSERT INTO users(name) VALUES(?)";

        try (PreparedStatement statement = connection.prepareStatement(insertTemplate)) {
            statement.setString(1, user.name);
            statement.executeUpdate();

            ResultSet cursor = statement.getGeneratedKeys();
            if (!cursor.next()) {
                throw new RuntimeException("Failed to insert user");
            }

            return cursor.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert user", e);
        }
    }

    public void updateUser(User user) {
        final String insertTemplate =
                "UPDATE users SET name = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(insertTemplate)) {
            statement.setString(1, user.name);
            statement.setInt(2, user.id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows != 1) {
                throw new IllegalArgumentException(
                        "Affected rows on update: " + affectedRows);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update user", e);
        }
    }
}
