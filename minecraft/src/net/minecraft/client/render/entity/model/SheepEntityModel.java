package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.passive.SheepEntity;

@Environment(EnvType.CLIENT)
public class SheepEntityModel<T extends SheepEntity> extends QuadrupedEntityModel<T> {
	private float field_3552;

	public SheepEntityModel() {
		super(12, 0.0F);
		this.head = new Cuboid(this, 0, 0);
		this.head.addBox(-3.0F, -4.0F, -6.0F, 6, 6, 8, 0.0F);
		this.head.setRotationPoint(0.0F, 6.0F, -8.0F);
		this.body = new Cuboid(this, 28, 8);
		this.body.addBox(-4.0F, -10.0F, -7.0F, 8, 16, 6, 0.0F);
		this.body.setRotationPoint(0.0F, 5.0F, 2.0F);
	}

	public void method_17120(T sheepEntity, float f, float g, float h) {
		super.animateModel(sheepEntity, f, g, h);
		this.head.rotationPointY = 6.0F + sheepEntity.method_6628(h) * 9.0F;
		this.field_3552 = sheepEntity.method_6641(h);
	}

	public void method_17121(T sheepEntity, float f, float g, float h, float i, float j, float k) {
		super.setAngles(sheepEntity, f, g, h, i, j, k);
		this.head.pitch = this.field_3552;
	}
}
