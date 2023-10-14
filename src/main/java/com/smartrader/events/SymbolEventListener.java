package com.smartrader.events;


import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
// 3. 创建一个事件监听器类
public class SymbolEventListener implements EventListener{
    
    private String symbolname; // 币种名称
    private String sDateTime;
    private String price;
    private MongoDatabase mdb = null;
    

    // 构造函数，设置币种名称
    public  SymbolEventListener(String sDateTime,String symbolname,String price){
        this.symbolname = symbolname;
        this.sDateTime = sDateTime;
        this.price = price;
    }
    @Override
    public void onEvent(String message) {
        System.out.println("Event received: " + message + "#" +this.sDateTime+"#"+this.symbolname+"#"+this.price);
        if(this.mdb != null) {
            MongoCollection<Document> collection = this.mdb.getCollection(symbolname + "_10S");

            // 转化报价的时间节点
            SimpleDateFormat sdf = new SimpleDateFormat( " yyyy-MM-dd HH:mm:ss " ); 
            Date dt;
            try {
                dt = sdf.parse(this.sDateTime);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                dt = new Date();
            }
            
            // 插入文档
            Document document = new Document("datetime", this.sDateTime)
                    .append("timestamp",dt.getTime())
                    .append("bid", this.price);
                    
            collection.insertOne(document);
        }
    }
    public void setMongodb(MongoDatabase mdb){
        this.mdb = mdb;
    }
}
