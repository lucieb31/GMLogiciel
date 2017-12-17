package fr.toulousescape.util;

import java.util.List;

public class GameMaster {

	private String firstName;
	
	private String lastName;
	
	private List<Session> session; //TODO

	public GameMaster(String lastName, String firstName) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}
	
	public String getNameToDisplay()
	{
		return firstName + " " + lastName.substring(0, 1) + ".";
	}
}
