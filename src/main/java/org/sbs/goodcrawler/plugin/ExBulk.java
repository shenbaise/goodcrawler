/**
 * 
 */
package org.sbs.goodcrawler.plugin;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.node.stats.NodeStats;
import org.elasticsearch.action.admin.cluster.node.stats.NodesStatsResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;



/**
 * @author zhaoziqiang
 * 具有失败重发机制的Bulk，
 * 不针对no node avilable的错误导致的数据丢失。
 */
public class ExBulk {
	
	private Client client = null;
	// 失败的记录存放位置
	public static String fDir = "/bulk-failure";
	
	public long s = 0;
	public long f = 0;
	
	public ExBulk(){
		client = EsClient.getClient();
		File file = new File(fDir);
		if(!file.exists() || !file.isDirectory())
			file.mkdir();
	}
	
	/**
	 * 批量建索引
	 * @param index
	 * @param type
	 * @param data
	 */
	public void bulkIndex(String index, String type, List<Map<String, String>> data){
		
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		try {
			for(int i=0;i<data.size();i++){
				XContentBuilder xBuilder = jsonBuilder().startObject();
				    xBuilder.map(null);
				xBuilder.endObject();
				bulkRequest.add(client.prepareIndex(index, type).setSource(xBuilder));
				xBuilder.close();
			}
			
			bulkRequest.execute(new ActionListener<BulkResponse>() {
				
				@Override
				public void onResponse(BulkResponse arg0) {
					for(BulkItemResponse bir:arg0.getItems() ){
						if(bir.isFailed()){
							f++;
						}
						else {
							s++;
						}
					}
				}
				
				@Override
				public void onFailure(Throwable arg0) {
					System.out.println(arg0.getMessage());
				}
				
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
