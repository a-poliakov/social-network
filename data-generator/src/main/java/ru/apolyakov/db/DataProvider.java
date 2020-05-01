package ru.apolyakov.db;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class DataProvider {
    private SqlScriptRunner sqlScriptRunner;

    @PostConstruct
    private Connection prepareConnection() throws SQLException {
        try {
            //Class.forName("org.hsqldb.jdbcDriver");
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
        //Connection conn = DriverManager.getConnection("jdbc:hsqldb:mem:test_data_generator;shutdown=true", "root", "example");
        Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/social_network", "root", "example");
        sqlScriptRunner = new SqlScriptRunner(conn, true, true);
//        sqlScriptRunner.runScript(new InputStreamReader(DataProvider.class.getClassLoader()
//                .getResourceAsStream(getDbSchemaResource())));
        return conn;
    }

    public void executeSqlInputStream(InputStream inputStream) throws IOException {
        try {
            //SqlScriptRunner sqlScriptRunner = new SqlScriptRunner(connection, true, true);
            sqlScriptRunner.runScript(new InputStreamReader(inputStream));
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    public void executeSqlFile(String file) throws IOException {
        try {
            //SqlScriptRunner sqlScriptRunner = new SqlScriptRunner(connection, true, true);
            sqlScriptRunner.runScript(new FileReader(file));
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }
}
