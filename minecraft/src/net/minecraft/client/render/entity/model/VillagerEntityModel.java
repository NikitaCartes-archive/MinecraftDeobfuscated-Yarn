package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class VillagerEntityModel extends Model {
	protected Cuboid head;
	protected Cuboid field_3610;
	protected Cuboid field_3609;
	protected Cuboid field_3607;
	protected Cuboid field_3606;
	protected Cuboid nose;

	public VillagerEntityModel(float f) {
		this(f, 0.0F, 64, 64);
	}

	public VillagerEntityModel(float f, float g, int i, int j) {
		this.head = new Cuboid(this).setTextureSize(i, j);
		this.head.setRotationPoint(0.0F, 0.0F + g, 0.0F);
		this.head.setTextureOffset(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, f);
		this.nose = new Cuboid(this).setTextureSize(i, j);
		this.nose.setRotationPoint(0.0F, g - 2.0F, 0.0F);
		this.nose.setTextureOffset(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2, 4, 2, f);
		this.head.addChild(this.nose);
		this.field_3610 = new Cuboid(this).setTextureSize(i, j);
		this.field_3610.setRotationPoint(0.0F, 0.0F + g, 0.0F);
		this.field_3610.setTextureOffset(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8, 12, 6, f);
		this.field_3610.setTextureOffset(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8, 18, 6, f + 0.5F);
		this.field_3609 = new Cuboid(this).setTextureSize(i, j);
		this.field_3609.setRotationPoint(0.0F, 0.0F + g + 2.0F, 0.0F);
		this.field_3609.setTextureOffset(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4, 8, 4, f);
		this.field_3609.setTextureOffset(44, 22).addBox(4.0F, -2.0F, -2.0F, 4, 8, 4, f, true);
		this.field_3609.setTextureOffset(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8, 4, 4, f);
		this.field_3607 = new Cuboid(this, 0, 22).setTextureSize(i, j);
		this.field_3607.setRotationPoint(-2.0F, 12.0F + g, 0.0F);
		this.field_3607.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
		this.field_3606 = new Cuboid(this, 0, 22).setTextureSize(i, j);
		this.field_3606.mirror = true;
		this.field_3606.setRotationPoint(2.0F, 12.0F + g, 0.0F);
		this.field_3606.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
	}

	@Override
	public void render(Entity entity, float f, float g, float h, float i, float j, float k) {
		this.setRotationAngles(f, g, h, i, j, k, entity);
		this.head.render(k);
		this.field_3610.render(k);
		this.field_3607.render(k);
		this.field_3606.render(k);
		this.field_3609.render(k);
	}

	@Override
	public void setRotationAngles(float f, float g, float h, float i, float j, float k, Entity entity) {
		this.head.yaw = i * (float) (Math.PI / 180.0);
		this.head.pitch = j * (float) (Math.PI / 180.0);
		this.field_3609.rotationPointY = 3.0F;
		this.field_3609.rotationPointZ = -1.0F;
		this.field_3609.pitch = -0.75F;
		this.field_3607.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g * 0.5F;
		this.field_3606.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g * 0.5F;
		this.field_3607.yaw = 0.0F;
		this.field_3606.yaw = 0.0F;
	}

	public Cuboid method_2838() {
		return this.head;
	}
}
