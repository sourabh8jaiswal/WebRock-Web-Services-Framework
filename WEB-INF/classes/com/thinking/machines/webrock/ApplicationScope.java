package com.thinking.machines.webrock;
import javax.servlet.*;
public class ApplicationScope
{
private ServletContext servletContext;
public ApplicationScope(ServletContext servletContext)
{
this.servletContext=servletContext;
}
public void setAttribute(String name,Object obj)
{
this.servletContext.setAttribute(name,obj);
}
public Object getAttribute(String name)
{
Object obj=this.servletContext.getAttribute(name);
return obj;
}
}
