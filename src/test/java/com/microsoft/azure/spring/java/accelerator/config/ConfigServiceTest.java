//package com.microsoft.azure.spring.java.accelerator.config;
//
//import org.junit.jupiter.api.Test;
//
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class ConfigServiceTest {
//
//    @Test
//    public void testInit() {
//        ConfigService configService = new ConfigService();
//        configService.init();
//        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
//        long count = threadSet.stream().filter(thread -> thread.getName().contains("JA-service")).count();
//        assertEquals(1, count);
//    }
//
//}
