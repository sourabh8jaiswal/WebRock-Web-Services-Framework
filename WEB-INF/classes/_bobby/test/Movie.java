package bobby.test;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.*;
@Path("/Movie")
public class Movie
{
@Path("/reelOne")
public void reelOne(ApplicationScope as,String m)
{
System.out.println("Movie starts");
}
@Path("/reelTwo")
public void reelTwo()
{
System.out.println("Movie ends");
}
@Path("/interval")
public void interval()
{
System.out.println("Its an interval ! Have coke for 50 Rs only ");
}
}