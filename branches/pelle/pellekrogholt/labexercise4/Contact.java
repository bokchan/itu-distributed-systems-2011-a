// Data structure for holding data for a contact
package pellekrogholt.labexercise4;

import java.io.*;

public class Contact implements Serializable {
  public String Name;

  public String PhoneNo;

  public Contact (String name, String phoneNo) {
    Name = name;
    PhoneNo = phoneNo;
  }

  public String toString () {
    return "{ Name: " + Name + "; PhoneNo: " + PhoneNo + " }";
  }
}
