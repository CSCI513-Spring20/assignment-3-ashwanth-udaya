
public abstract class Task implements Runnable {
	String name;
	boolean complete;

	public Task(String name) //Constructor class
	{
		this.name = name;
		this.complete = false;
	}

	public String getName()   //get Name
	{
		return name;
	}
 	
}