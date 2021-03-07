package bobby.test;
import com.thinking.machines.webrock.*;
import com.thinking.machines.webrock.annotations.*;
@InjectApplicationScope
@InjectSessionScope
@Path("/DesignationDTO")
public class DesignationDTO implements java.io.Serializable,Comparable<DesignationDTO>
{
private int code;
private String title;
private ApplicationScope applicationScope;
private SessionScope sessionScope;
public DesignationDTO()
{
this.code=0;
this.title="";
}
public void setApplicationScope(ApplicationScope applicationScope)
{
this.applicationScope=applicationScope;
}
public ApplicationScope getApplicationScope()
{
return this.applicationScope;
}
public void setSessionScope(SessionScope sessionScope)
{
this.sessionScope=sessionScope;
}
public SessionScope getSessionScope()
{
return this.sessionScope;
}
@Path("/setCode")
public void setCode(int code)
{
this.code=code;
}
@Path("/getCode")
public int getCode()
{
return this.code;
}
@Path("/setTitle")
public void setTitle(String title)
{
this.title=title;
}
@Path("/getTitle")
public String getTitle()
{
return this.title;
}
@Path("/setInScope")
public void setInScope()
{
applicationScope.setAttribute("username","sourabh");
sessionScope.setAttribute("password","svvv@123");
}
@Path("/getFromScope")
public  void getFromScope(ApplicationScope applicationScope,SessionScope sessionScope)
{
System.out.println(applicationScope.getAttribute("username"));
System.out.println(sessionScope.getAttribute("password"));
}
public boolean equals(Object object)
{
if(!(object instanceof DesignationDTO)) return false;
DesignationDTO other=(DesignationDTO)object;
return this.code==other.code;
}
public int compareTo(DesignationDTO other)
{
return this.title.compareToIgnoreCase(other.title);
}
public int hashCode()
{
return this.code;
}
}