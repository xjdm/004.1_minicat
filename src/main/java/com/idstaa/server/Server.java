package com.idstaa.server;

import java.util.Map;

/**
 * @author chenjie
 * @date 2020/12/10 21:30
 */
public class Server {
    // Server 里的 Service
    private Map<String, Mapper> serviceMap;

    public Server(Map<String, Mapper> serviceMap) {
        this.serviceMap = serviceMap;
    }

    public Map<String, Mapper> getServiceMap() {
        return serviceMap;
    }

    public void setServiceMap(Map<String, Mapper> serviceMap) {
        this.serviceMap = serviceMap;
    }
}
