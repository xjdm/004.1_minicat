package com.idstaa.server;

import com.mysql.jdbc.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author chenjie
 * @date 2020/12/3 22:30
 * Minicat的主类
 */
public class Bootstrap {
    /**
     * 定义socket监听的端⼝号
     */
    private int port = 8080;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Minicat启动需要初始化展开的⼀些操作
     */
    public void start() throws Exception {
        /*
        完成Minicat 1.0版本
        需求：浏览器请求http://localhost:8080,返回⼀个固定的字符串到⻚
        ⾯"Hello Minicat!"
        */
       ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("========>minicat start on port" + port);

       /* while (true) {
            Socket socket = serverSocket.accept();
            OutputStream outputStream = socket.getOutputStream();
            String data = "Hello Minicat!";
            String responseText = HttpProtocolUtil.getHttpHeader200(data.getBytes().length) + data;
            outputStream.write(responseText.getBytes());
            socket.close();
        }*/

/*        while (true) {
            Socket socket = serverSocket.accept();
            InputStream inputStream = socket.getInputStream();

            // 封装Request对象和Response对象
            Request request = new Request(inputStream);
            Response response = new Response(socket.getOutputStream());
            response.outputHtml(request.getUrl());
            socket.close();
        }*/

        /**
         * mini 3.0
         */
        /*while (true) {
            // 加载解析相关的配置，web.xml
            loadServlet();
            Socket socket = serverSocket.accept();
            InputStream inputStream = socket.getInputStream();

            // 封装Request对象和Response对象
            Request request = new Request(inputStream);
            Response response = new Response(socket.getOutputStream());

            // 静态资源处理
            if (servletMap.get(request.getUrl()) == null) {
                response.outputHtml(request.getUrl());
            } else {
                HttpServlet httpServlet = servletMap.get(request.getUrl());
                httpServlet.service(request, response);
            }

            socket.close();
        }*/
        /**
         * 多线程改造（不使用线程池）
         */
/*
        while (true) {
            Socket socket = serverSocket.accept();
            RequestProcessor requestProcessor = new RequestProcessor(socket,servletMap);
            requestProcessor.start();
        }*/

        /**
         * 多线程改造（使用线程池）
         */
        // 加载解析相关的配置，web.xml
        loadServlet();
        // 定义一个线程池
        int corePoolSize = 10;
        int maximumPoolSize = 50;
        long keepAliveTime = 100L;
        TimeUnit unit = TimeUnit.SECONDS;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(50);
        ThreadFactory  threadFactory = Executors.defaultThreadFactory();
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                threadFactory,
                handler
                );

        while (true) {
            Socket socket = serverSocket.accept();
            Server server = new Server(serviceMap);
            RequestProcessor requestProcessor = new RequestProcessor(socket,server);
            threadPoolExecutor.execute(requestProcessor);
        }
    }

    private Map<String, HttpServlet> servletMap = new HashMap<>();
    private Map<String, Mapper> serviceMap = new HashMap<>();

    /**
     * 加载解析web.xml，初始化Servlet
     */
    private void loadServlet() {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("server.xml");
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();
            // 解析Server标签
            Element serverElement = (Element) rootElement.selectSingleNode("//Server");
            // 解析 server 下的 Service 标签
            List<Element> serviceNodes = serverElement.selectNodes("//Service");
            // 存储各个 Host
            Map<String, Host> hostMap = new HashMap<>(8);
            //遍历 service
            for (int i = 0; i < serviceNodes.size(); i++) {
                Element element = serviceNodes.get(i);
                //  <servlet-name>idstaa</servlet-name>
                String serviceName = element.attributeValue("name");
                Element connectorNode = (Element) element.selectSingleNode("//Connector");
                String connectorPort = connectorNode.attributeValue("//port");
                if(connectorPort!=null && !"".equals(connectorPort)){
                    port = Integer.parseInt(connectorPort);
                }
                Element engineNode = (Element) element.selectSingleNode("//Engine");
                List<Element> hostNodes = engineNode.selectNodes("//Host");
                // 存储有多少个项目
                Map<String, Context> contextMap = new HashMap<>(8);
                for (Element hostElement : hostNodes) {
                    String hostName = hostElement.attributeValue("name");
                    String appBase = hostElement.attributeValue("appBase");
                    File file = new File(appBase);
                    if (!file.exists() || file.list() == null) {
                        break;
                    }
                    String[] list = file.list();
                    //遍历子文件夹，即：实际的项目列表
                    for (String path : list) {
                        //将项目封装成 context，并保存入map
                        contextMap.put(path, loadContextServlet(appBase + "/" + path));
                    }
                    hostMap.put((hostName + ":" + port), new Host(contextMap));
                }
                serviceMap.put(serviceName, new Mapper(hostMap));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 加载实际项目里配置的 Servlet
     * @param path
     * @return
     */
    private Context  loadContextServlet(String path) throws Exception {
        String webPath = path + "/web.xml";
        if (!(new File(webPath).exists())) {
            System.out.println("not found " + webPath);
            return null;
        }
        InputStream resourceAsStream = new FileInputStream(webPath);
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();
            List<Element> selectNodes = rootElement.selectNodes("//servlet");
            for (int i = 0; i < selectNodes.size(); i++) {
                Element element = selectNodes.get(i);
                //  <servlet-name>idstaa</servlet-name>
                Element servletNameElement = (Element) element.selectSingleNode("servlet-name");
                String servletName = servletNameElement.getStringValue();
                //  <servlet-class>server.idstaaServlet</servlet-class>
                Element servletClassElement = (Element) element.selectSingleNode("servlet-class");
                String servletClass = servletClassElement.getStringValue();

                // 根据servlet-name的值找到url-pattern
                Element servletMapping = (Element) rootElement
                        .selectSingleNode("/web-app/servlet-mapping[servlet-name='" + servletName + "']");
                // /idstaa
                String urlPattern = servletMapping.selectSingleNode("url-pattern").getStringValue();
                // 自定义类加载器，来加载 webapps 目录下的 class
                WebClassLoader webClassLoader = new WebClassLoader();
                Class<?> aClass = webClassLoader.findClass(path, servletClass);
                servletMap.put(urlPattern, (HttpServlet) aClass.getDeclaredConstructor().newInstance());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Context(servletMap);
    }

    public static void main(String[] args) throws Exception {
        new Bootstrap().start();
    }

}
