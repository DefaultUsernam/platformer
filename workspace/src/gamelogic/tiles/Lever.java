package gamelogic.tiles;

import gameengine.hitbox.RectHitbox;
import gamelogic.level.Level;
import java.awt.image.BufferedImage;
import gameengine.loaders.Tileset;
import gamelogic.GameResources;



public class Lever extends Tile{
	public boolean isRight;
    	public Lever(float x, float y, int size, BufferedImage image, Level level) {
		super(x, y, size, image, false, level);
		int offset =(int)(level.getLevelData().getTileSize()*0.1); //hitbox is offset by 10% of the tile size, code taken from SolidTile.java
		this.hitbox = new RectHitbox(x *size, y*size, 0, offset, size, size);
	}

	public void toRight(){
		super.setImage(GameResources.tileset.getImage("Right_lever")); //Switches image from a left lever to a right lever
	}

	public String toString() {
		return "I'm a lever at "+this.position.x+" "+this.position.y;
	}
}
