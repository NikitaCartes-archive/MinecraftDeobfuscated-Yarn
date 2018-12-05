package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class GuardianEntityModel extends Model {
	private final Cuboid field_3379;
	private final Cuboid field_3381;
	private final Cuboid[] field_3380;
	private final Cuboid[] field_3378;

	public GuardianEntityModel() {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.field_3380 = new Cuboid[12];
		this.field_3379 = new Cuboid(this);
		this.field_3379.setTextureOffset(0, 0).addBox(-6.0F, 10.0F, -8.0F, 12, 12, 16);
		this.field_3379.setTextureOffset(0, 28).addBox(-8.0F, 10.0F, -6.0F, 2, 12, 12);
		this.field_3379.setTextureOffset(0, 28).addBox(6.0F, 10.0F, -6.0F, 2, 12, 12, true);
		this.field_3379.setTextureOffset(16, 40).addBox(-6.0F, 8.0F, -6.0F, 12, 2, 12);
		this.field_3379.setTextureOffset(16, 40).addBox(-6.0F, 22.0F, -6.0F, 12, 2, 12);

		for (int i = 0; i < this.field_3380.length; i++) {
			this.field_3380[i] = new Cuboid(this, 0, 0);
			this.field_3380[i].addBox(-1.0F, -4.5F, -1.0F, 2, 9, 2);
			this.field_3379.addChild(this.field_3380[i]);
		}

		this.field_3381 = new Cuboid(this, 8, 0);
		this.field_3381.addBox(-1.0F, 15.0F, 0.0F, 2, 2, 1);
		this.field_3379.addChild(this.field_3381);
		this.field_3378 = new Cuboid[3];
		this.field_3378[0] = new Cuboid(this, 40, 0);
		this.field_3378[0].addBox(-2.0F, 14.0F, 7.0F, 4, 4, 8);
		this.field_3378[1] = new Cuboid(this, 0, 54);
		this.field_3378[1].addBox(0.0F, 14.0F, 0.0F, 3, 3, 7);
		this.field_3378[2] = new Cuboid(this);
		this.field_3378[2].setTextureOffset(41, 32).addBox(0.0F, 14.0F, 0.0F, 2, 2, 6);
		this.field_3378[2].setTextureOffset(25, 19).addBox(1.0F, 10.5F, 3.0F, 1, 9, 9);
		this.field_3379.addChild(this.field_3378[0]);
		this.field_3378[0].addChild(this.field_3378[1]);
		this.field_3378[1].addChild(this.field_3378[2]);
	}

	@Override
	public void render(Entity entity, float f, float g, float h, float i, float j, float k) {
		this.setRotationAngles(f, g, h, i, j, k, entity);
		this.field_3379.render(k);
	}

	@Override
	public void setRotationAngles(float f, float g, float h, float i, float j, float k, Entity entity) {
		GuardianEntity guardianEntity = (GuardianEntity)entity;
		float l = h - (float)guardianEntity.age;
		this.field_3379.yaw = i * (float) (Math.PI / 180.0);
		this.field_3379.pitch = j * (float) (Math.PI / 180.0);
		float[] fs = new float[]{1.75F, 0.25F, 0.0F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, 1.25F, 0.75F, 0.0F, 0.0F};
		float[] gs = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.25F, 1.75F, 1.25F, 0.75F, 0.0F, 0.0F, 0.0F, 0.0F};
		float[] hs = new float[]{0.0F, 0.0F, 0.25F, 1.75F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.75F, 1.25F};
		float[] is = new float[]{0.0F, 0.0F, 8.0F, -8.0F, -8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F, 8.0F, -8.0F};
		float[] js = new float[]{-8.0F, -8.0F, -8.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 8.0F, 8.0F, 8.0F};
		float[] ks = new float[]{8.0F, -8.0F, 0.0F, 0.0F, -8.0F, -8.0F, 8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F};
		float m = (1.0F - guardianEntity.method_7053(l)) * 0.55F;

		for (int n = 0; n < 12; n++) {
			this.field_3380[n].pitch = (float) Math.PI * fs[n];
			this.field_3380[n].yaw = (float) Math.PI * gs[n];
			this.field_3380[n].roll = (float) Math.PI * hs[n];
			this.field_3380[n].rotationPointX = is[n] * (1.0F + MathHelper.cos(h * 1.5F + (float)n) * 0.01F - m);
			this.field_3380[n].rotationPointY = 16.0F + js[n] * (1.0F + MathHelper.cos(h * 1.5F + (float)n) * 0.01F - m);
			this.field_3380[n].rotationPointZ = ks[n] * (1.0F + MathHelper.cos(h * 1.5F + (float)n) * 0.01F - m);
		}

		this.field_3381.rotationPointZ = -8.25F;
		Entity entity2 = MinecraftClient.getInstance().getCameraEntity();
		if (guardianEntity.method_7063()) {
			entity2 = guardianEntity.method_7052();
		}

		if (entity2 != null) {
			Vec3d vec3d = entity2.getCameraPosVec(0.0F);
			Vec3d vec3d2 = entity.getCameraPosVec(0.0F);
			double d = vec3d.y - vec3d2.y;
			if (d > 0.0) {
				this.field_3381.rotationPointY = 0.0F;
			} else {
				this.field_3381.rotationPointY = 1.0F;
			}

			Vec3d vec3d3 = entity.getRotationVec(0.0F);
			vec3d3 = new Vec3d(vec3d3.x, 0.0, vec3d3.z);
			Vec3d vec3d4 = new Vec3d(vec3d2.x - vec3d.x, 0.0, vec3d2.z - vec3d.z).normalize().rotateY((float) (Math.PI / 2));
			double e = vec3d3.dotProduct(vec3d4);
			this.field_3381.rotationPointX = MathHelper.sqrt((float)Math.abs(e)) * 2.0F * (float)Math.signum(e);
		}

		this.field_3381.visible = true;
		float o = guardianEntity.method_7057(l);
		this.field_3378[0].yaw = MathHelper.sin(o) * (float) Math.PI * 0.05F;
		this.field_3378[1].yaw = MathHelper.sin(o) * (float) Math.PI * 0.1F;
		this.field_3378[1].rotationPointX = -1.5F;
		this.field_3378[1].rotationPointY = 0.5F;
		this.field_3378[1].rotationPointZ = 14.0F;
		this.field_3378[2].yaw = MathHelper.sin(o) * (float) Math.PI * 0.15F;
		this.field_3378[2].rotationPointX = 0.5F;
		this.field_3378[2].rotationPointY = 0.5F;
		this.field_3378[2].rotationPointZ = 6.0F;
	}
}
