import bobby.test.*;
class testadd
{
public static void main(String gg[])
{
try
{
int rollNumber=Integer.parseInt(gg[0]);
String name=gg[1];
char gender=gg[2].charAt(0);
Student student=new Student();
student.setRollNumber(rollNumber);
student.setName(name);
student.setGender(gender);
StudentService studentService=new StudentService();
studentService.add(student);
}catch(Exception e)
{
System.out.println(e);
}
}
}