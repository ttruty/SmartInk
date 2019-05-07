package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class StrokeTableData {
	
	private StringProperty strokeIndexProperty;
	private StringProperty strokeLabelProperty;
	private StringProperty strokeDurationProperty;
	private StringProperty strokeDistanceProperty;

	public StrokeTableData(String strokeIndex, String strokeLabel, String strokeDuration, String strokeDistance) {
	    this.strokeIndexProperty = new SimpleStringProperty(strokeIndex);
	    this.strokeLabelProperty = new SimpleStringProperty(strokeLabel);
	    this.strokeDurationProperty = new SimpleStringProperty(strokeDuration);
	    this.strokeDistanceProperty = new SimpleStringProperty(strokeDistance);
	}
	public void setStrokeIndex( String strokeIndex ) {
        this.strokeIndexProperty.set( strokeIndex );
    }

    public String getStrokeIndex() {
        return this.strokeIndexProperty.get();
    }

    public StringProperty strokeIndexProperty() {
        return this.strokeIndexProperty;
    }
    
    public void setStrokeLabel( String strokeLabel ) {
        this.strokeLabelProperty.set( strokeLabel );
    }

    public String getStrokeLabel() {
        return this.strokeLabelProperty.get();
    }

    public StringProperty strokeLabelProperty() {
        return this.strokeLabelProperty;
    }
    
    public void setStrokeDuration( String strokeDuration ) {
        this.strokeDurationProperty.set( strokeDuration );
    }

    public String getStrokeDuration() {
        return this.strokeDurationProperty.get();
    }

    public StringProperty strokDurationProperty() {
        return this.strokeDurationProperty;
    }
    
    public void setStrokeDistance( String strokeDistance ) {
        this.strokeDistanceProperty.set( strokeDistance );
    }

    public String getStrokeDistance() {
        return this.strokeDistanceProperty.get();
    }

    public StringProperty strokDistanceProperty() {
        return this.strokeDistanceProperty;
    }
}