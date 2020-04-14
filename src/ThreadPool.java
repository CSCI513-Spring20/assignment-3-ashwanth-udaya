import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPool
{
	
	  // FIFO ordering
	  private boolean isShutdown = false;
	  private final LinkedBlockingQueue<Task> que;
	  private final ArrayList<Task> CTasks;
	  

	  //Thread pool size
	  private final int plSize;
	
	  //Internally pool is an array
	  private final InternalTask[] intTasks;
	
	  //Constructor of the class
	  public ThreadPool(int poolSize){
		  this.plSize = poolSize;
		  que = new LinkedBlockingQueue<Task>();
		  CTasks = new ArrayList<Task>();
		  intTasks = new InternalTask[this.plSize];
		  for (int i = 0; i < poolSize; i++) {
			  intTasks[i] = new InternalTask("Thread " + i);
			  intTasks[i].start();
		  }
	  }
	
	  public void execute(Task task) {
		  synchronized (que) {
			  que.add(task);
			  que.notify();
			  CTasks.add(task);
			  
		  }
	  }
	  
	
	  public void waitForAllTasks() {
		  boolean hasPendingTask = true;
		  while (hasPendingTask) {
			  hasPendingTask = false;
			  try {
				  Thread.sleep(100);
			  } catch (InterruptedException e) {
				  System.out.println("Exception  while waiting for threads to complete: " + e.getMessage());
			  }
		  }
	  }
	
	  public void shutdown() {
		  this.isShutdown = true;
	  }
	
	  private class InternalTask extends Thread {
		  public InternalTask(String name) {
			    super(name);
		  }
		  @Override
		  public void run() {
			  Task task;
			  while (true) {
				  synchronized (que) {
					  if (isShutdown && que.isEmpty()) {
						  break;
					  }
					  while (que.isEmpty()) {
						  try {
							  que.wait();
						  } catch (InterruptedException e) {
							  System.out.println("An error occurred: " + e.getMessage());
						  }
					  }
					  task = (Task) que.poll();
				  }	  
				  try {
					  task.run();
					  
				  } catch (RuntimeException e) {
					  System.out.println("Thread pool is terminated due to an error: " + e.getMessage());
				  }
			  }
		  }
	  	}

}