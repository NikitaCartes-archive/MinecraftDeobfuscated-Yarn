package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;

@Environment(EnvType.CLIENT)
public class BannerBlockEntityModel extends Model {
	private final Cuboid field_3309;
	private final Cuboid field_3311;
	private final Cuboid field_3310;

	public BannerBlockEntityModel() {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.field_3309 = new Cuboid(this, 0, 0);
		this.field_3309.addBox(-10.0F, 0.0F, -2.0F, 20, 40, 1, 0.0F);
		this.field_3311 = new Cuboid(this, 44, 0);
		this.field_3311.addBox(-1.0F, -30.0F, -1.0F, 2, 42, 2, 0.0F);
		this.field_3310 = new Cuboid(this, 0, 42);
		this.field_3310.addBox(-10.0F, -32.0F, -1.0F, 20, 2, 2, 0.0F);
	}

	public void method_2793() {
		this.field_3309.rotationPointY = -32.0F;
		this.field_3309.render(0.0625F);
		this.field_3311.render(0.0625F);
		this.field_3310.render(0.0625F);
	}

	public Cuboid method_2791() {
		return this.field_3311;
	}

	public Cuboid method_2792() {
		return this.field_3309;
	}
}
