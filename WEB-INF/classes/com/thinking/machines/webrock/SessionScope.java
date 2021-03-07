package com.thinking.machines.webrock;
import javax.servlet.*;
import javax.servlet.http.*;
public class SessionScope
{
private HttpSession httpSession;
public SessionScope(HttpSession httpSession)
{
this.httpSession=httpSession;
}
public void setAttribute(String name,Object obj)
{
this.httpSession.setAttribute(name,obj);
}
public Object getAttribute(String name)
{
Object obj=this.httpSession.getAttribute(name);
return obj;
}
}
