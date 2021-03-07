import bobby.test.*;
class testdelete
{
public static void main(String gg[])
{
try
{
int rollNumber=Integer.parseInt(gg[0]);
StudentService studentService=new StudentService();
studentService.delete(rollNumber);
}catch(Exception e)
{
System.out.println(e);
}
}
}