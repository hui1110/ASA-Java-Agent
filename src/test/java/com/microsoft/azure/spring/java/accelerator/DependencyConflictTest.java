//package com.microsoft.azure.spring.java.accelerator;
//
//import org.junit.jupiter.api.Test;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//public class DependencyConflictTest {
//
//    @Test
//    void testJaAgentDependencyConflict()  {
//        try {
//            String AGENT_JAR_PATH = "C:\\item\\ASA-Java-Agent\\target\\java-accelerator-agent-0.0.1-SNAPSHOT.jar";
//            String APPLICATION_JAR_PATH = "C:\\item\\Java-ACA-Demo\\target\\Java-ACA-Demo-0.0.1-SNAPSHOT.jar";
//            Process process = Runtime.getRuntime().exec("java -javaagent:" + AGENT_JAR_PATH + " -jar " + APPLICATION_JAR_PATH);
//            Thread.sleep(2000);
//            readProcessOutput(process);
//            assertTrue(process.isAlive());
//            process.destroy();
//        } catch (IOException | InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private static void readProcessOutput(final Process process) {
//        InputStream inputStream = process.getErrorStream();
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
//            String line;
//            while ((line = reader.readLine()) != null && !process.isAlive()) {
//                System.err.println(line);
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//}
