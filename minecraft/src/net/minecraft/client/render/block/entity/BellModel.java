package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;

@Environment(EnvType.CLIENT)
public class BellModel extends Model {
	private final ModelPart field_17129;
	private final ModelPart field_17130;

	public BellModel() {
		this.textureWidth = 32;
		this.textureHeight = 32;
		this.field_17129 = new ModelPart(this, 0, 0);
		this.field_17129.addCuboid(-3.0F, -6.0F, -3.0F, 6, 7, 6);
		this.field_17129.setPivot(8.0F, 12.0F, 8.0F);
		this.field_17130 = new ModelPart(this, 0, 13);
		this.field_17130.addCuboid(4.0F, 4.0F, 4.0F, 8, 2, 8);
		this.field_17130.setPivot(-8.0F, -12.0F, -8.0F);
		this.field_17129.addChild(this.field_17130);
	}

	public void method_17070(float f, float g, float h) {
		this.field_17129.pitch = f;
		this.field_17129.roll = g;
		this.field_17129.render(h);
	}
}
