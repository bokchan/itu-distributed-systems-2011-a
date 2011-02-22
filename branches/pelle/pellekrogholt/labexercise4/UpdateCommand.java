// Command wrapper class
package pellekrogholt.labexercise4;

import java.io.*;

public class UpdateCommand extends Command implements Serializable {
  private String Name, NewPhoneNo;

  public UpdateCommand (String name, String newPhoneNo) {
    Name = name;
    NewPhoneNo = newPhoneNo;
  }

  public Object Execute (IPhonebook phonebook) throws IOException {
    return phonebook.Update (Name, NewPhoneNo);
  }
}
