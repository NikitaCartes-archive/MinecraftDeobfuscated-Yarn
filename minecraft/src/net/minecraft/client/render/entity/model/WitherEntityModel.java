package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class WitherEntityModel extends Model {
	private final Cuboid[] field_3613;
	private final Cuboid[] field_3612;

	public WitherEntityModel(float f) {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.field_3613 = new Cuboid[3];
		this.field_3613[0] = new Cuboid(this, 0, 16);
		this.field_3613[0].addBox(-10.0F, 3.9F, -0.5F, 20, 3, 3, f);
		this.field_3613[1] = new Cuboid(this).setTextureSize(this.textureWidth, this.textureHeight);
		this.field_3613[1].setRotationPoint(-2.0F, 6.9F, -0.5F);
		this.field_3613[1].setTextureOffset(0, 22).addBox(0.0F, 0.0F, 0.0F, 3, 10, 3, f);
		this.field_3613[1].setTextureOffset(24, 22).addBox(-4.0F, 1.5F, 0.5F, 11, 2, 2, f);
		this.field_3613[1].setTextureOffset(24, 22).addBox(-4.0F, 4.0F, 0.5F, 11, 2, 2, f);
		this.field_3613[1].setTextureOffset(24, 22).addBox(-4.0F, 6.5F, 0.5F, 11, 2, 2, f);
		this.field_3613[2] = new Cuboid(this, 12, 22);
		this.field_3613[2].addBox(0.0F, 0.0F, 0.0F, 3, 6, 3, f);
		this.field_3612 = new Cuboid[3];
		this.field_3612[0] = new Cuboid(this, 0, 0);
		this.field_3612[0].addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8, f);
		this.field_3612[1] = new Cuboid(this, 32, 0);
		this.field_3612[1].addBox(-4.0F, -4.0F, -4.0F, 6, 6, 6, f);
		this.field_3612[1].rotationPointX = -8.0F;
		this.field_3612[1].rotationPointY = 4.0F;
		this.field_3612[2] = new Cuboid(this, 32, 0);
		this.field_3612[2].addBox(-4.0F, -4.0F, -4.0F, 6, 6, 6, f);
		this.field_3612[2].rotationPointX = 10.0F;
		this.field_3612[2].rotationPointY = 4.0F;
	}

	@Override
	public void render(Entity entity, float f, float g, float h, float i, float j, float k) {
		this.setRotationAngles(f, g, h, i, j, k, entity);

		for (Cuboid cuboid : this.field_3612) {
			cuboid.render(k);
		}

		for (Cuboid cuboid : this.field_3613) {
			cuboid.render(k);
		}
	}

	@Override
	public void setRotationAngles(float f, float g, float h, float i, float j, float k, Entity entity) {
		float l = MathHelper.cos(h * 0.1F);
		this.field_3613[1].pitch = (0.065F + 0.05F * l) * (float) Math.PI;
		this.field_3613[2].setRotationPoint(-2.0F, 6.9F + MathHelper.cos(this.field_3613[1].pitch) * 10.0F, -0.5F + MathHelper.sin(this.field_3613[1].pitch) * 10.0F);
		this.field_3613[2].pitch = (0.265F + 0.1F * l) * (float) Math.PI;
		this.field_3612[0].yaw = i * (float) (Math.PI / 180.0);
		this.field_3612[0].pitch = j * (float) (Math.PI / 180.0);
	}

	@Override
	public void animateModel(LivingEntity livingEntity, float f, float g, float h) {
		EntityWither entityWither = (EntityWither)livingEntity;

		for (int i = 1; i < 3; i++) {
			this.field_3612[i].yaw = (entityWither.method_6879(i - 1) - livingEntity.field_6283) * (float) (Math.PI / 180.0);
			this.field_3612[i].pitch = entityWither.method_6887(i - 1) * (float) (Math.PI / 180.0);
		}
	}
}
