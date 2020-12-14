package com.idstaa.server;

import java.util.Map;

/**
 * @author chenjie
 * @date 2020/12/10 21:30
 */
public class Mapper {
    private Map<String, Host> hostMap;

    public Mapper(Map<String, Host> hostMap) {
        this.hostMap = hostMap;
    }

    public Map<String, Host> getHostMap() {
        return hostMap;
    }

    public void setHostMap(Map<String, Host> hostMap) {
        this.hostMap = hostMap;
    }
}
