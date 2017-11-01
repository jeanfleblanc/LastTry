package org.egordorichev.lasttry.entity.system;

import org.egordorichev.lasttry.entity.Entity;

import java.util.ArrayList;

public class EntitySystem {
	/**
	 * The entity list
	 */
	private ArrayList<Entity> entities;

	public EntitySystem() {
		this.entities = new ArrayList<>();
	}

	/**
	 * Adds an entity
	 *
	 * @param entity Entity to add
	 */
	public void add(Entity entity) {
		this.entities.add(entity);
	}

	/**
	 * Removes an entity
	 *
	 * @param entity Entity to remove
	 */
	public void remove(Entity entity) {
		this.entities.remove(entity);
	}

	/**
	 * Updates all entities
	 *
	 * @param delta Time, since the last frame
	 */
	public void update(float delta) {
		for (Entity entity : this.entities) {
			entity.update(delta);
		}
	}

	/**
	 * Draws all entities
	 */
	public void render() {
		for (Entity entity : this.entities) {
			entity.render();
		}
	}

	/**
	 * @return All entities
	 */
	public ArrayList<Entity> getEntities() {
		return this.entities;
	}
}