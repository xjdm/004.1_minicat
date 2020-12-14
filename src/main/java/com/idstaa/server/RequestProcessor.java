package com.idstaa.server;

import java.io.InputStream;
import java.net.Socket;
import java.util.Map;

/**
 * @author chenjie
 * @date 2020/12/8 17:46
 */
public class RequestProcessor extends Thread {
    private Socket socket;
    private Server server;
    private Map<String,HttpServlet> servletMap;

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public void run(){
        try{
            InputStream inputStream = socket.getInputStream();

            // 封装Request对象和Response对象
            Request request = new Request(inputStream);
            Response response = new Response(socket.getOutputStream());

            // 静态资源处理
            HttpServlet httpServlet = findHttpServlet(request);
            if (httpServlet==null) {
                response.outputHtml(request.getUrl());
            } else {
                // 动态资源Servlet请求
                // HttpServlet httpServlet = servletMap.get(request.getUrl());
                httpServlet.service(request, response);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public RequestProcessor(Socket socket, Map<String, HttpServlet> servletMap) {
        this.socket = socket;
        this.servletMap = servletMap;
    }

    public RequestProcessor() {
    }

    public RequestProcessor(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    /**
     * 根据请求信息找到对应业务 Servlet
     * @param request
     * @return
     */
    private HttpServlet findHttpServlet(Request request) {
        HttpServlet businessServlet = null;
        Map<String, Mapper> serviceMap = server.getServiceMap();
        for (String key : serviceMap.keySet()) {
            String hostName = request.getHost();
            Map<String, Host> hostMap = serviceMap.get(key).getHostMap();
            Host host = hostMap.get(hostName);
            if (host != null) {
                Map<String, Context> contextMap = host.getContextMap();
                // 处理 url
                // eg: web-greet/greet
                String url = request.getUrl();
                String[] urlPattern = url.split("/");
                String contextName = urlPattern[1];
                String servletStr = "/";
                if (urlPattern.length > 2) {
                    servletStr += urlPattern[2];
                }
                // 获取上下文
                Context context = contextMap.get(contextName);
                if (context != null) {
                    Map<String, HttpServlet> servletMap = context.getServletMap();
                    businessServlet = servletMap.get(servletStr);
                }
            }
        }
        return businessServlet;
    }
}
