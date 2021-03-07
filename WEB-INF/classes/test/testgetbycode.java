import bobby.test.*;
class testgetbycode
{
public static void main(String gg[])
{
try
{
int rollNumber=Integer.parseInt(gg[0]);
StudentService studentService=new StudentService();
Student student=studentService.getByRollNumber(rollNumber);
System.out.println("roll number : "+student.getRollNumber());
System.out.println("name : "+student.getName());
System.out.println("gender : "+student.getGender());
}catch(Exception e)
{
System.out.println(e);
}
}
}