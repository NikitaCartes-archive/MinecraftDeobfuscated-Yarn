package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class MinecartEntityModel<T extends Entity> extends EntityModel<T> {
	private final ModelPart[] field_3432 = new ModelPart[7];

	public MinecartEntityModel() {
		this.field_3432[0] = new ModelPart(this, 0, 10);
		this.field_3432[1] = new ModelPart(this, 0, 0);
		this.field_3432[2] = new ModelPart(this, 0, 0);
		this.field_3432[3] = new ModelPart(this, 0, 0);
		this.field_3432[4] = new ModelPart(this, 0, 0);
		this.field_3432[5] = new ModelPart(this, 44, 10);
		int i = 20;
		int j = 8;
		int k = 16;
		int l = 4;
		this.field_3432[0].addCuboid(-10.0F, -8.0F, -1.0F, 20, 16, 2, 0.0F);
		this.field_3432[0].setPivot(0.0F, 4.0F, 0.0F);
		this.field_3432[5].addCuboid(-9.0F, -7.0F, -1.0F, 18, 14, 1, 0.0F);
		this.field_3432[5].setPivot(0.0F, 4.0F, 0.0F);
		this.field_3432[1].addCuboid(-8.0F, -9.0F, -1.0F, 16, 8, 2, 0.0F);
		this.field_3432[1].setPivot(-9.0F, 4.0F, 0.0F);
		this.field_3432[2].addCuboid(-8.0F, -9.0F, -1.0F, 16, 8, 2, 0.0F);
		this.field_3432[2].setPivot(9.0F, 4.0F, 0.0F);
		this.field_3432[3].addCuboid(-8.0F, -9.0F, -1.0F, 16, 8, 2, 0.0F);
		this.field_3432[3].setPivot(0.0F, 4.0F, -7.0F);
		this.field_3432[4].addCuboid(-8.0F, -9.0F, -1.0F, 16, 8, 2, 0.0F);
		this.field_3432[4].setPivot(0.0F, 4.0F, 7.0F);
		this.field_3432[0].pitch = (float) (Math.PI / 2);
		this.field_3432[1].yaw = (float) (Math.PI * 3.0 / 2.0);
		this.field_3432[2].yaw = (float) (Math.PI / 2);
		this.field_3432[3].yaw = (float) Math.PI;
		this.field_3432[5].pitch = (float) (-Math.PI / 2);
	}

	@Override
	public void render(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
		this.field_3432[5].pivotY = 4.0F - age;

		for (int i = 0; i < 6; i++) {
			this.field_3432[i].render(scale);
		}
	}
}
