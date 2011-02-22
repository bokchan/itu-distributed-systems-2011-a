// Command wrapper class
package pellekrogholt.labexercise4;

import java.io.*;

public class GetAllContactsCommand extends Command implements Serializable {
  public Object Execute (IPhonebook phonebook) throws IOException {
    return phonebook.GetAllContacts ();
  }
}