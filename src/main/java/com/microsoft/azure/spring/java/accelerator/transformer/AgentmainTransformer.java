package com.microsoft.azure.spring.java.accelerator.transformer;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class AgentmainTransformer implements ClassFileTransformer {
    private static final String TARGET_CLASS_NAME = "com.example.demo.controller.HelloController";

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        try {
            className = className.replace("/", ".");
            if (TARGET_CLASS_NAME.equals(className)) {
                System.out.println("Find target class: " + className);

                ClassPool classPool = ClassPool.getDefault();
                classPool.appendClassPath(new javassist.LoaderClassPath(loader));
                classPool.appendSystemPath();
                System.out.println("append class path success");
                CtClass ctClass = classPool.get(className);

                CtMethod mainMethod = ctClass.getDeclaredMethod("agentmain");
                String bodyStr = "{\n" + "return \"agentmain method changed\";" + "}";
                mainMethod.setBody(bodyStr);
                ctClass.detach();
                System.out.println("Method content changes: " + mainMethod.getName());
                return ctClass.toBytecode();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
