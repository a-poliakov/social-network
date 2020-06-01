package ru.apolyakov.db;

import org.springframework.jdbc.core.JdbcTemplate;
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
    private SqlScriptRunner sqlScriptRunner; // jdbc:mysql://127.0.0.1:3306/social_network

    public DataProvider(JdbcTemplate jdbcTemplate) {
        this.sqlScriptRunner = new SqlScriptRunner(jdbcTemplate, false);
    }

    public void executeSqlInputStream(InputStream inputStream) throws IOException {
        try {
            //SqlScriptRunner sqlScriptRunner = new SqlScriptRunner(connection, true, true);
            sqlScriptRunner.runScript(new InputStreamReader(inputStream));
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public void executeSqlFile(String file) throws IOException {
        try {
            //SqlScriptRunner sqlScriptRunner = new SqlScriptRunner(connection, true, true);
            sqlScriptRunner.runScript(new FileReader(file));
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
