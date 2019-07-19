package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.passive.SheepEntity;

@Environment(EnvType.CLIENT)
public class SheepWoolEntityModel<T extends SheepEntity> extends QuadrupedEntityModel<T> {
	private float field_3541;

	public SheepWoolEntityModel() {
		super(12, 0.0F);
		this.head = new ModelPart(this, 0, 0);
		this.head.addCuboid(-3.0F, -4.0F, -4.0F, 6, 6, 6, 0.6F);
		this.head.setPivot(0.0F, 6.0F, -8.0F);
		this.torso = new ModelPart(this, 28, 8);
		this.torso.addCuboid(-4.0F, -10.0F, -7.0F, 8, 16, 6, 1.75F);
		this.torso.setPivot(0.0F, 5.0F, 2.0F);
		float f = 0.5F;
		this.backRightLeg = new ModelPart(this, 0, 16);
		this.backRightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.5F);
		this.backRightLeg.setPivot(-3.0F, 12.0F, 7.0F);
		this.backLeftLeg = new ModelPart(this, 0, 16);
		this.backLeftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.5F);
		this.backLeftLeg.setPivot(3.0F, 12.0F, 7.0F);
		this.frontRightLeg = new ModelPart(this, 0, 16);
		this.frontRightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.5F);
		this.frontRightLeg.setPivot(-3.0F, 12.0F, -5.0F);
		this.frontLeftLeg = new ModelPart(this, 0, 16);
		this.frontLeftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.5F);
		this.frontLeftLeg.setPivot(3.0F, 12.0F, -5.0F);
	}

	public void animateModel(T sheepEntity, float f, float g, float h) {
		super.animateModel(sheepEntity, f, g, h);
		this.head.pivotY = 6.0F + sheepEntity.method_6628(h) * 9.0F;
		this.field_3541 = sheepEntity.method_6641(h);
	}

	public void setAngles(T sheepEntity, float f, float g, float h, float i, float j, float k) {
		super.setAngles(sheepEntity, f, g, h, i, j, k);
		this.head.pitch = this.field_3541;
	}
}
