//package com.hoaithidev.vidsonet_backend.config;
//
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.databind.DeserializationContext;
//import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
//import org.springframework.context.annotation.Configuration;
//
//import java.io.IOException;
//
//@Configuration
//public class BooleanDeserializer extends StdDeserializer<Boolean> {
//    public BooleanDeserializer() {
//        super(Boolean.class);
//    }
//    @Override
//    public Boolean deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
//        String value = jsonParser.getText();
//        return "true".equalsIgnoreCase(value);
//    }
//}
