import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPool
{
	  //Thread pool size
	  private final int poolSize;
	
	  //Internally pool is an array
	  private final InternalTask[] internalTasks;
	
	  // FIFO ordering
	  private boolean isShutdown = false;
	  private final LinkedBlockingQueue<Task> queue;
	  private final ArrayList<Task> allTasks;
	
	  //Constructor of the class
	  public ThreadPool(int poolSize){
		  this.poolSize = poolSize;
		  queue = new LinkedBlockingQueue<Task>();
		  allTasks = new ArrayList<Task>();
		  internalTasks = new InternalTask[this.poolSize];
		  for (int i = 0; i < poolSize; i++) {
			  internalTasks[i] = new InternalTask("Thread " + i);
			  internalTasks[i].start();
		  }
	  }
	
	  public void execute(Task task) {
		  synchronized (queue) {
			  queue.add(task);
			  allTasks.add(task);
			  queue.notify();
		  }
	  }
	  
	
	  public void waitForAllTasks() {
		  boolean hasPendingTask = true;
		  while (hasPendingTask) {
			  hasPendingTask = false;
			  for (Task task : allTasks) {
				  if (task.getIsFinished() == false) {
					  hasPendingTask = true;
				  }
			  }
			  try {
				  Thread.sleep(100);
			  } catch (InterruptedException e) {
				  System.out.println("Exception happened while wait for threads to complete: " + e.getMessage());
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
				  synchronized (queue) {
					  if (isShutdown && queue.isEmpty()) {
						  break;
					  }
					  while (queue.isEmpty()) {
						  try {
							  queue.wait();
						  } catch (InterruptedException e) {
							  System.out.println("An error occurred while queue is waiting: " + e.getMessage());
						  }
					  }
					  task = (Task) queue.poll();
				  }	  
				  try {
					  task.run();
					  task.setIsFinished();
				  } catch (RuntimeException e) {
					  System.out.println("Thread pool is interrupted due to an issue: " + e.getMessage());
				  }
			  }
		  }
	  	}

}