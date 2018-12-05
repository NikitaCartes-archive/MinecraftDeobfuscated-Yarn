package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class WitchEntityModel extends VillagerEntityModel {
	private boolean field_3614;
	private final Cuboid mole = new Cuboid(this).setTextureSize(64, 128);
	private final Cuboid hat;

	public WitchEntityModel(float f) {
		super(f, 0.0F, 64, 128);
		this.mole.setRotationPoint(0.0F, -2.0F, 0.0F);
		this.mole.setTextureOffset(0, 0).addBox(0.0F, 3.0F, -6.75F, 1, 1, 1, -0.25F);
		this.nose.addChild(this.mole);
		this.hat = new Cuboid(this).setTextureSize(64, 128);
		this.hat.setRotationPoint(-5.0F, -10.03125F, -5.0F);
		this.hat.setTextureOffset(0, 64).addBox(0.0F, 0.0F, 0.0F, 10, 2, 10);
		this.head.addChild(this.hat);
		Cuboid cuboid = new Cuboid(this).setTextureSize(64, 128);
		cuboid.setRotationPoint(1.75F, -4.0F, 2.0F);
		cuboid.setTextureOffset(0, 76).addBox(0.0F, 0.0F, 0.0F, 7, 4, 7);
		cuboid.pitch = -0.05235988F;
		cuboid.roll = 0.02617994F;
		this.hat.addChild(cuboid);
		Cuboid cuboid2 = new Cuboid(this).setTextureSize(64, 128);
		cuboid2.setRotationPoint(1.75F, -4.0F, 2.0F);
		cuboid2.setTextureOffset(0, 87).addBox(0.0F, 0.0F, 0.0F, 4, 4, 4);
		cuboid2.pitch = -0.10471976F;
		cuboid2.roll = 0.05235988F;
		cuboid.addChild(cuboid2);
		Cuboid cuboid3 = new Cuboid(this).setTextureSize(64, 128);
		cuboid3.setRotationPoint(1.75F, -2.0F, 2.0F);
		cuboid3.setTextureOffset(0, 95).addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.25F);
		cuboid3.pitch = (float) (-Math.PI / 15);
		cuboid3.roll = 0.10471976F;
		cuboid2.addChild(cuboid3);
	}

	@Override
	public void setRotationAngles(float f, float g, float h, float i, float j, float k, Entity entity) {
		super.setRotationAngles(f, g, h, i, j, k, entity);
		this.nose.x = 0.0F;
		this.nose.y = 0.0F;
		this.nose.z = 0.0F;
		float l = 0.01F * (float)(entity.getEntityId() % 10);
		this.nose.pitch = MathHelper.sin((float)entity.age * l) * 4.5F * (float) (Math.PI / 180.0);
		this.nose.yaw = 0.0F;
		this.nose.roll = MathHelper.cos((float)entity.age * l) * 2.5F * (float) (Math.PI / 180.0);
		if (this.field_3614) {
			this.nose.pitch = -0.9F;
			this.nose.z = -0.09375F;
			this.nose.y = 0.1875F;
		}
	}

	public Cuboid method_2839() {
		return this.nose;
	}

	public void method_2840(boolean bl) {
		this.field_3614 = bl;
	}
}
