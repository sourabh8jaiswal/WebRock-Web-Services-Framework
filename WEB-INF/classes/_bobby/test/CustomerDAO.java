package bobby.test;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.*;
@Path("/CustomerDAO")
public class CustomerDAO implements java.io.Serializable
{
@Path("/addCustomer")
public void addCustomer(Customer customer,SessionScope sessionScope)
{
System.out.println(customer.getFirstName());
System.out.println(customer.getLastName());
}
}