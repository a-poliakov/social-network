package ru.apolyakov.generator;

import com.presidentio.testdatagenerator.*;
import com.presidentio.testdatagenerator.model.Output;
import com.presidentio.testdatagenerator.model.Schema;
import com.presidentio.testdatagenerator.parser.JsonSchemaSerializer;
import com.presidentio.testdatagenerator.parser.SchemaBuilder;
import com.presidentio.testdatagenerator.parser.SchemaSerializer;
import com.presidentio.testdatagenerator.provider.CompositeValueProviderFactory;
import com.presidentio.testdatagenerator.provider.DefaultValueProviderFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.apolyakov.generator.providers.PasswordValueProviderFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class UserGenerator {
    private final Generator generator;

    public UserGenerator(PasswordValueProviderFactory passwordValueProviderFactory) {
        generator = new Generator();
        generator.setAsync(true);
        generator.setValueProviderFactory(DefaultValueProviderFactory.defaultProvider().extend(passwordValueProviderFactory));
    }

    protected List<String> getSchemaResource() {
        return Collections.singletonList("schema.json");
    }


    public Schema getSchemaFromResources() {
        SchemaBuilder schemaBuilder = new SchemaBuilder();
        for (String resource : getSchemaResource()) {
            schemaBuilder.fromResource(resource);
        }
        return schemaBuilder.build();
    }

    public Schema getSchema(InputStream inputStream) throws IOException {
        SchemaSerializer schemaSerializer = new JsonSchemaSerializer();
        return schemaSerializer.deserialize(Starter.class.getClassLoader().getResourceAsStream("schema.json"));
    }


    public Output generate(Schema schema) {
        generator.generate(schema);
        return schema.getOutput();
    }

}
