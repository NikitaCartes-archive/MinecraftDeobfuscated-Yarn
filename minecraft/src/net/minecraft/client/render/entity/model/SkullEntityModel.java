package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;

@Environment(EnvType.CLIENT)
public class SkullEntityModel extends Model {
	protected Cuboid field_3564;

	public SkullEntityModel() {
		this(0, 35, 64, 64);
	}

	public SkullEntityModel(int i, int j, int k, int l) {
		this.textureWidth = k;
		this.textureHeight = l;
		this.field_3564 = new Cuboid(this, i, j);
		this.field_3564.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
		this.field_3564.setRotationPoint(0.0F, 0.0F, 0.0F);
	}

	public void setRotationAngles(float f, float g, float h, float i, float j, float k) {
		this.field_3564.yaw = i * (float) (Math.PI / 180.0);
		this.field_3564.pitch = j * (float) (Math.PI / 180.0);
		this.field_3564.render(k);
	}
}
