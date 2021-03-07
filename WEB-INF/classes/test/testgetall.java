import bobby.test.*;
import java.util.*;
class testgetall
{
public static void main(String gg[])
{
try
{
StudentService studentService=new StudentService();
List<Student> students=studentService.getAll();
for(Student student : students)
{
System.out.println("Roll number : "+student.getRollNumber()+", Name : "+student.getName()+", Gender : "+student.getGender());
}
}catch(Exception e)
{
System.out.println(e);
}
}
}