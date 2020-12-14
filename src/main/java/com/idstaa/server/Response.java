package com.idstaa.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author chenjie
 * @date 2020/12/8 13:17
 * 封装Response对象，需要依赖OutputStream
 * 该对象需要提供核心方法，输出html
 */
public class Response {
    private OutputStream outputStream;

    public Response(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    // 输出指定字符串
    public void output(String content) throws IOException {
        outputStream.write(content.getBytes());
    }

    public Response() {
    }
    public void outputHtml(String path) throws IOException {
        String absoluteResourcePath = StaticResourceUtil.getAbsolutePath(path);
        File file = new File(absoluteResourcePath);
        if(file.exists() && file.isFile()){
            // 读取静态资源文件输出静态资源
            StaticResourceUtil.outputStaticResource(new FileInputStream(file),outputStream);
        } else {
            // 输出404
            output(HttpProtocolUtil.getHttpHeader404());
        }
    }
}
