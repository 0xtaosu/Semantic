package xyz.taosue.dao;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

/**
 * @author tao
 */
public class SchemaDao {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/test2?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC";
    static final String USER = "root";
    static final String PASS = "1234";
    static Connection conn;
    static PreparedStatement statement;

    /**
     * 建表
     *
     * @param createSQLSet
     */
    public static void createTable(Set<String> createSQLSet) {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(false);
            createSQLSet.forEach(createSQL -> {
                try {
                    statement = conn.prepareStatement(createSQL);
                    statement.executeUpdate();
                    System.out.println("=>"+statement.toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            conn.commit();
            statement.close();
            conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
