package com.thinking.machines.webrock;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import com.thinking.machines.webrock.pojo.*;
import java.lang.reflect.*;
import java.util.*;
import java.net.*;
import bobby.test.*;
import com.google.gson.*;
import bobby.test.*;
public class TMWebRock extends  HttpServlet
{

public HashMap<String,String> getRequestParameterMap(String queryString)
{
HashMap<String,String> parametersMap=new HashMap<>();
if(queryString==null) return parametersMap;
if(queryString.length()==0) return parametersMap;
queryString=URLDecoder.decode(queryString);
String[] str=queryString.split("&");
for(int i=0;i<str.length;i++)
{
String[] s=str[i].split("=");
parametersMap.put(s[0],s[1]);
}
return parametersMap;
}


public void doGet(HttpServletRequest request,HttpServletResponse response)
{
Gson gson=null;
PrintWriter pw=null;
try
{
response.setContentType("application/json");
gson=new Gson();
pw=response.getWriter();
String url=request.getRequestURI().toString();
String path=url.substring(url.lastIndexOf("/",url.lastIndexOf("/")-1),url.length());
//System.out.println("TMWebRock get method called");
//System.out.println("checkpoint 0");
HashMap<String,String> requestParameterMap=getRequestParameterMap(request.getQueryString());
//System.out.println("checkpoint 1");
ServletContext servletContext=getServletContext();
HttpSession httpSession=request.getSession(true);
HashMap<String,Service> servicesMap=(HashMap<String,Service>)servletContext.getAttribute("model");
Service service=servicesMap.get(path);
if(service==null)
{
try
{
response.sendError(HttpServletResponse.SC_NOT_FOUND);
return;
}catch(Exception e)
{
// do nothing
}
}

if(service.getIsPostAllowed())
{
response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
return;
}

/* here we will check about is service is being secured or not */
if(service.getIsSecured())
{
SecurityBean securityBean=service.getSecurityBean();
try
{
Class checkPost=Class.forName(securityBean.getCheckPost());
Object obj=checkPost.newInstance();
String guard=securityBean.getGuard();
Method methods[]=checkPost.getDeclaredMethods();
if(methods.length==0) throw new ServiceException("Guard : "+guard+" not found");
Method method=null;
int i=0;
for(i=0;i<methods.length;i++)
{
method=methods[i];
if(method.getName().equals(guard)) break;
}
if(i==methods.length) throw new ServiceException("Guard : "+guard+" not found");
Parameter parameters[]=method.getParameters();
if(parameters.length==0) method.invoke(obj);
if(parameters.length>0)
{
List<Object> objectList=new ArrayList<>();
for(Parameter parameter : parameters)
{
if(parameter.getType().getName().equals("com.thinking.machines.webrock.ApplicationDirectory"))
{
objectList.add(new ApplicationDirectory(new File(servletContext.getRealPath("/"))));
}
else if(parameter.getType().getName().equals("com.thinking.machines.webrock.ApplicationScope"))
{
objectList.add(new ApplicationScope(servletContext));
}
else if(parameter.getType().getName().equals("com.thinking.machines.webrock.SessionScope"))
{
objectList.add(new SessionScope(httpSession));
}
else if(parameter.getType().getName().equals("com.thinking.machines.webrock.RequestScope"))
{
objectList.add(new RequestScope(request));
}
else
{
throw new ServiceException("In method: "+method.getName()+", parameters should be of type ApplicationDirectory, ApplicationScope, SessionScope, RequestScope");
}
}// for loop ends
Object  objectArray[]=new Object[objectList.size()];
for(i=0;i<objectList.size();i++)
{
objectArray[i]=objectList.get(i);
}
method.invoke(obj,objectArray);
}
}catch(ServiceException ee)
{
ServiceResponse serviceResponse=new ServiceResponse();
serviceResponse.setResult(null);
serviceResponse.setException(ee);
serviceResponse.setIsSuccessful(false);
pw.println(gson.toJson(serviceResponse));
pw.flush();
response.sendError(HttpServletResponse.SC_NOT_FOUND);
return;
}
}



Class cls=service.getServiceClass();
Object obj=cls.newInstance();
if(service.getInjectApplicationDirectory())
{
Method m=null;
try
{
m=cls.getMethod("setApplicationDirectory",new Class[]{ApplicationDirectory.class});
}catch(Exception e)
{
throw new ServiceException("setApplicationDirectory method is missing");
}
if(m!=null)
{
String realPath=servletContext.getRealPath("/");
ApplicationDirectory applicationDirectory=new ApplicationDirectory(new File(realPath));
m.invoke(obj,applicationDirectory);
}
else
{
throw new ServiceException("setApplicationDirectory method is missing");
}
}

if(service.getInjectApplicationScope())
{
Method m=null;
try
{
m=cls.getMethod("setApplicationScope",new Class[]{ApplicationScope.class});
}catch(Exception e)
{
throw new ServiceException("setApplicationScope method is missing.");
}
if(m!=null)
{
ApplicationScope applicationScope=new ApplicationScope(servletContext);
m.invoke(obj,applicationScope);
}
else
{
throw new ServiceException("setApplicationScope method is missing.");
}
}
if(service.getInjectSessionScope())
{
Method m=null;
try
{
m=cls.getMethod("setSessionScope",new Class[]{SessionScope.class});
}catch(Exception e)
{
throw new ServiceException("setSessionScope method is missing.");
}
if(m!=null)
{
SessionScope sessionScope=new SessionScope(httpSession);
m.invoke(obj,sessionScope);
}
else
{
throw new ServiceException("setSessionScope method is missing.");
}
}
if(service.getInjectRequestScope())
{
Method m=null;
try
{
m=cls.getMethod("setRequestScope",new Class[]{RequestScope.class});
}catch(Exception e)
{
throw new ServiceException("setRequestScope method is missing.");
}
if(m!=null)
{
RequestScope requestScope=new RequestScope(request);
m.invoke(obj,requestScope);
}else
{
throw new ServiceException("setRequestScope method is missing.");
}
}

// Autowiring fields
List<AutoWiredField> autoWiredFieldSet=service.getAutoWiredFieldSet();
//System.out.println(autoWiredFieldSet.size());
for(AutoWiredField autoWiredField : autoWiredFieldSet)
{
//System.out.println(autoWiredField.getName());
//System.out.println(autoWiredField.getField().getName());
}
for(AutoWiredField autoWiredField : autoWiredFieldSet)
{
String name=autoWiredField.getName();
Field field=autoWiredField.getField();
String fieldName=field.getName();
String setterName="set"+String.valueOf(fieldName.charAt(0)).toUpperCase()+fieldName.substring(1);
Method setter=cls.getMethod(setterName,new Class[]{field.getType()});
if(setter!=null)
{
Object object=null;
object=request.getAttribute(name);
if(object==null)
{
object=httpSession.getAttribute(name);
if(object==null)
{
object=servletContext.getAttribute(name);
}
}
if(object!=null)
{
if(field.getType().isInstance(object))
{
setter.invoke(obj,object);
}else
{
throw new ServiceException("found object of incompatible type");
}
}else
{
throw new ServiceException("Unable to find property "+name+" in any scope.");
} // if(object!=null) ends here
}else //  if (setter!=null) condition else block
{
throw new ServiceException("setter method required for property : "+fieldName);
}
} // for loop on autowiredSet ends 


// Request Parameter Injection to Fields
List<RequestParameterInjectionField> requestParameterInjectionFields=service.getRequestParameterInjectionFields();
for(RequestParameterInjectionField requestParameterInjectionField : requestParameterInjectionFields)
{
String parameterName=requestParameterInjectionField.getParameterName();
Field f=requestParameterInjectionField.getField();
String fieldName=f.getName();
String fieldType=f.getType().getName();
String setterName="set"+String.valueOf(fieldName.charAt(0)).toUpperCase()+fieldName.substring(1);
Method setter=cls.getMethod(setterName,new Class[]{f.getType()});
if(setter==null)
{
throw new ServiceException("setter method required for property : "+fieldName);
}else
{
if(requestParameterMap.containsKey(parameterName))
{
String val=requestParameterMap.get(parameterName);
if(fieldType.equals("int"))
{
try
{
int k=Integer.parseInt(val);
setter.invoke(obj,k);
}catch(Exception e)
{
throw new ServiceException("Request Parameter type mismatch");
}
} 
if(fieldType.equals("short"))
{
try
{
short k=Short.parseShort(val);
setter.invoke(obj,k);
}catch(Exception e)
{
throw new ServiceException("Request Parameter type mismatch");
}
}
if(fieldType.equals("long"))
{
try
{
long k=Long.parseLong(val);
setter.invoke(obj,k);
}catch(Exception e)
{
throw new ServiceException("Request Parameter type mismatch");
}
}
if(fieldType.equals("float"))
{
try
{
float k=Float.parseFloat(val);
setter.invoke(obj,k);
}catch(Exception e)
{
throw new ServiceException("Request Parameter type mismatch");
}
}
if(fieldType.equals("double"))
{
try
{
double k=Double.parseDouble(val);
setter.invoke(obj,k);
}catch(Exception e)
{
throw new ServiceException("Request Parameter type mismatch");
}
}
if(fieldType.equals("boolean"))
{
try
{
boolean k=Boolean.parseBoolean(val);
setter.invoke(obj,k);
}catch(Exception e)
{
throw new ServiceException("Request Parameter type mismatch");
}
}
if(fieldType.equals("byte"))
{
try
{
byte k=Byte.parseByte(val);
setter.invoke(obj,k);
}catch(Exception e)
{
throw new ServiceException("Request Parameter type mismatch");
}
}
if(fieldType.equals("char"))
{
char k=val.charAt(0);
setter.invoke(obj,k);
}
if(fieldType.equals("java.lang.String"))
{
setter.invoke(obj,val);
}
}else
{
throw new ServiceException("unable to find property: "+parameterName+" in request scope");
}
}
} //for loop ends




List<ServiceParameter> serviceParameterList=service.getServiceParameterList();
List<Object> lst=new ArrayList<>();
for(ServiceParameter serviceParameter : serviceParameterList)
{
String nm=serviceParameter.getRequestParameterName();
String type=serviceParameter.getDataType();
if(type.equals("com.thinking.machines.webrock.ApplicationScope"))
{
lst.add(new ApplicationScope(servletContext));
continue;
}
if(type.equals("com.thinking.machines.webrock.SessionScope"))
{
lst.add(new SessionScope(httpSession));
continue;
}
if(type.equals("com.thinking.machines.webrock.RequestScope"))
{
lst.add(new RequestScope(request));
continue;
}

if(requestParameterMap.containsKey(nm))
{
String val=requestParameterMap.get(nm);
if(type.equals("int"))
{
try
{
int k=Integer.parseInt(val);
lst.add(k);
}catch(Exception e)
{
throw new ServiceException("Request Parameter type mismatch");
}
} 
if(type.equals("short"))
{
try
{
short k=Short.parseShort(val);
lst.add(k);
}catch(Exception e)
{
throw new ServiceException("Request Parameter type mismatch");
}
}
if(type.equals("long"))
{
try
{
long k=Long.parseLong(val);
lst.add(k);
}catch(Exception e)
{
throw new ServiceException("Request Parameter type mismatch");
}
}
if(type.equals("float"))
{
try
{
float k=Float.parseFloat(val);
lst.add(k);
}catch(Exception e)
{
throw new ServiceException("Request Parameter type mismatch");
}
}
if(type.equals("double"))
{
try
{
double k=Double.parseDouble(val);
lst.add(k);
}catch(Exception e)
{
throw new ServiceException("Request Parameter type mismatch");
}
}
if(type.equals("boolean"))
{
try
{
boolean k=Boolean.parseBoolean(val);
lst.add(k);
}catch(Exception e)
{
throw new ServiceException("Request Parameter type mismatch");
}
}
if(type.equals("byte"))
{
try
{
byte k=Byte.parseByte(val);
lst.add(k);
}catch(Exception e)
{
throw new ServiceException("Request Parameter type mismatch");
}
}
if(type.equals("char"))
{
char k=val.charAt(0);
lst.add(k);
}
if(type.equals("java.lang.String"))
{
lst.add(val);
}
}
}

//System.out.println("may be starting from here");
// calling service with required arguments
Object[] objArray=new Object[lst.size()];
for(int i=0;i<lst.size();i++)
{
objArray[i]=lst.get(i);
}
if(serviceParameterList.size()==0)
{
Object output=service.getService().invoke(obj);
if(service.getForwardTo()==null)
{
ServiceResponse serviceResponse=new ServiceResponse();
serviceResponse.setResult(output);
serviceResponse.setException(null);
serviceResponse.setIsSuccessful(true);
pw.println(gson.toJson(serviceResponse));
pw.flush();
}
}else
{
Object output=service.getService().invoke(obj,objArray);
if(service.getForwardTo()==null)
{
ServiceResponse serviceResponse=new ServiceResponse();
serviceResponse.setResult(output);
serviceResponse.setException(null);
serviceResponse.setIsSuccessful(true);
pw.println(gson.toJson(serviceResponse));
pw.flush();
}
}
//System.out.println("may be ends from here");

// if serive has applied FORWARD Annotation
if(service.getForwardTo()!=null)
{
String forwardTo=service.getForwardTo();
service=servicesMap.get(forwardTo);
if(service!=null)
{
obj=service.getServiceClass().newInstance();
Object output=service.getService().invoke(obj);
ServiceResponse serviceResponse=new ServiceResponse();
serviceResponse.setResult(output);
serviceResponse.setException(null);
serviceResponse.setIsSuccessful(true);
pw.println(gson.toJson(serviceResponse));
pw.flush();
}else
{
RequestDispatcher requestDispatcher=request.getRequestDispatcher(forwardTo);
requestDispatcher.forward(request,response);
}
}

}catch(ServiceException serviceException)
{
ServiceResponse serviceResponse=new ServiceResponse();
serviceResponse.setResult(null);
serviceResponse.setException(serviceException);
serviceResponse.setIsSuccessful(false);
pw.println(gson.toJson(serviceResponse));
pw.flush();
}catch(Exception exception)
{
ServiceResponse serviceResponse=new ServiceResponse();
serviceResponse.setResult(null);
serviceResponse.setException(exception);
serviceResponse.setIsSuccessful(false);
pw.println(gson.toJson(serviceResponse));
pw.flush();
}
}

public void doPost(HttpServletRequest request,HttpServletResponse response)
{
PrintWriter pw=null;
Gson gson=null;
try
{
response.setContentType("application/json");
gson=new Gson();
pw=response.getWriter();
String url=request.getRequestURI().toString();
String path=url.substring(url.lastIndexOf("/",url.lastIndexOf("/")-1),url.length());
//System.out.println(path);
//System.out.println("TMWebRock post method called");
//System.out.println("checkpoint 0");
HashMap<String,String> requestParameterMap=getRequestParameterMap("");
//System.out.println("checkpoint 1");
ServletContext servletContext=getServletContext();
HttpSession httpSession=request.getSession(true);
HashMap<String,Service> servicesMap=(HashMap<String,Service>)servletContext.getAttribute("model");
Service service=servicesMap.get(path);
if(service==null)
{
try
{
response.sendError(HttpServletResponse.SC_NOT_FOUND);
return;
}catch(Exception e)
{
// do nothing
}
}

if(service.getIsGetAllowed())
{
response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
return;
}

/* here we will check about is service is being secured or not */
if(service.getIsSecured())
{
SecurityBean securityBean=service.getSecurityBean();
try
{
Class checkPost=Class.forName(securityBean.getCheckPost());
Object obj=checkPost.newInstance();
String guard=securityBean.getGuard();
Method methods[]=checkPost.getDeclaredMethods();
if(methods.length==0) throw new ServiceException("Guard : "+guard+" not found");
Method method=null;
int i=0;
for(i=0;i<methods.length;i++)
{
method=methods[i];
if(method.getName().equals(guard)) break;
}
if(i==methods.length) throw new ServiceException("Guard : "+guard+" not found");
Parameter parameters[]=method.getParameters();
if(parameters.length==0) method.invoke(obj);
if(parameters.length>0)
{
List<Object> objectList=new ArrayList<>();
for(Parameter parameter : parameters)
{
if(parameter.getType().getName().equals("com.thinking.machines.webrock.ApplicationDirectory"))
{
objectList.add(new ApplicationDirectory(new File(servletContext.getRealPath("/"))));
}
else if(parameter.getType().getName().equals("com.thinking.machines.webrock.ApplicationScope"))
{
objectList.add(new ApplicationScope(servletContext));
}
else if(parameter.getType().getName().equals("com.thinking.machines.webrock.SessionScope"))
{
objectList.add(new SessionScope(httpSession));
}
else if(parameter.getType().getName().equals("com.thinking.machines.webrock.RequestScope"))
{
objectList.add(new RequestScope(request));
}
else
{
throw new ServiceException("In method: "+method.getName()+", parameters should be of type ApplicationDirectory, ApplicationScope, SessionScope, RequestScope");
}
}// for loop ends
Object  objectArray[]=new Object[objectList.size()];
for(i=0;i<objectList.size();i++)
{
objectArray[i]=objectList.get(i);
}
method.invoke(obj,objectArray);
}
}catch(ServiceException ee)
{
ServiceResponse serviceResponse=new ServiceResponse();
serviceResponse.setResult(null);
serviceResponse.setException(ee);
serviceResponse.setIsSuccessful(false);
pw.println(gson.toJson(serviceResponse));
pw.flush();
response.sendError(HttpServletResponse.SC_NOT_FOUND);
return;
}
}



Class cls=service.getServiceClass();
Object obj=cls.newInstance();
if(service.getInjectApplicationDirectory())
{
Method m=null;
try
{
m=cls.getMethod("setApplicationDirectory",new Class[]{ApplicationDirectory.class});
}catch(Exception e)
{
throw new ServiceException("setApplicationDirectory method is missing");
}
if(m!=null)
{
String realPath=servletContext.getRealPath("/");
ApplicationDirectory applicationDirectory=new ApplicationDirectory(new File(realPath));
m.invoke(obj,applicationDirectory);
}
else
{
throw new ServiceException("setter method for applicationDirectory is missing.");
}
}

if(service.getInjectApplicationScope())
{
Method m=null;
try
{
m=cls.getMethod("setApplicationScope",new Class[]{ApplicationScope.class});
}catch(Exception e)
{
throw new ServiceException("setApplicationScope method is missing");
}
if(m!=null)
{
ApplicationScope applicationScope=new ApplicationScope(servletContext);
m.invoke(obj,applicationScope);
}
else
{
throw new ServiceException("setter method for applicationScope is missing.");
}
}
if(service.getInjectSessionScope())
{
Method m=null;
try
{
m=cls.getMethod("setSessionScope",new Class[]{SessionScope.class});
}catch(Exception e)
{
throw new ServiceException("setSessionScope method is missing");
}
if(m!=null)
{
SessionScope sessionScope=new SessionScope(httpSession);
m.invoke(obj,sessionScope);
}
else
{
throw new ServiceException("setter method for sessionScope is missing.");
}
}
if(service.getInjectRequestScope())
{
Method m=null;
try
{
m=cls.getMethod("setRequestScope",new Class[]{RequestScope.class});
}catch(Exception e)
{
throw new ServiceException("setRequestScope method is missing");
}
if(m!=null)
{
RequestScope requestScope=new RequestScope(request);
m.invoke(obj,requestScope);
}else
{
throw new ServiceException("setter method for requestScope is missing.");
}
}

// Autowiring fields
List<AutoWiredField> autoWiredFieldSet=service.getAutoWiredFieldSet();
//System.out.println(autoWiredFieldSet.size());
for(AutoWiredField autoWiredField : autoWiredFieldSet)
{
//System.out.println(autoWiredField.getName());
//System.out.println(autoWiredField.getField().getName());
}
for(AutoWiredField autoWiredField : autoWiredFieldSet)
{
String name=autoWiredField.getName();
Field field=autoWiredField.getField();
String fieldName=field.getName();
String setterName="set"+String.valueOf(fieldName.charAt(0)).toUpperCase()+fieldName.substring(1);
Method setter=cls.getMethod(setterName,new Class[]{field.getType()});
if(setter!=null)
{
Object object=null;
object=request.getAttribute(name);
if(object==null)
{
object=httpSession.getAttribute(name);
if(object==null)
{
object=servletContext.getAttribute(name);
}
}
if(object!=null)
{
if(field.getType().isInstance(object))
{
setter.invoke(obj,object);
}else
{
throw new ServiceException("found object of incompatible type");
}
}else
{
throw new ServiceException("Unable to find property "+name+" in any scope.");
} // if(object!=null) ends here
}else //  if (setter!=null) condition else block
{
throw new ServiceException("setter method required for property : "+fieldName);
}
} // for loop on autowiredSet ends 


// Request Parameter Injection to Fields
List<RequestParameterInjectionField> requestParameterInjectionFields=service.getRequestParameterInjectionFields();
for(RequestParameterInjectionField requestParameterInjectionField : requestParameterInjectionFields)
{
String parameterName=requestParameterInjectionField.getParameterName();
Field f=requestParameterInjectionField.getField();
String fieldName=f.getName();
String fieldType=f.getType().getName();
String setterName="set"+String.valueOf(fieldName.charAt(0)).toUpperCase()+fieldName.substring(1);
Method setter=cls.getMethod(setterName,new Class[]{f.getType()});
if(setter==null)
{
throw new ServiceException("setter method required for property : "+fieldName);
}else
{
if(requestParameterMap.containsKey(parameterName))
{
String val=requestParameterMap.get(parameterName);
if(fieldType.equals("int"))
{
try
{
int k=Integer.parseInt(val);
setter.invoke(obj,k);
}catch(Exception e)
{
throw new ServiceException("Request Parameter type mismatch");
}
} 
if(fieldType.equals("short"))
{
try
{
short k=Short.parseShort(val);
setter.invoke(obj,k);
}catch(Exception e)
{
throw new ServiceException("Request Parameter type mismatch");
}
}
if(fieldType.equals("long"))
{
try
{
long k=Long.parseLong(val);
setter.invoke(obj,k);
}catch(Exception e)
{
throw new ServiceException("Request Parameter type mismatch");
}
}
if(fieldType.equals("float"))
{
try
{
float k=Float.parseFloat(val);
setter.invoke(obj,k);
}catch(Exception e)
{
throw new ServiceException("Request Parameter type mismatch");
}
}
if(fieldType.equals("double"))
{
try
{
double k=Double.parseDouble(val);
setter.invoke(obj,k);
}catch(Exception e)
{
throw new ServiceException("Request Parameter type mismatch");
}
}
if(fieldType.equals("boolean"))
{
try
{
boolean k=Boolean.parseBoolean(val);
setter.invoke(obj,k);
}catch(Exception e)
{
throw new ServiceException("Request Parameter type mismatch");
}
}
if(fieldType.equals("byte"))
{
try
{
byte k=Byte.parseByte(val);
setter.invoke(obj,k);
}catch(Exception e)
{
throw new ServiceException("Request Parameter type mismatch");
}
}
if(fieldType.equals("char"))
{
char k=val.charAt(0);
setter.invoke(obj,k);
}
if(fieldType.equals("java.lang.String"))
{
setter.invoke(obj,val);
}
}else
{
throw new ServiceException("unable to find property: "+parameterName+" in request scope");
}
}
} //for loop ends


/*  here we will check about JSON data  */
if(request.getContentType().equals("application/json"))
{
BufferedReader br=request.getReader();
StringBuffer sb=new StringBuffer();
String d;
while(true)
{
d=br.readLine();
if(d==null) break;
sb.append(d);
}
String rawData=sb.toString();
Object c=null;
//System.out.println(rawData);
try
{
c=gson.fromJson(rawData,service.getService().getParameters()[0].getType());
}catch(Exception i)
{
throw new ServiceException("Unable to parse JSON string to an object");
}
if(service.getService().getParameters().length>0)
{
if(service.getService().getParameters().length==1)
{
Object output=service.getService().invoke(obj,c);
ServiceResponse serviceResponse=new ServiceResponse();
serviceResponse.setResult(output);
serviceResponse.setException(null);
serviceResponse.setIsSuccessful(true);
pw.println(gson.toJson(serviceResponse));
pw.flush();
return;
}else
{
Parameter[] parameters=service.getService().getParameters();
int numberOfParameters=parameters.length;
List<Object> objectList=new ArrayList<>();
objectList.add(c);
for(int i=1;i<numberOfParameters;i++)
{
Parameter parameter=parameters[i];
if(parameter.getType().getName().equals("com.thinking.machines.webrock.ApplicationDirectory"))
{
objectList.add(new ApplicationDirectory(new File(servletContext.getRealPath("/"))));
}
else if(parameter.getType().getName().equals("com.thinking.machines.webrock.ApplicationScope"))
{
objectList.add(new ApplicationScope(servletContext));
}
else if(parameter.getType().getName().equals("com.thinking.machines.webrock.SessionScope"))
{
objectList.add(new SessionScope(httpSession));
}
else if(parameter.getType().getName().equals("com.thinking.machines.webrock.RequestScope"))
{
objectList.add(new RequestScope(request));
}
else
{
throw new ServiceException("In case of JSON, parameter should be of type ApplicationDirectory, ApplicationScope, SessionScope, RequestScope");
}
}// for loop ends
Object objectArray[]=new Object[numberOfParameters];
for(int j=0;j<numberOfParameters;j++) objectArray[j]=objectList.get(j);
Object output=service.getService().invoke(obj,objectArray);
ServiceResponse serviceResponse=new ServiceResponse();
serviceResponse.setResult(output);
serviceResponse.setException(false);
serviceResponse.setIsSuccessful(true);
pw.println(gson.toJson(serviceResponse));
pw.flush();
return;
}
}
}






List<ServiceParameter> serviceParameterList=service.getServiceParameterList();
List<Object> lst=new ArrayList<>();
for(ServiceParameter serviceParameter : serviceParameterList)
{
String nm=serviceParameter.getRequestParameterName();
String type=serviceParameter.getDataType();
if(type.equals("com.thinking.machines.webrock.ApplicationScope"))
{
lst.add(new ApplicationScope(servletContext));
continue;
}
if(type.equals("com.thinking.machines.webrock.SessionScope"))
{
lst.add(new SessionScope(httpSession));
continue;
}
if(type.equals("com.thinking.machines.webrock.RequestScope"))
{
lst.add(new RequestScope(request));
continue;
}

if(requestParameterMap.containsKey(nm))
{
String val=requestParameterMap.get(nm);
if(type.equals("int"))
{
try
{
int k=Integer.parseInt(val);
lst.add(k);
}catch(Exception e)
{
throw new ServiceException("Request Parameter type mismatch");
}
} 
if(type.equals("short"))
{
try
{
short k=Short.parseShort(val);
lst.add(k);
}catch(Exception e)
{
throw new ServiceException("Request Parameter type mismatch");
}
}
if(type.equals("long"))
{
try
{
long k=Long.parseLong(val);
lst.add(k);
}catch(Exception e)
{
throw new ServiceException("Request Parameter type mismatch");
}
}
if(type.equals("float"))
{
try
{
float k=Float.parseFloat(val);
lst.add(k);
}catch(Exception e)
{
throw new ServiceException("Request Parameter type mismatch");
}
}
if(type.equals("double"))
{
try
{
double k=Double.parseDouble(val);
lst.add(k);
}catch(Exception e)
{
throw new ServiceException("Request Parameter type mismatch");
}
}
if(type.equals("boolean"))
{
try
{
boolean k=Boolean.parseBoolean(val);
lst.add(k);
}catch(Exception e)
{
throw new ServiceException("Request Parameter type mismatch");
}
}
if(type.equals("byte"))
{
try
{
byte k=Byte.parseByte(val);
lst.add(k);
}catch(Exception e)
{
throw new ServiceException("Request Parameter type mismatch");
}
}
if(type.equals("char"))
{
char k=val.charAt(0);
lst.add(k);
}
if(type.equals("java.lang.String"))
{
lst.add(val);
}
}
}

//System.out.println("may be starting from here");
// calling service with required arguments
Object[] objArray=new Object[lst.size()];
for(int i=0;i<lst.size();i++)
{
objArray[i]=lst.get(i);
}
if(serviceParameterList.size()==0)
{
Object output=service.getService().invoke(obj);
if(service.getForwardTo()==null)
{
ServiceResponse serviceResponse=new ServiceResponse();
serviceResponse.setResult(output);
serviceResponse.setException(null);
serviceResponse.setIsSuccessful(true);
pw.println(gson.toJson(serviceResponse));
pw.flush();
}
}else
{
Object output=service.getService().invoke(obj,objArray);
if(service.getForwardTo()==null)
{
ServiceResponse serviceResponse=new ServiceResponse();
serviceResponse.setResult(output);
serviceResponse.setException(null);
serviceResponse.setIsSuccessful(true);
pw.println(gson.toJson(serviceResponse));
pw.flush();
}
}
//System.out.println("may be ends from here");

// if serive has applied FORWARD Annotation
if(service.getForwardTo()!=null)
{
String forwardTo=service.getForwardTo();
service=servicesMap.get(forwardTo);
if(service!=null)
{
obj=service.getServiceClass().newInstance();
Object output=service.getService().invoke(obj);
ServiceResponse serviceResponse=new ServiceResponse();
serviceResponse.setResult(output);
serviceResponse.setException(null);
serviceResponse.setIsSuccessful(true);
pw.println(gson.toJson(serviceResponse));
pw.flush();
}else
{
RequestDispatcher requestDispatcher=request.getRequestDispatcher(forwardTo);
requestDispatcher.forward(request,response);
}
}

}catch(ServiceException serviceException)
{
ServiceResponse serviceResponse=new ServiceResponse();
serviceResponse.setResult(null);
serviceResponse.setException(serviceException);
serviceResponse.setIsSuccessful(false);
pw.println(gson.toJson(serviceResponse));
pw.flush();
}catch(Exception exception)
{
ServiceResponse serviceResponse=new ServiceResponse();
serviceResponse.setResult(null);
serviceResponse.setException(exception);
serviceResponse.setIsSuccessful(false);
pw.println(gson.toJson(serviceResponse));
pw.flush();
}
}

}