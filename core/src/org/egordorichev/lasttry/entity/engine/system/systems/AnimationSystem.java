package org.egordorichev.lasttry.entity.engine.system.systems;

import org.egordorichev.lasttry.entity.Entity;
import org.egordorichev.lasttry.entity.engine.Engine;
import org.egordorichev.lasttry.entity.engine.system.System;
import org.egordorichev.lasttry.entity.entities.creature.AnimationComponent;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Updates all animations
 */
public class AnimationSystem implements System {
	/**
	 * List of entities, that have animations
	 */
	private ArrayList<Entity> entities = new ArrayList<>();

	/**
	 * Handles input
	 *
	 * @param delta Time, since the last frame
	 */
	@Override
	public void update(float delta) {
		for (Entity entity : this.entities) {
			AnimationComponent animation = entity.getComponent(AnimationComponent.class);

			if (animation.current != null) {
				animation.current.update(delta);
			}
		}
	}

	/**
	 * Handles in-coming messages
	 *
	 * @param message Message from the engine
	 */
	@Override
	public void handleMessage(String message) {
		if (Objects.equals(message, "entity_added")) {
			this.entities = Engine.getEntitiesFor(AnimationComponent.class);
		}
	}
}