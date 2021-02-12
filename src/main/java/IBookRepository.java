import models.Author;
import models.Book;
import models.Review;
import models.User;

import java.util.Collection;
import java.util.Optional;

public interface IBookRepository {
    /**
     * @return
     * @throws RuntimeException Если что-то пошло не так во время извлечения книг из хранилища.
     */
    Collection<Book> getAllBooks();

    Collection<Author> getAllAuthors();

    Collection<Review> getAllReviews();

    Optional<Book> getByIdBook(int id);

    Optional<Author> getByIdAuthor(int id);

    Optional<User> getByIdUser(int id);

    /**
     * Сохраняет книгу с написавшим ее автором.
     * Если книга или автор не сохранены, то они добавляются в хранилище.
     * Если книга или автор сохранены, то они обновляются в хранилище.
     */
    void saveBookAndAuthor(Book book, Author author);

    void saveUser(User user);

    /**
     * Сохраняет отзыв с написавшим его пользователем к книге которая есть в базе.
     * Если отзыв или пользователь не сохранены, то они добавляются в хранилище.
     * Если отзыв или пользователь сохранены, то они обновляются в хранилище.
     */

    void saveReviewAndUserToBook(Book book, User user,Review review);

    void deleteBook(int id);

    void deleteBook(Book book);

    void deleteAuthor(int id);

    void deleteAuthor(Author author);

    void deleteUser(int id);

    void deleteReview(int id);

}
