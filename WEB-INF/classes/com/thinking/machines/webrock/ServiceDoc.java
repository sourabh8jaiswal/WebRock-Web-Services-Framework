import java.io.*;
import java.util.*;
import com.thinking.machines.webrock.model.*;
import java.lang.reflect.*;
import java.lang.annotation.Annotation.*;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.pojo.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
public class ServiceDoc
{
public static void populateDatastructures(String packageName,File folder,WebRockModel tmwebrockmodel) throws Exception
{
File files[]=folder.listFiles();
for(File file : files)
{
String fileName=file.getName();
if(file.isDirectory())
{
packageName=packageName+"."+file.getName();
populateDatastructures(packageName,file,tmwebrockmodel);
}
if(file.isFile() && fileName.endsWith(".class"))
{
fileName=fileName.substring(0,fileName.length()-6);
Class cls=Class.forName(packageName+"."+fileName);
java.lang.annotation.Annotation pathAnnotationOnClass=cls.getAnnotation(Path.class);
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

java.lang.annotation.Annotation getAnnotationOnClass=cls.getAnnotation(GET.class);
if(getAnnotationOnClass!=null) getAppliedOnClass=true;
java.lang.annotation.Annotation postAnnotationOnClass=cls.getAnnotation(POST.class);
if(postAnnotationOnClass!=null) postAppliedOnClass=true;

java.lang.annotation.Annotation injectApplicationDirectoryAnnotation=cls.getAnnotation(InjectApplicationDirectory.class);
if(injectApplicationDirectoryAnnotation!=null)
{
injectApplicationDirectory=true;
}
java.lang.annotation.Annotation injectApplicationScopeAnnotation=cls.getAnnotation(InjectApplicationScope.class);
if(injectApplicationScopeAnnotation!=null)
{
injectApplicationScope=true;
}
java.lang.annotation.Annotation injectSessionScopeAnnotation=cls.getAnnotation(InjectSessionScope.class);
if(injectSessionScopeAnnotation!=null)
{
injectSessionScope=true;
}
java.lang.annotation.Annotation injectRequestScopeAnnotation=cls.getAnnotation(InjectRequestScope.class);
if(injectRequestScopeAnnotation!=null)
{
injectRequestScope=true;
}

/* SecuredAccess annotation checks */
java.lang.annotation.Annotation securedAccessAnnotationOnClass=cls.getAnnotation(SecuredAccess.class);
if(securedAccessAnnotationOnClass!=null)
{
securedAccessAppliedOnClass=true;
SecuredAccess sa=(SecuredAccess)securedAccessAnnotationOnClass;
securityBean=new SecurityBean(sa.checkPost(),sa.guard());
}

Field fields[]=cls.getDeclaredFields();
java.util.List<RequestParameterInjectionField> requestParameterInjectionFields=new ArrayList<>();
java.util.List<AutoWiredField> autoWiredFieldSet=new ArrayList<>();
for(Field field : fields)
{
java.lang.annotation.Annotation ann=field.getAnnotation(AutoWired.class);
if(ann!=null)
{
AutoWired autoWired=(AutoWired)ann;
String name=autoWired.name();
AutoWiredField autoWiredField=new AutoWiredField(name,field);
autoWiredFieldSet.add(autoWiredField);
System.out.println("auto wired field setted, name : "+name+" field name : "+field.getName());
}

java.lang.annotation.Annotation ann2=field.getAnnotation(InjectRequestParameter.class);
if(ann2!=null)
{
InjectRequestParameter injectRequestParameter=(InjectRequestParameter)ann2;
requestParameterInjectionFields.add(new RequestParameterInjectionField(injectRequestParameter.value(),field));
}
}


Method methods[]=cls.getDeclaredMethods();
for(Method method : methods)
{
java.lang.annotation.Annotation pathAnnotationOnMethod=method.getAnnotation(Path.class);
if(pathAnnotationOnMethod!=null)
{
System.out.println("---------->Scanning method: "+method.getName());
Path pth=(Path)pathAnnotationOnMethod;
String methodURL=pth.value();
String completeURL=classURL+methodURL;
Service service=new Service();
java.lang.annotation.Annotation forwardAnnotationOnMethod=method.getAnnotation(FORWARD.class);
if(forwardAnnotationOnMethod!=null)
{
FORWARD forward=(FORWARD)forwardAnnotationOnMethod;
service.setForwardTo(forward.value());
}

java.lang.annotation.Annotation onStartupAnnotation=method.getAnnotation(OnStartup.class);
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

java.util.List<ServiceParameter> parameterList=new LinkedList<>();
Parameter[] parameters=method.getParameters();
for(Parameter parameter : parameters)
{
java.lang.annotation.Annotation ann=parameter.getAnnotation(RequestParameter.class);
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
java.lang.annotation.Annotation getAnnotationOnMethod=method.getAnnotation(GET.class);
if(getAnnotationOnMethod!=null) service.setIsGetAllowed(true);
java.lang.annotation.Annotation postAnnotationOnMethod=method.getAnnotation(POST.class);
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
java.lang.annotation.Annotation securedAccessAnnotationOnMethod=method.getAnnotation(SecuredAccess.class);
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

} // if path annotation of class ends
} // if isFile() ends 
} // outer loop on iterating files ends
}


public static void main(String gg[])
{
try
{
String path=gg[0];
String pdfName=gg[1];
File folder=new File(path);
String packageName=folder.getName();
WebRockModel tmwebrockmodel=new WebRockModel();
populateDatastructures(packageName,folder,tmwebrockmodel);
Collection<Service> services=tmwebrockmodel.model.values();
Document document=new Document();
PdfWriter.getInstance(document,new FileOutputStream(pdfName));
Font headingFont=new Font(Font.FontFamily.HELVETICA,14,Font.BOLD,BaseColor.BLACK);
Font contentFont=new Font(Font.FontFamily.HELVETICA,12,Font.NORMAL,BaseColor.BLACK);
Font titleFont=new Font(Font.FontFamily.HELVETICA,14,Font.BOLD,BaseColor.RED);
Font paraFont=new Font(Font.FontFamily.HELVETICA,12,Font.NORMAL,BaseColor.DARK_GRAY);
document.open();
for(Service service : services)
{
String pathName=service.getPath();
String className=service.getClass().getName();
String methodName=service.getService().getName();
String returnType=service.getService().getReturnType().getName();
boolean isGet=service.getIsGetAllowed();
boolean isPost=service.getIsPostAllowed();
String methodType="";
if(isGet) methodType="GET";
if(isPost) methodType="POST";
if(!isGet && !isPost) methodType="GET/POST";
if(isGet && isPost) methodType="GET/POST";
boolean isSecured=service.getIsSecured();
String secured="";
if(isSecured) secured="YES";
else secured="NO";
boolean runOnStartup=service.getRunOnStartup();
Parameter[] parameters=service.getService().getParameters();
PdfPTable table=new PdfPTable(2);
table.setWidths(new int[]{1,2});
PdfPCell c1=new PdfPCell(new Phrase("Properties",headingFont));
c1.setHorizontalAlignment(Element.ALIGN_CENTER);
table.addCell(c1);
PdfPCell c2=new PdfPCell(new Phrase("Values",headingFont));
c2.setHorizontalAlignment(Element.ALIGN_CENTER);
table.addCell(c2);
c1=new PdfPCell(new Phrase("Path",titleFont));
c1.setHorizontalAlignment(Element.ALIGN_CENTER);
c1.setPaddingTop(10);
table.addCell(c1);
c2=new PdfPCell(new Phrase(pathName,contentFont));
c2.setPaddingTop(10);
c2.setPaddingLeft(10);
table.addCell(c2);
c1=new PdfPCell(new Phrase("Class",titleFont));
c1.setHorizontalAlignment(Element.ALIGN_CENTER);
c1.setPaddingTop(10);
table.addCell(c1);
c2=new PdfPCell(new Phrase(className,contentFont));
c2.setPaddingTop(10);
c2.setPaddingLeft(10);
table.addCell(c2);
c1=new PdfPCell(new Phrase("Method",titleFont));
c1.setHorizontalAlignment(Element.ALIGN_CENTER);
c1.setPaddingTop(10);
table.addCell(c1);
c2=new PdfPCell(new Phrase(methodName,contentFont));
c2.setPaddingTop(10);
c2.setPaddingLeft(10);
table.addCell(c2);

c1=new PdfPCell(new Phrase("Parameters",titleFont));
c1.setHorizontalAlignment(Element.ALIGN_CENTER);
c1.setPaddingTop(10);
table.addCell(c1);
c2=new PdfPCell(new Phrase(String.valueOf(parameters.length),contentFont));
c2.setPaddingTop(10);
c2.setPaddingLeft(10);
table.addCell(c2);
int i=0;
for(Parameter parameter : parameters)
{
c1=new PdfPCell(new Phrase("parameter "+i,paraFont));
c1.setHorizontalAlignment(Element.ALIGN_CENTER);
//c1.setPaddingTop(10);
table.addCell(c1);
c2=new PdfPCell(new Phrase(parameter.getType().getName(),paraFont));
//c2.setPaddingTop(10);
c2.setPaddingLeft(10);
table.addCell(c2);

java.lang.annotation.Annotation annotation=parameter.getAnnotation(RequestParameter.class);
if(annotation!=null)
{
RequestParameter rp=(RequestParameter)annotation;
c1=new PdfPCell(new Phrase("@RequestParameter",paraFont));
c1.setHorizontalAlignment(Element.ALIGN_CENTER);
//c1.setPaddingTop(10);
table.addCell(c1);
c2=new PdfPCell(new Phrase(rp.value(),paraFont));
//c2.setPaddingTop(10);
c2.setPaddingLeft(10);
table.addCell(c2);
}

i++;
}




c1=new PdfPCell(new Phrase("Return Type",titleFont));
c1.setHorizontalAlignment(Element.ALIGN_CENTER);
c1.setPaddingTop(10);
table.addCell(c1);
c2=new PdfPCell(new Phrase(returnType,contentFont));
c2.setPaddingTop(10);
c2.setPaddingLeft(10);
table.addCell(c2);

c1=new PdfPCell(new Phrase("Method Type",titleFont));
c1.setHorizontalAlignment(Element.ALIGN_CENTER);
c1.setPaddingTop(10);
table.addCell(c1);
c2=new PdfPCell(new Phrase(methodType,contentFont));
c2.setPaddingTop(10);
c2.setPaddingLeft(10);
table.addCell(c2);


c1=new PdfPCell(new Phrase("@SecuredAccess",titleFont));
c1.setHorizontalAlignment(Element.ALIGN_CENTER);
c1.setPaddingTop(10);
table.addCell(c1);
c2=new PdfPCell(new Phrase(secured,contentFont));
c2.setPaddingTop(10);
c2.setPaddingLeft(10);
table.addCell(c2);

if(runOnStartup)
{
c1=new PdfPCell(new Phrase("@OnStartup",titleFont));
c1.setHorizontalAlignment(Element.ALIGN_CENTER);
c1.setPaddingTop(10);
table.addCell(c1);
c2=new PdfPCell(new Phrase("YES",contentFont));
c2.setPaddingTop(10);
c2.setPaddingLeft(10);
table.addCell(c2);
c1=new PdfPCell(new Phrase("Priority",titleFont));
c1.setHorizontalAlignment(Element.ALIGN_CENTER);
c1.setPaddingTop(10);
table.addCell(c1);
c2=new PdfPCell(new Phrase(String.valueOf(service.getPriority()),contentFont));
c2.setPaddingTop(10);
c2.setPaddingLeft(10);
table.addCell(c2);
}else
{
c1=new PdfPCell(new Phrase("@OnStartup",titleFont));
c1.setHorizontalAlignment(Element.ALIGN_CENTER);
c1.setPaddingTop(10);
table.addCell(c1);
c2=new PdfPCell(new Phrase("NO",contentFont));
c2.setPaddingTop(10);
c2.setPaddingLeft(10);
table.addCell(c2);
}

if(service.getForwardTo()!=null)
{
c1=new PdfPCell(new Phrase("@FORWARD",titleFont));
c1.setHorizontalAlignment(Element.ALIGN_CENTER);
c1.setPaddingTop(10);
table.addCell(c1);
c2=new PdfPCell(new Phrase(service.getForwardTo(),contentFont));
c2.setPaddingTop(10);
c2.setPaddingLeft(10);
table.addCell(c2);
}


document.add(table);
document.add(new Paragraph("                                                                                                                                      "));
}
document.close();
}catch(Exception exception)
{
System.out.println(exception);
}
}

}