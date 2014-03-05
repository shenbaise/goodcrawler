package org.sbs.jetty;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Server;
import org.sbs.goodcrawler.bootstrap.BootStrap;
import org.sbs.goodcrawler.exception.ConfigurationException;

public class StartServer {

	public static final int PORT = 8080;
	public static final String CONTEXT = "/gc";
	private Log log = LogFactory.getLog(StartServer.class);

	public static void main(String[] args) throws Exception {
		
	}
	/**
	 * 启动爬虫
	 */
	public void startGC(){
		try {
			BootStrap.start();
		} catch (ConfigurationException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	/**
	 * 启动jetty服务
	 * @param port
	 * @param context
	 */
	public void startJetty(int port,String context){
		final Server server = JettyFactory.createServerInSource(PORT, CONTEXT);
		try {
			server.stop();
			server.start();
			log.info("Server running at http://localhost:" + PORT + CONTEXT);
			new Thread(new Runnable() {
				@Override
				public void run() {
					// 等待用户输入回车重载应用.
					log.info("Hit Enter to reload the application quickly");
					try {
						while (true) {
							char c = (char) System.in.read();
							if (c == '\n') {
								JettyFactory.reloadContext(server);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, "reload jetty server").start();
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
}