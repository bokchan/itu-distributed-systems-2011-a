package lab3.pellekrogholt;

import dk.pervasive.jcaf.relationship.TimedRelationship;

public class ContextMonitor extends TimedRelationship {
	
    private String source;
    
	public ContextMonitor() {
		super();
	}

	public ContextMonitor(String source) {
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
		return 	"<arrived souce=\"" + getSource() + "\" time=\"" + getTime() + "\" />";
	}

    public boolean equals(Object obj) {
        if (obj instanceof ContextMonitor) {
            ContextMonitor loc = (ContextMonitor) obj;
            return loc.getSource().equals(getSource());
        }

        return super.equals(obj);
    }
    
    public int hashCode() {
        return getSource().hashCode();
    }
    
    public String toString() {
        return "arrived [" + getSource() + "]";
    }
    
}
