package com.microsoft.azure.spring.java.accelerator;


import com.microsoft.azure.spring.java.accelerator.context.ApplicationContext;

import java.lang.instrument.Instrumentation;

public class JaAgent {

    public static void premain(String args, Instrumentation instrumentation){

        new ApplicationContext().init();

    }

}
