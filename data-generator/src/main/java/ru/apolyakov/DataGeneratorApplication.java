package ru.apolyakov;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.EventListener;
import org.springframework.util.StopWatch;
import ru.apolyakov.db.DataProvider;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

@SpringBootApplication
@Slf4j
public class DataGeneratorApplication  {
    @Autowired
    private DataProvider dataProvider;

    public static void main(String[] args) throws IOException, SQLException {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(DataGeneratorApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            //        UserGenerator userGenerator = applicationContext.getBean("userGenerator", UserGenerator.class);
            Logger logger = Logger.getGlobal();
//        logger.info("start generating data...");
            StopWatch stopWatch = new StopWatch();
//        stopWatch.start("generating data");
//        Schema schemaFromResources = userGenerator.getSchemaFromResources();
//        Output generated = userGenerator.generate(schemaFromResources);
//        logger.info("Generated info: " + generated.getProps().toString());
//        stopWatch.stop();
//        logger.info("Finish generating data. Elapsed time: " + stopWatch.getLastTaskInfo().getTimeSeconds() + "s");
            stopWatch.start("executing sql script");
//        DataProvider dataProvider = applicationContext.getBean("dataProvider", DataProvider.class);
//        dataProvider.prepareConnection();
//        dataProvider.executeSqlFile(generated.getProps().get(PropConst.FILE));
            dataProvider.executeSqlFile("C:\\Users\\avpge\\Documents\\a.sql");
            stopWatch.stop();
            log.info("Finish executing SQL script. Elapsed time: " + stopWatch.getLastTaskInfo().getTimeSeconds() + "s");
            log.info(stopWatch.prettyPrint());
        };
    }
}
