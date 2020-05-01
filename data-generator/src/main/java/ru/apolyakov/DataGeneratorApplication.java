package ru.apolyakov;

import com.presidentio.testdatagenerator.cons.PropConst;
import com.presidentio.testdatagenerator.model.Output;
import com.presidentio.testdatagenerator.model.Schema;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StopWatch;
import ru.apolyakov.db.DataProvider;
import ru.apolyakov.generator.UserGenerator;

import java.io.IOException;
import java.util.logging.Logger;

@SpringBootApplication
public class DataGeneratorApplication {
    public static void main(String[] args) throws IOException {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(DataGeneratorApplication.class, args);
        UserGenerator userGenerator = applicationContext.getBean("userGenerator", UserGenerator.class);
        Logger logger = Logger.getGlobal();
        logger.info("start generating data...");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("generating data");
        Schema schemaFromResources = userGenerator.getSchemaFromResources();
        Output generated = userGenerator.generate(schemaFromResources);
        stopWatch.stop();
        logger.info("Finish generating data. Elapsed time: " + stopWatch.getLastTaskInfo().getTimeSeconds() + "s");
        stopWatch.start("executing sql script");
        DataProvider dataProvider = applicationContext.getBean("dataProvider", DataProvider.class);
        dataProvider.executeSqlFile(generated.getProps().get(PropConst.FILE));
        stopWatch.stop();
        logger.info("Finish executing SQL script. Elapsed time: " + stopWatch.getLastTaskInfo().getTimeSeconds() + "s");
        stopWatch.prettyPrint();
    }
}
