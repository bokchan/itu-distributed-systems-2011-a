package lab3.pellekrogholt.relationship;

import dk.pervasive.jcaf.relationship.TimedRelationship;

public class Located extends TimedRelationship {
	
    private String source;
    
	public Located() {
		super();
	}

	public Located(String source) {
		this();
		this.source = source;
	}

    /**
     * @return Returns the source.
     */
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }

	public String toXML() {
		return 	"<located souce=\"" + getSource() + "\" time=\"" + getTime() + "\" />";
	}

    public boolean equals(Object obj) {
        if (obj instanceof Located) {
            Located loc = (Located) obj;
            return loc.getSource().equals(getSource());
        }

        return super.equals(obj);
    }
    
    public int hashCode() {
        return getSource().hashCode();
    }
    
    public String toString() {
        return "located [" + getSource() + "]";
    }
    
}
