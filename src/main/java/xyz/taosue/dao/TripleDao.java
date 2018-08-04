package xyz.taosue.dao;

import xyz.taosue.entity.Triple;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tao
 */
public class TripleDao {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/test?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC";
    static final String USER = "root";
    static final String PASS = "1234";
    static Connection conn;
    static PreparedStatement statement;

    /**
     * 更新三元组
     *
     * @param tripleList
     */
    public static void updateTriple(List<Triple> tripleList) {
    }

    /**
     * 插入三元组
     *
     * @param tripleList
     */
    public static void insertTriple(List<Triple> tripleList) {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(false);
            statement = conn.prepareStatement("INSERT INTO triple (subj,pred,obj,type) VALUES (?,?,?,?)");
            tripleList.forEach(triple -> {
                try {
                    statement.setString(1, triple.getSubj());
                    statement.setString(2, triple.getPred());
                    statement.setString(3, triple.getObj());
                    statement.setString(4, triple.getType());
                    System.out.println("=>" + statement.toString());
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            conn.commit();
            statement.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询三元组
     *
     * @return
     */
    public static List<Triple> selectTriple() {
        List<Triple> tripleList = new ArrayList<>();
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = conn.prepareStatement("SELECT * FROM triple");
            System.out.println("=>" + statement.toString());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Triple triple = new Triple();
                triple.setSubj(result.getString("subj"));
                triple.setPred(result.getString("pred"));
                triple.setObj(result.getString("obj"));
                triple.setType(result.getString("type"));
                System.out.println("<=" + triple.toString());
                tripleList.add(triple);
            }
            statement.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return tripleList;
    }
}
