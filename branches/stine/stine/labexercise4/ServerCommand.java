package stine.labexercise4;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;

import bok.labexercise4.PhonebookServer;

public abstract class ServerCommand implements Serializable
{
	private InetSocketAddress cp1;
	private InetSocketAddress cp2;
	
	public ServerCommand (InetSocketAddress cp1, InetSocketAddress cp2) {
		this.cp1 = cp1;
		this.cp2 = cp2;
	}
	
	public InetSocketAddress getJoiningServer() {
		return this.cp1;
	}
	
	public InetSocketAddress getTargetServer() {
		return this.cp2;
	}
	
	/***
	 * 
	 * @param phonebookServer Untill 
	 * @return
	 * @throws IOException
	 */
	abstract public Object Execute(stine.labexercise4.PhonebookServer phonebookServer) throws IOException;
}
