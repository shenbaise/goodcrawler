<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="java.util.List"%>
<%@page import="org.sbs.web.domain.JobStatus"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<title>Goodcrawler Controller Panel</title>
</head>
<body>
<p></p>
<p></p>
<p>
	<table style="width:100%;background-color:#DFC5A4;" cellpadding="2" cellspacing="0" border="1" bordercolor="#000000">
		<tbody>
			<tr>
				<td>
					<a class="ke-insertfile" href="start">启动</a><br />
				</td>
				<td>
					<a><font color="red"><%=request.getAttribute("start") %></font></a>
				</td>
			</tr>
			<tr>
				<td>
					<a class="ke-insertfile" href="stop">停止</a><br />
				</td>
				<td>
					<a><font color="red"><%=request.getAttribute("stop") %></font></a>
				</td>
			</tr>
			<%
			List<JobStatus> jobStatus = (List<JobStatus>)request.getAttribute("status");
			if(jobStatus!=null)
			for(JobStatus status:jobStatus){
			%>
			<tr>
				<td>
					<b>任务:</b>
				</td>
				<td>
					<a><font color="red"><%=status.getJobName() %></font></a>
				</td>
			</tr>
			<tr>
				<td>
					<b>运行状态(<a class="ke-insertfile" href="status">刷新</a>)<br />:</b>
				</td>
				<td>
					<a><font color="red"><%=status.getFetchStatus() %></font></a>
				</td>
				<td>
					<a><font color="red"><%=status.getExtractStatus() %></font></a>
				</td>
				<td>
					<a><font color="red"><%=status.getStoreStatus() %></font></a>
				</td>
			</tr>
			<%} %>
		</tbody>
	</table>
</body>
</html>