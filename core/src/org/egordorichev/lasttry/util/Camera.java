package org.egordorichev.lasttry.util;

import com.badlogic.gdx.graphics.OrthographicCamera;
import org.egordorichev.lasttry.entitySystem.component.LocationComponent;
import org.egordorichev.lasttry.graphics.Graphics;
import org.terasology.entitysystem.core.EntityRef;

public class Camera {
	private OrthographicCamera view;
	private EntityRef toFollow;

	public Camera(int width, int height) {
		view = new OrthographicCamera(width, height);
	}

	public void update(float dt) {
		if (this.toFollow != null) {
			LocationComponent pos = this.toFollow.getComponent(LocationComponent.class);

			this.view.position.x = pos.location.x;
			this.view.position.y = pos.location.y;
		}
	}

	public void apply() {
		Graphics.batch.setProjectionMatrix(this.view.combined);
	}

	public void follow(EntityRef toFollow) {
		this.toFollow = toFollow;
	}

	public float getX() {
		return this.view.position.x;
	}

	public float getY() {
		return this.view.position.y;
	}

	public OrthographicCamera getView() {
		return this.view;
	}
}