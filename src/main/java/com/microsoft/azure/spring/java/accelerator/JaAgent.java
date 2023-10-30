package com.microsoft.azure.spring.java.accelerator;


import com.microsoft.azure.spring.java.accelerator.context.ApplicationContext;

import java.lang.instrument.Instrumentation;

public class JaAgent {

    public static void agentmain(String agentArgs, Instrumentation inst) {
        startAgent(inst);
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        startAgent(inst);
    }

    public static void startAgent(Instrumentation instrumentation) {
        new ApplicationContext().init();
    }

}
