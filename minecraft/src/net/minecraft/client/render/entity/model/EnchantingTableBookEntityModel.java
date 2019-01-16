package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class EnchantingTableBookEntityModel extends Model {
	private final Cuboid field_3336 = new Cuboid(this).setTextureOffset(0, 0).addBox(-6.0F, -5.0F, 0.0F, 6, 10, 0);
	private final Cuboid field_3338 = new Cuboid(this).setTextureOffset(16, 0).addBox(0.0F, -5.0F, 0.0F, 6, 10, 0);
	private final Cuboid field_3337;
	private final Cuboid field_3335;
	private final Cuboid field_3334;
	private final Cuboid field_3339;
	private final Cuboid field_3333 = new Cuboid(this).setTextureOffset(12, 0).addBox(-1.0F, -5.0F, 0.0F, 2, 10, 0);

	public EnchantingTableBookEntityModel() {
		this.field_3337 = new Cuboid(this).setTextureOffset(0, 10).addBox(0.0F, -4.0F, -0.99F, 5, 8, 1);
		this.field_3335 = new Cuboid(this).setTextureOffset(12, 10).addBox(0.0F, -4.0F, -0.01F, 5, 8, 1);
		this.field_3334 = new Cuboid(this).setTextureOffset(24, 10).addBox(0.0F, -4.0F, 0.0F, 5, 8, 0);
		this.field_3339 = new Cuboid(this).setTextureOffset(24, 10).addBox(0.0F, -4.0F, 0.0F, 5, 8, 0);
		this.field_3336.setRotationPoint(0.0F, 0.0F, -1.0F);
		this.field_3338.setRotationPoint(0.0F, 0.0F, 1.0F);
		this.field_3333.yaw = (float) (Math.PI / 2);
	}

	public void render(float f, float g, float h, float i, float j, float k) {
		this.setAngles(f, g, h, i, j, k);
		this.field_3336.render(k);
		this.field_3338.render(k);
		this.field_3333.render(k);
		this.field_3337.render(k);
		this.field_3335.render(k);
		this.field_3334.render(k);
		this.field_3339.render(k);
	}

	private void setAngles(float f, float g, float h, float i, float j, float k) {
		float l = (MathHelper.sin(f * 0.02F) * 0.1F + 1.25F) * i;
		this.field_3336.yaw = (float) Math.PI + l;
		this.field_3338.yaw = -l;
		this.field_3337.yaw = l;
		this.field_3335.yaw = -l;
		this.field_3334.yaw = l - l * 2.0F * g;
		this.field_3339.yaw = l - l * 2.0F * h;
		this.field_3337.rotationPointX = MathHelper.sin(l);
		this.field_3335.rotationPointX = MathHelper.sin(l);
		this.field_3334.rotationPointX = MathHelper.sin(l);
		this.field_3339.rotationPointX = MathHelper.sin(l);
	}
}
