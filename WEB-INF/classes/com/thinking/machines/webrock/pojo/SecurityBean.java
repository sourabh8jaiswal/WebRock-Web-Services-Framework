package com.thinking.machines.webrock.pojo;
public class SecurityBean implements java.io.Serializable
{
private String checkPost;
private String guard;
public SecurityBean(String checkPost,String guard)
{
this.checkPost=checkPost;
this.guard=guard;
}
public void setCheckPost(String checkPost)
{
this.checkPost=checkPost;
}
public String getCheckPost()
{
return this.checkPost;
}
public void setGuard(String guard)
{
this.guard=guard;
}
public String getGuard()
{
return this.guard;
}
}