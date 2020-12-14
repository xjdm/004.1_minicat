package com.idstaa.server;

import java.util.Map;

/**
 * @author chenjie
 * @date 2020/12/10 21:29
 */
public class Host {
    private Map<String, Context> contextMap;

    public Host(Map<String, Context> contextMap) {
        this.contextMap = contextMap;
    }

    public Map<String, Context> getContextMap() {
        return contextMap;
    }

    public void setContextMap(Map<String, Context> contextMap) {
        this.contextMap = contextMap;
    }
}
