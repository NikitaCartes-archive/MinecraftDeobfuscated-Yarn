package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CreeperEntityModel extends Model {
	private final Cuboid head;
	private final Cuboid field_3362;
	private final Cuboid field_3361;
	private final Cuboid field_3359;
	private final Cuboid field_3358;
	private final Cuboid field_3363;
	private final Cuboid field_3357;

	public CreeperEntityModel() {
		this(0.0F);
	}

	public CreeperEntityModel(float f) {
		int i = 6;
		this.head = new Cuboid(this, 0, 0);
		this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, f);
		this.head.setRotationPoint(0.0F, 6.0F, 0.0F);
		this.field_3362 = new Cuboid(this, 32, 0);
		this.field_3362.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, f + 0.5F);
		this.field_3362.setRotationPoint(0.0F, 6.0F, 0.0F);
		this.field_3361 = new Cuboid(this, 16, 16);
		this.field_3361.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, f);
		this.field_3361.setRotationPoint(0.0F, 6.0F, 0.0F);
		this.field_3359 = new Cuboid(this, 0, 16);
		this.field_3359.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, f);
		this.field_3359.setRotationPoint(-2.0F, 18.0F, 4.0F);
		this.field_3358 = new Cuboid(this, 0, 16);
		this.field_3358.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, f);
		this.field_3358.setRotationPoint(2.0F, 18.0F, 4.0F);
		this.field_3363 = new Cuboid(this, 0, 16);
		this.field_3363.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, f);
		this.field_3363.setRotationPoint(-2.0F, 18.0F, -4.0F);
		this.field_3357 = new Cuboid(this, 0, 16);
		this.field_3357.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, f);
		this.field_3357.setRotationPoint(2.0F, 18.0F, -4.0F);
	}

	@Override
	public void render(Entity entity, float f, float g, float h, float i, float j, float k) {
		this.setRotationAngles(f, g, h, i, j, k, entity);
		this.head.render(k);
		this.field_3361.render(k);
		this.field_3359.render(k);
		this.field_3358.render(k);
		this.field_3363.render(k);
		this.field_3357.render(k);
	}

	@Override
	public void setRotationAngles(float f, float g, float h, float i, float j, float k, Entity entity) {
		this.head.yaw = i * (float) (Math.PI / 180.0);
		this.head.pitch = j * (float) (Math.PI / 180.0);
		this.field_3359.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		this.field_3358.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.field_3363.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.field_3357.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
	}
}
