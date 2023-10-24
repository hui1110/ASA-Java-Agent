package com.microsoft.azure.spring.java.accelerator.common;

import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class JaThreadFactory implements ThreadFactory {

    public static final String DEFAULT_THREAD_NAME_PREFIX = "Azure-Java-Accelerator-";

    private final static AtomicInteger FACTORY_NUMBER = new AtomicInteger(0);
    private final AtomicInteger threadNumber = new AtomicInteger(0);

    private final String threadPrefix;
    private final boolean daemon;

    public JaThreadFactory() {
        this("Azure-Java-Accelerator", false);
    }

    public JaThreadFactory(String threadName) {
        this(threadName, false);
    }

    public JaThreadFactory(String threadName, boolean daemon) {
        Objects.requireNonNull(threadName, "threadName");

        this.threadPrefix = prefix(threadName, FACTORY_NUMBER.getAndIncrement());
        this.daemon = daemon;
    }

    private String prefix(String threadName, int factoryId) {
        final StringBuilder buffer = new StringBuilder(32);
        buffer.append(threadName);
        buffer.append('(');
        buffer.append(factoryId);
        buffer.append('-');
        return buffer.toString();
    }

    @Override
    public Thread newThread(Runnable job) {
        String newThreadName = createThreadName();
        Thread thread = new Thread(job, newThreadName);
        if (daemon) {
            thread.setDaemon(true);
        }
        return thread;
    }

    private String createThreadName() {
        StringBuilder buffer = new StringBuilder(threadPrefix.length() + 8);
        buffer.append(threadPrefix);
        buffer.append(threadNumber.getAndIncrement());
        buffer.append(')');
        return buffer.toString();
    }

    public static JaThreadFactory createThreadFactory(String threadName) {
        return createThreadFactory(threadName, false);
    }

    public static JaThreadFactory createThreadFactory(String threadName, boolean daemon) {
        return new JaThreadFactory(threadName, daemon);
    }
}
