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
 * Servlet implementation class Start
 */
@WebServlet("/start")
public class Start extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Start() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("start");
		if(!CrawlerStatus.running){
			BootStrap.start("D:\\pioneer\\goodcrawler\\src\\main\\resources\\job_conf.xml");
		}
		request.setAttribute("start", "程序正在运行中。。。");
		request.setAttribute("jobs", BootStrap.getJobsNames());
		request.setAttribute("status", CrawlerStatus.getStatus());
		request.getRequestDispatcher("index.jsp").forward(request, response);
	}

}
