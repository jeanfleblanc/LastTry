package org.egordorichev.lasttry.entity.entities.item.tile;

import com.badlogic.gdx.utils.JsonValue;
import org.egordorichev.lasttry.entity.asset.Assets;
import org.egordorichev.lasttry.entity.component.IdComponent;
import org.egordorichev.lasttry.entity.component.SolidComponent;
import org.egordorichev.lasttry.entity.component.physics.CollisionComponent;
import org.egordorichev.lasttry.entity.entities.item.Item;
import org.egordorichev.lasttry.entity.entities.item.TileComponent;
import org.egordorichev.lasttry.entity.entities.world.World;
import org.egordorichev.lasttry.graphics.Graphics;
import org.egordorichev.lasttry.util.binary.BinaryPacker;

import java.util.Objects;

/**
 * The main part of the world
 */
public class Block extends Item {
	public static short SIZE = 16;

	public Block(String id) {
		super(id);

		this.addComponent(CollisionComponent.class);
		this.addComponent(TileComponent.class, SolidComponent.class);
	}

	/**
	 * Renders the block
	 *
	 * @param x Block X
	 * @param y Block Y
	 */
	public void render(short x, short y) {
		int neighbors = this.getNeighbors(x, y);

		Graphics.batch.draw(this.getComponent(TileComponent.class).tiles[0][neighbors], x * SIZE, y * SIZE);
	}

	/**
	 * Sets field according to the json asset
	 *
	 * @param asset Json asset
	 */
	@Override
	public void loadFields(JsonValue asset) {
		super.loadFields(asset);

		this.getComponent(TileComponent.class).tiles =
			Assets.getTexture("blocks/" + this.getComponent(IdComponent.class).id.replace(':', '_')
				+ "_tiles").split(SIZE, SIZE);

		this.getComponent(SolidComponent.class).solid =
			asset.getBoolean("solid", true);
	}

	/**
	 * Returns amount of tileable neighbors
	 *
	 * @param x Block X
	 * @param y Block Y
	 * @return Packed binary
	 */
	protected int getNeighbors(short x, short y) {
		String id = this.getComponent(IdComponent.class).id;

		boolean top = this.shouldTile(World.instance.getBlock(x, (short) (y + 1)), id);
		boolean right = this.shouldTile(World.instance.getBlock((short) (x + 1), y), id);
		boolean bottom = this.shouldTile(World.instance.getBlock(x, (short) (y - 1)), id);
		boolean left = this.shouldTile(World.instance.getBlock((short) (x - 1), y), id);

		return BinaryPacker.pack(top, right, bottom, left);
	}

	/**
	 * Returns, if blocks should tile
	 *
	 * @param neighbor Block type
	 * @param self Self type
	 * @return Should tile
	 */
	protected boolean shouldTile(String neighbor, String self) {
		return Objects.equals(neighbor, self);
	}
}