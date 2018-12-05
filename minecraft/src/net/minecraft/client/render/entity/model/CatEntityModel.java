package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CatEntity;

@Environment(EnvType.CLIENT)
public class CatEntityModel extends OcelotEntityModel {
	private float field_16245;
	private float field_16244;
	private float field_16243;

	public CatEntityModel(float f) {
		super(f);
	}

	@Override
	public void animateModel(LivingEntity livingEntity, float f, float g, float h) {
		CatEntity catEntity = (CatEntity)livingEntity;
		this.field_16245 = catEntity.method_16082(h);
		this.field_16244 = catEntity.method_16091(h);
		this.field_16243 = catEntity.method_16095(h);
		if (this.field_16245 <= 0.0F) {
			this.field_3435.pitch = 0.0F;
			this.field_3435.roll = 0.0F;
			this.field_3440.pitch = 0.0F;
			this.field_3440.roll = 0.0F;
			this.field_3438.pitch = 0.0F;
			this.field_3438.roll = 0.0F;
			this.field_3438.rotationPointX = -1.2F;
			this.field_3439.pitch = 0.0F;
			this.field_3441.pitch = 0.0F;
			this.field_3441.roll = 0.0F;
			this.field_3441.rotationPointX = -1.1F;
			this.field_3441.rotationPointY = 18.0F;
		}

		super.animateModel(livingEntity, f, g, h);
		if (catEntity.isSitting()) {
			this.field_3437.pitch = (float) (Math.PI / 4);
			this.field_3437.rotationPointY += -4.0F;
			this.field_3437.rotationPointZ += 5.0F;
			this.field_3435.rotationPointY += -3.3F;
			this.field_3435.rotationPointZ++;
			this.field_3436.rotationPointY += 8.0F;
			this.field_3436.rotationPointZ += -2.0F;
			this.field_3442.rotationPointY += 2.0F;
			this.field_3442.rotationPointZ += -0.8F;
			this.field_3436.pitch = 1.7278761F;
			this.field_3442.pitch = 2.670354F;
			this.field_3440.pitch = (float) (-Math.PI / 20);
			this.field_3440.rotationPointY = 15.8F;
			this.field_3440.rotationPointZ = -7.0F;
			this.field_3438.pitch = (float) (-Math.PI / 20);
			this.field_3438.rotationPointY = 15.8F;
			this.field_3438.rotationPointZ = -7.0F;
			this.field_3439.pitch = (float) (-Math.PI / 2);
			this.field_3439.rotationPointY = 21.0F;
			this.field_3439.rotationPointZ = 1.0F;
			this.field_3441.pitch = (float) (-Math.PI / 2);
			this.field_3441.rotationPointY = 21.0F;
			this.field_3441.rotationPointZ = 1.0F;
			this.field_3434 = 3;
		}
	}

	@Override
	public void setRotationAngles(float f, float g, float h, float i, float j, float k, Entity entity) {
		super.setRotationAngles(f, g, h, i, j, k, entity);
		if (this.field_16245 > 0.0F) {
			this.field_3435.roll = this.method_16018(this.field_3435.roll, -1.2707963F, this.field_16245);
			this.field_3435.yaw = this.method_16018(this.field_3435.yaw, 1.2707963F, this.field_16245);
			this.field_3440.pitch = -1.2707963F;
			this.field_3438.pitch = -0.47079635F;
			this.field_3438.roll = -0.2F;
			this.field_3438.rotationPointX = -0.2F;
			this.field_3439.pitch = -0.4F;
			this.field_3441.pitch = 0.5F;
			this.field_3441.roll = -0.5F;
			this.field_3441.rotationPointX = -0.3F;
			this.field_3441.rotationPointY = 20.0F;
			this.field_3436.pitch = this.method_16018(this.field_3436.pitch, 0.8F, this.field_16244);
			this.field_3442.pitch = this.method_16018(this.field_3442.pitch, -0.4F, this.field_16244);
		}

		if (this.field_16243 > 0.0F) {
			this.field_3435.pitch = this.method_16018(this.field_3435.pitch, -0.58177644F, this.field_16243);
		}
	}

	protected float method_16018(float f, float g, float h) {
		float i = g - f;

		while (i < (float) -Math.PI) {
			i += (float) (Math.PI * 2);
		}

		while (i >= (float) Math.PI) {
			i -= (float) (Math.PI * 2);
		}

		return f + h * i;
	}
}
