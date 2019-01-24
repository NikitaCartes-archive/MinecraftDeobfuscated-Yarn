package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;

@Environment(EnvType.CLIENT)
public class SkullOverlayEntityModel extends SkullEntityModel {
	private final Cuboid field_3377 = new Cuboid(this, 32, 0);

	public SkullOverlayEntityModel() {
		super(0, 0, 64, 64);
		this.field_3377.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.25F);
		this.field_3377.setRotationPoint(0.0F, 0.0F, 0.0F);
	}

	@Override
	public void setRotationAngles(float f, float g, float h, float i, float j, float k) {
		super.setRotationAngles(f, g, h, i, j, k);
		this.field_3377.yaw = this.field_3564.yaw;
		this.field_3377.pitch = this.field_3564.pitch;
		this.field_3377.render(k);
	}
}
