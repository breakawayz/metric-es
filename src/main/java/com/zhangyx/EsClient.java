package com.zhangyx.esclient;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class EsClient {
    private static TransportClient client;

    static {
        try {
            //设置集群名称
            Settings settings = Settings.builder().put("cluster.name", "elasticsearch").build();
            //创建client
            client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("100.114.170.97"), 9300));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("@timestamp", new Date());
        map.put("helloName", "hello");

        sendDate(map);
    }

    public static void sendDate(Map<String, Object> data) {
        try {
            BulkRequestBuilder bulkRequest = client.prepareBulk();
            XContentBuilder jsonbuilder = jsonBuilder().startObject();
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                jsonbuilder.field(entry.getKey(), entry.getValue());
            }
            jsonbuilder.field("@timestamp", new Date());
            jsonbuilder.endObject();
            bulkRequest.add(
                    client.prepareIndex("james-metrics-" + getIndexDate("yyyy-MM"), "trace", null)
                            .setSource(jsonbuilder)
            );
            //插入数据
            BulkResponse bulkResponse = bulkRequest.get();
            //输出结果
            if (bulkResponse.hasFailures()) {
                System.out.println("插入失败");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getIndexDate(String format) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String dateString = formatter.format(date);
        return dateString;

    }

    public static void close() {
        //关闭client
        client.close();
    }
}
