package dk.itu.smds.android.bluetweet.advanced;

import dk.itu.smds.android.bluetweet.AbstractAsyncBlueTweetConnection;
import dk.itu.smds.android.bluetweet.async.Command;

public class AsyncCommandExecutor extends Thread {

	boolean running = true;
	public void enqueue(Command command, AbstractAsyncBlueTweetConnection conn) {
		command.setConn(conn);
		
		// COMPLETE ME!!!!
		// HINT: look at the java.util.concurrent.BlockingQueue class...
	}
	
	public void cancel() {
		// IMPLEMENT ME!!!
	}
	
	@Override
	public void run() {
		Command cmd;
		while(running) {
			cmd = null;
			
			// COMPLETE ME!!!!!
			/*
			* obtain some how a Command object that was
			* previously enqueued using the enqueue method!
			*/
			
			if(cmd == null || cmd.getCmd() == -1) {
				running = false;
			} else {
				cmd.execute();
			}
		}
	}
}
