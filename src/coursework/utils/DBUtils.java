package coursework.utils;

import coursework.command.ClientAction;
import coursework.connection.ClientServerConnection;
import coursework.dto.*;
import coursework.model.User;

import java.io.IOException;
import java.net.Socket;
import java.util.List;


public class DBUtils {
    public static ClientServerConnection clientServerConnection;

    static {
        try {
            clientServerConnection = new ClientServerConnection(new Socket("127.0.0.1", 8001));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static boolean checkUserOnExist(String username, String password) {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername(username);
        loginDto.setPassword(password);
        clientServerConnection.writeObject(ClientAction.CHECK_IF_ACCOUNT_EXIST);
        clientServerConnection.writeObject(loginDto);
        return (boolean) clientServerConnection.readObject();
    }

    public static ClientResponseAfterLogin login(String login, String password) {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername(login);
        loginDto.setPassword(password);
        clientServerConnection.writeObject(ClientAction.LOGIN);
        clientServerConnection.writeObject(loginDto);
        return (ClientResponseAfterLogin) clientServerConnection.readObject();
    }

    public static String changePassword(int userID, String currentPassword, String newPassword) {
        ChangePasswordDto changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setUserId(userID);
        changePasswordDto.setCurrentPassword(currentPassword);
        changePasswordDto.setNewPassword(newPassword);
        clientServerConnection.writeObject(ClientAction.CHANGE_PASSWORD);
        clientServerConnection.writeObject(changePasswordDto);
        return (String) clientServerConnection.readObject();
    }

    public static void register(User user) {
        clientServerConnection.writeObject(ClientAction.REGISTRATION);
        clientServerConnection.writeObject(user);
    }

    public static boolean checkIfUserAccountIsExist(String username) {
        clientServerConnection.writeObject(ClientAction.CHECK_IF_ACCOUNT_ALREADY_EXIST);
        clientServerConnection.writeObject(username);
        return (boolean) clientServerConnection.readObject();
    }

    public static void updateStudent(BookDto bookDto) {
        clientServerConnection.writeObject(ClientAction.UPDATE_USER);
        clientServerConnection.writeObject(bookDto);
    }

    public static boolean checkIFIDExist(String bookId) {
        clientServerConnection.writeObject(ClientAction.CHECK_IF_ID_EXIST);
        clientServerConnection.writeObject(bookId);
        return (boolean) clientServerConnection.readObject();
    }

    public static void saveBook(BookDto bookDto) {
        clientServerConnection.writeObject(ClientAction.SAVE_BOOK);
        clientServerConnection.writeObject(bookDto);
    }

    public static void deleteStudentRecord(String bookID) {
        clientServerConnection.writeObject(ClientAction.DELETE_STUDENT_RECORD);
        clientServerConnection.writeObject(bookID);

    }

    public static void deleteStudent(String bookId) {
        clientServerConnection.writeObject(ClientAction.DELETE_STUDENT);
        clientServerConnection.writeObject(bookId);
    }

    public static List<BookDto> loadData() {
        clientServerConnection.writeObject(ClientAction.LOAD_DATA);
        return (List<BookDto>) clientServerConnection.readObject();
    }

    public static List<BookDto> loadArchievedRecord() {
        clientServerConnection.writeObject(ClientAction.LOAD_ARCHIEVED_RECORD);
        return (List<BookDto>) clientServerConnection.readObject();
    }

    public static void unArchiveRecord(BookDto bookDto) {
        clientServerConnection.writeObject(ClientAction.UN_ARCHIVE_RECORD);
        clientServerConnection.writeObject(bookDto);
    }

    public static AdminDto fetchUsername() {
        clientServerConnection.writeObject(ClientAction.FETCH_USERNAME);
        return (AdminDto) clientServerConnection.readObject();
    }

    public static void updateAdminInfo(AdminDto adminDto) {
        clientServerConnection.writeObject(ClientAction.UPDATE_ADMIN_INFO);
        clientServerConnection.writeObject(adminDto);
    }

    public static AdminDto loadAdminInfo() {
        clientServerConnection.writeObject(ClientAction.LOAD_ADMIN_INFO);
        return (AdminDto) clientServerConnection.readObject();
    }

    public static boolean checkIfUsernameExists(String username) {
        clientServerConnection.writeObject(ClientAction.CHECK_IF_USERNAME_EXISTS);
        clientServerConnection.writeObject(username);
        return (boolean) clientServerConnection.readObject();
    }

    public static List<LibrarianDto> loadDataLibrarian() {
        clientServerConnection.writeObject(ClientAction.LOAD_DATA_LIBRARIAN);
        return (List<LibrarianDto>) clientServerConnection.readObject();
    }

    public static void updateLibrarian(LibrarianDto librarianDto) {
        clientServerConnection.writeObject(ClientAction.UPDATE_LIBRARIAN);
        clientServerConnection.writeObject(librarianDto);
    }

    public static void saveLibrarian(LibrarianDto librarianDto) {
        clientServerConnection.writeObject(ClientAction.SAVE_LIBRARIAN);
        clientServerConnection.writeObject(librarianDto);
    }

    public static void deleteLibrarian(int id) {
        clientServerConnection.writeObject(ClientAction.DELETE_LIBRARIAN);
        clientServerConnection.writeObject(id);
    }

    public static boolean checkIfStudentOwnsBook(OwnsBookDto ownsBookDto) {
        clientServerConnection.writeObject(ClientAction.CHECK_IF_STUDENT_OWNS_BOOK);
        clientServerConnection.writeObject(ownsBookDto);
        return (boolean) clientServerConnection.readObject();
    }

    public static double checkLateFee() {
        clientServerConnection.writeObject(ClientAction.CHECK_LATE_FEE);
        return (double) clientServerConnection.readObject();
    }

    public static StudentDto searchStudent(String studentId) {
        clientServerConnection.writeObject(ClientAction.SEARCH_STUDENT);
        clientServerConnection.writeObject(studentId);
        return (StudentDto) clientServerConnection.readObject();
    }

    public static BookDto searchBook(String bookId) {
        clientServerConnection.writeObject(ClientAction.SEARCH_BOOK);
        clientServerConnection.writeObject(bookId);
        return (BookDto) clientServerConnection.readObject();
    }

    public static List<IssuedBookDto> getData(GetDataDto getDataDto) {
        clientServerConnection.writeObject(ClientAction.GET_DATA);
        clientServerConnection.writeObject(getDataDto);
        return (List<IssuedBookDto>) clientServerConnection.readObject();
    }

    public static String searchBookByBookId(String bookId) {
        clientServerConnection.writeObject(ClientAction.SEARCH_BOOK_BY_BOOKID);
        clientServerConnection.writeObject(bookId);
        return (String) clientServerConnection.readObject();
    }

    public static int selectShortTermBorrowedTime(String studentSearchTextField) {
        clientServerConnection.writeObject(ClientAction.SELECT_SHORT_TERM_BORROWED_TIME);
        clientServerConnection.writeObject(studentSearchTextField);
        return (int) clientServerConnection.readObject();
    }

    public static void insertIntoShortBook(ShortBookDto shortBookDto) {
        clientServerConnection.writeObject(ClientAction.INSERT_INTO_BOOK);
        clientServerConnection.writeObject(shortBookDto);
    }

    public static void changeAvailabilityInBookEntity(String bookId) {
        clientServerConnection.writeObject(ClientAction.CHANGE_AVAILABILITY_IN_BOOK_ENTITY);
        clientServerConnection.writeObject(bookId);
    }

    public static int   countOfRemainingBooks(String studentId) {
        clientServerConnection.writeObject(ClientAction.COUNT_OF_REMAINING_BOOKS);
        clientServerConnection.writeObject(studentId);
        return (int) clientServerConnection.readObject();
    }

    public static void updateRemainingBooks(UpdateRemainBookDto updateRemainBookDto) {
        clientServerConnection.writeObject(ClientAction.UPDATE_REMAINING_BOOKS);
        clientServerConnection.writeObject(updateRemainBookDto);
    }
}
