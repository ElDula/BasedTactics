package gamePackage.ventanas;
import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.Timer;

import gamePackage.sonidos.SoundMngr;
import gamePackage.entidades.Tropa;
import gamePackage.baseDatos.DataBase;
import gamePackage.entidades.*;
import gamePackage.logica.*;
import gamePackage.entidades.terrestres.*;
import gamePackage.terrenos.*;
import gamePackage.terrenos.estructuras.*;
import gamePackage.terrenos.terrenos.*;
import gamePackage.ventanas.*;
import gamePackage.ventanas.GamePlay;


@SuppressWarnings("serial")
public class Game extends JFrame{
	
	public static void main(String[] args){
		//creación de la instancia de la ventana y modificacion de algunos de sus atributos para que sea visible 
		Game juego = new Game("T");
		juego.pack();
		juego.setLocationRelativeTo(null);
		juego.setResizable(false);
		juego.setVisible(true);
		Logger logger = Logger.getLogger(Game.class.getName());

	}
	//public static int tiles = 17;  <-- Viejo tamaño de las casillas
	public static int mov = 32;		//Tamaño de las casillas y valor por el que se multiplica el valor de x e y de los labels
	public volatile HashMap<Point, ArrayList<ArrayList<Object>>> mapGrid = new HashMap<>();
	public int turn = 1;
	public boolean estadoMov = false;
	Point ogPos = new Point();
	Point attackPos = new Point();
	Point po = new Point(-1,-1);
	Player p1 = new Player(1);
	Player p2 = new Player(2);
	Player p3 = new Player(3);
	Player p4 = new Player(4);
	SoundMngr sic = new SoundMngr("combat1.wav",0,0);
	Thread mus = new Thread(sic);
	JInternalFrame[] shop ;
	ArrayList<Tropa> spentList = new ArrayList();
	ArrayList<JLabel> movedList = new ArrayList();
	ArrayList<Point> targetList = new ArrayList<Point>();	
	ArrayList<Point> casillaList = new ArrayList<Point>();	
	boolean moveSt=false;
	boolean attackSt=false;
	boolean targetSt=false;
	ArrayList<String> lastMov = new ArrayList();
	ArrayList<Point> targets = new ArrayList(); 
	ArrayList<JLabel> tList = new ArrayList(); 
	int lastPeaje;
	int ogFuel=0;
	int z=0;
	ArrayList<Integer> turnTest = new ArrayList();
	int turnInt=1;
	Tropa sel;
	Terreno selGrd;
	Tropa defender;
	Terreno defenderGrd;
	Point cN;
	Point cS;
	Point cE;
	Point cO;
	int peajeN;
	int peajeS;
	int peajeE;
	int peajeO;
	int building1 = 0;
	int building2 = 0;
	int building3 = 0;
	int building4 = 0;
	GamePlay gp = new GamePlay();
	JPanel movData = new JPanel();	
	
	public Game(String nomMapa) {
		mus.start();
		DataBase dataBase = new DataBase();
		dataBase.getConn();
		Container cp = this.getContentPane();
		cp.setLayout(new BoxLayout(cp, BoxLayout.X_AXIS));		//Se le pone un BoxLayout al contenedor de la ventana en el eje X que coloca los componentes en serie horizontalmente
		JLayeredPane layeredGamePanel = new JLayeredPane();		//Creación de un panel que permite colocar unos componentes por encima de otros
		//layeredGamePanel.setBackground(Color.green);		//for testing
		layeredGamePanel.setBounds(0, 0, 672, 672);		//Posición y tamaño del panel del juego
		layeredGamePanel.setPreferredSize(new Dimension(672, 672));		//Tamaño preferido para el panel que hace que alguna instrucción no ignore este valor
		
		JLabel cursor = new JLabel();		//Label que contiene el cursor
		JLabel explosion = new JLabel();
		
		//Instrucciones para colocar las imagenes en los labels
		cursor.setIcon(new ImageIcon(getClass().getResource("img/Cursor.gif")));
		explosion.setIcon(new ImageIcon(getClass().getResource("img/explosion.gif")));
		//mapLabel.setPreferredSize(new Dimension(672, 672));		//De nuevo se coloca el tamaño preferido para que las instrucciones tiendan a usar este valor
		
		JPanel mapPanel = new JPanel();		//Creación del panel en el que está el label del mapa (más tarde serán muchos componentes que forman un mapa)
		mapPanel.setLayout(null);		//Se le pone un BoxLayout al panel del mapa en el eje X que coloca los componentes en serie horizontalmente
		//mapPanel.add(mapLabel);		//Añadir el label que contiene el mapa
		mapPanel.setBounds(0, 0, 672, 672);		//Posición y tamaño del panel del mapa
		//mapPanel.setOpaque(true);		//Hace que se pueda ver lo que haya detrás del panel
		
		JPanel troopPanel = new JPanel();
		troopPanel.setLayout(null);
		troopPanel.setBounds(0, 1, 672, 672);
		troopPanel.setOpaque(false);
		lastMov.add("Start");
		
		//-------------------------------------------------------------------------
		

		
		
		//-------------------------------------------------------------------------
		
		/*
		ArrayList<ListaIDTerreno> buildings = new ArrayList<>(); buildings.add(ListaIDTerreno.CITY);buildings.add(ListaIDTerreno.FACTORY);buildings.add(ListaIDTerreno.HQ);
		int building1 = 0;
		int building2 = 0;
		int building3 = 0;
		int building4 = 0;
		for (Point p : mapGrid.keySet()) {
			try {
				Estructura struc = (Estructura) mapGrid.get(p).get(0).get(1);
				if (buildings.contains(struc.getIDTerreno())) {
					switch (struc.getTeam()) {
					case 1: 
						building1+=1;
						break;
					case 2:
						building2+=1;
						break;
					case 3:
						building3+=1;
						break;
					case 4:
						building4+=1;
						break;
					default:
						break;
					}
				}
			} catch (Exception e) {
				
			}
			//System.out.println(building1 + "-" + building2);	
		}
		*/
		

		cursor.setPreferredSize(new Dimension(32, 32));		//Tamaño preferido del label que contiene el gif del cursor
		cursor.setBounds(mov*8, mov*8, 32, 32);		//Lo mismo de antes pero siendo la posición el centro del mapa
		explosion.setPreferredSize(new Dimension(32, 32));		//Tamaño preferido del label que contiene el gif del cursor
		
		
		JLayeredPane entityPanel = new JLayeredPane();		//Creación del panel que contiene las entidades como tropas o edificios
		entityPanel.setLayout(null);		//Se le pone layout nulo para que deje poner componentes mediante posiciones absolutas
		entityPanel.setBounds(0, 0, 672, 672);		//Posición y tamaño del panel de entidades
		entityPanel.add(cursor, 3, 0);		//Se añade el label del cursor con una prioridad mayor que hace que esté sobre las tropas y entidades
		
		entityPanel.add(troopPanel, 1, 0);
		entityPanel.setOpaque(false);		//Se cambia el atributo del panel para hacer que se pueda ver lo que tiene debajo (otro panel)
		
		layeredGamePanel.add(mapPanel, 0, 0);		//Se añade el panel que contiene el mapa con prioridad baja para que esté por debajo del resto de cosas que se añadan
		layeredGamePanel.add(entityPanel, 1, 0);		//Se añade el panel de entidades con mayor prioridad que el del mapa para que se vean por encima de este
		JPanel troopInfo = new JPanel();		//Creación del panel inferior con información de tropas
		troopInfo.setBackground(Color.black);		//Instrucción de prueba para diferenciar los paneles mientras no estén programados
		troopInfo.setPreferredSize(new Dimension(256, 256));		//Tamaño preferido del panel de información de tropas
		//troopInfo.setLayout(new BoxLayout(troopInfo, BoxLayout.X_AXIS));
		/*class ThreadMV implements Runnable{
			volatile boolean stateSwitcher = false;
			volatile boolean on = true;
		
			Tropa t = (Tropa) mapGrid.get(ogPos).get(1).get(1);
			Point pos = ogPos;
			
			@Override
			
			public void run() {
				
				
				
				while (on) {
					while(stateSwitcher == true) {
						mapGrid = loadMap();
						Point mouse = MouseInfo.getPointerInfo().getLocation();
						SwingUtilities.convertPointFromScreen(mouse, layeredGamePanel);
						try {
							Thread.sleep(10);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						double x = mouse.getX();
						double y = mouse.getY();
						x = x / mov; y = y / mov;
						x = Math.floor(x); y = Math.floor(y);
						Point cN = new Point((int)pos.getX(), (int)pos.getY()-1);
						Point cS = new Point((int)pos.getX(), (int)pos.getY()+1);
						Point cE = new Point((int)pos.getX()+1, (int)pos.getY());
						Point cO = new Point((int)pos.getX()-1, (int)pos.getY());
						int peajeN = ((Terreno) mapGrid.get(cN).get(0).get(1)).getPeaje(t);
						int peajeS = ((Terreno) mapGrid.get(cS).get(0).get(1)).getPeaje(t);
						int peajeE = ((Terreno) mapGrid.get(cE).get(0).get(1)).getPeaje(t);
						int peajeO = ((Terreno) mapGrid.get(cO).get(0).get(1)).getPeaje(t);
						if (((t.getDistMax()-peajeN)>=0) && (t.getEnergia() != 0) && (mapGrid.get(cN).size() == 1) && (mouse.equals(cN))) {
							cursor.setLocation(cN);
						} else if (((t.getDistMax()-peajeS)>=0) && (t.getEnergia() != 0) && (mapGrid.get(cS).size() == 1) && (mouse.equals(cS))) {
							cursor.setLocation(cS);
						} else if (((t.getDistMax()-peajeE)>=0) && (t.getEnergia() != 0) && (mapGrid.get(cE).size() == 1) && (mouse.equals(cE))) {
							cursor.setLocation(cE);
						} else if (((t.getDistMax()-peajeO)>=0) && (t.getEnergia() != 0) && (mapGrid.get(cO).size() == 1) && (mouse.equals(cO))) {
							cursor.setLocation(cO);
						}
						if (x>= 0 && x <= 21 && y>= 0 && y <= 21) {
							int xCT = (int) (x * mov); int yCT = (int) (y * mov); //xCT = x Cursor Tile 
							cursor.setLocation(xCT, yCT);
						}
						
					}
				}
			}
			public void pause() {
				stateSwitcher = false;
			}
			
			public void resume() {
				stateSwitcher = true;
			}
			

			
		}
*/
		//ThreadMV mv = new ThreadMV();
		//Thread tMV = new Thread(mv);
		
		
		class CursorMovement implements Runnable{
			volatile boolean stateSwitcher = false;
			volatile boolean on = true;
			@Override
			public void run() {
				while (on) {
					while(stateSwitcher == true && !(moveSt)) {
						Point mouse = MouseInfo.getPointerInfo().getLocation();
						SwingUtilities.convertPointFromScreen(mouse, layeredGamePanel);
						try {
							Thread.sleep(10);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						double x = mouse.getX();
						double y = mouse.getY();
						x = x / mov; y = y / mov;
						x = Math.floor(x); y = Math.floor(y);
						if (x>= 0 && x <= 21 && y>= 0 && y <= 21) {
							int xCT = (int) (x * mov); int yCT = (int) (y * mov); //xCT = x Cursor Tile 
							cursor.setLocation(xCT, yCT);
						}
					}
				}
			}
			public void pause() {
				stateSwitcher = false;
			}
			
			public void resume() {
				stateSwitcher = true;
			}
			

			
		}

		CursorMovement cm = new CursorMovement();
		Thread tCM = new Thread(cm);
		tCM.start();
		
		//----------------------------------
		
		
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(nomMapa + ".dat"));
			
			mapGrid = (HashMap) ois.readObject();
			//System.out.println("ini");
			//System.out.println(mapGrid.get(new Point(0,0)).get(0).get(0));
			//System.out.println("fin");

			ois.close();

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//System.out.println(mapGrid);
		mapPanel.removeAll();
		
		Set<Point> keys = mapGrid.keySet();
		JLabel jl = new JLabel();
		for (Point i : keys) {
			jl = (JLabel) mapGrid.get(i).get(0).get(0);
			mapPanel.add(jl);
		}
		Point p = new Point(-1,-1);
		if (((Config) mapGrid.get(p).get(0).get(1)).getRed()!=0) {
			turnTest.add(1);
		}else {
			JLabel bk = new JLabel();
			troopInfo.add(bk);
			bk.setBounds(0,0,256,64);
			bk.setIcon(new ImageIcon(getClass().getResource("img/UI/BlackOut.png")));
		}
		if (((Config) mapGrid.get(p).get(0).get(1)).getBlue()!=0) {
			turnTest.add(2);
		}else {
			JLabel bk = new JLabel();
			troopInfo.add(bk);
			bk.setBounds(0,64,256,64);
			bk.setIcon(new ImageIcon(getClass().getResource("img/UI/BlackOut.png")));
		}
		if (((Config) mapGrid.get(p).get(0).get(1)).getGreen()!=0) {
			turnTest.add(3);
		}else {
			JLabel bk = new JLabel();
			troopInfo.add(bk);
			bk.setBounds(0,128,256,64);
			bk.setIcon(new ImageIcon(getClass().getResource("img/UI/BlackOut.png")));
		}
		if (((Config) mapGrid.get(p).get(0).get(1)).getOrange()!=0) {
			turnTest.add(4);
		}else {
			JLabel bk = new JLabel();
			troopInfo.add(bk);
			bk.setBounds(0,192,256,64);
			bk.setIcon(new ImageIcon(getClass().getResource("img/UI/BlackOut.png")));
		}
		turnInt=turnTest.get(0);
		switch(turnInt) {
			case 1:
				p1.setfunds(((Config) mapGrid.get(po).get(0).get(1)).getRed()*1000);
				break;
			case 2:
				p2.setfunds(((Config) mapGrid.get(po).get(0).get(1)).getBlue()*1000);
				break;
			case 3:
				p3.setfunds(((Config) mapGrid.get(po).get(0).get(1)).getGreen()*1000);
				break;
			case 4:
				p4.setfunds(((Config) mapGrid.get(po).get(0).get(1)).getOrange()*1000);
				break;
		}
		mapPanel.repaint();
		
		//----------------------------------
		
		MouseListener ml = new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				cm.pause();	
				entityPanel.requestFocus();
				
				Point casilla = new Point((int) cursor.getX()/32, (int) cursor.getY()/32);
				Point conf = new Point(-1,-1);
				if (mapGrid.get(casilla).size() == 2) {
					if (estadoMov == false) {
						if (((Tropa) mapGrid.get(casilla).get(1).get(1)).getTeam() == turnInt) {
								estadoMov = true;
								ogPos = casilla;
								cm.pause();
						} 
					}
				 else {
				
					cm.resume();
					estadoMov = false;
				 }
				} else if (!(((Terreno) mapGrid.get(casilla).get(0).get(1)).getIDTerreno() != ListaIDTerreno.FACTORY || mapGrid.get(casilla).size() == 2)) {
					if (((Factory) mapGrid.get(casilla).get(0).get(1)).getTeam() == turnInt) {
						createTropa(cursor.getLocation(), mapGrid, layeredGamePanel, troopPanel, turn, casilla, entityPanel);
						} 
				}
				System.out.println();
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
			
				cm.resume();
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		};
		
		
		
		//KeyListener para mover el cursor
		KeyListener kListener = new KeyListener() {
			boolean released = true;		//Redundante de momento pero sirve para nno dejar que se mantenga pulsada la tecla y haya que soltar la tecla antes de que se vuelva a registrar
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				released = true;
				int key = e.getKeyCode();
				if (key == KeyEvent.VK_A) {
					 targetSt=false;
					 for (int i = 0; i < tList.size(); i++) {
						 entityPanel.remove(tList.get(i));
						
					}
					entityPanel.repaint();
				}
			}
			int z=0;
			@Override
			public void keyPressed(KeyEvent e) { 
				// TODO Auto-generated method stub
				int x = cursor.getLocation().x;
				int y = cursor.getLocation().y;
				int key = e.getKeyCode();
				
				if (key == KeyEvent.VK_LEFT && x > 0) {
					if(moveSt) {
						if (!(lastMov.get(lastMov.size()-1)=="E")){
						if(sel.getDistancia()>=peajeO) {
							if(!(mapGrid.get(cO).size()==2 && ((Tropa) mapGrid.get(cO).get(1).get(1)).getTeam()!=sel.getTeam())){
								cursor.setLocation(x - mov, y);
								Point casilla = new Point((int) cursor.getX()/32, (int) cursor.getY()/32);
								sel.setDistancia(sel.getDistancia()-peajeO);
								cN = new Point((int)casilla.getX(), (int)casilla.getY()-1);
								cS = new Point((int)casilla.getX(), (int)casilla.getY()+1);
								cE = new Point((int)casilla.getX()+1, (int)casilla.getY());
								cO = new Point((int)casilla.getX()-1, (int)casilla.getY());
								if(mapGrid.get(cN)!=null)peajeN = ((Terreno) mapGrid.get(cN).get(0).get(1)).getPeaje(sel);
								if(mapGrid.get(cS)!=null)peajeS = ((Terreno) mapGrid.get(cS).get(0).get(1)).getPeaje(sel);
								if(mapGrid.get(cE)!=null)peajeE = ((Terreno) mapGrid.get(cE).get(0).get(1)).getPeaje(sel);
								if(mapGrid.get(cO)!=null)peajeO = ((Terreno) mapGrid.get(cO).get(0).get(1)).getPeaje(sel);
								lastMov.add("O");
							}
							
						}
						}else {
							cursor.setLocation(x - mov, y);
							Point casilla = new Point((int) cursor.getX()/32, (int) cursor.getY()/32);
							cN = new Point((int)casilla.getX(), (int)casilla.getY()-1);
							cS = new Point((int)casilla.getX(), (int)casilla.getY()+1);
							cE = new Point((int)casilla.getX()+1, (int)casilla.getY());
							cO = new Point((int)casilla.getX()-1, (int)casilla.getY());
							sel.setDistancia(sel.getDistancia()+((Terreno) mapGrid.get(cE).get(0).get(1)).getPeaje(sel));
							if(mapGrid.get(cN)!=null)peajeN = ((Terreno) mapGrid.get(cN).get(0).get(1)).getPeaje(sel);
							if(mapGrid.get(cS)!=null)peajeS = ((Terreno) mapGrid.get(cS).get(0).get(1)).getPeaje(sel);
							if(mapGrid.get(cE)!=null)peajeE = ((Terreno) mapGrid.get(cE).get(0).get(1)).getPeaje(sel);
							if(mapGrid.get(cO)!=null)peajeO = ((Terreno) mapGrid.get(cO).get(0).get(1)).getPeaje(sel);
							
							lastMov.remove(lastMov.size()-1);
						}
					}else if(attackSt){
						
						
						if(z>0) {
							z--;
						}else {
							z=targets.size()-1;
						}
						Point casilla = new Point((int) targets.get(z).getX()*mov, (int) targets.get(z).getY()*mov);
						cursor.setLocation(casilla);
						defender=(Tropa) mapGrid.get(targets.get(z)).get(1).get(1);
						defenderGrd= (Terreno) mapGrid.get(targets.get(z)).get(0).get(1);
					}else {
						cursor.setLocation(x - mov, y);
					}
			        //System.out.println("left");
					//released = false;		//quitar el comentario del inicio de esta linea para dar uso a la variable released
					System.out.println(lastMov);
			    }

			    if (key == KeyEvent.VK_RIGHT && x < 640) {
			    	//System.out.println(moveSt);
			    	
			    	if(moveSt) {
			    		if (!(lastMov.get(lastMov.size()-1)=="O")){
							
							
							if(sel.getDistancia()>=peajeE) {
								if(!(mapGrid.get(cE).size()==2 && ((Tropa) mapGrid.get(cE).get(1).get(1)).getTeam()!=sel.getTeam())){
								cursor.setLocation(x + mov, y);
								Point casilla = new Point((int) cursor.getX()/32, (int) cursor.getY()/32);
								sel.setDistancia(sel.getDistancia()-peajeE);
								cN = new Point((int)casilla.getX(), (int)casilla.getY()-1);
								cS = new Point((int)casilla.getX(), (int)casilla.getY()+1);
								cE = new Point((int)casilla.getX()+1, (int)casilla.getY());
								cO = new Point((int)casilla.getX()-1, (int)casilla.getY());
								if(mapGrid.get(cN)!=null)peajeN = ((Terreno) mapGrid.get(cN).get(0).get(1)).getPeaje(sel);
								if(mapGrid.get(cS)!=null)peajeS = ((Terreno) mapGrid.get(cS).get(0).get(1)).getPeaje(sel);
								if(mapGrid.get(cE)!=null)peajeE = ((Terreno) mapGrid.get(cE).get(0).get(1)).getPeaje(sel);
								if(mapGrid.get(cO)!=null)peajeO = ((Terreno) mapGrid.get(cO).get(0).get(1)).getPeaje(sel);
								
								lastMov.add("E");
								}
							}
					}else {
								cursor.setLocation(x + mov, y);
								Point casilla = new Point((int) cursor.getX()/32, (int) cursor.getY()/32);
								cN = new Point((int)casilla.getX(), (int)casilla.getY()-1);
								cS = new Point((int)casilla.getX(), (int)casilla.getY()+1);
								cE = new Point((int)casilla.getX()+1, (int)casilla.getY());
								cO = new Point((int)casilla.getX()-1, (int)casilla.getY());
								sel.setDistancia(sel.getDistancia()+((Terreno) mapGrid.get(cO).get(0).get(1)).getPeaje(sel));
								if(mapGrid.get(cN)!=null)peajeN = ((Terreno) mapGrid.get(cN).get(0).get(1)).getPeaje(sel);
								if(mapGrid.get(cS)!=null)peajeS = ((Terreno) mapGrid.get(cS).get(0).get(1)).getPeaje(sel);
								if(mapGrid.get(cE)!=null)peajeE = ((Terreno) mapGrid.get(cE).get(0).get(1)).getPeaje(sel);
								if(mapGrid.get(cO)!=null)peajeO = ((Terreno) mapGrid.get(cO).get(0).get(1)).getPeaje(sel);
								
								lastMov.remove(lastMov.size()-1);
							}
			    	}else if(attackSt) {
						
						
						if(z<(targets.size())-1) {
							z++;
						}else {
							z=0;
						}
						Point casilla = new Point((int) targets.get(z).getX()*mov, (int) targets.get(z).getY()*mov);
						cursor.setLocation(casilla);
						defender=(Tropa) mapGrid.get(targets.get(z)).get(1).get(1);
						defenderGrd= (Terreno) mapGrid.get(targets.get(z)).get(0).get(1);

						
						
						
						
						//supuestamente deberia a�adir uno cuando doy derecha restar uno si doy izquierda y hacer loop si llego a 0 o al maximo
						//pero solo recorre dos
					}else {
						cursor.setLocation(x + mov, y);
					}
			    	//System.out.println("right");
			    	System.out.println(lastMov);
			    }

			    if (key == KeyEvent.VK_UP && y > 0) {
			    	//System.out.println(moveSt);
			    	if(moveSt) {
			    		if (!(lastMov.get(lastMov.size()-1)=="S")){
							
							
							if(sel.getDistancia()>=peajeN) {
								if(!(mapGrid.get(cN).size()==2 && ((Tropa) mapGrid.get(cN).get(1).get(1)).getTeam()!=sel.getTeam())){
								cursor.setLocation( x, y - mov);
								Point casilla = new Point((int) cursor.getX()/32, (int) cursor.getY()/32);
								sel.setDistancia(sel.getDistancia()-peajeN);
								cN = new Point((int)casilla.getX(), (int)casilla.getY()-1);
								cS = new Point((int)casilla.getX(), (int)casilla.getY()+1);
								cE = new Point((int)casilla.getX()+1, (int)casilla.getY());
								cO = new Point((int)casilla.getX()-1, (int)casilla.getY());
								if(mapGrid.get(cN)!=null)peajeN = ((Terreno) mapGrid.get(cN).get(0).get(1)).getPeaje(sel);
								if(mapGrid.get(cS)!=null)peajeS = ((Terreno) mapGrid.get(cS).get(0).get(1)).getPeaje(sel);
								if(mapGrid.get(cE)!=null)peajeE = ((Terreno) mapGrid.get(cE).get(0).get(1)).getPeaje(sel);
								if(mapGrid.get(cO)!=null)peajeO = ((Terreno) mapGrid.get(cO).get(0).get(1)).getPeaje(sel);
								
								lastMov.add("N");
								}
							}
							}else {
								cursor.setLocation(x , y - mov);
								Point casilla = new Point((int) cursor.getX()/32, (int) cursor.getY()/32);
								cN = new Point((int)casilla.getX(), (int)casilla.getY()-1);
								cS = new Point((int)casilla.getX(), (int)casilla.getY()+1);
								cE = new Point((int)casilla.getX()+1, (int)casilla.getY());
								cO = new Point((int)casilla.getX()-1, (int)casilla.getY());
								sel.setDistancia(sel.getDistancia()+((Terreno) mapGrid.get(cS).get(0).get(1)).getPeaje(sel));
								if(mapGrid.get(cN)!=null)peajeN = ((Terreno) mapGrid.get(cN).get(0).get(1)).getPeaje(sel);
								if(mapGrid.get(cS)!=null)peajeS = ((Terreno) mapGrid.get(cS).get(0).get(1)).getPeaje(sel);
								if(mapGrid.get(cE)!=null)peajeE = ((Terreno) mapGrid.get(cE).get(0).get(1)).getPeaje(sel);
								if(mapGrid.get(cO)!=null)peajeO = ((Terreno) mapGrid.get(cO).get(0).get(1)).getPeaje(sel);
								
								lastMov.remove(lastMov.size()-1);
							}
						
					}else if (attackSt) {
						
					}else {
						cursor.setLocation(x, y - mov);
					}
			    	//System.out.println("up");
			    	//released = false;
			    	System.out.println(lastMov);
			    }

			    if (key == KeyEvent.VK_DOWN && y < 640) {
			    	//System.out.println(moveSt);
			    	if(moveSt) {
			    		if (!(lastMov.get(lastMov.size()-1)=="N")){
							
			    			if(!(mapGrid.get(cS).size()==2 && ((Tropa) mapGrid.get(cS).get(1).get(1)).getTeam()!=sel.getTeam())){
							if(sel.getDistancia()>=peajeS) {
								cursor.setLocation( x, y + mov);
								Point casilla = new Point((int) cursor.getX()/32, (int) cursor.getY()/32);
								sel.setDistancia(sel.getDistancia()-peajeN);
								cN = new Point((int)casilla.getX(), (int)casilla.getY()-1);
								cS = new Point((int)casilla.getX(), (int)casilla.getY()+1);
								cE = new Point((int)casilla.getX()+1, (int)casilla.getY());
								cO = new Point((int)casilla.getX()-1, (int)casilla.getY());
								if(mapGrid.get(cN)!=null)peajeN = ((Terreno) mapGrid.get(cN).get(0).get(1)).getPeaje(sel);
								if(mapGrid.get(cS)!=null)peajeS = ((Terreno) mapGrid.get(cS).get(0).get(1)).getPeaje(sel);
								if(mapGrid.get(cE)!=null)peajeE = ((Terreno) mapGrid.get(cE).get(0).get(1)).getPeaje(sel);
								if(mapGrid.get(cO)!=null)peajeO = ((Terreno) mapGrid.get(cO).get(0).get(1)).getPeaje(sel);
							
								lastMov.add("S");
								
							}
							}
							}else {
								cursor.setLocation(x , y + mov);
								Point casilla = new Point((int) cursor.getX()/32, (int) cursor.getY()/32);
								cN = new Point((int)casilla.getX(), (int)casilla.getY()-1);
								cS = new Point((int)casilla.getX(), (int)casilla.getY()+1);
								cE = new Point((int)casilla.getX()+1, (int)casilla.getY());
								cO = new Point((int)casilla.getX()-1, (int)casilla.getY());
								sel.setDistancia(sel.getDistancia()+((Terreno) mapGrid.get(cN).get(0).get(1)).getPeaje(sel));
								if(mapGrid.get(cN)!=null)peajeN = ((Terreno) mapGrid.get(cN).get(0).get(1)).getPeaje(sel);
								if(mapGrid.get(cS)!=null)peajeS = ((Terreno) mapGrid.get(cS).get(0).get(1)).getPeaje(sel);
								if(mapGrid.get(cE)!=null)peajeE = ((Terreno) mapGrid.get(cE).get(0).get(1)).getPeaje(sel);
								if(mapGrid.get(cO)!=null)peajeO = ((Terreno) mapGrid.get(cO).get(0).get(1)).getPeaje(sel);
							
								lastMov.remove(lastMov.size()-1);
							}
						
			    	}else if (attackSt) {
						
					}else {
						cursor.setLocation(x, y + mov);
					}
			    	//System.out.println("down");
			    	//released = false;
			    	System.out.println(lastMov);
			    }
			    
			    if (key == KeyEvent.VK_SPACE) {
			    	
					
			    	Point casilla = new Point((int) cursor.getX()/32, (int) cursor.getY()/32);
			    	System.out.println(casilla);
					if(!moveSt) {
						if (mapGrid.get(casilla).size() == 2) {
						//	System.out.println((((Tropa) mapGrid.get(casilla).get(1).get(1)).getTeam()));
								sel =(Tropa) mapGrid.get(casilla).get(1).get(1);
								if (sel.getTeam() == turnInt &&  !(spentList.contains(sel))) {
									ogFuel=((Tropa) mapGrid.get(casilla).get(1).get(1)).getEnergia();
									moveSt = true;
									cm.pause();
									cursor.setIcon(new ImageIcon(getClass().getResource("img/CursorMov.gif")));
									ogPos = casilla;
									cN = new Point((int)ogPos.getX(), (int)ogPos.getY()-1);
									cS = new Point((int)ogPos.getX(), (int)ogPos.getY()+1);
									cE = new Point((int)ogPos.getX()+1, (int)ogPos.getY());
									cO = new Point((int)ogPos.getX()-1, (int)ogPos.getY());
									if(mapGrid.get(cN)!=null)peajeN = ((Terreno) mapGrid.get(cN).get(0).get(1)).getPeaje(sel);
									if(mapGrid.get(cS)!=null)peajeS = ((Terreno) mapGrid.get(cS).get(0).get(1)).getPeaje(sel);
									if(mapGrid.get(cE)!=null)peajeE = ((Terreno) mapGrid.get(cE).get(0).get(1)).getPeaje(sel);
									if(mapGrid.get(cO)!=null)peajeO = ((Terreno) mapGrid.get(cO).get(0).get(1)).getPeaje(sel);
								
									
									
							} 
					} else if (!(((Terreno) mapGrid.get(casilla).get(0).get(1)).getIDTerreno() != ListaIDTerreno.FACTORY || mapGrid.get(casilla).size() == 2)) {
						if (((Factory) mapGrid.get(casilla).get(0).get(1)).getTeam() == turnInt) {
							createTropa(cursor.getLocation(), mapGrid, layeredGamePanel, troopPanel, turn, casilla, entityPanel);
							} 	
						} 
					}else {
						Point casilla1 = new Point((int) cursor.getX()/32, (int) cursor.getY()/32);
						// TODO Auto-generated method stub
						if(moveSt) {
							if(!(mapGrid.get(casilla1).size()==2)) {
							JLabel jj = new JLabel();
							entityPanel.add(jj, 2, 0); jj.setIcon(new ImageIcon(getClass().getResource("img/Moved.png"))); jj.setVisible(true); jj.setBounds(mov*8, mov*8, 32, 32); jj.setLocation(x,y); movedList.add(jj);
							
							// Esto es para que se oscurezcan las tropas ya movidas, al terminar turno lo borrare
							
							spentList.add(sel);
						
							mapGrid = moverTropa(mapGrid,layeredGamePanel, troopPanel, casilla1.getLocation(), ogPos);
							cursor.setIcon(new ImageIcon(getClass().getResource("img/Cursor.gif")));
							mapPanel.repaint();
							entityPanel.repaint();
							troopPanel.repaint();
							((JLabel) mapGrid.get(casilla1).get(1).get(0)).setLocation(cursor.getLocation());
							moveSt=false;
							lastMov.clear();
							lastMov.add("Start");
							//((JLabel) mapGrid.get(neg).get(1).get(1)).setIcon(new ImageIcon(getClass().getResource("img/troop/blue/InftBLUE.png")));
							}
						}
					}
					entityPanel.requestFocus();
					
					
				}
		   if (key == KeyEvent.VK_ESCAPE) {
				   
					moveSt=false;  
					attackSt=false;  
					cursor.setIcon(new ImageIcon(getClass().getResource("img/Cursor.gif")));
					int ogX= (int)ogPos.getX();
					int ogY= (int)ogPos.getY();
					
					cursor.setLocation(ogX*mov, ogY*mov);
					entityPanel.requestFocus();
					if(sel!=null) {
						sel.setDistancia(sel.getDistMax());
						sel=null;
					
					}
				
			   }
		   if (key == KeyEvent.VK_A) {
			   if(moveSt) {
				   System.out.println(sel.getDistancia());
					attackPos = new Point((int) cursor.getX()/32, (int) cursor.getY()/32);
					selGrd = (Terreno) mapGrid.get(attackPos).get(0).get(1);
					if(sel.getAlcance()[1]==1) {
						moveSt=false;
						if(mapGrid.get(cN)!=null && mapGrid.get(cN).size()==2){
							if (!(mapGrid.get(cN).get(1).isEmpty())) {
								if((((Tropa) mapGrid.get(cN).get(1).get(1)).getTeam()!=sel.getTeam()))	{
									Point casillaN = new Point((int) cN.getX(), (int) cN.getY());
									targets.add(casillaN);
								}
							}
						}
						if(mapGrid.get(cE)!=null &&mapGrid.get(cE).size()==2){
							if (!(mapGrid.get(cE).get(1).isEmpty())) {
								if((((Tropa) mapGrid.get(cE).get(1).get(1)).getTeam()!=sel.getTeam()))	{
									Point casillaE = new Point((int) cE.getX(), (int) cE.getY());
									targets.add(casillaE);
								}
							}
						}
						if(mapGrid.get(cS)!=null && mapGrid.get(cS).size()==2){
							if (mapGrid.get(cS).size()==2 && !(mapGrid.get(cS).get(1).isEmpty())) {
								if((((Tropa) mapGrid.get(cS).get(1).get(1)).getTeam()!=sel.getTeam()))	{
									Point casillaS = new Point((int) cS.getX(), (int) cS.getY());
									targets.add(casillaS);
								}
							}
						}
						if(mapGrid.get(cO)!=null && mapGrid.get(cO).size()==2){	
							if (!(mapGrid.get(cO).get(1).isEmpty())) {
								if((((Tropa) mapGrid.get(cO).get(1).get(1)).getTeam()!=sel.getTeam()))	{
									Point casillaO = new Point((int) cO.getX(), (int) cO.getY());
									targets.add(casillaO);
								}
							
							}
						}
					}else if (!(sel.getAlcance()[1]==1) && (sel.getDistancia()==sel.getDistMax()) || sel.getIDTropa()==ListaIDTropa.N_SHIP) {
						moveSt=false;
						targetList = GamePlay.rangeFind(sel);
						for (int i = 0; i < targetList.size(); i++) {
							Point casilla1 = new Point ((int) ogPos.getX() , (int) ogPos.getY());
							casilla1 = GamePlay.sumaPoints(casilla1, targetList.get(i));
							if ((casilla1.getX()>=0 && casilla1.getY()>=0 && casilla1.getX()<=21 && casilla1.getY()<=21)) {
								System.out.println(casilla1);
								casillaList.add(casilla1);
							}
							
							//System.out.println(casillaList);
						}
						for (int i = 0; i < casillaList.size(); i++) {
							if ((mapGrid.get(casillaList.get(i)).size()==2) && !(((Tropa) mapGrid.get(casillaList.get(i)).get(1).get(1)).getTeam()==turnInt)) {
								targets.add(casillaList.get(i));
							}
						}
						System.out.println(targets);
					}
					if(targets.size()!=0) {
				
						attackSt=true;
						movData.setBackground(Color.red.darker());
						
						cursor.setIcon(new ImageIcon(getClass().getResource("img/CursorAtk.gif")));			
					}
					
				}else if (attackSt){
					attackSt=false;
					movData.setBackground(Color.yellow);
					ArrayList<Tropa> results = new ArrayList<Tropa>(); 
					Timer timer= new Timer(600, new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							// TODO Auto-generated method stub
							entityPanel.remove(explosion);
							entityPanel.repaint();
						
						}
					});
					
					timer.setRepeats(false);
					
				
					cursor.setIcon(new ImageIcon(getClass().getResource("img/Cursor.gif")));
					Point casillaPos = new Point ((int) attackPos.getX()*mov , (int) attackPos.getY()*mov);
					cursor.setLocation(casillaPos);
					
					
					results = GamePlay.combat(sel,defender,selGrd,defenderGrd);
					
					sel = results.get(0);
					defender = results.get(1);
					System.out.println(results);
					ArrayList<Point> contra = new ArrayList();
					ArrayList<Point> contra1 = new ArrayList();
					
					System.out.println(contra1);
					((JLabel) mapGrid.get(ogPos).get(1).get(0)).setLocation(cursor.getLocation());
					mapGrid = moverTropa(mapGrid,layeredGamePanel, troopPanel, attackPos.getLocation(), ogPos);
				
					if (defender.getSalud()==0){				
							Point casilla = new Point ((int) targets.get(z).getX()*mov , (int) targets.get(z).getY()*mov);
							explosion.setBounds((int)casilla.getX(),(int)casilla.getY(),32,32);
					    	entityPanel.add(explosion, 2, 0);
					    	entityPanel.repaint();
					    	System.out.println("I cant breathe");
					    	
					    	timer.start();
								
								System.out.println("Its ok guys it was just a misunderstanding i can breathe");
								
						
							troopPanel.remove((JLabel)mapGrid.get(targets.get(z)).get(1).get(0));
							mapGrid.get(targets.get(z)).remove(1);
							troopPanel.repaint();
							Point casilla2 = new Point ((int) attackPos.getX()*mov , (int) attackPos.getY()*mov);
							JLabel jj = new JLabel();
							entityPanel.add(jj, 2, 0); jj.setIcon(new ImageIcon(getClass().getResource("img/Moved.png"))); jj.setVisible(true); jj.setBounds(mov*8, mov*8, 32, 32); jj.setLocation(casilla2); movedList.add(jj);
							
							// Esto es para que se oscurezcan las tropas ya movidas, al terminar turno lo borrar
							spentList.add(sel);
							targets.clear();
							spentList.add(sel);
					}else if (sel.getSalud()==0){	
						Point casilla1 = new Point ((int) attackPos.getX()*mov , (int) attackPos.getY()*mov);
							explosion.setBounds((int)casilla1.getX(),(int)casilla1.getY(),32,32);
							entityPanel.add(explosion,2,0);
							entityPanel.repaint();
					
								System.out.println("I cant breathe");
								timer.start();
								System.out.println("Its ok guys it was just a misunderstanding i can breathe");
								
							troopPanel.remove((JLabel)mapGrid.get(attackPos).get(1).get(0));
							mapGrid.get(attackPos).remove(1);
							troopPanel.repaint();
							
					}else {
						Point casilla2 = new Point ((int) attackPos.getX()*mov , (int) attackPos.getY()*mov);
						JLabel jj = new JLabel();
						entityPanel.add(jj, 2, 0); jj.setIcon(new ImageIcon(getClass().getResource("img/Moved.png"))); jj.setVisible(true); jj.setBounds(mov*8, mov*8, 32, 32); jj.setLocation(casilla2); movedList.add(jj);
						
						// Esto es para que se oscurezcan las tropas ya movidas, al terminar turno lo borrar
						spentList.add(sel);
						targets.clear();
						spentList.add(sel);
					}
					
					
				
					
					cursor.setIcon(new ImageIcon(getClass().getResource("img/Cursor.gif")));
					mapPanel.repaint();
					entityPanel.repaint();
					troopPanel.repaint();
				
					
				}else if (!targetSt){
					Point casilla = new Point ((int) cursor.getLocation().getX()/mov , (int) cursor.getLocation().getY()/mov);
					if(mapGrid.get(casilla).size()==2) {
					
						Tropa troop;
						troop=(Tropa) mapGrid.get(casilla).get(1).get(1);
						targetList =GamePlay.rangeFind(troop);
						for (int i = 0; i < targetList.size(); i++) {
							Point casilla1 = new Point ((int) casilla.getX() , (int) casilla.getY());
							casilla1 = GamePlay.sumaPoints(casilla1, targetList.get(i));
							if ((casilla1.getX()>=0 && casilla1.getY()>=0 && casilla1.getX()<=21 && casilla1.getY()<=21)) {
								casilla1.setLocation(casilla1.getX()*mov , casilla1.getY()*mov);
								JLabel target = new JLabel();
								target.setIcon(new ImageIcon(getClass().getResource("img/targetSelect.png")));
								entityPanel.add(target,2,0);
								target.setPreferredSize(new Dimension(32,32));
								target.setBounds(32,32,32,32);
								target.setLocation(casilla1);
								entityPanel.repaint();
								
								tList.add(target);
							}
						}
						targetSt=true;
					}
					System.out.println(casillaList);
				}
			   
				
				
		   }
		   		if (key == KeyEvent.VK_T) {
		   			System.out.println(p4.getfunds());
		   			System.out.println((Config)mapGrid.get(po).get(0).get(1));
		   			if (mapGrid.get(po)!=null) {
		   				if (((Config) mapGrid.get(po).get(0).get(1)).getRed()==0) {
		   					JLabel bk = new JLabel();
		   					troopInfo.add(bk);
		   					bk.setBounds(0,0,256,64);
		   					bk.setIcon(new ImageIcon(getClass().getResource("img/UI/BlackOut.png")));
		   					mapPanel.repaint();
							entityPanel.repaint();
							troopPanel.repaint();
		   					if (turnTest.contains(1)) {
		   						for (int i = 0; i < turnTest.size(); i++) {
									if(turnTest.get(i)==1) {
										turnTest.remove(i);
									}
								}
		   					}
		   				}
		   				if (((Config) mapGrid.get(po).get(0).get(1)).getBlue()==0) {
		   					JLabel bk = new JLabel();
		   					troopInfo.add(bk);
		   					bk.setBounds(0,64,256,64);
		   					bk.setIcon(new ImageIcon(getClass().getResource("img/UI/BlackOut.png")));
		   					mapPanel.repaint();
							entityPanel.repaint();
							troopPanel.repaint();
		   					if (turnTest.contains(2)) {
		   						for (int i = 0; i < turnTest.size(); i++) {
									if(turnTest.get(i)==2) {
										turnTest.remove(i);
									}
								}
		   					}		   				
		   				}
		   				if (((Config) mapGrid.get(po).get(0).get(1)).getGreen()==0) {
		   					JLabel bk = new JLabel();
		   					troopInfo.add(bk);
		   					bk.setBounds(0,128,256,64);
		   					bk.setIcon(new ImageIcon(getClass().getResource("img/UI/BlackOut.png")));
		   					mapPanel.repaint();
							entityPanel.repaint();
							troopPanel.repaint();
		   					if (turnTest.contains(3)) {
		   						for (int i = 0; i < turnTest.size(); i++) {
									if(turnTest.get(i)==3) {
										turnTest.remove(i);
									}
								}
		   					}
		   				}
		   				if (((Config) mapGrid.get(po).get(0).get(1)).getOrange()==0) {
		   					JLabel bk = new JLabel();
		   					troopInfo.add(bk);
		   					bk.setBounds(0,192,256,64);
		   					bk.setIcon(new ImageIcon(getClass().getResource("img/UI/BlackOut.png")));
		   					troopInfo.repaint();
		   					mapPanel.repaint();
		   					if (turnTest.contains(4)) {
		   						for (int i = 0; i < turnTest.size(); i++) {
									if(turnTest.get(i)==4) {
										turnTest.remove(i);
									}
								}
		   					}
		   				}
		   			}
		   			if(turnTest.indexOf(turnInt)==(turnTest.size()-1)){
		   				turnInt=turnTest.get(0);
		   			}else {
		   				turnInt=turnTest.get(turnTest.indexOf(turnInt)+1);
		   			}
		   			System.out.println(turnInt);
		   			for (Object tr : spentList) {
						Tropa p = (Tropa) tr;
						p.setDistancia(p.getDistMax());
					} 
					spentList.clear();
					for(JLabel jl : movedList) {
						entityPanel.remove(jl);
					}
					entityPanel.revalidate();
					entityPanel.repaint();
					movedList.clear();
					terminaTurno(turn, mapPanel, mapGrid);
					
					
				//	dataBase.actualizaTEquipos(p1.getfunds(),0,p2.getfunds(),0);
					entityPanel.requestFocus();
						
		   			
		   		}
		   
			    
			}
			
		};
		
		
		
		JPanel derecha = new JPanel();		//Creación del panel de la derecha que contiene otros paneles que mostrarán la información de tropas y terrenos
		derecha.setPreferredSize(new Dimension(416, 672));		//Se define el tamaño preferido del panel
		derecha.setLayout(new BoxLayout(derecha, BoxLayout.Y_AXIS));		//Se le pone un BoxLayout en el eje Y que coloca los componentes en serie verticalmente
		
		JLayeredPane info = new JLayeredPane();		//Creación del panel superior de información general de la partida
		info.setPreferredSize(new Dimension(416, 416));		//Tamaño preferido del panel de información general
		
		
		JLabel infoBckgrd = new JLabel();
		infoBckgrd.setIcon(new ImageIcon(getClass().getResource("img/InfBackground.gif")));
		infoBckgrd.setSize(416,416);
		info.add(infoBckgrd,0,0);
		
		
		JPanel abajo = new JPanel();		//Creación del panel inferior de la seccion de información (parte derecha de la pantalla)
		abajo.setLayout(new BoxLayout(abajo, BoxLayout.X_AXIS));		//Sele pone un BoxLayout en el eje Y que coloca los componentes en serie horizontalmente
		
			//Creación del panel inferior con información de movimiento
		movData.setBackground(Color.yellow);		//Instrucción de prueba para diferenciar los paneles mientras no estén programados
		movData.setPreferredSize(new Dimension(160, 256));		//Tamaño preferido del panel de información de tropas
		//movData.setLayout(new BoxLayout(movData, BoxLayout.Y_AXIS));
		
		
		
		
		//Labels de los atributos del panel info
		/*
		JLabel troopNum = new JLabel();
		JLabel buildingNum = new JLabel();
		JLabel gold = new JLabel();
		
		info.add(troopNum);
		info.add(buildingNum);
		info.add(gold);
		*/
		JButton exit = new JButton("");
		JButton back = new JButton("");
		JButton save = new JButton("");
		JButton load = new JButton("");
		
		ActionListener escButtons = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource()==exit) {
					mus.interrupt();
					try {
						sic.stop();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					new Thread(new SoundMngr("weegee.wav", 0, 0)).start();
					// TODO Auto-generated method stub
					System.exit(0);
				} else if(e.getSource()==back) {
					mus.interrupt();
					try {
						sic.stop();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					new Thread(new SoundMngr("weegee.wav", 0, 0)).start();
					MapLoaderPrompt juego = new MapLoaderPrompt();
					juego.pack();
					juego.setLocationRelativeTo(null);
					juego.setResizable(false);
					juego.setVisible(true);
					dispose();
				} else if(e.getSource()==save) {
					new Thread(new SoundMngr("bip2.wav", 0, 0)).start();
					try {
						ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("QS"+ ".dat"));
						oos.writeObject(mapGrid);
						oos.close();
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else if(e.getSource()==load) {
					new Thread(new SoundMngr("bip3.wav", 0, 0)).start();
					quickLoad(mapPanel).repaint();
				}
			}};
		
		exit.addActionListener(escButtons);
		back.addActionListener(escButtons);
		save.addActionListener(escButtons);
		load.addActionListener(escButtons);
		//Labels de los atributos del panel movData

		JButton move = new JButton("MOVER");
		JButton attack = new JButton("ATACAR (A)");
		JButton capture = new JButton("CAPTURAR");
		JButton turnEnd = new JButton("TERMINAR TURNO (T)");
		
		ActionListener gameButtons = new ActionListener() {
			Point casilla = new Point((int) cursor.getX()/32, (int) cursor.getY()/32);
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource()==move) {
				
					// TODO Auto-generated method stub
					if(moveSt) {
					
						
						mapGrid = moverTropa(mapGrid,layeredGamePanel, troopPanel, casilla.getLocation(), ogPos);
						
						mapPanel.repaint();
						entityPanel.repaint();
						troopPanel.repaint();
						((JLabel) mapGrid.get(casilla).get(1).get(1)).setLocation(cursor.getLocation());
						Point neg = new Point((int) cursor.getX()/32, (int) cursor.getY()/32);
						moveSt=false;
						entityPanel.requestFocus();
						//((JLabel) mapGrid.get(neg).get(1).get(1)).setIcon(new ImageIcon(getClass().getResource("img/troop/blue/InftBLUE.png")));
					}
				} else if (e.getSource()==turnEnd) {
					for (Object tr : spentList) {
						Tropa p = (Tropa) tr;
						p.setDistancia(p.getDistMax());
					} 
					spentList.clear();
					for(JLabel jl : movedList) {
						entityPanel.remove(jl);
					}
					entityPanel.revalidate();
					entityPanel.repaint();
					movedList.clear();
					terminaTurno(turn, mapPanel, mapGrid);
					
				//	dataBase.actualizaTEquipos(p1.getfunds(),0,p2.getfunds(),0);
					entityPanel.requestFocus();
				} else if (e.getSource()==capture) {
					Point casilla = new Point((int) cursor.getX()/32, (int) cursor.getY()/32);
					int x = cursor.getLocation().x;
					int y = cursor.getLocation().y;	
					int team;
					if(moveSt && sel.getCaptura() && (((Estructura) (mapGrid.get(casilla).get(0).get(1))).getTeam()!=sel.getTeam())) {
						Estructura target= (Estructura) mapGrid.get(casilla).get(0).get(1);
						team=target.getTeam();
						switch(GamePlay.captureCalc(sel, target)){
						case 2:
							Timer timer= new Timer(600, new ActionListener() {
								
								@Override
								public void actionPerformed(ActionEvent e) {
									// TODO Auto-generated method stub
									entityPanel.remove(explosion);
									entityPanel.repaint();
								
								}
							});
							
							timer.setRepeats(false);
							switch (team) {
							case 1:
								 ((Config) mapGrid.get(po).get(0).get(1)).setRed(0);
								break;
							case 2:
								 ((Config) mapGrid.get(po).get(0).get(1)).setBlue(0);
								break;
							case 3:
								 ((Config) mapGrid.get(po).get(0).get(1)).setGreen(0);
								break;
							case 4:
								 ((Config) mapGrid.get(po).get(0).get(1)).setOrange(0);
								break;
							}
							System.out.println("you win");
							Set<Point> keys = mapGrid.keySet();
							Point t = new Point(-1,-1);
							mapGrid.get(casilla).get(0).remove(1);
							City Ct = new City(sel.getTeam());
							mapGrid.get(casilla).get(0).add(Ct);
							switch(sel.getTeam()) {
							case 1:
								((Config) mapGrid.get(po).get(0).get(1)).addRed();
							 	((JLabel) (mapGrid.get(casilla).get(0).get(0))).setIcon(new ImageIcon(getClass().getResource("img/structure/CityRED.png")));
								for (Point i : keys) {
									if(i.getX()!=-1) {
										if(mapGrid.get(i).size()==2 && ((Tropa) mapGrid.get(i).get(1).get(1)).getTeam()==team) {
											System.out.println(i);
											JLabel explosion = new JLabel();
											explosion.setIcon(new ImageIcon(getClass().getResource("img/explosion.gif")));
											explosion.setBounds((int)i.getX()*32,(int)i.getY()*32,32,32);
											
											entityPanel.add(explosion,2,0);
											entityPanel.repaint();
											timer.start();
											troopPanel.remove((JLabel)mapGrid.get(i).get(1).get(0));
											mapGrid.get(i).remove(1);
												
										}
									if(((Terreno) mapGrid.get(i).get(0).get(1)).getIDTerreno()==ListaIDTerreno.HQ || ((Terreno) mapGrid.get(i).get(0).get(1)).getIDTerreno()==ListaIDTerreno.AIRPORT || ((Terreno) mapGrid.get(i).get(0).get(1)).getIDTerreno()==ListaIDTerreno.FACTORY || ((Terreno) mapGrid.get(i).get(0).get(1)).getIDTerreno()==ListaIDTerreno.CITY || ((Terreno) mapGrid.get(i).get(0).get(1)).getIDTerreno()==ListaIDTerreno.PORT){
																												
										if(((Estructura) mapGrid.get(i).get(0).get(1)).getTeam()==team) {
										switch(((Estructura) mapGrid.get(i).get(0).get(1)).getNombre()) {
											case "Ciudad":
											((JLabel) (mapGrid.get(i).get(0).get(0))).setIcon(new ImageIcon(getClass().getResource("img/structure/CityRED.png")));
											((Estructura) mapGrid.get(i).get(0).get(1)).setTeam(1);
											break;
											case "Fabrica":			
											((JLabel) (mapGrid.get(i).get(0).get(0))).setIcon(new ImageIcon(getClass().getResource("img/structure/FactoryRED.png")));
											((Estructura) mapGrid.get(i).get(0).get(1)).setTeam(1);
											break;
											case "Cuartel General":
											((JLabel) (mapGrid.get(i).get(0).get(0))).setIcon(new ImageIcon(getClass().getResource("img/structure/CityRED.png")));
											((Estructura) mapGrid.get(i).get(0).get(1)).setTeam(1);
											break;									
										}
										
									}
								}
								}	
								}
							break;
							case 2:
								((JLabel) (mapGrid.get(casilla).get(0).get(0))).setIcon(new ImageIcon(getClass().getResource("img/structure/CityBLUE.png")));
								for (Point i : keys) {
									if(i.getX()!=-1) {
										if(mapGrid.get(i).size()==2 && ((Tropa) mapGrid.get(i).get(1).get(1)).getTeam()==team) {
											System.out.println(i);
											JLabel explosion = new JLabel();
											explosion.setIcon(new ImageIcon(getClass().getResource("img/explosion.gif")));
											explosion.setBounds((int)i.getX()*32,(int)i.getY()*32,32,32);
											
											entityPanel.add(explosion,2,0);
											entityPanel.repaint();
											timer.start();
											troopPanel.remove((JLabel)mapGrid.get(i).get(1).get(0));
											mapGrid.get(i).remove(1);
												
										}
									if(((Terreno) mapGrid.get(i).get(0).get(1)).getIDTerreno()==ListaIDTerreno.HQ || ((Terreno) mapGrid.get(i).get(0).get(1)).getIDTerreno()==ListaIDTerreno.AIRPORT || ((Terreno) mapGrid.get(i).get(0).get(1)).getIDTerreno()==ListaIDTerreno.FACTORY || ((Terreno) mapGrid.get(i).get(0).get(1)).getIDTerreno()==ListaIDTerreno.CITY || ((Terreno) mapGrid.get(i).get(0).get(1)).getIDTerreno()==ListaIDTerreno.PORT){
									if(((Estructura) mapGrid.get(i).get(0).get(1)).getTeam()==team) {
										switch(((Estructura) mapGrid.get(i).get(0).get(1)).getNombre()) {
											case "Ciudad":
											((JLabel) (mapGrid.get(i).get(0).get(0))).setIcon(new ImageIcon(getClass().getResource("img/structure/CityBLUE.png")));
											((Estructura) mapGrid.get(i).get(0).get(1)).setTeam(2);
											break;
											case "Fabrica":			
											((JLabel) (mapGrid.get(i).get(0).get(0))).setIcon(new ImageIcon(getClass().getResource("img/structure/FactoryBLUE.png")));
											((Estructura) mapGrid.get(i).get(0).get(1)).setTeam(2);
											break;
											case "Cuartel General":
											((JLabel) (mapGrid.get(i).get(0).get(0))).setIcon(new ImageIcon(getClass().getResource("img/structure/CityBLUE.png")));
											((Estructura) mapGrid.get(i).get(0).get(1)).setTeam(2);
											break;									
										}
									}
								}
								}
								}					
								break;
							case 3:
								((Config) mapGrid.get(po).get(0).get(1)).addGrn();
								((JLabel) (mapGrid.get(casilla).get(0).get(0))).setIcon(new ImageIcon(getClass().getResource("img/structure/CityGREEN.png")));	
								for (Point i : keys) {
									if(i.getX()!=-1) {
										if(mapGrid.get(i).size()==2 && ((Tropa) mapGrid.get(i).get(1).get(1)).getTeam()==team) {
											System.out.println(i);
											JLabel explosion = new JLabel();
											explosion.setIcon(new ImageIcon(getClass().getResource("img/explosion.gif")));
											explosion.setBounds((int)i.getX()*32,(int)i.getY()*32,32,32);
											
											entityPanel.add(explosion,2,0);
											entityPanel.repaint();
											timer.start();
											troopPanel.remove((JLabel)mapGrid.get(i).get(1).get(0));
											mapGrid.get(i).remove(1);
												
										}
									if(((Terreno) mapGrid.get(i).get(0).get(1)).getIDTerreno()==ListaIDTerreno.HQ || ((Terreno) mapGrid.get(i).get(0).get(1)).getIDTerreno()==ListaIDTerreno.AIRPORT || ((Terreno) mapGrid.get(i).get(0).get(1)).getIDTerreno()==ListaIDTerreno.FACTORY || ((Terreno) mapGrid.get(i).get(0).get(1)).getIDTerreno()==ListaIDTerreno.CITY || ((Terreno) mapGrid.get(i).get(0).get(1)).getIDTerreno()==ListaIDTerreno.PORT){

									if(((Estructura) mapGrid.get(i).get(0).get(1)).getTeam()==team) {
										switch(((Estructura) mapGrid.get(i).get(0).get(1)).getNombre()) {
											case "Ciudad":
											((JLabel) (mapGrid.get(i).get(0).get(0))).setIcon(new ImageIcon(getClass().getResource("img/structure/CityGREEN.png")));
											((Estructura) mapGrid.get(i).get(0).get(1)).setTeam(3);
											break;
											case "Fabrica":			
											((JLabel) (mapGrid.get(i).get(0).get(0))).setIcon(new ImageIcon(getClass().getResource("img/structure/FactoryGREEN.png")));
											((Estructura) mapGrid.get(i).get(0).get(1)).setTeam(3);
											break;
											case "Cuartel General":
											((JLabel) (mapGrid.get(i).get(0).get(0))).setIcon(new ImageIcon(getClass().getResource("img/structure/CityGREEN.png")));
											((Estructura) mapGrid.get(i).get(0).get(1)).setTeam(3);
											break;									
										}
									}
								}
									}
								}
								break;
								
							case 4:
								((Config) mapGrid.get(po).get(0).get(1)).addOrg();								
								((JLabel) (mapGrid.get(casilla).get(0).get(0))).setIcon(new ImageIcon(getClass().getResource("img/structure/CityORANGE.png")));	
								for (Point i : keys) {
									if(i.getX()!=-1) {
										if(mapGrid.get(i).size()==2 && ((Tropa) mapGrid.get(i).get(1).get(1)).getTeam()==team) {
											System.out.println(i);
											JLabel explosion = new JLabel();
											explosion.setIcon(new ImageIcon(getClass().getResource("img/explosion.gif")));
											explosion.setBounds((int)i.getX()*32,(int)i.getY()*32,32,32);
											
											entityPanel.add(explosion,2,0);
											entityPanel.repaint();
											timer.start();
											troopPanel.remove((JLabel)mapGrid.get(i).get(1).get(0));
											mapGrid.get(i).remove(1);
												
										}
									if(((Terreno) mapGrid.get(i).get(0).get(1)).getIDTerreno()==ListaIDTerreno.HQ || ((Terreno) mapGrid.get(i).get(0).get(1)).getIDTerreno()==ListaIDTerreno.AIRPORT || ((Terreno) mapGrid.get(i).get(0).get(1)).getIDTerreno()==ListaIDTerreno.FACTORY || ((Terreno) mapGrid.get(i).get(0).get(1)).getIDTerreno()==ListaIDTerreno.CITY || ((Terreno) mapGrid.get(i).get(0).get(1)).getIDTerreno()==ListaIDTerreno.PORT){

									if(((Estructura) mapGrid.get(i).get(0).get(1)).getTeam()==team) {
										switch(((Estructura) mapGrid.get(i).get(0).get(1)).getNombre()) {
											case "Ciudad":
											((JLabel) (mapGrid.get(i).get(0).get(0))).setIcon(new ImageIcon(getClass().getResource("img/structure/CityORANGE.png")));
											((Estructura) mapGrid.get(i).get(0).get(1)).setTeam(4);
											break;
											case "Fabrica":			
											((JLabel) (mapGrid.get(i).get(0).get(0))).setIcon(new ImageIcon(getClass().getResource("img/structure/FactoryORANGE.png")));
											((Estructura) mapGrid.get(i).get(0).get(1)).setTeam(4);
											break;
											case "Cuartel General":
											((JLabel) (mapGrid.get(i).get(0).get(0))).setIcon(new ImageIcon(getClass().getResource("img/structure/CityORANGE.png")));
											((Estructura) mapGrid.get(i).get(0).get(1)).setTeam(4);
											break;									
										}
									}
								}
						}
								}
								break;
							}
							target.setNombre("Cuartel General");
							break;
						case 1:
							switch (team) {
							case 1:
								 ((Config) mapGrid.get(po).get(0).get(1)).subRed();
								break;
							case 2:
								 ((Config) mapGrid.get(po).get(0).get(1)).subBlu();
								break;
							case 3:
								 ((Config) mapGrid.get(po).get(0).get(1)).subGrn();
								break;
							case 4:
								 ((Config) mapGrid.get(po).get(0).get(1)).subOrg();
								break;
							}
							target.setTeam(sel.getTeam());
							switch(sel.getTeam()) {
							case 1:
							 ((Config) mapGrid.get(po).get(0).get(1)).addRed();
								switch(((Estructura) mapGrid.get(casilla).get(0).get(1)).getNombre()) {
								case "Ciudad":
									((JLabel) (mapGrid.get(casilla).get(0).get(0))).setIcon(new ImageIcon(getClass().getResource("img/structure/CityRED.png")));
									break;
								case "Fabrica":			
									((JLabel) (mapGrid.get(casilla).get(0).get(0))).setIcon(new ImageIcon(getClass().getResource("img/structure/FactoryRED.png")));
									break;
								case "Cuartel General":
									((JLabel) (mapGrid.get(casilla).get(0).get(0))).setIcon(new ImageIcon(getClass().getResource("img/structure/CityRED.png")));
									break;
								}
								break;
							case 2:
								((Config) mapGrid.get(po).get(0).get(1)).addBlu();
								switch(((Estructura) mapGrid.get(casilla).get(0).get(1)).getNombre()) {
								case "Ciudad":
									((JLabel) (mapGrid.get(casilla).get(0).get(0))).setIcon(new ImageIcon(getClass().getResource("img/structure/CityBLUE.png")));
									break;
								case "Fabrica":
									((JLabel) (mapGrid.get(casilla).get(0).get(0))).setIcon(new ImageIcon(getClass().getResource("img/structure/FactoryBLUE.png")));
									break;
								case "Cuartel General":
									((JLabel) (mapGrid.get(casilla).get(0).get(0))).setIcon(new ImageIcon(getClass().getResource("img/structure/CityBLUE.png")));
									break;
								}
								break;
							case 3:
								 ((Config) mapGrid.get(po).get(0).get(1)).addGrn();
								switch(((Estructura) mapGrid.get(casilla).get(0).get(1)).getNombre()) {
								case "Ciudad":
									((JLabel) (mapGrid.get(casilla).get(0).get(0))).setIcon(new ImageIcon(getClass().getResource("img/structure/CityGREEN.png")));
									break;
								case "Fabrica":
									((JLabel) (mapGrid.get(casilla).get(0).get(0))).setIcon(new ImageIcon(getClass().getResource("img/structure/FactoryGREEN.png")));
									break;
								case "Cuartel General":
									((JLabel) (mapGrid.get(casilla).get(0).get(0))).setIcon(new ImageIcon(getClass().getResource("img/structure/CityGREEN.png")));
									break;
								}
								break;
								
							case 4:
								 ((Config) mapGrid.get(po).get(0).get(1)).addOrg();
								switch(((Estructura) mapGrid.get(casilla).get(0).get(1)).getNombre()) {
								case "Ciudad":
									((JLabel) (mapGrid.get(casilla).get(0).get(0))).setIcon(new ImageIcon(getClass().getResource("img/structure/CityORANGE.png")));
									break;
								case "Fabrica":
									((JLabel) (mapGrid.get(casilla).get(0).get(0))).setIcon(new ImageIcon(getClass().getResource("img/structure/FactoryORANGE.png")));
									break;
								case "Cuartel General":
									((JLabel) (mapGrid.get(casilla).get(0).get(0))).setIcon(new ImageIcon(getClass().getResource("img/structure/CityORANGE.png")));
									break;
								}
								break;
								
							
							}
							default:
							break;
						}
						mapPanel.repaint();
						entityPanel.repaint();
						if(moveSt) {
							
							JLabel jj = new JLabel();
							entityPanel.add(jj, 2, 0); jj.setIcon(new ImageIcon(getClass().getResource("img/Moved.png"))); jj.setVisible(true); jj.setBounds(mov*8, mov*8, 32, 32); jj.setLocation(x,y); movedList.add(jj);
							
							// Esto es para que se oscurezcan las tropas ya movidas, al terminar turno lo borrare
							
							spentList.add(sel);
							mapGrid = moverTropa(mapGrid,layeredGamePanel, troopPanel, casilla.getLocation(), ogPos);
							cursor.setIcon(new ImageIcon(getClass().getResource("img/Cursor.gif")));
							mapPanel.repaint();
							entityPanel.repaint();
							troopPanel.repaint();
							((JLabel) mapGrid.get(casilla).get(1).get(0)).setLocation(cursor.getLocation());
							Point neg = new Point((int) cursor.getX()/32, (int) cursor.getY()/32);
							moveSt=false;
							cm.resume();
							//((JLabel) mapGrid.get(neg).get(1).get(1)).setIcon(new ImageIcon(getClass().getResource("img/troop/blue/InftBLUE.png")));
						}
						
					}
					
				} else if (e.getSource()==attack) {
					   if(moveSt) {
						   System.out.println(sel.getDistancia());
							attackPos = new Point((int) cursor.getX()/32, (int) cursor.getY()/32);
							selGrd = (Terreno) mapGrid.get(attackPos).get(0).get(1);
							if(sel.getAlcance()[1]==1) {
								moveSt=false;
								if(mapGrid.get(cN)!=null && mapGrid.get(cN).size()==2){
									if (!(mapGrid.get(cN).get(1).isEmpty())) {
										if((((Tropa) mapGrid.get(cN).get(1).get(1)).getTeam()!=sel.getTeam()))	{
											Point casillaN = new Point((int) cN.getX(), (int) cN.getY());
											targets.add(casillaN);
										}
									}
								}
								if(mapGrid.get(cE)!=null &&mapGrid.get(cE).size()==2){
									if (!(mapGrid.get(cE).get(1).isEmpty())) {
										if((((Tropa) mapGrid.get(cE).get(1).get(1)).getTeam()!=sel.getTeam()))	{
											Point casillaE = new Point((int) cE.getX(), (int) cE.getY());
											targets.add(casillaE);
										}
									}
								}
								if(mapGrid.get(cS)!=null && mapGrid.get(cS).size()==2){
									if (mapGrid.get(cS).size()==2 && !(mapGrid.get(cS).get(1).isEmpty())) {
										if((((Tropa) mapGrid.get(cS).get(1).get(1)).getTeam()!=sel.getTeam()))	{
											Point casillaS = new Point((int) cS.getX(), (int) cS.getY());
											targets.add(casillaS);
										}
									}
								}
								if(mapGrid.get(cO)!=null && mapGrid.get(cO).size()==2){	
									if (!(mapGrid.get(cO).get(1).isEmpty())) {
										if((((Tropa) mapGrid.get(cO).get(1).get(1)).getTeam()!=sel.getTeam()))	{
											Point casillaO = new Point((int) cO.getX(), (int) cO.getY());
											targets.add(casillaO);
										}
									
									}
								}
							}else if (!(sel.getAlcance()[1]==1) && (sel.getDistancia()==sel.getDistMax()) || sel.getIDTropa()==ListaIDTropa.N_SHIP) {
								moveSt=false;
								targetList = GamePlay.rangeFind(sel);
								for (int i = 0; i < targetList.size(); i++) {
									Point casilla1 = new Point ((int) ogPos.getX() , (int) ogPos.getY());
									casilla1 = GamePlay.sumaPoints(casilla1, targetList.get(i));
									if ((casilla1.getX()>=0 && casilla1.getY()>=0 && casilla1.getX()<=21 && casilla1.getY()<=21)) {
										System.out.println(casilla1);
										casillaList.add(casilla1);
									}
									
									//System.out.println(casillaList);
								}
								for (int i = 0; i < casillaList.size(); i++) {
									if ((mapGrid.get(casillaList.get(i)).size()==2) && !(((Tropa) mapGrid.get(casillaList.get(i)).get(1).get(1)).getTeam()==turnInt)) {
										targets.add(casillaList.get(i));
									}
								}
								System.out.println(targets);
							}
							if(targets.size()!=0) {
						
								attackSt=true;
								movData.setBackground(Color.red.darker());
								
								cursor.setIcon(new ImageIcon(getClass().getResource("img/CursorAtk.gif")));			
							}
							
						}else if (attackSt){
							attackSt=false;
							movData.setBackground(Color.yellow);
							ArrayList<Tropa> results = new ArrayList<Tropa>(); 
							Timer timer= new Timer(600, new ActionListener() {
								
								@Override
								public void actionPerformed(ActionEvent e) {
									// TODO Auto-generated method stub
									entityPanel.remove(explosion);
									entityPanel.repaint();
								
								}
							});
							
							timer.setRepeats(false);
							
						
							cursor.setIcon(new ImageIcon(getClass().getResource("img/Cursor.gif")));
							Point casillaPos = new Point ((int) attackPos.getX()*mov , (int) attackPos.getY()*mov);
							cursor.setLocation(casillaPos);
							
							
							results = GamePlay.combat(sel,defender,selGrd,defenderGrd);
							
							sel = results.get(0);
							defender = results.get(1);
							System.out.println(results);
							ArrayList<Point> contra = new ArrayList();
							ArrayList<Point> contra1 = new ArrayList();
							
							System.out.println(contra1);
							((JLabel) mapGrid.get(ogPos).get(1).get(0)).setLocation(cursor.getLocation());
							mapGrid = moverTropa(mapGrid,layeredGamePanel, troopPanel, attackPos.getLocation(), ogPos);
						
							if (defender.getSalud()==0){				
									Point casilla = new Point ((int) targets.get(z).getX()*mov , (int) targets.get(z).getY()*mov);
									explosion.setBounds((int)casilla.getX(),(int)casilla.getY(),32,32);
							    	entityPanel.add(explosion, 2, 0);
							    	entityPanel.repaint();
							    	System.out.println("I cant breathe");
							    	
							    	timer.start();
										
										System.out.println("Its ok guys it was just a misunderstanding i can breathe");
										
								
									troopPanel.remove((JLabel)mapGrid.get(targets.get(z)).get(1).get(0));
									mapGrid.get(targets.get(z)).remove(1);
									troopPanel.repaint();
									Point casilla2 = new Point ((int) attackPos.getX()*mov , (int) attackPos.getY()*mov);
									JLabel jj = new JLabel();
									entityPanel.add(jj, 2, 0); jj.setIcon(new ImageIcon(getClass().getResource("img/Moved.png"))); jj.setVisible(true); jj.setBounds(mov*8, mov*8, 32, 32); jj.setLocation(casilla2); movedList.add(jj);
									
									// Esto es para que se oscurezcan las tropas ya movidas, al terminar turno lo borrar
									spentList.add(sel);
									targets.clear();
									spentList.add(sel);
							}else if (sel.getSalud()==0){	
								Point casilla1 = new Point ((int) attackPos.getX()*mov , (int) attackPos.getY()*mov);
									explosion.setBounds((int)casilla1.getX(),(int)casilla1.getY(),32,32);
									entityPanel.add(explosion,2,0);
									entityPanel.repaint();
							
										System.out.println("I cant breathe");
										timer.start();
										System.out.println("Its ok guys it was just a misunderstanding i can breathe");
										
									troopPanel.remove((JLabel)mapGrid.get(attackPos).get(1).get(0));
									mapGrid.get(attackPos).remove(1);
									troopPanel.repaint();
									
							}else {
								Point casilla2 = new Point ((int) attackPos.getX()*mov , (int) attackPos.getY()*mov);
								JLabel jj = new JLabel();
								entityPanel.add(jj, 2, 0); jj.setIcon(new ImageIcon(getClass().getResource("img/Moved.png"))); jj.setVisible(true); jj.setBounds(mov*8, mov*8, 32, 32); jj.setLocation(casilla2); movedList.add(jj);
								
								// Esto es para que se oscurezcan las tropas ya movidas, al terminar turno lo borrar
								spentList.add(sel);
								targets.clear();
								spentList.add(sel);
							}
							
							
						
							
							cursor.setIcon(new ImageIcon(getClass().getResource("img/Cursor.gif")));
							mapPanel.repaint();
							entityPanel.repaint();
							troopPanel.repaint();
						
							
						}else if (!targetSt){
							Point casilla = new Point ((int) cursor.getLocation().getX()/mov , (int) cursor.getLocation().getY()/mov);
							if(mapGrid.get(casilla).size()==2) {
							
								Tropa troop;
								troop=(Tropa) mapGrid.get(casilla).get(1).get(1);
								targetList =GamePlay.rangeFind(troop);
								for (int i = 0; i < targetList.size(); i++) {
									Point casilla1 = new Point ((int) casilla.getX() , (int) casilla.getY());
									casilla1 = GamePlay.sumaPoints(casilla1, targetList.get(i));
									if ((casilla1.getX()>=0 && casilla1.getY()>=0 && casilla1.getX()<=21 && casilla1.getY()<=21)) {
										casilla1.setLocation(casilla1.getX()*mov , casilla1.getY()*mov);
										JLabel target = new JLabel();
										target.setIcon(new ImageIcon(getClass().getResource("img/targetSelect.png")));
										entityPanel.add(target,2,0);
										target.setPreferredSize(new Dimension(32,32));
										target.setBounds(32,32,32,32);
										target.setLocation(casilla1);
										entityPanel.repaint();
										
										tList.add(target);
									}
								}
								targetSt=true;
							}
							System.out.println(casillaList);
						}
					   
					}	
						
			   }
			};
		
		move.addActionListener(gameButtons);
		turnEnd.addActionListener(gameButtons);
		capture.addActionListener(gameButtons);
		attack.addActionListener(gameButtons);
		JLabel red = new JLabel();
		JLabel blue = new JLabel();
		JLabel green = new JLabel();
		JLabel orange = new JLabel();
		red.setBounds(0,0,256,64);
		blue.setBounds(0,64,256,64);
		green.setBounds(0,128,256,64);
		orange.setBounds(0,192,256,64);
		
		movData.add(move);
		movData.add(attack);
		movData.add(capture);
		movData.add(turnEnd);
		movData.add(save);
		movData.add(load);
		movData.add(back);
		movData.add(exit);
		
		troopInfo.setLayout(null);
		
		troopInfo.add(red);
		troopInfo.add(blue);
		troopInfo.add(green);
		troopInfo.add(orange);
		
		red.setIcon(new ImageIcon(getClass().getResource("img/UI/RedR.png")));
		blue.setIcon(new ImageIcon(getClass().getResource("img/UI/BlueB.png")));
		green.setIcon(new ImageIcon(getClass().getResource("img/UI/GreenG.png")));
		orange.setIcon(new ImageIcon(getClass().getResource("img/UI/AmberA.png")));
		
		save.setIcon(new ImageIcon(getClass().getResource("img/SaveIcon.png")));
		load.setIcon(new ImageIcon(getClass().getResource("img/Load.png")));
		back.setIcon(new ImageIcon(getClass().getResource("img/Back.png")));
		exit.setIcon(new ImageIcon(getClass().getResource("img/Exit.png")));
		save.setOpaque(false);save.setContentAreaFilled(false);save.setBorderPainted(false);
		load.setOpaque(false);load.setContentAreaFilled(false);load.setBorderPainted(false);
		back.setOpaque(false);back.setContentAreaFilled(false);back.setBorderPainted(false);
		exit.setOpaque(false);exit.setContentAreaFilled(false);exit.setBorderPainted(false);
		turnEnd.addActionListener(gameButtons);
		/*move.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cm.pause();	
				
				Point casilla = new Point((int) cursor.getX()/32, (int) cursor.getY()/32);
				
				int turnInt;
				switch (turn%2) {
				case 2:
					turnInt = 2;
					break;
				default:
					turnInt = 1;
					break;
				}
				if (mapGrid.get(casilla).size() == 2) {
					if (estadoMov == false) {
						if (((Tropa) mapGrid.get(casilla).get(1).get(1)).getTeam() == turnInt) {
								estadoMov = true;
								ogPos = casilla;
								cm.pause();
								
						} 
						
				
					}
				 else {
					
					cm.resume();
					estadoMov = false;
				 }
				} else if (!(((Terreno) mapGrid.get(casilla).get(0).get(1)).getIDTerreno() != ListaIDTerreno.FACTORY || mapGrid.get(casilla).size() == 2)) {
					if (((Factory) mapGrid.get(casilla).get(0).get(1)).getTeam() == turnInt) {
						createTropa(cursor.getLocation(), mapGrid, layeredGamePanel, troopPanel, turn, casilla, entityPanel);
						} 
				}
			}
		});*/
		
		//Labels de los atributos del panel troopInfo
		/*
		JLabel name = new JLabel();
		JLabel health = new JLabel();
		JLabel attack1 = new JLabel();
		JLabel attack2 = new JLabel();
		JLabel minRange = new JLabel();
		JLabel maxRange = new JLabel();
		JLabel ammo = new JLabel();
		JLabel energy = new JLabel();
		JLabel terrain = new JLabel();
		
		troopInfo.add(name);
		troopInfo.add(health);
		troopInfo.add(attack1);
		troopInfo.add(attack2);
		troopInfo.add(minRange);
		troopInfo.add(maxRange);
		troopInfo.add(ammo);
		troopInfo.add(energy);
		*/
		
		abajo.add(movData);			//Se añade el panel de información de movimiento al panel que contiene toda la parte inferior
		abajo.add(troopInfo);		//Se añade el panel de información de tropas al panel que contiene toda la parte inferior
		
		derecha.add(info);		//Se añade el panel superior que contiene información general de la partida al panel que contiene toda la parte derecha (informacion)
		derecha.add(abajo);		//Se añade el panel inferior que contiene los paneles movData y troopInfo al panel que contiene toda la parte derecha (informacion)
		
		cp.add(layeredGamePanel);		//Se añade el panel del juego al contenedor de la ventana
		cp.add(derecha);		//Se añade el panel de la derecha (información) al contenedor de la ventana
		
		//redundant in newer versions
		//int width = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getWidth();
		//int height = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getHeight();
		
		
		this.pack();//Se asegura de que todos los componentes están por lo menos a su tamaño preferido
		entityPanel.setFocusable(true);
		entityPanel.requestFocus();
		entityPanel.addKeyListener(kListener);		//Se añade el KeyListener a la ventana
		addMouseListener(ml);
		this.setTitle("B.A.S.E.D Tactics");		//Se cambia el titulo de la ventana
		this.setIconImage(new ImageIcon(getClass().getResource("img/tankicon.png")).getImage());		//Coloca el icono de la ventana
		this.setSize(new Dimension(1088, 672));		//Se cambia el tamaño de la ventana
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);		//El proceso se termina cuando se cierra la ventana
		
		
		
		
		}
	/*
	public HashMap<Point, ArrayList<ArrayList<Object>>> loadHashMap() {
		HashMap<Point, ArrayList<ArrayList<Object>>> mapGridFunc = new HashMap<>();
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream("BeanIsland.dat"));
			
			mapGridFunc = (HashMap) ois.readObject();
			System.out.println("fuck");
			System.out.println(mapGridFunc.get(new Point(0,0)).get(0).get(0));
			System.out.println("endFuck");

			ois.close();

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println(mapGridFunc);
		return mapGridFunc;
	}
	
	public JPanel loadMap(JPanel mapPanel, HashMap<Point, ArrayList<ArrayList<Object>>> mapGridFunc2) {
			Set<Point> keys = mapGridFunc2.keySet();
			mapPanel.removeAll();
			JLabel jl = new JLabel();
			for (Point i : keys) {
				jl = (JLabel) mapGridFunc2.get(i).get(0).get(0);
				mapPanel.add(jl);
			}

		return mapPanel;
	}
	
	*/

	
	
	public int income(int count) {
		return count*1000;
	}
		
	public boolean troopTeamFinder(Point casilla, HashMap<Point, ArrayList<ArrayList<Object>>> mapGrid,Tropa troop) {
		
		if(((Tropa) mapGrid.get(casilla).get(1).get(1)).getTeam() == troop.getTeam()){
			 return true;
		}
		else {
			return false;
		}
		 
		
	}
	public void terminaTurno(int team, JPanel mapPanel, HashMap<Point, ArrayList<ArrayList<Object>>> mapGridFunc3) {
		System.out.println("p1:"+p1.getfunds()+"p2:"+p2.getfunds()+"p3:"+p3.getfunds()+"p4:"+p4.getfunds());
		switch (turnInt) { 
		case 2:
			p2.setfunds(p2.getfunds()+((Config) mapGrid.get(po).get(0).get(1)).getBlue()*1000);
			this.turn++;
			sic.changeSound("combat1.wav");
			mus.interrupt();
			mus.start();
			break;
		case 3:
			p3.setfunds(p3.getfunds()+((Config) mapGrid.get(po).get(0).get(1)).getGreen()*1000);
			this.turn++;
			sic.changeSound("combat2.wav");
			mus.interrupt();
			mus.start();
			break;
		case 4:
			p4.setfunds(p4.getfunds()+((Config) mapGrid.get(po).get(0).get(1)).getOrange()*1000);
			this.turn++;
			sic.changeSound("combat2.wav");
			mus.interrupt();
			mus.start();
			break;
		case 1:
			p1.setfunds(p1.getfunds()+((Config) mapGrid.get(po).get(0).get(1)).getRed()*1000);
			this.turn++;
			sic.changeSound("combat2.wav");
			mus.interrupt();
			mus.start();
			break;
		}
		
		
		
	}
	
	public void createTropa(Point pos, HashMap<Point, ArrayList<ArrayList<Object>>> mapGrid, JLayeredPane lp, JPanel troopPanel, int turn, Point casilla, JLayeredPane entityPanel) {
		JInternalFrame jif = new JInternalFrame();
		
		jif.pack();
		jif.setLocation(192, 160);
		jif.setResizable(false);
		jif.setVisible(true);
		Container jifCP = jif.getContentPane();
		JLabel jm = new JLabel();
		JButton infFoot = new JButton("1500");
		infFoot.setVerticalTextPosition(JButton.BOTTOM);
		switch (turnInt) {
		case 2:
			infFoot.setIcon(new ImageIcon(getClass().getResource("img/troop/blue/InftBLUE.png")));
			break;
		case 1:
			infFoot.setIcon(new ImageIcon(getClass().getResource("img/troop/red/InftRED.png")));
			break;
		case 3:
			infFoot.setIcon(new ImageIcon(getClass().getResource("img/troop/green/InftGREEN.png")));
			break;
		case 4:
			infFoot.setIcon(new ImageIcon(getClass().getResource("img/troop/orange/InftORANGE.png")));
			break;
		}
		
		JButton infMech = new JButton("2500");
		infMech.setVerticalTextPosition(JButton.BOTTOM);
		switch (turnInt) {
		case 2:
			infMech.setIcon(new ImageIcon(getClass().getResource("img/troop/blue/MecBLUE.png")));
			break;
		case 1:
			infMech.setIcon(new ImageIcon(getClass().getResource("img/troop/red/MecRED.png")));
			break;
		case 3:
			infMech.setIcon(new ImageIcon(getClass().getResource("img/troop/green/MecGREEN.png")));
			break;
		case 4:
			infMech.setIcon(new ImageIcon(getClass().getResource("img/troop/orange/MecORANGE.png")));
			break;
		}
		
		JButton infBike = new JButton("2500");
		infBike.setVerticalTextPosition(JButton.BOTTOM);
		switch (turnInt) {
		case 2:
			infBike.setIcon(new ImageIcon(getClass().getResource("img/troop/blue/BikeBLUE.png")));
			break;
		case 1:
			infBike.setIcon(new ImageIcon(getClass().getResource("img/troop/red/BikeRED.png")));
			break;
		case 3:
			infBike.setIcon(new ImageIcon(getClass().getResource("img/troop/green/BikeGREEN.png")));
			break;
		case 4:
			infBike.setIcon(new ImageIcon(getClass().getResource("img/troop/orange/BikeORANGE.png")));
			break;
		}
		
		JButton rocl = new JButton("15000");
		rocl.setVerticalTextPosition(JButton.BOTTOM);
		switch (turnInt) {
		case 2:
			rocl.setIcon(new ImageIcon(getClass().getResource("img/troop/blue/RocketBLUE.png")));
			break;
		case 1:
			rocl.setIcon(new ImageIcon(getClass().getResource("img/troop/red/RocketRED.png")));
			break;
		case 3:
			rocl.setIcon(new ImageIcon(getClass().getResource("img/troop/green/RocketGREEN.png")));
			break;
		case 4:
			rocl.setIcon(new ImageIcon(getClass().getResource("img/troop/orange/RocketORANGE.png")));
			break;
		}

		JButton antiA = new JButton("8000");
		antiA.setVerticalTextPosition(JButton.BOTTOM);
		switch (turnInt) {
		case 2:
			antiA.setIcon(new ImageIcon(getClass().getResource("img/troop/blue/AaBLUE.png")));
			break;
		case 1:
			antiA.setIcon(new ImageIcon(getClass().getResource("img/troop/red/AaRED.png")));
			break;
		case 3:
			antiA.setIcon(new ImageIcon(getClass().getResource("img/troop/green/AaGREEN.png")));
			break;
		case 4:
			antiA.setIcon(new ImageIcon(getClass().getResource("img/troop/orange/AaORANGE.png")));
			break;
		}
		
		JButton vAPC = new JButton("5000");
		vAPC.setVerticalTextPosition(JButton.BOTTOM);
		switch (turnInt) {
		case 2:
			vAPC.setIcon(new ImageIcon(getClass().getResource("img/troop/blue/ToaBLUE.png")));
			break;
		case 1:
			vAPC.setIcon(new ImageIcon(getClass().getResource("img/troop/red/ToaRED.png")));
			break;
		case 3:
			vAPC.setIcon(new ImageIcon(getClass().getResource("img/troop/green/ToaGREEN.png")));
			break;
		case 4:
			vAPC.setIcon(new ImageIcon(getClass().getResource("img/troop/orange/ToaORANGE.png")));
			break;
		}
		
		JButton arty = new JButton("6000");
		arty.setVerticalTextPosition(JButton.BOTTOM);
		switch (turnInt) {
		case 2:
			arty.setIcon(new ImageIcon(getClass().getResource("img/troop/blue/ArtilleryBLUE.png")));
			break;
		case 1:
			arty.setIcon(new ImageIcon(getClass().getResource("img/troop/red/ArtilleryRED.png")));
			break;
		case 3:
			arty.setIcon(new ImageIcon(getClass().getResource("img/troop/green/ArtilleryGREEN.png")));
			break;
		case 4:
			arty.setIcon(new ImageIcon(getClass().getResource("img/troop/orange/ArtilleryORANGE.png")));
			break;
		}
		
		JButton tankL = new JButton("6000");
		tankL.setVerticalTextPosition(JButton.BOTTOM);
		switch (turnInt) {
		case 2:
			tankL.setIcon(new ImageIcon(getClass().getResource("img/troop/blue/LTankBLUE.png")));
			break;
		case 1:
			tankL.setIcon(new ImageIcon(getClass().getResource("img/troop/red/LTankRED.png")));
			break;
		case 3:
			tankL.setIcon(new ImageIcon(getClass().getResource("img/troop/green/LTankGREEN.png")));
			break;
		case 4:
			tankL.setIcon(new ImageIcon(getClass().getResource("img/troop/orange/LTankORANGE.png")));
			break;
		}
		
		JButton tankM = new JButton("12000");
		tankM.setVerticalTextPosition(JButton.BOTTOM);
		switch (turnInt) {
		case 2:
			tankM.setIcon(new ImageIcon(getClass().getResource("img/troop/blue/MTankBLUE.png")));
			break;
		case 1:
			tankM.setIcon(new ImageIcon(getClass().getResource("img/troop/red/MTankRED.png")));
			break;
		case 3:
			tankM.setIcon(new ImageIcon(getClass().getResource("img/troop/green/MTankGREEN.png")));
			break;
		case 4:
			tankM.setIcon(new ImageIcon(getClass().getResource("img/troop/orange/MTankORANGE.png")));
			break;
		}
		
		JButton tankH = new JButton("16000");
		tankH.setVerticalTextPosition(JButton.BOTTOM);
		switch (turnInt) {
		case 2:
			tankH.setIcon(new ImageIcon(getClass().getResource("img/troop/blue/HTankBLUE.png")));
			break;
		case 1:
			tankH.setIcon(new ImageIcon(getClass().getResource("img/troop/red/HTankRED.png")));
			break;
		case 3:
			tankH.setIcon(new ImageIcon(getClass().getResource("img/troop/green/HTankGREEN.png")));
			break;
		case 4:
			tankH.setIcon(new ImageIcon(getClass().getResource("img/troop/orange/HTankORANGE.png")));
			break;
		}
		
		JButton vRecon = new JButton("4000");
		vRecon.setVerticalTextPosition(JButton.BOTTOM);
		switch (turnInt) {
		case 2:
			vRecon.setIcon(new ImageIcon(getClass().getResource("img/troop/blue/ReconBLUE.png")));
			break;
		case 1:
			vRecon.setIcon(new ImageIcon(getClass().getResource("img/troop/red/ReconRED.png")));
			break;
		case 3:
			vRecon.setIcon(new ImageIcon(getClass().getResource("img/troop/green/ReconGREEN.png")));
			break;
		case 4:
			vRecon.setIcon(new ImageIcon(getClass().getResource("img/troop/orange/ReconORANGE.png")));
			break;
		}
		
		JButton close = new JButton("CLOSE");
		tankH.setVerticalTextPosition(JButton.BOTTOM);
		
		ActionListener troops = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Tropa tr = null;
				Player pt = new Player(1);
				switch	(turnInt) {
				case 1:
					pt.setfunds(p1.getfunds());
					break;
				case 2:
					pt.setfunds(p2.getfunds());
					break;
				case 3:
					pt.setfunds(p3.getfunds());
					break;
				case 4:
					pt.setfunds(p4.getfunds());
					break;
				}
				if (e.getSource()==infFoot && pt.getfunds()>=1500) {
					pt.setfunds(pt.getfunds()-1500);
					tr = new InfFoot(0);
					switch (turnInt){
						case 2:
							tr.setTeam(2);
							jm.setIcon(new ImageIcon(getClass().getResource("img/troop/blue/InftBLUE.png")));
							break;
						case 1:
							tr.setTeam(1);
							jm.setIcon(new ImageIcon(getClass().getResource("img/troop/red/InftRED.png")));
							break;
						case 3:
							tr.setTeam(3);
							jm.setIcon(new ImageIcon(getClass().getResource("img/troop/green/InftGREEN.png")));
							break;
						case 4:
							tr.setTeam(4);
							jm.setIcon(new ImageIcon(getClass().getResource("img/troop/orange/InftORANGE.png")));
							break;
					}		
				} else if (e.getSource()==infMech && pt.getfunds()>=2500) {
					tr = new InfMech(0);
					pt.setfunds(pt.getfunds()-2500);
					switch (turnInt){
						case 2:
							tr.setTeam(2);
							jm.setIcon(new ImageIcon(getClass().getResource("img/troop/blue/MecBLUE.png")));
							break;
						case 1:
							tr.setTeam(1);
							jm.setIcon(new ImageIcon(getClass().getResource("img/troop/red/MecRED.png")));
							break;
						case 3:
							tr.setTeam(3);
							jm.setIcon(new ImageIcon(getClass().getResource("img/troop/green/MecGREEN.png")));
							break;
						case 4:
							tr.setTeam(4);
							jm.setIcon(new ImageIcon(getClass().getResource("img/troop/orange/MecORANGE.png")));
							break;
					}		
				} else if (e.getSource()==infBike && pt.getfunds()>=2500) {
					tr = new InfBike(0);
					pt.setfunds(pt.getfunds()-2500);
					switch (turnInt){
					case 2:
						tr.setTeam(2);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/blue/bikeBLUE.png")));
						break;
					case 1:
						tr.setTeam(1);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/red/bikeRED.png")));
						break;
					case 3:
						tr.setTeam(3);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/green/bikeGREEN.png")));
						break;
					case 4:
						tr.setTeam(4);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/orange/bikeORANGE.png")));
						break;
					}		
				} else if (e.getSource()==vRecon && pt.getfunds()>=4000) {
					tr = new VRecon(0);
					pt.setfunds(pt.getfunds()-4000);
					switch (turnInt){
					case 2:
						tr.setTeam(2);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/blue/ReconBLUE.png")));
						break;
					case 1:
						tr.setTeam(1);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/red/ReconRED.png")));
						break;
					case 3:
						tr.setTeam(3);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/green/ReconGREEN.png")));
						break;
					case 4:
						tr.setTeam(4);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/orange/ReconORANGE.png")));
						break;
					}		
				} else if (e.getSource()==antiA && pt.getfunds()>=8000) {
					tr = new AntiA(0);
					pt.setfunds(pt.getfunds()-8000);
					switch (turnInt){
					case 2:
						tr.setTeam(2);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/blue/AaBLUE.png")));
						break;
					case 1:
						tr.setTeam(1);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/red/AaRED.png")));
						break;
					case 3:
						tr.setTeam(3);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/green/AaGREEN.png")));
						break;
					case 4:
						tr.setTeam(4);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/orange/AaORANGE.png")));
						break;
					}		
				} else if (e.getSource()==arty && pt.getfunds()>=6000) {
					tr = new Arty(0);
					pt.setfunds(pt.getfunds()-6000);
					switch (turnInt){
					case 2:
						tr.setTeam(2);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/blue/ArtilleryBLUE.png")));
						break;
					case 1:
						tr.setTeam(1);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/red/ArtilleryRED.png")));
						break;
					case 3:
						tr.setTeam(3);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/green/ArtilleryGREEN.png")));
						break;
					case 4:
						tr.setTeam(4);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/orange/ArtilleryORANGE.png")));
						break;
					}		
				} else if (e.getSource()==rocl && pt.getfunds()>=15000) {
					tr = new Rocl(0);
					pt.setfunds(pt.getfunds()-15000);
					switch (turnInt){
					case 2:
						tr.setTeam(2);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/blue/RocketBLUE.png")));
						break;
					case 1:
						tr.setTeam(1);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/red/RocketRED.png")));
						break;
					case 3:
						tr.setTeam(3);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/green/RocketGREEN.png")));
						break;
					case 4:
						tr.setTeam(4);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/orange/RocketORANGE.png")));
						break;
					}		
				} else if (e.getSource()==tankL && pt.getfunds()>=6000) {
					tr = new TankL(0);
					pt.setfunds(pt.getfunds()-6000);
					switch (turnInt){
					case 2:
						tr.setTeam(2);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/blue/LTankBLUE.png")));
						break;
					case 1:
						tr.setTeam(1);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/red/LTankRED.png")));
						break;
					case 3:
						tr.setTeam(3);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/green/LTankGREEN.png")));
						break;
					case 4:
						tr.setTeam(4);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/orange/LTankORANGE.png")));
						break;
					}		
				} else if (e.getSource()==tankM && pt.getfunds()>=1200) {
					tr = new TankM(0);
					pt.setfunds(pt.getfunds()-12000);
					switch (turnInt){
					case 2:
						tr.setTeam(2);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/blue/MTankBLUE.png")));
						break;
					case 1:
						tr.setTeam(1);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/red/MTankRED.png")));
						break;
					case 3:
						tr.setTeam(3);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/green/MTankGREEN.png")));
						break;
					case 4:
						tr.setTeam(4);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/orange/MTankORANGE.png")));
						break;
					}		
				} else if (e.getSource()==tankH && pt.getfunds()>=16000) {
					tr = new TankH(0);
					pt.setfunds(pt.getfunds()-16000);
					switch (turnInt){
					case 2:
						tr.setTeam(2);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/blue/HTankBLUE.png")));
						break;
					case 1:
						tr.setTeam(1);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/red/HTankRED.png")));
						break;
					case 3:
						tr.setTeam(3);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/green/HtankGREEN.png")));
						break;
					case 4:
						tr.setTeam(4);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/orange/HTankORANGE.png")));
						break;
					}		
				} else if (e.getSource()==vAPC && pt.getfunds()>=5000) {
					tr = new VApc(0);
					pt.setfunds(pt.getfunds()-5000);
					switch (turnInt){
					case 2:
						tr.setTeam(2);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/blue/ToaBLUE.png")));
						break;
					case 1:
						tr.setTeam(1);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/red/ToaRED.png")));
						break;
					case 3:
						tr.setTeam(3);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/green/ToaGREEN.png")));
						break;
					case 4:
						tr.setTeam(4);
						jm.setIcon(new ImageIcon(getClass().getResource("img/troop/orange/ToaORANGE.png")));
						break;
					}
				} else if (e.getSource()==close) {
					jif.dispose(); entityPanel.requestFocus();
				}
				if (e.getSource()!=close) {
					if (tr!=null) {
						ArrayList<Object> troopsList = new ArrayList<>();
						jm.setBounds((int) pos.getX(), (int) pos.getY(), 32, 32);
						troopsList.add(jm);
						troopsList.add(tr);
						mapGrid.get(casilla).add(troopsList);
						troopPanel.add(jm);
						JLabel jj = new JLabel();
						entityPanel.add(jj, 2, 0); 
						jj.setIcon(new ImageIcon(getClass().getResource("img/Moved.png"))); 
						jj.setVisible(true); 
						jj.setBounds(mov*8, mov*8, 32, 32); 
						jj.setLocation((int) casilla.getX()*32, (int) casilla.getY()*32); 
						movedList.add(jj);
						spentList.add(tr);
					}
					switch	(turnInt) {
					case 1:
						p1.setfunds(pt.getfunds());
						break;
					case 2:
						p2.setfunds(pt.getfunds());
						break;
					case 3:
						p3.setfunds(pt.getfunds());
						break;
					case 4:
						p4.setfunds(pt.getfunds());
						break;
					}
					jif.dispose(); entityPanel.requestFocus();
					
					
				}
			}
		};
		infFoot.addActionListener(troops);
		infMech.addActionListener(troops);
		infBike.addActionListener(troops);
		vRecon.addActionListener(troops);
		antiA.addActionListener(troops);
		arty.addActionListener(troops);
		rocl.addActionListener(troops);
		tankL.addActionListener(troops);
		tankM.addActionListener(troops);
		tankH.addActionListener(troops);
		vAPC.addActionListener(troops);
		close.addActionListener(troops);
				
		jifCP.setLayout(new GridLayout(4, 3));
		
		jifCP.add(infFoot);
		jifCP.add(infMech);
		jifCP.add(infBike);
		jifCP.add(vRecon);
		jifCP.add(antiA);
		jifCP.add(arty);
		jifCP.add(rocl);
		jifCP.add(tankL);
		jifCP.add(tankM);
		jifCP.add(tankH);
		jifCP.add(vAPC);
		jifCP.add(close);
		
		jif.pack();		//Se asegura de que todos los componentes están por lo menos a su tamaño preferido
		jif.setSize(new Dimension(336, 336));		//Se cambia el tamaño de la ventana
		lp.add(jif, 4, 0);
	}
	
	public synchronized HashMap<Point, ArrayList<ArrayList<Object>>> reloadHMap(){
		return this.mapGrid;
	}
	public HashMap<Point, ArrayList<ArrayList<Object>>> moverTropa(HashMap<Point, ArrayList<ArrayList<Object>>> mapGrid, JLayeredPane lp, JPanel troopPanel, Point casilla, Point og) {
		Tropa t = (Tropa) mapGrid.get(og).get(1).get(1);
		JLabel tl = (JLabel) mapGrid.get(og).get(1).get(0);
		ArrayList<Object> al = new ArrayList<>();
		al.add(tl); al.add(t);
		mapGrid.get(og).remove(1);
		mapGrid.get(casilla).add(al);
		troopPanel.repaint();
		return mapGrid;
	}
	
	public HashMap<Point, ArrayList<ArrayList<Object>>> loadMap(){
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream("T.dat"));

			mapGrid = (HashMap) ois.readObject();
			Point p =new Point(-1,-1);
			((Config) mapGrid.get(p).get(0).get(1)).getPlayers().add(p1);
			((Config) mapGrid.get(p).get(0).get(1)).getPlayers().add(p2);
			((Config) mapGrid.get(p).get(0).get(1)).getPlayers().add(p3);
			((Config) mapGrid.get(p).get(0).get(1)).getPlayers().add(p4);

			ois.close();

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return mapGrid;
	}
	public JPanel quickLoad(JPanel mapPanel) {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream("QS.dat"));

			mapGrid = (HashMap) ois.readObject();

			ois.close();
			Set<Point> keys = mapGrid.keySet();
			mapPanel.removeAll();
			JLabel jl = new JLabel();
			JLabel jt = new JLabel();
			for (Point i : keys) {
				jl = (JLabel) mapGrid.get(i).get(0).get(0);
				if	(mapGrid.get(i).size()==2 && (JLabel) mapGrid.get(i).get(1).get(0)!=null) {
					jt = (JLabel) mapGrid.get(i).get(1).get(0);
				}
				mapPanel.add(jt);
				mapPanel.add(jl);

			}

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return mapPanel;
	}
	
}

/*	
	public static int grafoVert(int trop) {
		int n = 1;
		int vert = 1;
		for (int i = 0; i < (trop - 1); i++) {
			n += 2;
			vert += n;		
			//System.out.println("n: " + n + " vert: " + vert);
		}
		vert *= 2;
		vert += n + 2;
		return vert;
	}
	public static int grafoArist(int trop) {
		int n = 1;
		int arist = 1;
		for (int i = 0; i < ((trop * 2) - 2); i++) {
			n += 1;
			arist += n;		
			//System.out.println("n: " + n + " arist: " + arist);
		}
		arist *= 2;
		arist += n + 1;
		return arist;
	}
	public static Dimension grafoVertPos() {
		
		
		
		return new Dimension(1,2);
	}
	
*/	
	
	


//TODO list
/*
·Panel de información para quien quiera hacerlo
 
·Methods (...)
·Graph:
	-node positions relative to map
	-positions in map relative to troop



Problems:
�detecting things that are on top of eachother
�movement -> graph detecting what the troop can be on top of and calculate where it cant reach anymore
�








 
 */
