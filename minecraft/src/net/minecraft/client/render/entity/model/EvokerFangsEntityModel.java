package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class EvokerFangsEntityModel<T extends Entity> extends EntityModel<T> {
	private final Cuboid field_3374 = new Cuboid(this, 0, 0);
	private final Cuboid field_3376;
	private final Cuboid field_3375;

	public EvokerFangsEntityModel() {
		this.field_3374.setRotationPoint(-5.0F, 22.0F, -5.0F);
		this.field_3374.addBox(0.0F, 0.0F, 0.0F, 10, 12, 10);
		this.field_3376 = new Cuboid(this, 40, 0);
		this.field_3376.setRotationPoint(1.5F, 22.0F, -4.0F);
		this.field_3376.addBox(0.0F, 0.0F, 0.0F, 4, 14, 8);
		this.field_3375 = new Cuboid(this, 40, 0);
		this.field_3375.setRotationPoint(-1.5F, 22.0F, 4.0F);
		this.field_3375.addBox(0.0F, 0.0F, 0.0F, 4, 14, 8);
	}

	@Override
	public void render(T entity, float f, float g, float h, float i, float j, float k) {
		float l = f * 2.0F;
		if (l > 1.0F) {
			l = 1.0F;
		}

		l = 1.0F - l * l * l;
		this.field_3376.roll = (float) Math.PI - l * 0.35F * (float) Math.PI;
		this.field_3375.roll = (float) Math.PI + l * 0.35F * (float) Math.PI;
		this.field_3375.yaw = (float) Math.PI;
		float m = (f + MathHelper.sin(f * 2.7F)) * 0.6F * 12.0F;
		this.field_3376.rotationPointY = 24.0F - m;
		this.field_3375.rotationPointY = this.field_3376.rotationPointY;
		this.field_3374.rotationPointY = this.field_3376.rotationPointY;
		this.field_3374.render(k);
		this.field_3376.render(k);
		this.field_3375.render(k);
	}
}
