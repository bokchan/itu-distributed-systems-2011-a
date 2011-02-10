package stine.labexercise2;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Dog implements Serializable
{
	int legs;
	String name;

	public Dog(int legs, String name)
	{
		this.legs=legs;
		this.name=name;
	}
	
	
	public void print()
	{
		System.out.println("Your new dogs name is " + this.name + ".");
		System.out.println(this.name + " has " + this.legs + " legs!");
		
	}
	
}
