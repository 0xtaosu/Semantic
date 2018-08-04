package xyz.taosue.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tao
 */
public class RecordDao {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/test?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC";
    static final String USER = "root";
    static final String PASS = "1234";
    static Connection conn;
    static PreparedStatement statement;

    /**
     * 读取评论
     *
     * @return
     */
    public static List<String> selectRecord() {
        List<String> textList = new ArrayList<>();
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = conn.prepareStatement("SELECT review FROM record");
            System.out.println("=>" + statement.toString());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                String review = result.getString("review");
                System.out.println("<=" + review);
                textList.add(review);
            }
            statement.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return textList;
    }
}
