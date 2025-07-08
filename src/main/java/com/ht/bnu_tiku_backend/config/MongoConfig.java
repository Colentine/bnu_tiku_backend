//package com.ht.bnu_tiku_backend.config;
//
//import com.mongodb.client.MongoClient;
//import com.mongodb.client.MongoClients;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.mapping.model.FieldNamingStrategy;
//import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//@Configuration
//@EnableTransactionManagement
//public class MongoConfig extends AbstractMongoClientConfiguration {
//
//    @Value("${spring.data.mongodb.uri}")
//    private String mongoUri;
//
//    @Override
//    protected String getDatabaseName() {
//        String[] uriSplit = mongoUri.split("/");
//        String[] split = uriSplit[uriSplit.length - 1].split("\\?");
//        return split[0];
//    }
//
//    @Bean
//    public MongoClient mongoClient() {
//        return MongoClients.create("mongodb+srv://huangtaomai:ko2348218@cluster0.p7c0rzx.mongodb.net?retryWrites=true&w=majority");
//    }
//
//    @Bean
//    public MongoTemplate mongoTemplate() {
//        return new MongoTemplate(mongoClient(), "bnu_tiku");
//    }
//
////    public String getDatabaseName1() {
////        String[] uriSplit = mongoUri.split("/");
////        String[] split = uriSplit[uriSplit.length - 1].split("\\?");
////        return split[0];
////    }
//
//    @Override
//    public FieldNamingStrategy fieldNamingStrategy() {
//        return new SnakeCaseFieldNamingStrategy();
//    }
//}