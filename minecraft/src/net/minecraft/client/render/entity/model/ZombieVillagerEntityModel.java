package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3884;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ZombieVillagerEntityModel<T extends ZombieEntity> extends BipedEntityModel<T> implements class_3884 {
	private Cuboid hat;

	public ZombieVillagerEntityModel() {
		this(0.0F, false);
	}

	public ZombieVillagerEntityModel(float f, boolean bl) {
		super(f, 0.0F, 64, bl ? 32 : 64);
		if (bl) {
			this.head = new Cuboid(this, 0, 0);
			this.head.addBox(-4.0F, -10.0F, -4.0F, 8, 8, 8, f);
			this.body = new Cuboid(this, 16, 16);
			this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, f + 0.1F);
			this.legRight = new Cuboid(this, 0, 16);
			this.legRight.setRotationPoint(-2.0F, 12.0F, 0.0F);
			this.legRight.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f + 0.1F);
			this.legLeft = new Cuboid(this, 0, 16);
			this.legLeft.mirror = true;
			this.legLeft.setRotationPoint(2.0F, 12.0F, 0.0F);
			this.legLeft.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f + 0.1F);
		} else {
			this.head = new Cuboid(this, 0, 0);
			this.head.setTextureOffset(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, f);
			this.head.setTextureOffset(24, 0).addBox(-1.0F, -3.0F, -6.0F, 2, 4, 2, f);
			this.headwear = new Cuboid(this, 32, 0);
			this.headwear.addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, f + 0.5F);
			this.hat = new Cuboid(this);
			this.hat.setTextureOffset(30, 47).addBox(-8.0F, -8.0F, -6.0F, 16, 16, 1, f);
			this.hat.pitch = (float) (-Math.PI / 2);
			this.headwear.addChild(this.hat);
			this.body = new Cuboid(this, 16, 20);
			this.body.addBox(-4.0F, 0.0F, -3.0F, 8, 12, 6, f);
			this.body.setTextureOffset(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8, 18, 6, f + 0.05F);
			this.armRight = new Cuboid(this, 44, 22);
			this.armRight.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, f);
			this.armRight.setRotationPoint(-5.0F, 2.0F, 0.0F);
			this.armLeft = new Cuboid(this, 44, 22);
			this.armLeft.mirror = true;
			this.armLeft.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, f);
			this.armLeft.setRotationPoint(5.0F, 2.0F, 0.0F);
			this.legRight = new Cuboid(this, 0, 22);
			this.legRight.setRotationPoint(-2.0F, 12.0F, 0.0F);
			this.legRight.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
			this.legLeft = new Cuboid(this, 0, 22);
			this.legLeft.mirror = true;
			this.legLeft.setRotationPoint(2.0F, 12.0F, 0.0F);
			this.legLeft.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
		}
	}

	public void method_17135(T zombieEntity, float f, float g, float h, float i, float j, float k) {
		super.method_17087(zombieEntity, f, g, h, i, j, k);
		float l = MathHelper.sin(this.swingProgress * (float) Math.PI);
		float m = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - this.swingProgress)) * (float) Math.PI);
		this.armRight.roll = 0.0F;
		this.armLeft.roll = 0.0F;
		this.armRight.yaw = -(0.1F - l * 0.6F);
		this.armLeft.yaw = 0.1F - l * 0.6F;
		float n = (float) -Math.PI / (zombieEntity.hasArmsRaised() ? 1.5F : 2.25F);
		this.armRight.pitch = n;
		this.armLeft.pitch = n;
		this.armRight.pitch += l * 1.2F - m * 0.4F;
		this.armLeft.pitch += l * 1.2F - m * 0.4F;
		this.armRight.roll = this.armRight.roll + MathHelper.cos(h * 0.09F) * 0.05F + 0.05F;
		this.armLeft.roll = this.armLeft.roll - (MathHelper.cos(h * 0.09F) * 0.05F + 0.05F);
		this.armRight.pitch = this.armRight.pitch + MathHelper.sin(h * 0.067F) * 0.05F;
		this.armLeft.pitch = this.armLeft.pitch - MathHelper.sin(h * 0.067F) * 0.05F;
	}

	@Override
	public void method_17150(boolean bl) {
		this.head.visible = bl;
		this.headwear.visible = bl;
		this.hat.visible = bl;
	}
}
