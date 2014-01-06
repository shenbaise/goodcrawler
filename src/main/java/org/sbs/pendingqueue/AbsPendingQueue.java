/**
 * @工程 goodcrawler
 * @文件 IpendingQueue.java
 * @时间 2014年1月6日 下午4:40:17
 * @作者  赵自强（shenbaise1001@126.com）
 * @描述 
 */
package org.sbs.pendingqueue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sbs.goodcrawler.conf.GlobalConstants;
import org.sbs.goodcrawler.conf.PropertyConfigurationHelper;
import org.sbs.goodcrawler.exception.QueueException;

/**
 * @author shenbaise（shenbaise1001@126.com）
 * @desc
 */
public abstract class AbsPendingQueue<T> implements Serializable{
	private static final long serialVersionUID = 5705584299326194783L;
	
	private Log log = LogFactory.getLog(this.getClass());
	private BlockingQueue<T> Queue = null;
	/**
	 * 提交的元素个数
	 */
	private AtomicLong count = new AtomicLong(0L);
	/**
	 * 已经成功处理的元素个数
	 */
	private AtomicLong success = new AtomicLong(0L);
	/**
	 * 处理失败的元素个数
	 */
	private AtomicInteger failure = new AtomicInteger(0);
	/**
	 * 被忽略的元素个数
	 */
	private AtomicLong ignored = new AtomicLong(0);

	/**
	 * 构造函数
	 */
	protected AbsPendingQueue(String jobName) {
		init(jobName);
	}
	
	/**
	 * @desc 初始化队列
	 */
	@SuppressWarnings("unchecked")
	private void init(String jobName) {
		File file = new File(PropertyConfigurationHelper.getInstance()
				.getString("status.save.path", "status")
				+ File.separator + jobName + File.separator
				+ this.getClass().getSimpleName() + ".good");
		if (file.exists()) {
			try {
				FileInputStream fisUrl = new FileInputStream(file);
				ObjectInputStream oisUrl = new ObjectInputStream(fisUrl);
				AbsPendingQueue<T> queue = (AbsPendingQueue<T>) oisUrl.readObject();
				oisUrl.close();
				fisUrl.close();
				this.Queue = queue.Queue;
				this.failure = queue.failure;
				this.success = queue.success;
				this.count = queue.count;
				this.ignored = queue.ignored;
				log.info("recovery url queue..." + Queue.size());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} 
		if(null==Queue)
			Queue = new ArrayBlockingQueue<>(PropertyConfigurationHelper
					.getInstance().getInt(GlobalConstants.pendingUrlsQueueSize,
							1000000));
	}

	/**
	 * @param t
	 * @desc 加入一个待处理的元素，元素总数+1
	 */
	public void addElement(T t) throws QueueException {
		try {
			if (null != t) {
				Queue.put(t);
				count.getAndIncrement();
			}
		} catch (InterruptedException e) {
			log.error(e.getMessage());
			throw new QueueException("待处理元素队列加入操作中断");
		}
	}
	/**
	 * 加入一个待处理的元素，元素总数+1。超时将导致元素被丢弃。
	 * @param t
	 * @param timeout (MILLISECONDS)
	 * @return
	 * @throws QueueException
	 */
	public boolean addElement(T t,int timeout) throws QueueException {
		if (t != null) {
			try {
				boolean b = Queue.offer(t, timeout, TimeUnit.MILLISECONDS);
				if(b){
					count.getAndIncrement();
				}
				return b;
			} catch (InterruptedException e) {
				throw new QueueException("待处理元素加入操作中断");
			}
		}
		return false;
	}
	
	/**
	 * @return
	 * @desc 返回一个将要处理的URL
	 */
	public T getElementT() throws QueueException {
		try {
			return Queue.take();
		} catch (InterruptedException e) {
			log.error(e.getMessage());
			throw new QueueException("待处理元素队列取出操作中断");
		}
	}

	public boolean isEmpty() {
		return Queue.isEmpty();
	}

	/**
	 * @param c
	 * @return
	 * @desc 成功处理元素个数+c
	 */
	public long processedSuccess(long c) {
		return success.addAndGet(c);
	}

	/**
	 * @return
	 * @desc 成功处理元素个数+1
	 */
	public long processedSuccess() {
		return success.incrementAndGet();
	}

	/**
	 * @param c
	 * @return
	 * @desc 处理失败元素个数+c
	 */
	public int processedFailure(int c) {
		return failure.addAndGet(c);
	}

	/**
	 * @return
	 * @desc 处理失败元素个数+1
	 */
	public int processedFailure() {
		return failure.incrementAndGet();
	}

	/**
	 * @return
	 * @desc 返回接收到的总元素个数
	 */
	public long urlCount() {
		return count.get();
	}

	/**
	 * @return
	 * @desc 处理成功的元素个数
	 */
	public long success() {
		return success.get();
	}

	/**
	 * @return
	 * @desc 处理失败的元素个数
	 */
	public int failure() {
		return failure.get();
	}

	/**
	 * @param c
	 * @return
	 * @desc 被忽略元素数+c
	 */
	public long processedIgnored(long c) {
		return ignored.addAndGet(c);
	}

	/**
	 * @return
	 * @desc 被忽略元素数+1
	 */
	public long processedIgnored() {
		return ignored.incrementAndGet();
	}

	/**
	 * @return
	 * @desc 被忽略链接个数
	 */
	public long ignored() {
		return ignored.get();
	}
	
	/**
	 * 当前状态
	 * @return
	 */
	public String pendingStatus() {
		StringBuilder sb = new StringBuilder(32);
		sb.append("队列中等待处理的元素有").append(Queue.size()).append("个，")
				.append("截至目前共接收到").append(count).append("个元素。\n已成功处理")
				.append(success.get()).append("个，失败").append(failure.get())
				.append("个，忽略").append(ignored()).append("个");
		return sb.toString();
	}

	public AtomicLong getCount() {
		return count;
	}

	public void setCount(AtomicLong count) {
		this.count = count;
	}

	public AtomicLong getSuccess() {
		return success;
	}

	public void setSuccess(AtomicLong success) {
		this.success = success;
	}

	public AtomicInteger getFailure() {
		return failure;
	}

	public void setFailure(AtomicInteger failure) {
		this.failure = failure;
	}

	public AtomicLong getIgnored() {
		return ignored;
	}

	public void setIgnored(AtomicLong ignored) {
		this.ignored = ignored;
	}
	
}
