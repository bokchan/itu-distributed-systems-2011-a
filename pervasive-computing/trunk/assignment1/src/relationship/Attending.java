package relationship;


/**
 * Relationship to connect a visitor to different ITUEvents.
 * 
 * @author mSpazzy
 *
 */
public class Attending extends Located {
	
	public Attending(String source) {
		super(source);
	}
	
	public String toXML() {
		return 	"<attending source=\"" + getSource() + "\" time=\"" + getTime() + "\" />";
	}
}