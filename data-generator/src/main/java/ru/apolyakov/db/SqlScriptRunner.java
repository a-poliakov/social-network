package ru.apolyakov.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Tool to run database scripts
 */
@Slf4j
public class SqlScriptRunner {

    private static final String DEFAULT_DELIMITER = ";";

    private JdbcTemplate jdbcTemplate;

    private boolean stopOnError;

    private String delimiter = DEFAULT_DELIMITER;
    private boolean fullLineDelimiter = false;

    private static AtomicLong proceedCount = new AtomicLong(0L);
    private static AtomicLong proceedWithError = new AtomicLong(0L);


    /**
     * Default constructor
     */
    public SqlScriptRunner(JdbcTemplate jdbcTemplate, boolean stopOnError) {
        this.jdbcTemplate = jdbcTemplate;
        this.stopOnError = stopOnError;
    }

    public void setDelimiter(String delimiter, boolean fullLineDelimiter) {
        this.delimiter = delimiter;
        this.fullLineDelimiter = fullLineDelimiter;
    }

    /**
     * Runs an SQL script (read in using the Reader parameter) using the
     * connection passed in
     *
     * @param reader - the source of the script
     * @throws IOException if there is an error reading from the Reader
     */
    public void runScript(Reader reader) throws IOException {
        proceedCount.set(0L);
        proceedWithError.set(0L);
        StringBuffer command = null;
        try {
            LineNumberReader lineReader = new LineNumberReader(reader);
            String line;
            while ((line = lineReader.readLine()) != null) {
                if (command == null) {
                    command = new StringBuffer();
                }
                String trimmedLine = line.trim();
                if (trimmedLine.startsWith("--") || trimmedLine.length() < 1 || trimmedLine.startsWith("//")) {
                } else if (!fullLineDelimiter
                        && trimmedLine.endsWith(getDelimiter())
                        || fullLineDelimiter
                        && trimmedLine.equals(getDelimiter())) {
                    command.append(line, 0, line
                            .lastIndexOf(getDelimiter()));
                    command.append(" ");

                    boolean hasResults = false;
                    if (stopOnError) {
                        jdbcTemplate.execute(command.toString());
                        //hasResults = statement.execute(command.toString());
                        proceedCount.incrementAndGet();
                        log.info("Proceed successful {}.", proceedCount.get());
                    } else {
                        try {
                            jdbcTemplate.execute(command.toString());
                            proceedCount.incrementAndGet();
                            log.info("Proceed successful {}.", proceedCount.get());
                        } catch (Exception e) {
                            e.fillInStackTrace();
                            log.error("Error executing: " + command);
                            log.error(e.toString());
                            proceedWithError.incrementAndGet();
                            log.error("Proceed  with errors {}.", proceedWithError.get());
                        }
                    }
                    command = new StringBuffer();
                } else {
                    command.append(line);
                    command.append(" ");
                }
            }
            log.info("Proceed successful {}/{} and with errors {}.", proceedCount.get(), proceedCount.get() + proceedWithError.get(), proceedWithError.get());
        } catch (Exception e) {
            log.info("Proceed successful {}/{} and with errors {}.", proceedCount.get(), proceedCount.get() + proceedWithError.get(), proceedWithError.get());
            e.fillInStackTrace();
            log.error("Error executing: " + command);
            log.error(e.toString());
            throw e;
        }
    }

    private String getDelimiter() {
        return delimiter;
    }
}
