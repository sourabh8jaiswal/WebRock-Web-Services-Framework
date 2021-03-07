package bobby.test;
public class Customer implements java.io.Serializable
{
private String firstName;
private String lastName;
public Customer()
{
this.firstName="";
this.lastName="";
}
public void setFirstName(String firstName)
{
this.firstName=firstName;
}
public String getFirstName()
{
return this.firstName;
}
public void setLastName(String lastName)
{
this.lastName=lastName;
}
public String getLastName()
{
return this.lastName;
}
}