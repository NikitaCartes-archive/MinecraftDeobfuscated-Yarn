package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class class_612 extends Model {
	private final Cuboid field_3589;
	private final Cuboid field_3591;
	private final Cuboid field_3590;
	private final Cuboid field_3588;
	private final Cuboid field_3587;

	public class_612() {
		this(0.0F);
	}

	public class_612(float f) {
		this.textureWidth = 32;
		this.textureHeight = 32;
		int i = 22;
		this.field_3589 = new Cuboid(this, 0, 0);
		this.field_3589.addBox(-1.0F, -1.5F, -3.0F, 2, 3, 6, f);
		this.field_3589.setRotationPoint(0.0F, 22.0F, 0.0F);
		this.field_3591 = new Cuboid(this, 22, -6);
		this.field_3591.addBox(0.0F, -1.5F, 0.0F, 0, 3, 6, f);
		this.field_3591.setRotationPoint(0.0F, 22.0F, 3.0F);
		this.field_3590 = new Cuboid(this, 2, 16);
		this.field_3590.addBox(-2.0F, -1.0F, 0.0F, 2, 2, 0, f);
		this.field_3590.setRotationPoint(-1.0F, 22.5F, 0.0F);
		this.field_3590.yaw = (float) (Math.PI / 4);
		this.field_3588 = new Cuboid(this, 2, 12);
		this.field_3588.addBox(0.0F, -1.0F, 0.0F, 2, 2, 0, f);
		this.field_3588.setRotationPoint(1.0F, 22.5F, 0.0F);
		this.field_3588.yaw = (float) (-Math.PI / 4);
		this.field_3587 = new Cuboid(this, 10, -5);
		this.field_3587.addBox(0.0F, -3.0F, 0.0F, 0, 3, 6, f);
		this.field_3587.setRotationPoint(0.0F, 20.5F, -3.0F);
	}

	@Override
	public void render(Entity entity, float f, float g, float h, float i, float j, float k) {
		this.setRotationAngles(f, g, h, i, j, k, entity);
		this.field_3589.render(k);
		this.field_3591.render(k);
		this.field_3590.render(k);
		this.field_3588.render(k);
		this.field_3587.render(k);
	}

	@Override
	public void setRotationAngles(float f, float g, float h, float i, float j, float k, Entity entity) {
		float l = 1.0F;
		if (!entity.isInsideWater()) {
			l = 1.5F;
		}

		this.field_3591.yaw = -l * 0.45F * MathHelper.sin(0.6F * h);
	}
}
