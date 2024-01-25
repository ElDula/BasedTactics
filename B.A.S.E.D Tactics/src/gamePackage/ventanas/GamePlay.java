package gamePackage.ventanas;
import  gamePackage.entidades.terrestres.*;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.Math;
import  gamePackage.entidades.*;
import  gamePackage.ventanas.*;
import  gamePackage.logica.*;
import gamePackage.terrenos.Estructura;
import gamePackage.terrenos.Terreno;


public  class GamePlay {
// metodos para la partida	
	public static int captureCalc(Tropa t,Estructura c) {
	
		c.setHp(c.getHp()-	t.getHP()*2);
		if (c.getHp()<=0) {
			if (c.getCG()==true) {
				return 2;
			}
			c.setHp(2);
			return 1;
			
		}
		return 0;
	}
	public static float damage(Tropa atacante, Tropa defensor, Terreno cobertura){
		float damage = 0; 
		
		
		switch (weaponChoice(atacante,defensor)) {
		case 0:
			damage = ((atacante.getGolpePrim() * damageNumbers(atacante.getArmas()[0] , defensor.getBlindaje())* atacante.getHP()) * (((atacante.getNivel()/10)+1)) * (1 - (cobertura.getDefensa()/10)));
			damage = damage + (damage * (float) Math.random()/20);
			break;
		case 1:
			damage = ((atacante.getGolpeSec() * damageNumbers(atacante.getArmas()[1] , defensor.getBlindaje())   * atacante.getHP()) * ((atacante.getNivel()/10)+1) * (1 - (cobertura.getDefensa()/10)));
			damage = damage + (damage * (float) Math.random()/20);
			break;
		case 2:
			damage = 0;
			break;
		}
		return damage;
	}
	public static ArrayList<Tropa> combat(Tropa atacante, Tropa defensor, Terreno cobertura, Terreno cobertura1){
		ArrayList<Tropa> results = new ArrayList<Tropa>(); 

		defensor.setSalud((defensor.getSalud()-damage(atacante, defensor, cobertura1)));
		if (weaponChoice(atacante, defensor)==0) {
			atacante.setMuniciones(atacante.getMuniciones()-1);
		}
		if (defensor.getSalud()<=0) {
			defensor.setSalud(0);
			if (atacante.getNivel()<3) {
				atacante.setNivel(atacante.getNivel()+1);
				}
		} else if (defensor.getAlcance()[0]==1 && atacante.getAlcance()[1]==1) {
			
			atacante.setSalud((atacante.getSalud()-damage(defensor, atacante, cobertura)));
			if (weaponChoice(defensor, atacante)==0) {
				defensor.setMuniciones(atacante.getMuniciones()-1);
			}
			if (atacante.getSalud()<=0) {
				atacante.setSalud(0);
				if (defensor.getNivel()<3) {
					defensor.setNivel(defensor.getNivel()+1);		
				}		
			}		
		}
		results.add(atacante);
		results.add(defensor);
		return results;
	}
	
	public static ArrayList<Point> rangeCalc(int range){
		ArrayList<Point> targetList = new ArrayList<Point>();	
		int max = range;
		int d=max;
		ArrayList<Integer> xMax = new ArrayList<Integer>();
		ArrayList<Integer> yMax = new ArrayList<Integer>();
		for (int i = -max; i <= max; i++) {
			yMax.add(i);
		}
		for (int i =0; i <max+1; i++) {
			
			xMax.add(i);
			d = d-1;
			}

		for (int i = 0; i <max*2+1; i++) {
			int test =max - Math.abs(yMax.get(i));
			for (int v=1; v<test+1;v++) {
				
				Point target= new Point();
				target.setLocation(xMax.get(v), yMax.get(i));		
				if (!(targetList.contains(target))) {
					targetList.add(target);
				}
				Point targetN= new Point();
				targetN.setLocation(-xMax.get(v), yMax.get(i));		
				if (!(targetList.contains(targetN))) {
					targetList.add(targetN);
				}
			}
			if(!(yMax.get(i)==0)) {
				Point target= new Point();
				target.setLocation(xMax.get(0), yMax.get(i));		
				if (!(targetList.contains(target))) {
					targetList.add(target);
				}
				}
		}
		
		return targetList;
	}
	
	public static ArrayList<Point> rangeFind(Tropa troop){
		ArrayList<Point> result = new ArrayList<Point>();	
		result = removePointFrom(rangeCalc(troop.getAlcance()[0]-1) , rangeCalc(troop.getAlcance()[1]));
		
		return result;
	}
	public static ArrayList<Point> removePointFrom(ArrayList<Point> a, ArrayList<Point> b){
		
		for (int i = 0; i < a.size(); i++) {
			for (int j = 0; j < b.size(); j++) {
				if (a.get(i).equals(b.get(j))) {
					b.remove(j);
				}
			}
		
		}
		
		return b;
	}
	public static Point sumaPoints(Point a, Point b) {
		Point c = new Point();	
		c.setLocation((a.getX() + b.getX()) , (a.getY()+ b.getY()));
		return c;
	}
	
	//arma que usa cada tropa en combate 
	public static int weaponChoice(Tropa  a , Tropa d) {
		
		int attack = 0;
		if (d.getIDTropa()==ListaIDTropa.INF) {
			switch (a.getArmas()[0]) {
			case ARMA_AA:
				attack = 0;
				break;
			case CANNON:
				attack = 0;
				break;
			case COHETE:
				attack = 0;
				break;
			default:
				if (a.getArmas()[1]==ListaArmas.AMETRALLADORA) {
					attack= 1;
				}
				else {
					attack=2;
					}	
				break;
			}
		}
		else {
			if (a.getArmas()[0]==ListaArmas.NADA) {
				attack=1;
			}else {
				attack=0;
			}
		}
		if (attack==0) {
			if (a.getMuniciones() == 0) {
				if (a.getArmas()[1]==ListaArmas.AMETRALLADORA) {
					attack= 1;
				}
				else {
					attack=2;
					}	
				
			}
		}
		return attack;
	}
	public static float damageNumbers(ListaArmas a , ListaBlindaje d) {
		float reduction = 100;
		switch (a) {
		case AMETRALLADORA:
			switch (d) {
			case INFANTERIA:
				reduction= 100;
				break;
			case VH_L:
				reduction= 75;
				break;
			case VH_H:
				reduction= 45;
				break;
			case TK:
				reduction= 10;
				break;
			}		
			break;
		case TCANNON:
			switch (d) {
			case VH_L:
				reduction= 120;
				break;
			case VH_H:
				reduction= 100;
				break;
			case TK:
				reduction= 100;
				break;
			default:
				break;
			}
			
			break;
		case TCANNON_MED:
			switch (d) {
			case VH_L:
				reduction= 120;
				break;
			case VH_H:
				reduction= 100;
				break;
			case TK:
				reduction= 100;
				break;
			default:
				break;	
			}
			
			break;
		case CANNON:
			switch (d) {
			case INFANTERIA:
				reduction= 50;
				break;
			case VH_L:
				reduction= 100;
				break;
			case VH_H:
				reduction= 100;
				break;
			case TK:
				reduction= 100;
				break;
			}
			
			break;
		case ARMA_AA:
			switch (d) {
			case INFANTERIA:
				reduction= 100;
				break;
			case VH_L:
				reduction= 75;
				break;
			case VH_H:
				reduction= 60;
				break;
			case TK:
				reduction= 10;
				break;
			default:
				break;

			}
			break;
		case COHETE:
			switch (d) {
			case INFANTERIA:
				reduction= 50;
				break;
			case VH_L:
				reduction= 80;
				break;
			case VH_H:
				reduction= 80;
				break;
			case TK:
				reduction= 100;
				break;
			}
			
			break;
			
		default:
			reduction=100;
			break;
		}
		reduction=reduction/100;
		return reduction;
	}
	
	
}

