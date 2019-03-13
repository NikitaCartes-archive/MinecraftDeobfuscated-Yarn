package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3884;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ZombieVillagerEntityModel<T extends ZombieEntity> extends BipedEntityModel<T> implements class_3884 {
	private Cuboid field_17144;

	public ZombieVillagerEntityModel() {
		this(0.0F, false);
	}

	public ZombieVillagerEntityModel(float f, boolean bl) {
		super(f, 0.0F, 64, bl ? 32 : 64);
		if (bl) {
			this.field_3398 = new Cuboid(this, 0, 0);
			this.field_3398.addBox(-4.0F, -10.0F, -4.0F, 8, 8, 8, f);
			this.field_3391 = new Cuboid(this, 16, 16);
			this.field_3391.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, f + 0.1F);
			this.field_3392 = new Cuboid(this, 0, 16);
			this.field_3392.setRotationPoint(-2.0F, 12.0F, 0.0F);
			this.field_3392.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f + 0.1F);
			this.field_3397 = new Cuboid(this, 0, 16);
			this.field_3397.mirror = true;
			this.field_3397.setRotationPoint(2.0F, 12.0F, 0.0F);
			this.field_3397.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f + 0.1F);
		} else {
			this.field_3398 = new Cuboid(this, 0, 0);
			this.field_3398.setTextureOffset(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, f);
			this.field_3398.setTextureOffset(24, 0).addBox(-1.0F, -3.0F, -6.0F, 2, 4, 2, f);
			this.field_3394 = new Cuboid(this, 32, 0);
			this.field_3394.addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, f + 0.5F);
			this.field_17144 = new Cuboid(this);
			this.field_17144.setTextureOffset(30, 47).addBox(-8.0F, -8.0F, -6.0F, 16, 16, 1, f);
			this.field_17144.pitch = (float) (-Math.PI / 2);
			this.field_3394.addChild(this.field_17144);
			this.field_3391 = new Cuboid(this, 16, 20);
			this.field_3391.addBox(-4.0F, 0.0F, -3.0F, 8, 12, 6, f);
			this.field_3391.setTextureOffset(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8, 18, 6, f + 0.05F);
			this.field_3401 = new Cuboid(this, 44, 22);
			this.field_3401.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, f);
			this.field_3401.setRotationPoint(-5.0F, 2.0F, 0.0F);
			this.field_3390 = new Cuboid(this, 44, 22);
			this.field_3390.mirror = true;
			this.field_3390.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, f);
			this.field_3390.setRotationPoint(5.0F, 2.0F, 0.0F);
			this.field_3392 = new Cuboid(this, 0, 22);
			this.field_3392.setRotationPoint(-2.0F, 12.0F, 0.0F);
			this.field_3392.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
			this.field_3397 = new Cuboid(this, 0, 22);
			this.field_3397.mirror = true;
			this.field_3397.setRotationPoint(2.0F, 12.0F, 0.0F);
			this.field_3397.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
		}
	}

	public void method_17135(T zombieEntity, float f, float g, float h, float i, float j, float k) {
		super.method_17087(zombieEntity, f, g, h, i, j, k);
		float l = MathHelper.sin(this.swingProgress * (float) Math.PI);
		float m = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - this.swingProgress)) * (float) Math.PI);
		this.field_3401.roll = 0.0F;
		this.field_3390.roll = 0.0F;
		this.field_3401.yaw = -(0.1F - l * 0.6F);
		this.field_3390.yaw = 0.1F - l * 0.6F;
		float n = (float) -Math.PI / (zombieEntity.hasArmsRaised() ? 1.5F : 2.25F);
		this.field_3401.pitch = n;
		this.field_3390.pitch = n;
		this.field_3401.pitch += l * 1.2F - m * 0.4F;
		this.field_3390.pitch += l * 1.2F - m * 0.4F;
		this.field_3401.roll = this.field_3401.roll + MathHelper.cos(h * 0.09F) * 0.05F + 0.05F;
		this.field_3390.roll = this.field_3390.roll - (MathHelper.cos(h * 0.09F) * 0.05F + 0.05F);
		this.field_3401.pitch = this.field_3401.pitch + MathHelper.sin(h * 0.067F) * 0.05F;
		this.field_3390.pitch = this.field_3390.pitch - MathHelper.sin(h * 0.067F) * 0.05F;
	}

	@Override
	public void setHeadVisible(boolean bl) {
		this.field_3398.visible = bl;
		this.field_3394.visible = bl;
		this.field_17144.visible = bl;
	}
}
