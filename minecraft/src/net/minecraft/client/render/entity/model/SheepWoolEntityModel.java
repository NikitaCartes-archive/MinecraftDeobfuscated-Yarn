package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;

@Environment(EnvType.CLIENT)
public class SheepWoolEntityModel extends QuadrupedEntityModel {
	private float field_3552;

	public SheepWoolEntityModel() {
		super(12, 0.0F);
		this.head = new Cuboid(this, 0, 0);
		this.head.addBox(-3.0F, -4.0F, -6.0F, 6, 6, 8, 0.0F);
		this.head.setRotationPoint(0.0F, 6.0F, -8.0F);
		this.body = new Cuboid(this, 28, 8);
		this.body.addBox(-4.0F, -10.0F, -7.0F, 8, 16, 6, 0.0F);
		this.body.setRotationPoint(0.0F, 5.0F, 2.0F);
	}

	@Override
	public void animateModel(LivingEntity livingEntity, float f, float g, float h) {
		super.animateModel(livingEntity, f, g, h);
		this.head.rotationPointY = 6.0F + ((SheepEntity)livingEntity).method_6628(h) * 9.0F;
		this.field_3552 = ((SheepEntity)livingEntity).method_6641(h);
	}

	@Override
	public void setRotationAngles(float f, float g, float h, float i, float j, float k, Entity entity) {
		super.setRotationAngles(f, g, h, i, j, k, entity);
		this.head.pitch = this.field_3552;
	}
}
