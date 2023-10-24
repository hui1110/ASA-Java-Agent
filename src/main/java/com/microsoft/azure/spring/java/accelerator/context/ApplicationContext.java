package com.microsoft.azure.spring.java.accelerator.context;

import com.microsoft.azure.spring.java.accelerator.config.ConfigService;
import com.microsoft.azure.spring.java.accelerator.profiler.Profiler;

public class ApplicationContext {

    private final ConfigService configService;
    private final Profiler profiler;

    public ApplicationContext() {
        configService = new ConfigService();
        profiler = new Profiler();
    }

    public void init() {
        configService.init();
        profiler.init();
    }

}
