package stine.labexercise4;

import java.io.IOException;
import java.net.InetSocketAddress;

@SuppressWarnings("serial")
public class JoinCommand extends Command
{
	InetSocketAddress joining;
	InetSocketAddress coordinator;
	
	public JoinCommand(InetSocketAddress joining,InetSocketAddress coordinator)
	{
		this.coordinator= coordinator;
		this.joining=joining;
	}
	
	
	@Override
	public Object Execute(IPhonebook phonebook) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	


}
