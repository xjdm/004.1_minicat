package com.idstaa.server;

/**
 * @author chenjie
 * @date 2020/12/8 13:54
 */
public interface Servlet {
    void init() throws Exception;

    void destory() throws Exception;

    void service(Request request, Response response) throws Exception;
}
