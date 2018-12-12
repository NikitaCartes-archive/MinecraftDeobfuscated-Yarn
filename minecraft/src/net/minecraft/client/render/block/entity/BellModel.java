package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;

@Environment(EnvType.CLIENT)
public class BellModel extends Model {
	private final Cuboid field_17129;
	private final Cuboid field_17130;

	public BellModel() {
		this.textureWidth = 32;
		this.textureHeight = 32;
		this.field_17129 = new Cuboid(this, 0, 0);
		this.field_17129.addBox(-3.0F, -6.0F, -3.0F, 6, 7, 6);
		this.field_17129.setRotationPoint(8.0F, 12.0F, 8.0F);
		this.field_17130 = new Cuboid(this, 0, 13);
		this.field_17130.addBox(4.0F, 4.0F, 4.0F, 8, 2, 8);
		this.field_17130.setRotationPoint(-8.0F, -12.0F, -8.0F);
		this.field_17129.addChild(this.field_17130);
	}

	public void method_17070(float f, float g, float h) {
		this.field_17129.pitch = f;
		this.field_17129.roll = g;
		this.field_17129.render(h);
	}
}
