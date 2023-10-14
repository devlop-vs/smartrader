package com.smartrader;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.smartrader.events.SymbolEventListener;
import com.smartrader.events.SymbolEventSource;



public class SmartMain
{
    public static void main(String[] args)
    {
        ConfigReader.load();
         //String message = "2023.10.14 02:56:34,EURUSD=1.05061,GBPUSD=1.21362";
         //fireEvent(message);
         //if(message.length()>0) return;
         // 连接到 MongoDB 服务器
        MongoClient mongoClient = MongoClients.create(ConfigReader.dbUrl);

        // 选择数据库
        MongoDatabase database = mongoClient.getDatabase("mt4");
       
        try {
            ZContext context = new ZContext();
            // Socket to talk to clients
            ZMQ.Socket socket = context.createSocket(SocketType.REP);
            //socket.bind("tcp://10.211.55.2:9999");
            socket.bind(ConfigReader.zmqAddress);

            while (!Thread.currentThread().isInterrupted()) {
                // Block until a message is received
                byte[] reply = socket.recv(0);

                // Print the message
                System.out.println(
                        "Received: [" + new String(reply, ZMQ.CHARSET) + "]"
                );

                // fire event
                fireEvent(new String(reply,ZMQ.CHARSET),database);

                // Send a response
                String response = "Hello, world!";
                socket.send(response.getBytes(ZMQ.CHARSET), 0);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        // 关闭 MongoDB 连接
        mongoClient.close();
    }
    public static void fireEvent(String message,MongoDatabase database){
        int postime = message.indexOf(",", 0);
        if(postime <= 0){
            return;
        }
        String sDateTime = message.substring(0, postime);
        String sMsgSymbols = message.substring(postime+1);
        
        // 创建事件触发器
        SymbolEventSource eventSource = new SymbolEventSource();

        int posIdx =0;
        while((posIdx = sMsgSymbols.indexOf(","))>0){
            String symbol = sMsgSymbols.substring(0, posIdx);
            sMsgSymbols = sMsgSymbols.substring(posIdx+1);
            int posequal = symbol.indexOf("=");

            String symbolname = symbol.substring(0, posequal);
            String symbolprice = symbol.substring(posequal+1);

            // 创建事件监听器
            SymbolEventListener listener1 = new SymbolEventListener(sDateTime,symbolname,symbolprice);
            listener1.setMongodb(database);//设置数据库连接

            // 注册事件监听器
            eventSource.addListener(listener1);
        }
        

        if(sMsgSymbols.length()>0){
            String symbol = sMsgSymbols;
            int posequal = symbol.indexOf("=");

            String symbolname = symbol.substring(0, posequal);
            String symbolprice = symbol.substring(posequal+1);

            // 创建事件监听器
            SymbolEventListener listener2 = new SymbolEventListener(sDateTime,symbolname,symbolprice);
            listener2.setMongodb(database);//设置数据库连接
            eventSource.addListener(listener2);
        }
        

        // 触发事件
        eventSource.fireEvent("analysized:");       
        
    }
}