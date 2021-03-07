package com.thinking.machines.webrock.pojo;
import java.lang.reflect.*;
import java.util.*;
public class Service implements java.io.Serializable
{
private Class serviceClass;
private String path;
private Method service;
private boolean isGetAllowed;
private boolean isPostAllowed;
private String forwardTo;
private boolean runOnStartup;
private int priority;
private boolean injectSessionScope;
private boolean injectApplicationScope;
private boolean injectRequestScope;
private boolean injectApplicationDirectory;
private List<AutoWiredField> autoWiredFieldSet;
private HashMap<String,String> requestParameterMap;
private List<ServiceParameter> serviceParameterList;
private List<RequestParameterInjectionField> requestParameterInjectionFields;
private boolean isSecured;
private SecurityBean securityBean;
public Service()
{
this.serviceClass=null;
this.path=null;
this.service=null;
this.isGetAllowed=false;
this.isPostAllowed=false;
this.forwardTo=null;
this.runOnStartup=false;
this.priority=0;
this.injectSessionScope=false;
this.injectApplicationScope=false;
this.injectRequestScope=false;
this.injectApplicationDirectory=false;
this.autoWiredFieldSet=null;
this.requestParameterMap=null;
this.serviceParameterList=null;
this.requestParameterInjectionFields=null;
this.isSecured=false;
this.securityBean=null;
}
public void setServiceClass(Class serviceClass)
{
this.serviceClass=serviceClass;
}
public Class getServiceClass()
{
return this.serviceClass;
}
public void setPath(String path)
{
this.path=path;
}
public String getPath()
{
return this.path;
}
public void setService(Method service)
{
this.service=service;
}
public Method getService()
{
return this.service;
}
public void setIsGetAllowed(boolean isGetAllowed)
{
this.isGetAllowed=isGetAllowed;
}
public boolean getIsGetAllowed()
{
return this.isGetAllowed;
}

public void setIsPostAllowed(boolean isPostAllowed)
{
this.isPostAllowed=isPostAllowed;
}
public boolean getIsPostAllowed()
{
return this.isPostAllowed;
}
public void setForwardTo(String forwardTo)
{
this.forwardTo=forwardTo;
}
public String getForwardTo()
{
return this.forwardTo;
}
public void setRunOnStartup(boolean runOnStartup)
{
this.runOnStartup=runOnStartup;
}
public boolean getRunOnStartup()
{
return this.runOnStartup;
}
public void setPriority(int priority)
{
this.priority=priority;
}
public int getPriority()
{
return this.priority;
}
public void setInjectSessionScope(boolean injectSessionScope)
{
this.injectSessionScope=injectSessionScope;
}
public boolean getInjectSessionScope()
{
return this.injectSessionScope;
}
public void setInjectApplicationScope(boolean injectApplicationScope)
{
this.injectApplicationScope=injectApplicationScope;
}
public boolean getInjectApplicationScope()
{
return this.injectApplicationScope;
}
public void setInjectRequestScope(boolean injectRequestScope)
{
this.injectRequestScope=injectRequestScope;
}
public boolean getInjectRequestScope()
{
return this.injectRequestScope;
}
public void setInjectApplicationDirectory(boolean injectApplicationDirectory)
{
this.injectApplicationDirectory=injectApplicationDirectory;
}
public boolean getInjectApplicationDirectory()
{
return this.injectApplicationDirectory;
}
public void setAutoWiredFieldSet(List<AutoWiredField> autoWiredFieldSet)
{
this.autoWiredFieldSet=autoWiredFieldSet;
}
public List<AutoWiredField> getAutoWiredFieldSet()
{
return this.autoWiredFieldSet;
}
public void setRequestParameterMap(HashMap<String,String> requestParameterMap)
{
this.requestParameterMap=requestParameterMap;
}
public HashMap<String,String> getRequestParameterMap()
{
return this.requestParameterMap;
}
public void setServiceParameterList(List<ServiceParameter> serviceParameterList)
{
this.serviceParameterList=serviceParameterList;
}
public List<ServiceParameter> getServiceParameterList()
{
return this.serviceParameterList;
}
public void setRequestParameterInjectionFields(List<RequestParameterInjectionField> requestParameterInjectionFields)
{
this.requestParameterInjectionFields=requestParameterInjectionFields;
}
public List<RequestParameterInjectionField> getRequestParameterInjectionFields()
{
return this.requestParameterInjectionFields;
}
public void setIsSecured(boolean isSecured)
{
this.isSecured=isSecured;
}
public boolean getIsSecured()
{
return this.isSecured;
}
public void setSecurityBean(SecurityBean securityBean)
{
this.securityBean=securityBean;
}
public SecurityBean getSecurityBean()
{
return this.securityBean;
}
}