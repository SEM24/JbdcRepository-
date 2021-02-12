import models.Author;
import models.Book;
import models.Review;
import models.User;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

public class Main {
    public static void main(String[] args) {
        try {
            new Main().run();
        } catch (SQLException e) {
            System.out.println("Failed to do something: " + e.getLocalizedMessage());
        }
    }

    private void run() throws SQLException {
        try (Connection connection =
                     DriverManager.getConnection("jdbc:sqlite:books.db")) {
            doWork(connection);
        }
    }

    private void doWork(Connection connection) throws SQLException {

        createTables(connection);
        IBookRepository repository = new JdbcBookRepository(connection);

        final Author author1 = new Author();
        author1.name = "Толкин";
        author1.birthYear = 1901;

        final Book book1 = new Book();
        book1.title = "Властелин колец";
        book1.publishYear = 1940;
        book1.price = new BigDecimal("3000.33");

        final Author author2 = new Author();
        author2.name = "Дейтел";
        author2.birthYear = 1960;

        final Book book2 = new Book();
        book2.title = "Java для начинающих";
        book2.publishYear = 2005;
        book2.price = new BigDecimal("100500700.255123");


        final Author author3 = new Author();
        author3.name = "Герберт Шилдт";
        author3.birthYear = 1951;

        final Book book3 = new Book();
        book3.title = "Java: The Complete Reference";
        book3.publishYear = 1997;
        book3.price = new BigDecimal("400500700.276763");

        final Book book4 = new Book();
        book4.title = "Java: A Beginner S Guide";
        book4.publishYear = 2001;
        book4.price = new BigDecimal("4343.21212");

        final Book book5 = new Book();
        book5.title = "C#: The Complete Reference";
        book5.publishYear = 2002;
        book5.price = new BigDecimal("31314.1213");

        final User user1 = new User();
        user1.name = "Егор";
        final User user2 = new User();
        user1.name = "Игорь";

        final Review review1 = new Review();
        review1.date = "22.01.2020";
        review1.text = "Хорошая книга";

        final Review review2 = new Review();
        review2.date = "24.04.2020";
        review2.text = "Ничего не понятно, но очень интересно";

        repository.saveBookAndAuthor(book1, author1);
        repository.saveBookAndAuthor(book2, author2);
        repository.saveBookAndAuthor(book3, author3);
        repository.saveBookAndAuthor(book4, author3);
        repository.saveBookAndAuthor(book5, author3);

        repository.saveReviewAndUserToBook(book1, user1, review2);
        repository.saveReviewAndUserToBook(book2, user2, review1);
        repository.saveReviewAndUserToBook(book3, user1, review2);

        Collection<Book> books = repository.getAllBooks();
        System.out.println("Books count:" + books.size());

        Collection<Review> reviews = repository.getAllReviews();
        System.out.println("Reviews: \n" + reviews);
    }

    public final String CreateBooksTableQuery = "CREATE TABLE IF NOT EXISTS books (" +
            " id INTEGER PRIMARY KEY AUTOINCREMENT," +
            " title VARCHAR(100)," +
            " publish_year INTEGER," +
            " price DECIMAL(10,2)," +
            " author_id INTEGER" +
            ")";

    public final String CreateAuthorsTableQuery = "CREATE TABLE IF NOT EXISTS authors (" +
            " id INTEGER PRIMARY KEY AUTOINCREMENT," +
            " name VARCHAR(100)," +
            " birth_year INTEGER" +
            ")";

    public final String CreateReviewsTableQuery = "CREATE TABLE IF NOT EXISTS reviews (" +
            " id INTEGER PRIMARY KEY AUTOINCREMENT," +
            " text VARCHAR(500)," +
            " date VARCHAR(100)," +
            " user_id INTEGER," +
            " book_id INTEGER" +
            ")";

    public final String CreateUsersTableQuery = "CREATE TABLE IF NOT EXISTS users (" +
            " id INTEGER PRIMARY KEY AUTOINCREMENT," +
            " name VARCHAR(100)," +
            " email VARCHAR(50)" +
            ")";

    public final String CreateAuthorsBooksTableQuery = "CREATE TABLE IF NOT EXISTS authors_books (" +
            " id INTEGER PRIMARY KEY AUTOINCREMENT," +
            " authorId INTEGER," +
            " bookId INTEGER," +
            " FOREIGN KEY (authorId) REFERENCES authors(id)," +
            " FOREIGN KEY (bookId) REFERENCES books(id)" +
            ")";


    private void createTables(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(CreateAuthorsTableQuery);

            statement.executeUpdate(CreateBooksTableQuery);

            statement.executeUpdate(CreateAuthorsBooksTableQuery);

            statement.executeUpdate(CreateReviewsTableQuery);

            statement.executeUpdate(CreateUsersTableQuery);
        }
    }
}
