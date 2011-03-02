package bok.labexercise4.extended.commands;

import java.io.IOException;

import bok.labexercise4.ServerResult;
import bok.labexercise4.extended.AbstractServer;

public class RemoveServerCommand extends ServerCommand<RemoveServerCommand> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Object Execute(AbstractServer o) {
		// The message was sent from server to joiner
		if(o.getIP().equals(this.getReceiver())) {
			o.getConnectionPoints().remove(this.getSender());
			try {
				o.broadcast(this);
				return ServerResult.RemoveServerInitiatedFromServer;
			} catch (IOException e){
				e.printStackTrace();
				return ServerResult.Removed;
			}
		} else if (this.getSender().equals(o.getIP())) {
			o.getConnectionPoints().removeAll(o.getConnectionPoints());
			return ServerResult.JoiningServerRemoved;

		} else {
			System.out.println("Broadcast server remove");
			o.getConnectionPoints().remove(this.getSender());
			return ServerResult.Removed;
		}
	}
}