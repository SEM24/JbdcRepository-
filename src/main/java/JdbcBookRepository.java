import dao.AuthorDao;
import dao.BookDao;
import dao.ReviewDao;
import dao.UserDao;
import models.Author;
import models.Book;
import models.Review;
import models.User;

import java.sql.Connection;
import java.util.Collection;
import java.util.Optional;

public class JdbcBookRepository implements IBookRepository {
    private static final int INVALID_ID = 0;

    private final AuthorDao authorDao;
    private final BookDao bookDao;
    private final UserDao userDao;
    private final ReviewDao reviewDao;

    public JdbcBookRepository(Connection connection) {
        bookDao = new BookDao(connection);
        authorDao = new AuthorDao(connection);
        userDao = new UserDao(connection);
        reviewDao = new ReviewDao(connection);
    }

    @Override
    public Collection<Book> getAllBooks() {
        return bookDao.getAllBooks();
    }

    @Override
    public Collection<Author> getAllAuthors() {
        return authorDao.getAllAuthors();
    }

    @Override
    public Collection<Review> getAllReviews() {
        return reviewDao.getAllReviews();
    }

    @Override
    public Optional<Book> getByIdBook(int id) {
        return bookDao.getBookById(id);
    }

    @Override
    public Optional<Author> getByIdAuthor(int id) {
        return authorDao.getAuthorById(id);
    }

    @Override
    public Optional<User> getByIdUser(int id) {
        return userDao.getUserById(id);
    }

    @Override
    public void saveBookAndAuthor(Book book, Author author) {

        if (author.id == INVALID_ID) {
            Optional<Author> matchingAuthor = authorDao.getAuthorByName(author.name);
            if (matchingAuthor.isPresent()) {
                author.id = matchingAuthor.get().id;
                authorDao.updateAuthor(author);
            } else {
                author.id = authorDao.insertAuthor(author);
            }
        } else {
            authorDao.updateAuthor(author);
        }

        if (book.id == INVALID_ID) {
            Optional<Book> matchingBook =
                    bookDao.getBookByTitleAndAuthor(book.title, author.name);
            if (matchingBook.isPresent()) {
                book.id = matchingBook.get().id;
                bookDao.updateBook(book);
            } else {
                book.id = bookDao.insertBook(book);
            }
        } else {
            bookDao.updateBook(book);
        }
    }

    @Override
    public void saveUser(User user) {
        userDao.insertUser(user);
    }

    @Override
    public void saveReviewAndUserToBook(Book book, User user, Review review) {
        review.book_Id = book.id;
        if (user.id == INVALID_ID) {
            Optional<User> matchingUser = userDao.getUserById(user.id);
            if (matchingUser.isPresent()) {
                user.id = matchingUser.get().id;
                userDao.updateUser(user);
            } else {
                user.id = userDao.insertUser(user);
            }
        } else {
            userDao.updateUser(user);
        }

        review.user_id = user.id;

        if (review.text.length() != 0) {
            if (review.id == INVALID_ID) {
                Optional<Review> matchingReview =
                        reviewDao.getReviewByTextAndUser(review.user_id, review.text);
                if (matchingReview.isPresent()) {
                    review.id = matchingReview.get().id;
                    reviewDao.updateReview(review);
                } else {
                    reviewDao.insertReview(review);
                }
            } else {
                reviewDao.updateReview(review);
            }
        }
    }


    @Override
    public void deleteBook(int id) {
        bookDao.delete(id);
    }

    @Override
    public void deleteBook(Book book) {
        bookDao.delete(book.id);
    }

    @Override
    public void deleteAuthor(int id) {
        authorDao.delete(id);
    }

    @Override
    public void deleteAuthor(Author author) {
        authorDao.delete(author.id);
    }

    @Override
    public void deleteUser(int id) {
        userDao.delete(id);
    }


    @Override
    public void deleteReview(int id) {
        reviewDao.delete(id);
    }


}
