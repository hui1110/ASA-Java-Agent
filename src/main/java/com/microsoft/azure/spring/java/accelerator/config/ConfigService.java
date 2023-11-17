package com.microsoft.azure.spring.java.accelerator.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.spring.java.accelerator.common.JaThreadFactory;
import com.microsoft.azure.spring.java.accelerator.model.DumpSetting;
import com.microsoft.azure.spring.java.accelerator.profiler.DumpJstack;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConfigService {

    private ScheduledExecutorService scheduledExecutorService;

    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    private final ObjectMapper objectMapper = new ObjectMapper();

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
            String url = "https://yonghui-apps-dev-agent-app.azuremicroservices.io/config";
            HttpPost httpPost = new HttpPost(url);
            List formParams = new ArrayList();
//            formParams.add(new BasicNameValuePair("appId", appId));
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(formParams, "UTF-8");
            httpPost.setEntity(urlEncodedFormEntity);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            String result = EntityUtils.toString(response.getEntity(), "UTF-8");
            DumpSetting dumpSetting = objectMapper.readValue(result, DumpSetting.class);
            System.out.println("pull result: " + dumpSetting);
            if(dumpSetting != null && dumpSetting.getLastModifiedDate() != null &&
                dumpSetting.getLastModifiedDate().getTime() != lastModifiedTimestamp) {
                this.lastModifiedTimestamp = dumpSetting.getLastModifiedDate().getTime();
                this.dumpSetting = dumpSetting;
                DumpJstack.dumpJStack();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            httpClient.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
