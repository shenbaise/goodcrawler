package org.sbs.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sbs.goodcrawler.bootstrap.BootStrap;
import org.sbs.goodcrawler.bootstrap.CrawlerStatus;

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
			request.setAttribute("start", "程序正在运行中。。。");
			request.setAttribute("jobs", BootStrap.getJobsNames());
			request.setAttribute("status", CrawlerStatus.getStatus());
		}else {
			request.setAttribute("stop", "程序停止运行。。。");
		}
		response.sendRedirect("index.jsp");
	}


}
