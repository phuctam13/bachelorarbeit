package org.matsim.project;

/**
 * @author Phuc Tam Nguyen Van
 * Network link class for the network converter (created my own link, because I wanted to add 
 * necessary attributes to the class
 */
public class MyLink {

	private double mittelpunktX;
	private double mittelpunktY;
	private double rotation;
	private double durchmesser;
	
	public MyLink() {
		
	}

	public double getMittelpunktX() {
		return mittelpunktX;
	}

	public void setMittelpunktX(double mittelpunktX) {
		this.mittelpunktX = mittelpunktX;
	}

	public double getMittelpunktY() {
		return mittelpunktY;
	}

	public void setMittelpunktY(double mittelpunktY) {
		this.mittelpunktY = mittelpunktY;
	}

	public double getRotation() {
		return rotation;
	}

	public void setRotation(double rotation) {
		this.rotation = rotation;
	}

	public double getDurchmesser() {
		return durchmesser;
	}

	public void setDurchmesser(double durchmesser) {
		this.durchmesser = durchmesser;
	}

	
	
	

}
