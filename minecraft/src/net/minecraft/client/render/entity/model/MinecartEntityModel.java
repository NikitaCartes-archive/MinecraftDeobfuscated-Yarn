package net.minecraft.client.render.entity.model;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class MinecartEntityModel<T extends Entity> extends CompositeEntityModel<T> {
	private final ModelPart[] parts = new ModelPart[6];

	public MinecartEntityModel() {
		this.parts[0] = new ModelPart(this, 0, 10);
		this.parts[1] = new ModelPart(this, 0, 0);
		this.parts[2] = new ModelPart(this, 0, 0);
		this.parts[3] = new ModelPart(this, 0, 0);
		this.parts[4] = new ModelPart(this, 0, 0);
		this.parts[5] = new ModelPart(this, 44, 10);
		int i = 20;
		int j = 8;
		int k = 16;
		int l = 4;
		this.parts[0].addCuboid(-10.0F, -8.0F, -1.0F, 20.0F, 16.0F, 2.0F, 0.0F);
		this.parts[0].setPivot(0.0F, 4.0F, 0.0F);
		this.parts[5].addCuboid(-9.0F, -7.0F, -1.0F, 18.0F, 14.0F, 1.0F, 0.0F);
		this.parts[5].setPivot(0.0F, 4.0F, 0.0F);
		this.parts[1].addCuboid(-8.0F, -9.0F, -1.0F, 16.0F, 8.0F, 2.0F, 0.0F);
		this.parts[1].setPivot(-9.0F, 4.0F, 0.0F);
		this.parts[2].addCuboid(-8.0F, -9.0F, -1.0F, 16.0F, 8.0F, 2.0F, 0.0F);
		this.parts[2].setPivot(9.0F, 4.0F, 0.0F);
		this.parts[3].addCuboid(-8.0F, -9.0F, -1.0F, 16.0F, 8.0F, 2.0F, 0.0F);
		this.parts[3].setPivot(0.0F, 4.0F, -7.0F);
		this.parts[4].addCuboid(-8.0F, -9.0F, -1.0F, 16.0F, 8.0F, 2.0F, 0.0F);
		this.parts[4].setPivot(0.0F, 4.0F, 7.0F);
		this.parts[0].pitch = (float) (Math.PI / 2);
		this.parts[1].yaw = (float) (Math.PI * 3.0 / 2.0);
		this.parts[2].yaw = (float) (Math.PI / 2);
		this.parts[3].yaw = (float) Math.PI;
		this.parts[5].pitch = (float) (-Math.PI / 2);
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		this.parts[5].pivotY = 4.0F - animationProgress;
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return Arrays.asList(this.parts);
	}
}
