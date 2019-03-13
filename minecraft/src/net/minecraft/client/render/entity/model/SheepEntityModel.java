package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.passive.SheepEntity;

@Environment(EnvType.CLIENT)
public class SheepEntityModel<T extends SheepEntity> extends QuadrupedEntityModel<T> {
	private float field_3541;

	public SheepEntityModel() {
		super(12, 0.0F);
		this.field_3535 = new Cuboid(this, 0, 0);
		this.field_3535.addBox(-3.0F, -4.0F, -4.0F, 6, 6, 6, 0.6F);
		this.field_3535.setRotationPoint(0.0F, 6.0F, -8.0F);
		this.field_3538 = new Cuboid(this, 28, 8);
		this.field_3538.addBox(-4.0F, -10.0F, -7.0F, 8, 16, 6, 1.75F);
		this.field_3538.setRotationPoint(0.0F, 5.0F, 2.0F);
		float f = 0.5F;
		this.field_3536 = new Cuboid(this, 0, 16);
		this.field_3536.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.5F);
		this.field_3536.setRotationPoint(-3.0F, 12.0F, 7.0F);
		this.field_3534 = new Cuboid(this, 0, 16);
		this.field_3534.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.5F);
		this.field_3534.setRotationPoint(3.0F, 12.0F, 7.0F);
		this.field_3533 = new Cuboid(this, 0, 16);
		this.field_3533.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.5F);
		this.field_3533.setRotationPoint(-3.0F, 12.0F, -5.0F);
		this.field_3539 = new Cuboid(this, 0, 16);
		this.field_3539.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.5F);
		this.field_3539.setRotationPoint(3.0F, 12.0F, -5.0F);
	}

	public void method_17118(T sheepEntity, float f, float g, float h) {
		super.animateModel(sheepEntity, f, g, h);
		this.field_3535.rotationPointY = 6.0F + sheepEntity.method_6628(h) * 9.0F;
		this.field_3541 = sheepEntity.method_6641(h);
	}

	public void method_17119(T sheepEntity, float f, float g, float h, float i, float j, float k) {
		super.setAngles(sheepEntity, f, g, h, i, j, k);
		this.field_3535.pitch = this.field_3541;
	}
}
