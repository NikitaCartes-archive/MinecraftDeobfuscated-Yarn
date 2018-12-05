package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class MinecartEntityModel extends Model {
	private final Cuboid[] field_3432 = new Cuboid[7];

	public MinecartEntityModel() {
		this.field_3432[0] = new Cuboid(this, 0, 10);
		this.field_3432[1] = new Cuboid(this, 0, 0);
		this.field_3432[2] = new Cuboid(this, 0, 0);
		this.field_3432[3] = new Cuboid(this, 0, 0);
		this.field_3432[4] = new Cuboid(this, 0, 0);
		this.field_3432[5] = new Cuboid(this, 44, 10);
		int i = 20;
		int j = 8;
		int k = 16;
		int l = 4;
		this.field_3432[0].addBox(-10.0F, -8.0F, -1.0F, 20, 16, 2, 0.0F);
		this.field_3432[0].setRotationPoint(0.0F, 4.0F, 0.0F);
		this.field_3432[5].addBox(-9.0F, -7.0F, -1.0F, 18, 14, 1, 0.0F);
		this.field_3432[5].setRotationPoint(0.0F, 4.0F, 0.0F);
		this.field_3432[1].addBox(-8.0F, -9.0F, -1.0F, 16, 8, 2, 0.0F);
		this.field_3432[1].setRotationPoint(-9.0F, 4.0F, 0.0F);
		this.field_3432[2].addBox(-8.0F, -9.0F, -1.0F, 16, 8, 2, 0.0F);
		this.field_3432[2].setRotationPoint(9.0F, 4.0F, 0.0F);
		this.field_3432[3].addBox(-8.0F, -9.0F, -1.0F, 16, 8, 2, 0.0F);
		this.field_3432[3].setRotationPoint(0.0F, 4.0F, -7.0F);
		this.field_3432[4].addBox(-8.0F, -9.0F, -1.0F, 16, 8, 2, 0.0F);
		this.field_3432[4].setRotationPoint(0.0F, 4.0F, 7.0F);
		this.field_3432[0].pitch = (float) (Math.PI / 2);
		this.field_3432[1].yaw = (float) (Math.PI * 3.0 / 2.0);
		this.field_3432[2].yaw = (float) (Math.PI / 2);
		this.field_3432[3].yaw = (float) Math.PI;
		this.field_3432[5].pitch = (float) (-Math.PI / 2);
	}

	@Override
	public void render(Entity entity, float f, float g, float h, float i, float j, float k) {
		this.field_3432[5].rotationPointY = 4.0F - h;

		for (int l = 0; l < 6; l++) {
			this.field_3432[l].render(k);
		}
	}
}
