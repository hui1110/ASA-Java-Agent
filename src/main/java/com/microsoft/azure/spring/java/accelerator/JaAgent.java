package com.microsoft.azure.spring.java.accelerator;

import com.microsoft.azure.spring.java.accelerator.context.ApplicationContext;
import com.microsoft.azure.spring.java.accelerator.transformer.AgentmainTransformer;
import com.microsoft.azure.spring.java.accelerator.transformer.PremainTransformer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.jar.JarFile;

public class JaAgent {

    private static final String AGENT_CONTROLLER = "com.example.demo.controller.HelloController";
    public static void agentmain(String agentArgs, Instrumentation inst) throws ClassNotFoundException, UnmodifiableClassException, IOException, URISyntaxException {
        System.out.println("agentmain called");
        File assistFile  = downloadAgentJar();
        inst.appendToBootstrapClassLoaderSearch(new JarFile(assistFile));
        inst.addTransformer(new AgentmainTransformer(), true);
        inst.retransformClasses(Class.forName(AGENT_CONTROLLER, false, ClassLoader.getSystemClassLoader()));
//        startAgent(inst);
    }

    public static void premain(String agentArgs, Instrumentation inst) throws IOException, URISyntaxException {
        System.out.println("premain called");
        File assistFile  = downloadAgentJar();
        inst.appendToBootstrapClassLoaderSearch(new JarFile(assistFile));
        inst.addTransformer(new PremainTransformer(), true);
//        startAgent(inst);
    }

    public static void startAgent(Instrumentation instrumentation) {
        new ApplicationContext().init();
    }

    private static File downloadAgentJar() throws IOException, URISyntaxException {
        String assistUrl = "https://repo1.maven.org/maven2/org/javassist/javassist/3.29.2-GA/javassist-3.29.2-GA.jar";
        String[] split = assistUrl.split("/");
        String fileName = split[split.length - 1];
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        HttpURLConnection connection = (HttpURLConnection) new URI(assistUrl).toURL().openConnection();
        connection.connect();
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = Files.newOutputStream(file.toPath())) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
        } finally {
            connection.disconnect();
        }
        System.out.println("---download agent jar success---");
        return file;
    }

}
