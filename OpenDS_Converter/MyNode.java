package org.matsim.project;

public class MyNode {

	private double x;
	//y is height of this node (always 20 for our scenarios)
	private double y = 28;
	private double z;
	private int id;
	
	public MyNode() {
		// TODO Auto-generated constructor stub
	}

	public double getX() {
		return x;
	}

	public void setX(double nodeX) {
		this.x = nodeX;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}

	public void setZ(double nodeZ) {
		this.z = nodeZ;
	}
	
	
	public double getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}
}
