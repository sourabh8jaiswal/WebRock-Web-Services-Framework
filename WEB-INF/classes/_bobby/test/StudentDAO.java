package bobby.test;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.*;

@Path("/StudentDAO")
public class StudentDAO
{
private ApplicationScope applicationScope;
@AutoWired(name="xyz")
private Movie movie;
public void setMovie(Movie movie)
{
this.movie=movie;
}
public Movie getMovie()
{
return this.movie;
}
public void setAppplicationScope(ApplicationScope applicationScope)
{
this.applicationScope=applicationScope;
}


@GET
@Path("/addStudent")
public void addStudent(@RequestParameter("rno.") int code,@RequestParameter("nm") String name)
{
System.out.println("StudentDAO --> addStudent() called");
}

@FORWARD("/abcd.html")
@OnStartup(priority=2)
@GET
@Path("/printListOfStudents")
public void printListOfStudents()
{
System.out.println("StudentDAO --> printListOfStudents() called");
}


}