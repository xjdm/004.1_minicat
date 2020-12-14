package com.idstaa.server;

import java.io.IOException;

/**
 * @author chenjie
 * @date 2020/12/8 13:58
 */
public class IndstaaServlet extends HttpServlet {
    @Override
    public void doGet(Request request, Response response) {
        String content = "<h1>idstaaServlet  Get</h1>";
        try {
            response.output((HttpProtocolUtil.getHttpHeader200(content.getBytes().length)+content));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void doPost(Request request, Response response) {
        String content = "<h1>idstaaServlet  Post</h1>";
        try {
            response.output((HttpProtocolUtil.getHttpHeader200(content.getBytes().length)+content));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() throws Exception {

    }

    @Override
    public void destory() throws Exception {

    }
}
