package bobby.test;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.*;
@InjectSessionScope
@Path("/Item")
public class Item
{
private SessionScope sessionScope;
public void setSessionScope(SessionScope sessionScope)
{
this.sessionScope=sessionScope;
}

@SecuredAccess(checkPost="bobby.test.Movie",guard="reelOne")
@Path("/addItem")
public void addItem()
{
this.sessionScope.setAttribute("xyz",new Movie());
System.out.println("addItem got called");
}
public void removeItem()
{
System.out.println("removeItem got called");
}
@OnStartup(priority=1)
@Path("/listItems")
public void listItems()
{
System.out.println("listItems got called");
}
@Path("/getItemByCode")
public void getItemByCode()
{
System.out.println("getItemByCode got called");
}
@OnStartup(priority=2)
@Path("/getItemCode")
public int getItemCode()
{
System.out.println("getItemCode got called");
return 1;
}
@OnStartup(priority=5)
@Path("/setItemCode")
public void setItemCode(int code)
{
System.out.println("setItemCode got called");
}
}