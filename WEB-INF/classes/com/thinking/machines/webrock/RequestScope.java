package com.thinking.machines.webrock;
import javax.servlet.*;
import javax.servlet.http.*;
public class RequestScope
{
private HttpServletRequest httpServletRequest;
public RequestScope(HttpServletRequest httpServletRequest)
{
this.httpServletRequest=httpServletRequest;
}
public void setAttribute(String name,Object obj)
{
this.httpServletRequest.setAttribute(name,obj);
}
public Object getAttribute(String name)
{
Object obj=this.httpServletRequest.getAttribute(name);
return obj;
}
}
