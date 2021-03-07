package bobby.test;
import java.sql.*;
import java.util.*;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.*;
@Path("/StudentService")
public class StudentService
{
@Path("/add")
public void add(Student student) throws ServiceException
{
int rollNumber=student.getRollNumber();
String name=student.getName();
String gender=student.getGender();
try
{
Class.forName("com.mysql.cj.jdbc.Driver");
Connection connection=DriverManager.getConnection("jdbc:mysql://localhost:3306/sjdb","sj","sj");
PreparedStatement preparedStatement=connection.prepareStatement("select gender from student where roll_number=?");
preparedStatement.setInt(1,rollNumber);
ResultSet resultSet=preparedStatement.executeQuery();
if(resultSet.next())
{
resultSet.close();
preparedStatement.close();
connection.close();
throw new ServiceException("student with roll number : "+rollNumber+" already exists");
}
resultSet.close();
preparedStatement.close();
preparedStatement=connection.prepareStatement("insert into student values(?,?,?)");
preparedStatement.setInt(1,rollNumber);
preparedStatement.setString(2,name);
preparedStatement.setString(3,gender);
preparedStatement.executeUpdate();
System.out.println("Student added");
preparedStatement.close();
connection.close();
}catch(Exception exception)
{
throw new ServiceException(exception.getMessage());
}
}
@Path("/update")
public void update(Student student) throws ServiceException
{
int rollNumber=student.getRollNumber();
String name=student.getName();
String gender=student.getGender();
try
{
Class.forName("com.mysql.cj.jdbc.Driver");
Connection connection=DriverManager.getConnection("jdbc:mysql://localhost:3306/sjdb","sj","sj");
PreparedStatement preparedStatement=connection.prepareStatement("select gender from student where roll_number=?");
preparedStatement.setInt(1,rollNumber);
ResultSet resultSet=preparedStatement.executeQuery();
if(!resultSet.next())
{
resultSet.close();
preparedStatement.close();
connection.close();
throw new ServiceException("invalid roll number: "+rollNumber);
}
resultSet.close();
preparedStatement.close();
preparedStatement=connection.prepareStatement("update student set name=?, gender=? where roll_number=?");
preparedStatement.setString(1,name);
preparedStatement.setString(2,gender);
preparedStatement.setInt(3,rollNumber);
preparedStatement.executeUpdate();
preparedStatement.close();
connection.close();
System.out.println("student updated");
}catch(Exception exception)
{
throw new ServiceException(exception.getMessage());
}
}
@Path("/delete")
public void delete(@RequestParameter("rollNumber")int rollNumber) throws ServiceException
{
try
{
Class.forName("com.mysql.cj.jdbc.Driver");
Connection connection=DriverManager.getConnection("jdbc:mysql://localhost:3306/sjdb","sj","sj");
PreparedStatement preparedStatement=connection.prepareStatement("select * from student where roll_number=?");
preparedStatement.setInt(1,rollNumber);
ResultSet resultSet=preparedStatement.executeQuery();
if(!resultSet.next())
{
resultSet.close();
preparedStatement.close();
connection.close();
throw new ServiceException("invalid roll number: "+rollNumber);
}
resultSet.close();
preparedStatement.close();
preparedStatement=connection.prepareStatement("delete from student where roll_number=?");
preparedStatement.setInt(1,rollNumber);
preparedStatement.executeUpdate();
preparedStatement.close();
connection.close();
System.out.println("student deleted");
}catch(Exception exception)
{
throw new ServiceException(exception.getMessage());
}
}
@Path("/getByRollNumber")
public Student getByRollNumber(@RequestParameter("rollNumber")int rollNumber) throws ServiceException
{
Student student=null;
try
{
Class.forName("com.mysql.cj.jdbc.Driver");
Connection connection=DriverManager.getConnection("jdbc:mysql://localhost:3306/sjdb","sj","sj");
PreparedStatement preparedStatement=connection.prepareStatement("select gender from student where roll_number=?");
preparedStatement.setInt(1,rollNumber);
ResultSet resultSet=preparedStatement.executeQuery();
if(!resultSet.next())
{
resultSet.close();
preparedStatement.close();
connection.close();
throw new ServiceException("invalid roll number : "+rollNumber);
}
resultSet.close();
preparedStatement.close();
preparedStatement=connection.prepareStatement("select * from student where roll_number=?");
preparedStatement.setInt(1,rollNumber);
resultSet=preparedStatement.executeQuery();
resultSet.next();
student=new Student();
student.setRollNumber(resultSet.getInt("roll_number"));
student.setName(resultSet.getString("name").trim());
student.setGender(resultSet.getString("gender").trim());
resultSet.close();
preparedStatement.close();
connection.close();
}catch(Exception exception)
{
throw new ServiceException(exception.getMessage());
}
return student;
}
@Path("/getAll")
public List<Student> getAll() throws ServiceException
{
List<Student> students=new ArrayList<>();
try
{
Class.forName("com.mysql.cj.jdbc.Driver");
Connection connection=DriverManager.getConnection("jdbc:mysql://localhost:3306/sjdb","sj","sj");
Statement statement=connection.createStatement();
ResultSet resultSet=statement.executeQuery("select * from student");
Student student;
while(resultSet.next())
{
student=new Student();
student.setRollNumber(resultSet.getInt("roll_number"));
student.setName(resultSet.getString("name").trim());
student.setGender(resultSet.getString("gender").trim());
students.add(student);
}
}catch(Exception exception)
{
throw new ServiceException(exception.getMessage());
}
return students;
}
}