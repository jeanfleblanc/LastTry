package org.egordorichev.lasttry.world.components;

import org.egordorichev.lasttry.LastTry;
import org.egordorichev.lasttry.item.Item;
import org.egordorichev.lasttry.item.ItemID;
import org.egordorichev.lasttry.item.block.Block;
import org.egordorichev.lasttry.world.World;
import org.egordorichev.lasttry.world.chunk.Chunk;

public class WorldBlocksComponent extends WorldComponent {
	public WorldBlocksComponent(World world) {
		super(world);
	}

	public Block get(int x, int y) {
		return (Block) Item.fromID(this.getID(x, y));
	}

	public short getID(int x, int y) {
		Chunk chunk = this.getChunk(x, y);

		if (chunk == null) {
			return ItemID.none;
		}

		return chunk.getBlock(x, y);
	}

	public void set(short id, int x, int y) {
		Chunk chunk = this.getChunk(x, y);

		if (chunk == null) {
			return;
		}

		chunk.setBlock(id, x, y);
	}

	public byte getHP(int x, int y) {
		Chunk chunk = this.getChunk(x, y);

		if (chunk == null) {
			return Block.MAX_HP;
		}

		return chunk.getBlockHP(x, y);
	}

	public void setHP(byte hp, int x, int y) {
		Chunk chunk = this.getChunk(x, y);

		if (chunk == null) {
			return;
		}

		chunk.setBlockHP(hp, x, y);
	}

	private Chunk getChunk(int x, int y) {
		if (!LastTry.world.isInside(x, y)) {
			return null;
		}

		Chunk chunk = LastTry.world.chunks.getFor(x, y);

		if (chunk == null) {
			LastTry.world.chunks.load(x / Chunk.SIZE, y / Chunk.SIZE);
			return null;
		}

		return chunk;
	}
}