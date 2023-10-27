package com.microsoft.azure.spring.java.accelerator.config;

import com.microsoft.azure.spring.java.accelerator.common.JaThreadFactory;
import com.microsoft.azure.spring.java.accelerator.model.DumpRequest;
import com.microsoft.azure.spring.java.accelerator.model.DumpSetting;
import com.microsoft.azure.spring.java.accelerator.profiler.DumpJstack;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConfigService {

    private ScheduledExecutorService scheduledExecutorService;

    private final RestTemplate restTemplate = new RestTemplate();

    private volatile DumpSetting dumpSetting;

    private volatile long lastModifiedTimestamp;


    public void init() {
        scheduledExecutorService = Executors.newScheduledThreadPool(
            1, new JaThreadFactory("JA-service", true));
        startPollingConfig();
    }

    private void startPollingConfig() {
        scheduledExecutorService.scheduleAtFixedRate(
                this::pullConfig,
            5,
            10, // code hard for current
            TimeUnit.SECONDS);
    }

    private void pullConfig() {
        try {
            System.out.println("start to pull");
            HttpEntity<DumpRequest> request = new HttpEntity<DumpRequest>(new DumpRequest());
            DumpSetting dumpSetting = restTemplate.postForObject("http://localhost:8080/config", request, DumpSetting.class);
            System.out.println("pull result: " + dumpSetting);
            if(dumpSetting != null && dumpSetting.getLastModifiedDate() != null &&
                dumpSetting.getLastModifiedDate().getTime() != lastModifiedTimestamp) {
                System.out.println("dumpSetting: " + dumpSetting.toString());
                this.lastModifiedTimestamp = dumpSetting.getLastModifiedDate().getTime();
                this.dumpSetting = dumpSetting;
                DumpJstack.dumpJStack();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
