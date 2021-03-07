package com.thinking.machines.webrock;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
public class JSFileServer extends HttpServlet
{
public void doGet(HttpServletRequest request, HttpServletResponse response)
{
try
{
PrintWriter pw=response.getWriter();
response.setContentType("text/js");
String name=request.getParameter("name");
ServletContext servletContext=request.getServletContext();
String fileName=servletContext.getRealPath("WEB-INF//js//"+name);
File file=new File(fileName);
RandomAccessFile randomAccessFile=new RandomAccessFile(file,"rw");
while(randomAccessFile.getFilePointer()<randomAccessFile.length())
{
pw.println(randomAccessFile.readLine());
}
randomAccessFile.close();
pw.flush();
}catch(Exception e)
{
System.out.println(e);
}
}
}