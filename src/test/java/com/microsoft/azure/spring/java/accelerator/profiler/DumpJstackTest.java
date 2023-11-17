//package com.microsoft.azure.spring.java.accelerator.profiler;
//
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//
//import java.io.FileOutputStream;
//import java.lang.management.ThreadInfo;
//
//public class DumpJstackTest {
//
//    private final ThreadInfo threadInfo = Mockito.mock(ThreadInfo.class);
//    private final FileOutputStream fileOutputStream = Mockito.mock(FileOutputStream.class);
//
//    @Test
//    void getThreadDumpStringTest() throws Exception {
//        Mockito.when(threadInfo.getThreadName()).thenReturn("testThread");
//        Mockito.when(threadInfo.getThreadState()).thenReturn(Thread.State.RUNNABLE);
//        Mockito.when(threadInfo.getStackTrace()).thenReturn(new StackTraceElement[0]);
//        DumpJstack.jstack(fileOutputStream);
//        DumpJstack.dumpJStack();
//    }
//}
