package com.thinking.machines.webrock;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import com.thinking.machines.webrock.model.*;
import java.lang.reflect.*;
import java.lang.annotation.*;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.pojo.*;
public class TMWebRockStartup extends HttpServlet
{

public static void populateDatastructures(String packageName,File folder,WebRockModel tmwebrockmodel,HashMap<String,Class> pojoClassMap,HashMap<String,Class> serviceClassMap) throws Exception
{
File files[]=folder.listFiles();
for(File file : files)
{
String fileName=file.getName();
if(file.isDirectory())
{
packageName=packageName+"."+file.getName();
populateDatastructures(packageName,file,tmwebrockmodel,pojoClassMap,serviceClassMap);
}
if(file.isFile() && fileName.endsWith(".class"))
{
fileName=fileName.substring(0,fileName.length()-6);
Class cls=Class.forName(packageName+"."+fileName);
Annotation pathAnnotationOnClass=cls.getAnnotation(Path.class);
if(pathAnnotationOnClass!=null)
{
System.out.println("--------------------->Scanning class: "+cls.getName());
boolean getAppliedOnClass=false;
boolean postAppliedOnClass=false;
boolean securedAccessAppliedOnClass=false;
boolean injectApplicationDirectory=false;
boolean injectApplicationScope=false;
boolean injectSessionScope=false;
boolean injectRequestScope=false;
SecurityBean securityBean=null;
Path path=(Path)pathAnnotationOnClass;
String classURL=path.value();

Annotation getAnnotationOnClass=cls.getAnnotation(GET.class);
if(getAnnotationOnClass!=null) getAppliedOnClass=true;
Annotation postAnnotationOnClass=cls.getAnnotation(POST.class);
if(postAnnotationOnClass!=null) postAppliedOnClass=true;

Annotation injectApplicationDirectoryAnnotation=cls.getAnnotation(InjectApplicationDirectory.class);
if(injectApplicationDirectoryAnnotation!=null)
{
injectApplicationDirectory=true;
}
Annotation injectApplicationScopeAnnotation=cls.getAnnotation(InjectApplicationScope.class);
if(injectApplicationScopeAnnotation!=null)
{
injectApplicationScope=true;
}
Annotation injectSessionScopeAnnotation=cls.getAnnotation(InjectSessionScope.class);
if(injectSessionScopeAnnotation!=null)
{
injectSessionScope=true;
}
Annotation injectRequestScopeAnnotation=cls.getAnnotation(InjectRequestScope.class);
if(injectRequestScopeAnnotation!=null)
{
injectRequestScope=true;
}

/* SecuredAccess annotation checks */
Annotation securedAccessAnnotationOnClass=cls.getAnnotation(SecuredAccess.class);
if(securedAccessAnnotationOnClass!=null)
{
securedAccessAppliedOnClass=true;
SecuredAccess sa=(SecuredAccess)securedAccessAnnotationOnClass;
securityBean=new SecurityBean(sa.checkPost(),sa.guard());
}

Field fields[]=cls.getDeclaredFields();
List<RequestParameterInjectionField> requestParameterInjectionFields=new ArrayList<>();
List<AutoWiredField> autoWiredFieldSet=new ArrayList<>();
for(Field field : fields)
{
Annotation ann=field.getAnnotation(AutoWired.class);
if(ann!=null)
{
AutoWired autoWired=(AutoWired)ann;
String name=autoWired.name();
AutoWiredField autoWiredField=new AutoWiredField(name,field);
autoWiredFieldSet.add(autoWiredField);
System.out.println("auto wired field setted, name : "+name+" field name : "+field.getName());
}

Annotation ann2=field.getAnnotation(InjectRequestParameter.class);
if(ann2!=null)
{
InjectRequestParameter injectRequestParameter=(InjectRequestParameter)ann2;
requestParameterInjectionFields.add(new RequestParameterInjectionField(injectRequestParameter.value(),field));
}
}


Method methods[]=cls.getDeclaredMethods();
for(Method method : methods)
{
Annotation pathAnnotationOnMethod=method.getAnnotation(Path.class);
if(pathAnnotationOnMethod!=null)
{
System.out.println("---------->Scanning method: "+method.getName());
Path pth=(Path)pathAnnotationOnMethod;
String methodURL=pth.value();
String completeURL=classURL+methodURL;
Service service=new Service();
Annotation forwardAnnotationOnMethod=method.getAnnotation(FORWARD.class);
if(forwardAnnotationOnMethod!=null)
{
FORWARD forward=(FORWARD)forwardAnnotationOnMethod;
service.setForwardTo(forward.value());
}

Annotation onStartupAnnotation=method.getAnnotation(OnStartup.class);
if(onStartupAnnotation!=null)
{
Class returnType=method.getReturnType();
if(returnType.getName().equals("void") && method.getParameters().length==0)
{
OnStartup onStartup=(OnStartup)onStartupAnnotation;
service.setRunOnStartup(true);
service.setPriority(onStartup.priority());
}
}

List<ServiceParameter> parameterList=new LinkedList<>();
Parameter[] parameters=method.getParameters();
for(Parameter parameter : parameters)
{
pojoClassMap.put(parameter.getType().getName(),parameter.getType());
Annotation ann=parameter.getAnnotation(RequestParameter.class);
if(ann!=null)
{
RequestParameter requestParameter=(RequestParameter)ann;
parameterList.add(new ServiceParameter(parameter.getType().getName(),requestParameter.value()));
System.out.println("Request Parameter : "+requestParameter.value()+", "+parameter.getType().getName());
}else
{
parameterList.add(new ServiceParameter(parameter.getType().getName(),parameter.getType().getName()));
System.out.println("Request Parameter : "+parameter.getType().getName());
}
}
//for(int i=0;i<parameterList.size();i++) System.out.println("parameter name "+parameterList.get(i).getRequestParameterName()+" type : "+parameterList.get(i).getDataType());



if(getAppliedOnClass)
{
service.setIsGetAllowed(true);
}else
{
if(postAppliedOnClass)
{
service.setIsPostAllowed(true);
}else
{
Annotation getAnnotationOnMethod=method.getAnnotation(GET.class);
if(getAnnotationOnMethod!=null) service.setIsGetAllowed(true);
Annotation postAnnotationOnMethod=method.getAnnotation(POST.class);
if(postAnnotationOnMethod!=null) service.setIsPostAllowed(true);
} // else ends
} //else ends


/* SecuredAccess Annotation */
if(securedAccessAppliedOnClass)
{
service.setIsSecured(true);
service.setSecurityBean(securityBean);
}else
{
Annotation securedAccessAnnotationOnMethod=method.getAnnotation(SecuredAccess.class);
if(securedAccessAnnotationOnMethod!=null)
{
SecuredAccess securedAccess=(SecuredAccess)securedAccessAnnotationOnMethod;
securityBean=new SecurityBean(securedAccess.checkPost(),securedAccess.guard());
service.setIsSecured(true);
service.setSecurityBean(securityBean);
}
}


service.setServiceClass(cls);
service.setPath(completeURL);
service.setService(method);
service.setInjectApplicationDirectory(injectApplicationDirectory);
service.setInjectApplicationScope(injectApplicationScope);
service.setInjectSessionScope(injectSessionScope);
service.setInjectRequestScope(injectRequestScope);
service.setAutoWiredFieldSet(autoWiredFieldSet);
service.setServiceParameterList(parameterList);
service.setRequestParameterInjectionFields(requestParameterInjectionFields);
tmwebrockmodel.model.put(completeURL,service);

} // if path annotation of method ends
} // for loop of method ends
serviceClassMap.put(cls.getName(),cls);
} // if path annotation of class ends

} // if isFile() ends 

} // outer loop on iterating files ends

}

public static void invokeStartupServices(List<Service> startupServices)
{
Class c;
Method m;
Service s;
Object obj;
try
{
for(int i=0;i<startupServices.size();i++)
{
s=startupServices.get(i);
c=s.getServiceClass();
m=s.getService();
obj=c.newInstance();
m.invoke(obj);
}
}catch(Exception exception)
{
System.out.println(exception);
}
}


public void init(ServletConfig servletConfig)
{
try
{
String packageName=servletConfig.getInitParameter("SERVICE_PACKAGE_PREFIX");
String jsFileName=servletConfig.getInitParameter("JS_FILE");
String urlPrefix=servletConfig.getInitParameter("URL_PREFIX");
ServletContext servletContext=servletConfig.getServletContext();
HashMap<String,Class> pojoClassMap=new HashMap<String,Class>();
HashMap<String,Class> serviceClassMap=new HashMap<String,Class>();
WebRockModel tmwebrockmodel=new WebRockModel();
String folderName=servletContext.getRealPath("WEB-INF\\classes\\"+packageName);
File folder=new File(folderName);
populateDatastructures(packageName,folder,tmwebrockmodel,pojoClassMap,serviceClassMap);
servletContext.setAttribute("model",tmwebrockmodel.model);
Collection<Service> ds=tmwebrockmodel.model.values();
LinkedList<Service> startupServices=new LinkedList<>();
for(Service service : ds)
{
System.out.println(service.getPath());
if(service.getRunOnStartup()) startupServices.add(service);
}
Collections.sort(startupServices,new SortByPriority());
invokeStartupServices(startupServices);
pojoClassMap.remove("int");
pojoClassMap.remove("java.lang.String");
pojoClassMap.remove("com.thinking.machines.webrock.ApplicationDirectory");
pojoClassMap.remove("com.thinking.machines.webrock.ApplicationScope");
pojoClassMap.remove("com.thinking.machines.webrock.SessionScope");
pojoClassMap.remove("com.thinking.machines.webrock.RequestScope");
System.out.println(pojoClassMap);
System.out.println(serviceClassMap);
folderName=servletContext.getRealPath("WEB-INF\\js");
folder=new File(folderName);
if(folder.exists()==false) folder.mkdir();
folder=new File(folderName+"\\"+jsFileName);
System.out.println(folder);
generateJSFile(folder,urlPrefix,pojoClassMap,serviceClassMap);
}catch(Exception exception)
{
System.out.println(exception);
}
}
public static void generateJSFile(File file,String urlPrefix,HashMap<String,Class> pojoClassMap,HashMap<String,Class> serviceClassMap) throws Exception
{
Collection<Class> pojoClasses=pojoClassMap.values();
Collection<Class> serviceClasses=serviceClassMap.values();
RandomAccessFile randomAccessFile=new RandomAccessFile(file,"rw");
for(Class c : pojoClasses)
{
String className=c.getSimpleName();
randomAccessFile.writeBytes("class "+className+"\r\n");
randomAccessFile.writeBytes("{\r\n");
randomAccessFile.writeBytes("constructor()\r\n");
randomAccessFile.writeBytes("{\r\n");
Field[] fields=c.getDeclaredFields();
for(Field field: fields)
{
randomAccessFile.writeBytes("this."+field.getName()+"=\"\";\r\n");
}// loop on fields ends
randomAccessFile.writeBytes("}\r\n");
for(Field field: fields)
{
String name=field.getName();
randomAccessFile.writeBytes("set"+String.valueOf(name.charAt(0)).toUpperCase()+name.substring(1)+"("+name+")\r\n");
randomAccessFile.writeBytes("{\r\n");
randomAccessFile.writeBytes("this."+name+"="+name+";\r\n");
randomAccessFile.writeBytes("}\r\n");
randomAccessFile.writeBytes("get"+String.valueOf(name.charAt(0)).toUpperCase()+name.substring(1)+"()\r\n");
randomAccessFile.writeBytes("{\r\n");
randomAccessFile.writeBytes("return this."+name+";\r\n");
randomAccessFile.writeBytes("}\r\n");
}//loop ends
randomAccessFile.writeBytes("};\r\n");
}

for(Class c: serviceClasses)
{
String className=c.getSimpleName();
String entityName="whatever";
System.out.println(entityName);
randomAccessFile.writeBytes("class "+className+"\r\n");
randomAccessFile.writeBytes("{\r\n");
Method[] methods=c.getDeclaredMethods();
for(Method method: methods)
{
String name=method.getName();
Parameter[] parameters=method.getParameters();
randomAccessFile.writeBytes(name+"(");
if(parameters.length==0)
{
randomAccessFile.writeBytes(")\r\n");
}else
{
randomAccessFile.writeBytes(entityName+")\r\n");
}
randomAccessFile.writeBytes("{\r\n");
if(parameters.length==0)
{
randomAccessFile.writeBytes("return $.ajax({\r\n");
randomAccessFile.writeBytes("url: '"+urlPrefix+"/"+className+"/"+name+"',\r\n");
randomAccessFile.writeBytes("type: 'GET'\r\n");
randomAccessFile.writeBytes("});\r\n");
}
else
{
System.out.println(parameters[0].getType());
if(parameters[0].getType().toString().equals("int") || parameters[0].getType().toString().equals("float") || parameters[0].getType().toString().equals("char") || parameters[0].getType().toString().equals("boolean") || parameters[0].getType().toString().equals("java.lang.String"))
{
String parameterName=entityName;
Annotation requestParameterAnnotation=parameters[0].getAnnotation(RequestParameter.class);
if(requestParameterAnnotation!=null)
{
RequestParameter requestParameter=(RequestParameter)requestParameterAnnotation;
parameterName=requestParameter.value();
}
randomAccessFile.writeBytes("return $.ajax({\r\n");
randomAccessFile.writeBytes("url: '"+urlPrefix+"/"+className+"/"+name+"',\r\n");
randomAccessFile.writeBytes("type: 'GET',\r\n");
randomAccessFile.writeBytes("data: \""+parameterName+"=\"+"+entityName+"\r\n");
randomAccessFile.writeBytes("});\r\n");
}
else 
{
randomAccessFile.writeBytes("return $.ajax({\r\n");
randomAccessFile.writeBytes("url: '"+urlPrefix+"/"+className+"/"+name+"',\r\n");
randomAccessFile.writeBytes("type: 'POST',\r\n");
randomAccessFile.writeBytes("data: JSON.stringify("+entityName+"),\r\n");
randomAccessFile.writeBytes("contentType: 'application/json'\r\n");
randomAccessFile.writeBytes("});\r\n");
}
}
randomAccessFile.writeBytes("}\r\n");

}//loop ends
randomAccessFile.writeBytes("};\r\n");
}
randomAccessFile.close();
}
}
class SortByPriority implements Comparator<Service>
{
public int compare(Service a,Service b)
{
return a.getPriority()-b.getPriority();
}
}