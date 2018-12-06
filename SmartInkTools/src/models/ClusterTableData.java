package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ClusterTableData {
	
	private StringProperty clusterIndexProperty;
	private StringProperty clusterLabelProperty;
	private StringProperty clusterCountProperty;

	public ClusterTableData(String clusterIndex, String clusterLabel, String clusterCount) {
	    this.clusterIndexProperty = new SimpleStringProperty(clusterIndex);
	    this.clusterLabelProperty = new SimpleStringProperty(clusterLabel);
	    this.clusterCountProperty = new SimpleStringProperty(clusterCount);
	}
	public void setStrokeIndex( String strokeIndex ) {
        this.clusterIndexProperty.set( strokeIndex );
    }

    public String getStrokeIndex() {
        return this.clusterIndexProperty.get();
    }

    public StringProperty strokeIndexProperty() {
        return this.clusterIndexProperty;
    }
    
    public void setStrokeLabel( String strokeLabel ) {
        this.clusterLabelProperty.set( strokeLabel );
    }

    public String getStrokeLabel() {
        return this.clusterLabelProperty.get();
    }

    public StringProperty strokeLabelProperty() {
        return this.clusterLabelProperty;
    }
    
    public void setStrokeDuration( String strokeDuration ) {
        this.clusterCountProperty.set( strokeDuration );
    }

    public String getStrokeDuration() {
        return this.clusterCountProperty.get();
    }

    public StringProperty strokDurationProperty() {
        return this.clusterCountProperty;
    }
}