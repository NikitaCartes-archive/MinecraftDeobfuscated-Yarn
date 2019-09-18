package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class CowEntityModel<T extends Entity> extends QuadrupedEntityModel<T> {
	public CowEntityModel() {
		super(12, 0.0F);
		this.head = new ModelPart(this, 0, 0);
		this.head.addCuboid(-4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 6.0F, 0.0F);
		this.head.setRotationPoint(0.0F, 4.0F, -8.0F);
		this.head.setTextureOffset(22, 0).addCuboid(-5.0F, -5.0F, -4.0F, 1.0F, 3.0F, 1.0F, 0.0F);
		this.head.setTextureOffset(22, 0).addCuboid(4.0F, -5.0F, -4.0F, 1.0F, 3.0F, 1.0F, 0.0F);
		this.body = new ModelPart(this, 18, 4);
		this.body.addCuboid(-6.0F, -10.0F, -7.0F, 12.0F, 18.0F, 10.0F, 0.0F);
		this.body.setRotationPoint(0.0F, 5.0F, 2.0F);
		this.body.setTextureOffset(52, 0).addCuboid(-2.0F, 2.0F, -8.0F, 4.0F, 6.0F, 1.0F);
		this.leg1.rotationPointX--;
		this.leg2.rotationPointX++;
		this.leg1.rotationPointZ += 0.0F;
		this.leg2.rotationPointZ += 0.0F;
		this.leg3.rotationPointX--;
		this.leg4.rotationPointX++;
		this.leg3.rotationPointZ--;
		this.leg4.rotationPointZ--;
		this.field_3537 += 2.0F;
	}

	public ModelPart getHead() {
		return this.head;
	}
}
