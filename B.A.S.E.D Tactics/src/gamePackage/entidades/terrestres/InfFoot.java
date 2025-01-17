package gamePackage.entidades.terrestres;

import java.awt.Point;
import java.io.Serializable;

import gamePackage.entidades.Tropa;
import gamePackage.logica.*;

public class InfFoot implements Tropa , Serializable{

	// ATRIBUTOS LOGICOS
	int team;
	ListaIDTropa idTropa = ListaIDTropa.INF;
	ListaTipos tipoTropa = ListaTipos.INFANTERIA;
	ListaTipos tipoMov = ListaTipos.INFANTERIA;
	ListaBlindaje tipoBlindaje=ListaBlindaje.INFANTERIA;
	boolean captura = true;
	boolean construye = false;
	int saludMaxima = 100;
	float salud = this.saludMaxima;
	int golpePrim = 0;
	int golpeSec = 55;
	ListaArmas armaPrincipal = ListaArmas.NADA;
	ListaArmas armaSecundaria = ListaArmas.AMETRALLADORA;
	int precio = 1500;
	int nivel = 0;
	int vision = 2;
	int alcanceMin = 1;
	int alcanceMax = this.alcanceMin;
	int municionesMax = 0;
	int municionesPrim = this.municionesMax;
	int energiaMaxima = 99;
	int energia = this.getEnergiaMaxima();
	int distanciaMaxima = 3;
	int distancia= this.distanciaMaxima;
	// ATRIBUTOS FISICOS
	String nombre = "Infanteria";
	Point pos = new Point(0, 0); // Posicion
	String imagen = ""; // Dirección de imagen
	

	// Constructor
	public InfFoot(int team) {
		super();
		this.team = team;
	}

	// ToString
	@Override
	public String toString() {
		return "InfFoot [idTropa=" + idTropa + ", tipoTropa=" + tipoTropa + ", tipoMov=" + tipoMov + ", captura="
				+ captura + ", construye=" + construye + ", saludMaxima=" + saludMaxima + ", salud=" + salud
				+ ", golpePrim=" + golpePrim + ", golpeSec=" + golpeSec + ", armaPrincipal=" + armaPrincipal
				+ ", armaSecundaria=" + armaSecundaria + ", precio=" + precio + ", nivel=" + nivel + ", vision="
				+ vision + ", alcanceMin=" + alcanceMin + ", alcanceMax=" + alcanceMax + ", municionesMax="
				+ municionesMax + ", municionesPrim=" + municionesPrim + ", energiaMaxima=" + energiaMaxima
				+ ", energia=" + energia + ", distanciaMaxima=" + distanciaMaxima + ", nombre=" + nombre + ", pos="
				+ pos + ", imagen=" + imagen +"team=" + team +"]";
	}

	// Metodos Heredados
	// Setters

	public float getHP() {
		float percent = (getSalud()/getSaludMax());
		percent = percent * 10;
		percent=(float) Math.ceil(percent);
		percent=percent/10;
		return percent;
		 
	}
	@Override
	public void setSalud(float salud) {
		// TODO Auto-generated method stub
		if (salud <= saludMaxima)
			this.salud = salud;
		else {
			// Logger?
		}
	}


	@Override
	public void setEnergia(int energia) {
		// TODO Auto-generated method stub
		if (energia <= getEnergiaMaxima())
			this.energia = energia;
		else {
			// Logger?
		}
	}

	@Override
	public void setMuniciones(int municiones) {
		// TODO Auto-generated method stub
		if (municiones <= municionesMax)
			this.municionesPrim = municiones;
		else {
			// Logger?
		}
	}

	@Override
	public void setNivel(int nivel) {
		// TODO Auto-generated method stub
		this.nivel = nivel;
	}

	// Getters
	@Override
	public String getNombre() {
		// TODO Auto-generated method stub
		return nombre;
	}

	@Override
	public ListaIDTropa getIDTropa() {
		// TODO Auto-generated method stub
		return getIdTropa();
	}

	@Override
	public ListaTipos getTipoTropa() {
		// TODO Auto-generated method stub
		return tipoTropa;
	}

	@Override
	public ListaTipos getTipoMov() {
		// TODO Auto-generated method stub
		return tipoMov;
	}

	@Override
	public ListaArmas[] getArmas() {
		ListaArmas[] armas = new ListaArmas[2];
		armas[0] = armaPrincipal;
		armas[1] = armaSecundaria;
		// TODO Auto-generated method stub
		return armas;
	}

	@Override
	public boolean getCaptura() {
		// TODO Auto-generated method stub
		return captura;
	}

	@Override
	public int getPrecio() {
		// TODO Auto-generated method stub
		return precio;
	}

	@Override
	public boolean getConstruye() {
		// TODO Auto-generated method stub
		return construye;
	}

	@Override
	public float getSalud() {
		// TODO Auto-generated method stub
		return salud;
	}

	@Override
	public int getGolpePrim() {
		// TODO Auto-generated method stub
		return golpePrim;
	}

	@Override
	public int getGolpeSec() {
		// TODO Auto-generated method stub
		return golpeSec;
	}

	@Override
	public int getDistMax() {
		// TODO Auto-generated method stub
		return distanciaMaxima;
	}

	@Override
	public int getEnergia() {
		// TODO Auto-generated method stub
		return energia;
	}

	@Override
	public int[] getAlcance() {
		int[] alc = new int[2];
		alc[0] = alcanceMin;
		alc[1] = alcanceMax;
		// TODO Auto-generated method stub
		return alc;
	}

	@Override
	public int getMuniciones() {
		// TODO Auto-generated method stub
		return municionesPrim;
	}

	@Override
	public int getVision() {
		// TODO Auto-generated method stub
		return vision;
	}

	@Override
	public int getNivel() {
		// TODO Auto-generated method stub
		return nivel;
	}

	// Metodos Logicos
	@Override
	public boolean atacaA(ListaTipos tipoTropa) {
		// TODO Auto-generated method stub
		boolean target;
		switch (tipoTropa) {
		case AEREO:
			target = true;
			break;
		case ACORAZADO:
			target = true;
			break;
		case V_LIGERO:
			target = true;
			break;
		case V_PESADO:
			target = true;
			break;
		case ACUATICO:
			target = false;
			break;
		case SUBACUATICO:
			target = false;
			break;
		case INFANTERIA:
			target = true;
			break;
		default:
			target = false;
		}

		return target;
	}

//Metodos Inutiles Para Esta Tropa
	@Override
	public void setTipoMov(ListaTipos tipoMov) {
		// TODO Auto-generated method stub
		// Nada
	}

	@Override
	public void setTipoTropa(ListaTipos tipoTropa) {
		// TODO Auto-generated method stub
		// Nada
	}

//Metodos Unicos Para Esta Tropa
	// Setters
	public void setVision(int vis) {
		this.vision = vis;
	}
	// Getters

	@Override
	public int getSaludMax() {
		// TODO Auto-generated method stub
		return saludMaxima;
	}

	@Override
	public int getEnergiaMax() {
		// TODO Auto-generated method stub
		return getEnergiaMaxima();
	}

	@Override
	public int getMunicionesMax() {
		// TODO Auto-generated method stub
		return municionesMax;
	}

	@Override
	public boolean necesitaSuministro() {
		if (this.municionesPrim < this.municionesMax || this.salud < this.saludMaxima
				|| this.energia < this.getEnergiaMaxima()) {
			return true;
		} else
			return false;
	}

	public int getEnergiaMaxima() {
		return energiaMaxima;
	}

	public ListaIDTropa getIdTropa() {
		return idTropa;
	}

	@Override
	public void setTropa() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPos() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setImagen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTeam(int team) {
		// TODO Auto-generated method stub
		this.team=team;
		
	}

	@Override
	public int getTeam() {
		// TODO Auto-generated method stub
		return team;
	}

	@Override
	public Point getPos() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListaBlindaje getBlindaje() {
		// TODO Auto-generated method stub
		return tipoBlindaje;
	}

	@Override
	
	public int getDistancia() {
		return this.distancia;
	}
	public void setDistancia(int i) {
		this.distancia=i;
	}

}
