package bobby.test;
public class Student implements java.io.Serializable
{
private int rollNumber;
private String name;
private String gender;
public Student()
{
this.rollNumber=0;
this.name="";
this.gender="";
}
public void setRollNumber(int rollNumber)
{
this.rollNumber=rollNumber;
}
public int getRollNumber()
{
return this.rollNumber;
}
public void setName(String name)
{
this.name=name;
}
public String getName()
{
return this.name;
}
public void setGender(String gender)
{
this.gender=gender;
}
public String getGender()
{
return this.gender;
}
}