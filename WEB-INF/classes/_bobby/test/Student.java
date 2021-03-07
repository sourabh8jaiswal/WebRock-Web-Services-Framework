package bobby.test;
import com.thinking.machines.webrock.*;
import com.thinking.machines.webrock.annotations.*;
@Path("/Student")
public class Student
{
@InjectRequestParameter("fname")
private String firstName;
@InjectRequestParameter("lname")
private String lastName;

public void setFirstName(String firstName)
{
this.firstName=firstName;
}
public void setLastName(String lastName)
{
this.lastName=lastName;
}


@Path("/setName")
public void setName()
{
System.out.println("Student --> setName() got called");
System.out.println(this.firstName+" , "+this.lastName);
}
}