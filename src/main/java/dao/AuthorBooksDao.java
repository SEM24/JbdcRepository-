package dao;

import models.AuthorBooks;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class AuthorBooksDao {
    private final Connection connection;

    public AuthorBooksDao(Connection connection) {
        this.connection = connection;
    }

    public Collection<AuthorBooks> getAllAuthorsAndBooks() {
        try (Statement statement = connection.createStatement()) {
            final Collection<AuthorBooks> authorsBooks = new ArrayList<>();
            ResultSet cursor = statement.executeQuery("SELECT * FROM authors_books");
            while (cursor.next()) {
                authorsBooks.add(createAuthorBookFromCursorIfPossible(cursor));
            }
            return authorsBooks;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch authors", e);
        }
    }

    public int insertAuthorBook(int authorId, int bookId) {
        final String insertTemplate =
                "INSERT INTO authors_books (authorId, bookId) VALUES(?,?)";
        try (PreparedStatement statement = connection.prepareStatement(insertTemplate)) {
            statement.setInt(1, authorId);
            statement.setInt(2, bookId);
            statement.executeUpdate();
            ResultSet cursor = statement.getGeneratedKeys();
            if (!cursor.next()) {
                throw new RuntimeException("Failed to insert ");
            }
            return cursor.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert ", e);
        }
    }

    public Optional<AuthorBooks> getAuthorBookById(int authorID, int bookID) {
        final String template = "SELECT * FROM authors_books" +
                " WHERE authorId = ? AND bookId = ?" +
                " LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(template)) {
            statement.setInt(1, authorID);
            statement.setInt(2, bookID);
            ResultSet cursor = statement.executeQuery();
            if (!cursor.next()) {
                return Optional.empty();
            }
            return Optional.of(createAuthorBookFromCursorIfPossible(cursor));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch", e);
        }
    }

    public void updateAuthorBook(AuthorBooks authorBook) {
        final String updateTemplate =
                "UPDATE authors_books SET authorId = ?, bookId = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateTemplate)) {
            statement.setInt(1, authorBook.authorId);
            statement.setInt(2, authorBook.bookId);
            statement.setInt(3, authorBook.id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows != 1) {
                throw new IllegalArgumentException(
                        "Affected rows on update: " + affectedRows);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update", e);
        }
    }

    private AuthorBooks createAuthorBookFromCursorIfPossible(ResultSet cursor) throws SQLException {
        final AuthorBooks authorBooks = new AuthorBooks();
        authorBooks.id = cursor.getInt("id");
        authorBooks.authorId = cursor.getInt("authorId");
        authorBooks.bookId = cursor.getInt("bookId");
        return authorBooks;
    }

}
