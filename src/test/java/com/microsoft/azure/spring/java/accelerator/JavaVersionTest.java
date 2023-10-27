package com.microsoft.azure.spring.java.accelerator;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.JRE;
import org.mockito.Mockito;

import java.lang.instrument.Instrumentation;

import static org.mockito.Mockito.verify;

class JavaVersionTest {

    private final Instrumentation mockInstrumentation = Mockito.mock(Instrumentation.class);
    private final String mockArgs = "mockArgs";

    @Test
    @EnabledOnJre(JRE.JAVA_8)
    @Order(1)
    void testJaAgentPremainMethodWithJava8() throws InterruptedException {
        JaAgent.premain(mockArgs, mockInstrumentation);
        Thread.sleep(5000);
    }

    @Test
    @EnabledOnJre(JRE.JAVA_11)
    @Order(2)
    void testJaAgentPremainMethodWithJava11() throws InterruptedException {
        JaAgent.premain(mockArgs, mockInstrumentation);
        Thread.sleep(5000);
    }

    @Test
    @EnabledOnJre(JRE.JAVA_17)
    @Order(3)
    void testJaAgentPremainMethodWithJava17() throws InterruptedException {
        JaAgent.premain(mockArgs, mockInstrumentation);
        Thread.sleep(5000);
    }

}
