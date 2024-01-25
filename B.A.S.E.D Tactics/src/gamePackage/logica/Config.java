package gamePackage.logica;

import java.io.Serializable;
import java.util.ArrayList;

public class Config implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8523439484706281581L;
	int buildingCountRed;
	int buildingCountBlue;
	int buildingCountGreen;
	int buildingCountOrange;
	ArrayList<Player> players = new ArrayList();
	
	public Config(int r, int b, int g, int o) {
		super();
		this.buildingCountBlue=b;
		this.buildingCountGreen=g;
		this.buildingCountOrange=o;
		this.buildingCountRed=r;
	}
	public String toString() {
		return "Red "+ this.buildingCountRed + " Blue " + this.buildingCountBlue + " Green " + this.buildingCountGreen + " Orange "+ this.buildingCountOrange;
	}
	public int getRed() {
		return buildingCountRed;
	}
	public void setRed(int buildingCountRed) {
		this.buildingCountRed = buildingCountRed;
	}
	public int getBlue() {
		return buildingCountBlue;
	}
	public void setBlue(int buildingCountBlue) {
		this.buildingCountBlue = buildingCountBlue;
	}
	public int getGreen() {
		return buildingCountGreen;
	}
	public void setGreen(int buildingCountGreen) {
		this.buildingCountGreen = buildingCountGreen;
	}
	public int getOrange() {
		return buildingCountOrange;
	}
	public void setOrange(int buildingCountOrange) {
		this.buildingCountOrange = buildingCountOrange;
	}
	public void addRed(){
		this.buildingCountRed = this.buildingCountRed+1;
	}
	public void subRed() {
		this.buildingCountRed = this.buildingCountRed-1;
	}
	public void addBlu(){
		this.buildingCountBlue = this.buildingCountBlue+1;
	}
	public void subBlu() {
		this.buildingCountBlue = this.buildingCountBlue-1;
	}
	public void addGrn(){
		this.buildingCountGreen = this.buildingCountGreen+1;
	}
	public void subGrn() {
		this.buildingCountGreen = this.buildingCountGreen-1;		
	}
	public void addOrg(){
		this.buildingCountOrange = this.buildingCountOrange+1;
	}
	public void subOrg() {
		this.buildingCountOrange = this.buildingCountOrange-1;
	}
	public void setPlayers(ArrayList<Player> players) {
		this.players=players;
	}
	public ArrayList<Player> getPlayers() {
		return	this.players;
	}

	
}
