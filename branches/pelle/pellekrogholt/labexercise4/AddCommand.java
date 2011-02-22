// Command wrapper class
package pellekrogholt.labexercise4;

import java.io.*;

public class AddCommand extends Command implements Serializable {
  private Contact Contact;

  public AddCommand (Contact contact) {
    Contact = contact;
  }

  public Object Execute (IPhonebook phonebook) throws IOException {
    return phonebook.AddContact (Contact);
  }
}
