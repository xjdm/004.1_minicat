package com.idstaa.server;

import java.io.*;

/**
 * @author chenjie
 * @date 2020/12/10 21:39
 */
public class WebClassLoader extends ClassLoader  {
    @Override
    protected Class<?> findClass(String basePath, String className) {
        byte[] classBytes = getClassBytes(basePath, className);
        return defineClass(className, classBytes, 0, classBytes.length);
    }

    /**
     * 读取类的字节码
     *
     * @param basePath  根路径
     * @param className 类的全限定名
     * @return servlet 的字节码信息
     * @throws IOException
     */
    private byte[] getClassBytes(String basePath, String className) {
        InputStream in = null;
        ByteArrayOutputStream out = null;
        String path = basePath + File.separatorChar +
                className.replace('.', File.separatorChar) + ".class";
        try {
            in = new FileInputStream(path);
            out = new ByteArrayOutputStream();
            byte[] buffer = new byte[2048];
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
