package stine.labexercise4;
// Command wrapper class

import java.io.*;

public class GetAllContactsCommand extends Command implements Serializable {
  public Object Execute (IPhonebook phonebook) throws IOException
  {
    return phonebook.GetAllContacts ();
  }
}