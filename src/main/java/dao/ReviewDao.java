package dao;

import models.Review;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class ReviewDao {
    private final Connection connection;

    public ReviewDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * Удаляем отзывы
     */
    public void delete(int id) {
        final String template = "DELETE FROM reviews WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(template)) {
            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows != 1) {
                throw new IllegalArgumentException(
                        "Affected rows on delete: " + affectedRows);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete review", e);
        }
    }

    /**
     * Достаем отзывы через текс и юзера
     */
    public Optional<Review> getReviewByTextAndUser(int userId, String text) {
        final String template = "SELECT * FROM reviews" +
                " WHERE user_id = ? AND text = ? LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(template)) {
            statement.setInt(1, userId);
            statement.setString(2, text);
            ResultSet cursor = statement.executeQuery();
            if (!cursor.next()) {
                return Optional.empty();
            }

            return Optional.of(createReviewFromCursorIfPossible(cursor));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch one book", e);
        }
    }

    public Collection<Review> getAllReviews() {
        try (Statement statement = connection.createStatement()) {
            final Collection<Review> books = new ArrayList<>();
            ResultSet cursor = statement.executeQuery("SELECT * FROM reviews");
            while (cursor.next()) {
                books.add(createReviewFromCursorIfPossible(cursor));
            }
            return books;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch reviews", e);
        }
    }

    //Вставка
    public int insertReview(Review review) {
        final String insertTemplate =
                "INSERT INTO reviews(text,date,user_id,book_id) VALUES(?,?,?,?)";

        try (PreparedStatement statement = connection.prepareStatement(insertTemplate)) {
            statement.setString(1, review.text);
            statement.setString(2, review.date);
            statement.setInt(3, review.user_id);
            statement.setInt(4, review.book_Id);
            statement.executeUpdate();


            ResultSet cursor = statement.getGeneratedKeys();
            if (!cursor.next()) {
                throw new RuntimeException("Failed to insert review");
            }

            return cursor.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert review", e);
        }
    }

    //обновление
    public void updateReview(Review review) {
        final String updateTemplate =
                "UPDATE reviews" +
                        " SET text=?, date=?, user_id=?, book_id=?" +
                        " WHERE id=?";

        try (PreparedStatement statement = connection.prepareStatement(updateTemplate)) {
            statement.setString(1, review.text);
            statement.setString(2, review.date);
            statement.setInt(3, review.user_id);
            statement.setInt(4, review.book_Id);
            statement.setInt(5, review.id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows != 1) {
                throw new IllegalArgumentException(
                        "Affected rows on update: " + affectedRows);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update book", e);
        }
    }


    private Review createReviewFromCursorIfPossible(ResultSet cursor) throws SQLException {
        final Review review = new Review();
        review.id = cursor.getInt("id");
        review.text = cursor.getString("text");
        review.date = cursor.getString("date");
        review.user_id = cursor.getInt("user_id");
        review.book_Id = cursor.getInt("book_id");
        return review;
    }

    public Collection<Review> getReviewsForBook(String bookTitle) {
        final String template = "SELECT * FROM reviews" +
                " JOIN books ON books.id = reviews.book_id" +
                " WHERE books.title = ?";
        try (PreparedStatement statement = connection.prepareStatement(template)) {
            final Collection<Review> reviews = new ArrayList<>();
            statement.setString(1, bookTitle);
            ResultSet cursor = statement.executeQuery();
            while (cursor.next()) {
                reviews.add(createReviewFromCursorIfPossible(cursor));
            }
            return reviews;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch reviews", e);
        }
    }


    public Collection<Review> getReviewsFromUser(String userName) {
        final String template = "SELECT * FROM reviews" +
                " JOIN users ON users.id = reviews.user_id" +
                " WHERE users.name = ? ";
        try (PreparedStatement statement = connection.prepareStatement(template)) {
            final Collection<Review> reviews = new ArrayList<>();
            statement.setString(1, userName);
            ResultSet cursor = statement.executeQuery();
            while (cursor.next()) {
                reviews.add(createReviewFromCursorIfPossible(cursor));
            }
            return reviews;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch one review", e);
        }
    }
}
