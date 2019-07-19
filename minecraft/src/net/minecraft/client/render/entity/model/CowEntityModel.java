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
		this.head.addCuboid(-4.0F, -4.0F, -6.0F, 8, 8, 6, 0.0F);
		this.head.setPivot(0.0F, 4.0F, -8.0F);
		this.head.setTextureOffset(22, 0).addCuboid(-5.0F, -5.0F, -4.0F, 1, 3, 1, 0.0F);
		this.head.setTextureOffset(22, 0).addCuboid(4.0F, -5.0F, -4.0F, 1, 3, 1, 0.0F);
		this.torso = new ModelPart(this, 18, 4);
		this.torso.addCuboid(-6.0F, -10.0F, -7.0F, 12, 18, 10, 0.0F);
		this.torso.setPivot(0.0F, 5.0F, 2.0F);
		this.torso.setTextureOffset(52, 0).addCuboid(-2.0F, 2.0F, -8.0F, 4, 6, 1);
		this.backRightLeg.pivotX--;
		this.backLeftLeg.pivotX++;
		this.backRightLeg.pivotZ += 0.0F;
		this.backLeftLeg.pivotZ += 0.0F;
		this.frontRightLeg.pivotX--;
		this.frontLeftLeg.pivotX++;
		this.frontRightLeg.pivotZ--;
		this.frontLeftLeg.pivotZ--;
		this.field_3537 += 2.0F;
	}

	public ModelPart method_2800() {
		return this.head;
	}
}
