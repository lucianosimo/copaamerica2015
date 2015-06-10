package com.lucianosimo.copaam2015.object;

import java.util.Random;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Ball extends Sprite{

	private Body body;
	private FixtureDef fixture;
	private final static int OMEGA_MAX = 9;
	private final static int OMEGA_MIN = 4;
	
	public Ball(float pX, float pY, VertexBufferObjectManager vbom, Camera camera, PhysicsWorld physicsWorld, ITextureRegion texture) {
		super(pX, pY, texture, vbom);
		createPhysics(camera, physicsWorld);
	}
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
		fixture = PhysicsFactory.createFixtureDef(0, 0, 0f);
		fixture.filter.groupIndex = -1;
		
		//n = rand.nextInt(max - min + 1) + min;
		Random rand = new Random();
		final int random = rand.nextInt(OMEGA_MAX - OMEGA_MIN + 1) + OMEGA_MIN;
		final float omega = random;
		
		body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.KinematicBody, fixture);
		
		body.setFixedRotation(true);
		
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				body.setAngularVelocity(omega);
			}
		});
		
		this.registerEntityModifier(new LoopEntityModifier(new RotationModifier(20, 0, -(omega * 180))));
	}
	
	public Body getBallBody() {
		return body;
	}
	
	public void setBallUserData(String userData) {
		this.setUserData(userData);
		body.setUserData(userData);
	}
	
	public void setBallFallingSpeed(float speed) {
		body.setLinearVelocity(new Vector2(0, -speed));
	}

}
