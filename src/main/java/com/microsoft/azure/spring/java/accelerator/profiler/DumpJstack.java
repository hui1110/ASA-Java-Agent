package com.microsoft.azure.spring.java.accelerator.profiler;

import com.microsoft.azure.spring.java.accelerator.common.JaThreadFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class DumpJstack {

    private static final String JSTACKT_PATH = "/tmp/jstack";

//    private static volatile long lastPrintTime = 0L;
//    private static Semaphore guard = new Semaphore(1);

    private static final ScheduledExecutorService executorService =
        new ScheduledThreadPoolExecutor(1, new JaThreadFactory("azure.ja.agent.dump.jstack", true));


    public static void dumpJStack() {
        SimpleDateFormat sdf;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        } else {
            sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        }
        String dateStr = sdf.format(new Date());
        FileOutputStream jstackStream = null;
        try {
            File stackFile = new File(DumpJstack.JSTACKT_PATH, "JA_JStack.log." + dateStr);
            File fileParent = stackFile.getParentFile();
            if (!fileParent.exists())
                fileParent.mkdirs();
            if (!stackFile.exists())
                try {
                    stackFile.createNewFile();
                } catch (IOException e) {
                    // log error
                    System.err.println("writePortFile path:" + DumpJstack.JSTACKT_PATH + " error:" + e.getMessage());
                }
            jstackStream = new FileOutputStream(stackFile);
            DumpJstack.jstack(jstackStream);
            System.out.println("dump jstack success");
        } catch (Throwable t) {
            System.err.println("dump jstack error: " + t.getMessage());
        } finally {
            if (jstackStream != null)
                try {
                    jstackStream.flush();
                    jstackStream.close();
                } catch (IOException iOException) {}
        }
    }

//    public static void dumpJStack() {
//        long now = System.currentTimeMillis();
//        if (now - lastPrintTime < 600000L)
//            return;
//        if (!guard.tryAcquire())
//            return;
//        try {
//            executorService.execute(new Runnable() {
//                public void run() {
//                    SimpleDateFormat sdf;
//                    String os = System.getProperty("os.name").toLowerCase();
//                    if (os.contains("win")) {
//                        sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
//                    } else {
//                        sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
//                    }
//                    String dateStr = sdf.format(new Date());
//                    FileOutputStream jstackStream = null;
//                    try {
//                        File stackFile = new File(DumpJstack.JSTACKT_PATH, "JA_JStack.log." + dateStr);
//                        File fileParent = stackFile.getParentFile();
//                        if (!fileParent.exists())
//                            fileParent.mkdirs();
//                        if (!stackFile.exists())
//                            try {
//                                stackFile.createNewFile();
//                            } catch (IOException e) {
//                                // log error
//                                System.err.println("writePortFile path:" + DumpJstack.JSTACKT_PATH + " error:" + e.getMessage());
//                            }
//                        jstackStream = new FileOutputStream(stackFile);
//                        DumpJstack.jstack(jstackStream);
//                    } catch (Throwable t) {
//                        System.err.println("dump jstack error: " + t.getMessage());
//                    } finally {
//                        DumpJstack.guard.release();
//                        if (jstackStream != null)
//                            try {
//                                jstackStream.flush();
//                                jstackStream.close();
//                            } catch (IOException iOException) {}
//                    }
//                    DumpJstack.lastPrintTime = System.currentTimeMillis();
//                }
//            });
//        } catch (Exception e) {
//            System.err.println("execute dumpJStack task fail, exception:" + e.getMessage());
//        }
//    }

    public static void jstack(OutputStream stream) throws Exception {
        ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
        for (ThreadInfo threadInfo : threadMxBean.dumpAllThreads(true, true))
            stream.write(getThreadDumpString(threadInfo).getBytes());
    }

    private static String getThreadDumpString(ThreadInfo threadInfo) {
        StringBuilder sb = new StringBuilder("\"" + threadInfo.getThreadName() + "\"" + " Id=" + threadInfo.getThreadId() + " " + threadInfo.getThreadState());
        if (threadInfo.getLockName() != null)
            sb.append(" on " + threadInfo.getLockName());
        if (threadInfo.getLockOwnerName() != null)
            sb.append(" owned by \"" + threadInfo.getLockOwnerName() + "\" Id=" + threadInfo.getLockOwnerId());
        if (threadInfo.isSuspended())
            sb.append(" (suspended)");
        if (threadInfo.isInNative())
            sb.append(" (in native)");
        sb.append('\n');
        int i = 0;
        StackTraceElement[] stackTrace = threadInfo.getStackTrace();
        MonitorInfo[] lockedMonitors = threadInfo.getLockedMonitors();
        for (; i < stackTrace.length && i < 32; i++) {
            StackTraceElement ste = stackTrace[i];
            sb.append("\tat " + ste.toString());
            sb.append('\n');
            if (i == 0 && threadInfo.getLockInfo() != null) {
                Thread.State ts = threadInfo.getThreadState();
                switch (ts) {
                    case BLOCKED:
                        sb.append("\t-  blocked on " + threadInfo.getLockInfo());
                        sb.append('\n');
                        break;
                    case WAITING:
                        sb.append("\t-  waiting on " + threadInfo.getLockInfo());
                        sb.append('\n');
                        break;
                    case TIMED_WAITING:
                        sb.append("\t-  waiting on " + threadInfo.getLockInfo());
                        sb.append('\n');
                        break;
                }
            }
            for (MonitorInfo mi : lockedMonitors) {
                if (mi.getLockedStackDepth() == i) {
                    sb.append("\t-  locked " + mi);
                    sb.append('\n');
                }
            }
        }
        if (i < stackTrace.length) {
            sb.append("\t...");
            sb.append('\n');
        }
        LockInfo[] locks = threadInfo.getLockedSynchronizers();
        if (locks.length > 0) {
            sb.append("\n\tNumber of locked synchronizers = " + locks.length);
            sb.append('\n');
            for (LockInfo li : locks) {
                sb.append("\t- " + li);
                sb.append('\n');
            }
        }
        sb.append('\n');
        return sb.toString();
    }

}
