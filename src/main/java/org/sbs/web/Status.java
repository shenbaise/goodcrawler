package org.sbs.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sbs.goodcrawler.bootstrap.BootStrap;
import org.sbs.goodcrawler.bootstrap.CrawlerStatus;
import org.sbs.goodcrawler.bootstrap.JobStops;
import org.sbs.goodcrawler.jobconf.Job;
import org.sbs.goodcrawler.queue.PendingExtract;
import org.sbs.goodcrawler.queue.PendingFetch;
import org.sbs.goodcrawler.queue.PendingStore;
import org.sbs.web.domain.JobStatus;

import com.google.common.collect.Lists;

/**
 * Servlet implementation class Status
 */
@WebServlet("/status")
public class Status extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Status() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Status");
		
		if(CrawlerStatus.running){
			List<JobStatus> jobStatus = Lists.newArrayList();
			for(Job job:BootStrap.getJobs()){
				JobStatus js = new JobStatus();
				js.setJobName(job.getJobName());
				js.setFetchStatus(PendingFetch.getPendingFetch(job.getJobName()).pendingStatus());
				js.setExtractStatus(PendingExtract.getPendingExtract(job.getJobName()).pendingStatus());
				js.setStoreStatus(PendingStore.getPendingStore(job.getJobName()).pendingStatus());
				js.setRunning(!JobStops.isStop(job.getJobName()));
			}
			request.setAttribute("status", jobStatus);
			request.setAttribute("start", "程序正在运行中。。。");
		}else {
			request.setAttribute("stop", "程序停止运行。。。");
		}
//		response.sendRedirect("index.jsp");
		request.getRequestDispatcher("index.jsp").forward(request, response);
	}


}
