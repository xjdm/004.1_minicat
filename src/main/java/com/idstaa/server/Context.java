package com.idstaa.server;

import java.util.Map;

/**
 * @author chenjie
 * @date 2020/12/10 21:29
 */
public class Context {
    // Context 中的 Servlet
    private Map<String, HttpServlet> servletMap;

    public Context(Map<String, HttpServlet> servletMap) {
        this.servletMap = servletMap;
    }

    public Map<String, HttpServlet> getServletMap() {
        return servletMap;
    }

    public void setServletMap(Map<String, HttpServlet> servletMap) {
        this.servletMap = servletMap;
    }
}
