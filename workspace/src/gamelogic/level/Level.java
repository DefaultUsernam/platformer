package gamelogic.level;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import gameengine.PhysicsObject;
import gameengine.graphics.Camera;
import gameengine.loaders.Mapdata;
import gameengine.loaders.Tileset;
import gamelogic.GameResources;
import gamelogic.Main;
import gamelogic.enemies.Enemy;
import gamelogic.player.Player;
import gamelogic.tiledMap.Map;
import gamelogic.tiles.Flag;
import gamelogic.tiles.Flower;
import gamelogic.tiles.Gas;
import gamelogic.tiles.Lever;
import gamelogic.tiles.SolidTile;
import gamelogic.tiles.Spikes;
import gamelogic.tiles.Tile;
import gamelogic.tiles.Water;

public class Level {

	private LevelData leveldata;
	private Map map;
	private Enemy[] enemies;
	public static Player player;
	private Camera camera;

	private boolean active;
	private boolean playerDead;
	private boolean playerWin;

	private ArrayList<Enemy> enemiesList = new ArrayList<>();
	private ArrayList<Flower> flowers = new ArrayList<>();
	private ArrayList<Water> waters = new ArrayList<>();
	private ArrayList<Gas> gases = new ArrayList<>();
	private ArrayList<Lever> levers = new ArrayList<>();

	private List<PlayerDieListener> dieListeners = new ArrayList<>();
	private List<PlayerWinListener> winListeners = new ArrayList<>();

	private Mapdata mapdata;
	private int width;
	private int height;
	private int tileSize;
	private Tileset tileset;
	public static float GRAVITY = 70;
	private long time;
	private int dyingTime;
	private boolean inGas;
	private boolean inWater;
	// System.currentTimeMillis() - time = current time in milliseconds, divide by 1000 to get time in seconds

	public Level(LevelData leveldata) {
		this.leveldata = leveldata;
		mapdata = leveldata.getMapdata();
		width = mapdata.getWidth();
		height = mapdata.getHeight();
		tileSize = mapdata.getTileSize();
		restartLevel();
		time = System.currentTimeMillis();
	}

	public LevelData getLevelData(){
		return leveldata;
	}

	public void restartLevel() {
		int[][] values = mapdata.getValues();
		Tile[][] tiles = new Tile[width][height];
		waters = new ArrayList();
		gases = new ArrayList();
		levers = new ArrayList();

	int dyingTime = 200;
	 boolean inGas = false;
	 boolean inWater = false;
		for (int x = 0; x < width; x++) {
			int xPosition = x;
			for (int y = 0; y < height; y++) {
				int yPosition = y;

				tileset = GameResources.tileset;

				tiles[x][y] = new Tile(xPosition, yPosition, tileSize, null, false, this);
				if (values[x][y] == 0)
					tiles[x][y] = new Tile(xPosition, yPosition, tileSize, null, false, this); // Air
				else if (values[x][y] == 1)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid"), this);

				else if (values[x][y] == 2)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.HORIZONTAL_DOWNWARDS, this);
				else if (values[x][y] == 3)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.HORIZONTAL_UPWARDS, this);
				else if (values[x][y] == 4)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.VERTICAL_LEFTWARDS, this);
				else if (values[x][y] == 5)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.VERTICAL_RIGHTWARDS, this);
				else if (values[x][y] == 6)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Dirt"), this);
				else if (values[x][y] == 7)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Grass"), this);
				else if (values[x][y] == 8)
					enemiesList.add(new Enemy(xPosition*tileSize, yPosition*tileSize, this)); // TODO: objects vs tiles
				else if (values[x][y] == 9)
					tiles[x][y] = new Flag(xPosition, yPosition, tileSize, tileset.getImage("Flag"), this);
				else if (values[x][y] == 10) {
					tiles[x][y] = new Flower(xPosition, yPosition, tileSize, tileset.getImage("Flower1"), this, 1);
					flowers.add((Flower) tiles[x][y]);
				} else if (values[x][y] == 11) {
					tiles[x][y] = new Flower(xPosition, yPosition, tileSize, tileset.getImage("Flower2"), this, 2);
					flowers.add((Flower) tiles[x][y]);
				} else if (values[x][y] == 12)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_down"), this);
				else if (values[x][y] == 13)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_up"), this);
				else if (values[x][y] == 14)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_middle"), this);
				else if (values[x][y] == 15)
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasOne"), this, 1);
				else if (values[x][y] == 16)
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasTwo"), this, 2);
				else if (values[x][y] == 17)
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasThree"), this, 3);
				else if (values[x][y] == 18){
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Falling_water"), this, 0);
					waters.add((Water) tiles[x][y]);
				}
				else if (values[x][y] == 19){
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Full_water"), this, 3);
					waters.add((Water) tiles[x][y]);
				}
				else if (values[x][y] == 20){
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Half_water"), this, 2);
					waters.add((Water) tiles[x][y]);
				}
				else if (values[x][y] == 21){
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Quarter_water"), this, 1);
					waters.add((Water) tiles[x][y]);
				}
				else if (values[x][y] == 22){
					Lever l = new Lever(xPosition, yPosition, tileSize, tileset.getImage("Left_lever"), this);
					tiles[x][y] = l;
					levers.add(l);
				}
				else if (values[x][y] == 23){
					tiles[x][y] = new Lever(xPosition, yPosition, tileSize, tileset.getImage("Right_lever"), this);
				}
			}

		}
		enemies = new Enemy[enemiesList.size()];
		map = new Map(width, height, tileSize, tiles);
		camera = new Camera(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT, 0, map.getFullWidth(), map.getFullHeight());
		for (int i = 0; i < enemiesList.size(); i++) {
			enemies[i] = new Enemy(enemiesList.get(i).getX(), enemiesList.get(i).getY(), this);
		}
		player = new Player(leveldata.getPlayerX() * map.getTileSize(), leveldata.getPlayerY() * map.getTileSize(),
				this);
		camera.setFocusedObject(player);

		active = true;
		playerDead = false;
		playerWin = false;
	}

	public void onPlayerDeath() {
		active = false;
		playerDead = true;
		throwPlayerDieEvent();
	}

	public void onPlayerWin() {
		active = false;
		playerWin = true;
		throwPlayerWinEvent();
	}

	//time since last frame (tslf)
	public void update(float tslf) {
		if (active) {
			// Update the player
			player.update(tslf);

			// Player death
			if (map.getFullHeight() + 100 < player.getY())
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.BOT] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.TOP] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.LEF] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.RIG] instanceof Spikes)
				onPlayerDeath();

			for (int i = 0; i < flowers.size(); i++) {
				if (flowers.get(i).getHitbox().isIntersecting(player.getHitbox())) {
					if(flowers.get(i).getType() == 1)
						water(flowers.get(i).getCol(), flowers.get(i).getRow(), map, 3);
					else
						addGas(flowers.get(i).getCol(), flowers.get(i).getRow(), map, 20, new ArrayList<Gas>());
					flowers.remove(i);
					i--;
				}
			}

			inWater = false;
			for(Water w: waters){
				//checks if player is in water
				if(w.getHitbox().isIntersecting(player.getHitbox())){
					inWater = true;
				}
			}
			
				if (inWater){
					player.setMovementY(-30);
					GRAVITY = 10;
					
				}
				 else{
				 	GRAVITY = 70;
				}
				

						for(Gas g: gases){
				//checks if player is in a gas
				if(g.getHitbox().isIntersecting(player.getHitbox())){
					inGas = true;
				}
			}
			if(inGas){
				player.inGas = true;
				camera.inGas = true;
				Camera.SHOW_CAMERA = true;
				//set player to blue color
				//blocks out screen
			}
			else{
				player.inGas = false;
				camera.inGas = false;
				Camera.SHOW_CAMERA = false;
				//revert back to normal
			}
			inGas = false;
			for(Lever l: levers){
				if(l.getHitbox().isIntersecting(player.getHitbox())){
					Lever j = (Lever) l;
					if (!j.isRight){
						float m = (float) (Math.random()*2000);
						float n = (float) (Math.random()*200);
					player.setX(m);
					player.setY(n);
					player.setHitboxX(m);
					player.setHitboxY(n);
					j.toRight();
					l.isRight = true;
					}
				}

			}
		

			// Update the enemies
			for (int i = 0; i < enemies.length; i++) {
				enemies[i].update(tslf);
				if (player.getHitbox().isIntersecting(enemies[i].getHitbox())) {
					onPlayerDeath();
				}
			}

			// Update the map
			map.update(tslf);

			// Update the camera
			camera.update(tslf);
		}
	}
	
	
	//#############################################################################################################
	//Your code goes here! 
	//Please make sure you read the rubric/directions carefully and implement the solution recursively!
	private void water(int col, int row, Map map, int fullness) {
		                       //make water (You’ll need modify this to make different kinds of water such as half water and quarter water)
		
		//Creates a water with graphics based on the fullness level
		Water w = new Water (col, row, tileSize, tileset.getImage("Full_water"), this, fullness);
		if(fullness==3){
			w = new Water (col, row, tileSize, tileset.getImage("Full_water"), this, fullness);
		}
		else if(fullness==2){
		 w = new Water (col, row, tileSize, tileset.getImage("Half_water"), this, fullness);
		}
		else if(fullness==1){
		 w = new Water (col, row, tileSize, tileset.getImage("Quarter_water"), this, fullness);
		}
		else{
		 w = new Water (col, row, tileSize, tileset.getImage("Falling_water"), this, fullness);

		//Places the new Water tile
		}
		map.addTile(col, row, w);
			waters.add(w);


                       //check if we can go down

			//Checks the to see if water tiles can be placed below and if the follow tile is solid so that a full water can be placed on it
			if(row+1 < map.getTiles()[0].length && !(map.getTiles()[col][row+1] instanceof Water) && !(map.getTiles()[col][row+1].isSolid())){
				if (row+2 < map.getTiles()[0].length && map.getTiles()[col][row+2].isSolid()){
					water(col, row+1, map, 3);
				}
				else{
					water(col, row+1, map, 0);
				}
			}
						//if we can’t go down go left and right.

			//Checks to see if the water can flow left and/or right based on the fullness of the current water tile and if the blocks that the water would occupy exist and aren't solid or water
			else if (row < map.getTiles()[0].length  && fullness > 1) {
						//right
				if(col+1 < map.getTiles().length && !(map.getTiles()[col+1][row] instanceof Water) && !(map.getTiles()[col+1][row].isSolid())){
					water(col+1, row, map, fullness-1);
				}
				//left
				if(col-1 >= 0 && !(map.getTiles()[col-1][row] instanceof Water) && !(map.getTiles()[col-1][row].isSolid())) {
					water(col-1, row, map, fullness-1);
				}
			}

	}

	//Adds gas tiles until the requisite number of squares are filled or there is no more room 

	
	private void addGas(int col, int row, Map map, int numSquaresToFill, ArrayList<Gas> placedThisRound) {
	
		//Creates the original starting gas tile
	Gas g = new Gas (col, row, tileSize, tileset.getImage("GasOne"), this, 0);
	gases.add(g);
	map.addTile(col, row, g);
	placedThisRound.add(g);
	numSquaresToFill--;

	//Places gas in the pattern assigned
	while(placedThisRound.size()>0 && numSquaresToFill>0) {
		
		int c = placedThisRound.get(0).getCol();
		int r = placedThisRound.get(0).getRow();
		placedThisRound.remove(0);
		for (int rowIndex = r-1; rowIndex<=r+1; rowIndex++){
			for(int colIndex = c; colIndex>c-2; colIndex-=2){
				//Makes sure the correct amount of gas tiles are placed so that they don't get placed on existing gas or solid tiles
				if(!(colIndex==col && rowIndex==row) && !(map.getTiles()[colIndex][rowIndex].isSolid()) && !(map.getTiles()[colIndex][rowIndex] instanceof Gas) && numSquaresToFill>0){//last condition added after submit, idk if it helped or not
						Gas k = new Gas (colIndex, rowIndex, tileSize, tileset.getImage("GasOne"), this, 0);
						gases.add(k);
						placedThisRound.add(k);
						map.addTile(colIndex,rowIndex, k);
						numSquaresToFill--;
				}
				if (colIndex == c){
					colIndex+=3;
				}

		}
	}
		
		//make the desired pattern centered at the location of placedThisRound.get(0), remove the tile once processed from placedThisRound, be sure to add every tile you make to placedThisRound
	}
	}	

	public void draw(Graphics g) {
	   	 g.translate((int) -camera.getX(), (int) -camera.getY());
	   	 // Draw the map
	   	 for (int x = 0; x < map.getWidth(); x++) {
	   		 for (int y = 0; y < map.getHeight(); y++) {
	   			 Tile tile = map.getTiles()[x][y];
	   			 if (tile == null)
	   				 continue;
	   			 if(tile instanceof Gas) {
	   				
	   				 int adjacencyCount =0;
	   				 for(int i=-1; i<2; i++) {
	   					 for(int j =-1; j<2; j++) {
	   						 if(j!=0 || i!=0) {
	   							 if((x+i)>=0 && (x+i)<map.getTiles().length && (y+j)>=0 && (y+j)<map.getTiles()[x].length) {
	   								 if(map.getTiles()[x+i][y+j] instanceof Gas) {
	   									 adjacencyCount++;
	   								 }
	   							 }
	   						 }
	   					 }
	   				 }
	   				 if(adjacencyCount == 8) {
	   					 ((Gas)(tile)).setIntensity(2);
	   					 tile.setImage(tileset.getImage("GasThree"));
	   				 }
	   				 else if(adjacencyCount >5) {
	   					 ((Gas)(tile)).setIntensity(1);
	   					tile.setImage(tileset.getImage("GasTwo"));
	   				 }
	   				 else {
	   					 ((Gas)(tile)).setIntensity(0);
	   					tile.setImage(tileset.getImage("GasOne"));
	   				 }
	   			 }
	   			 if (camera.isVisibleOnCamera(tile.getX(), tile.getY(), tile.getSize(), tile.getSize()))
	   				 tile.draw(g);
	   		 }
			 if(inWater){
				g.drawRect(0, 0, width, height);
				g.setColor(Color.BLUE);
			 }
	   	 }


	   	 // Draw the enemies
	   	 for (int i = 0; i < enemies.length; i++) {
	   		 enemies[i].draw(g);
	   	 }


	   	 // Draw the player
	   	 player.draw(g);




	   	 // used for debugging
	   	 if (Camera.SHOW_CAMERA)
	   		 camera.draw(g);
	   	 g.translate((int) +camera.getX(), (int) +camera.getY());
	    }


	// --------------------------Die-Listener
	public void throwPlayerDieEvent() {
		for (PlayerDieListener playerDieListener : dieListeners) {
			playerDieListener.onPlayerDeath();
		}
	}

	public void addPlayerDieListener(PlayerDieListener listener) {
		dieListeners.add(listener);
	}

	// ------------------------Win-Listener
	public void throwPlayerWinEvent() {
		for (PlayerWinListener playerWinListener : winListeners) {
			playerWinListener.onPlayerWin();
		}
	}

	public void addPlayerWinListener(PlayerWinListener listener) {
		winListeners.add(listener);
	}

	// ---------------------------------------------------------Getters
	public boolean isActive() {
		return active;
	}

	public boolean isPlayerDead() {
		return playerDead;
	}

	public boolean isPlayerWin() {
		return playerWin;
	}

	public Map getMap() {
		return map;
	}

	public Player getPlayer() {
		return player;
	}
}