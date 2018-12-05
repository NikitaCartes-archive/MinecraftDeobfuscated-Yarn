package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.sortme.OptionMainHand;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class VexEntityModel extends BipedEntityModel {
	private final Cuboid field_3601;
	private final Cuboid field_3602;

	public VexEntityModel() {
		this(0.0F);
	}

	public VexEntityModel(float f) {
		super(f, 0.0F, 64, 64);
		this.legLeft.visible = false;
		this.headwear.visible = false;
		this.legRight = new Cuboid(this, 32, 0);
		this.legRight.addBox(-1.0F, -1.0F, -2.0F, 6, 10, 4, 0.0F);
		this.legRight.setRotationPoint(-1.9F, 12.0F, 0.0F);
		this.field_3602 = new Cuboid(this, 0, 32);
		this.field_3602.addBox(-20.0F, 0.0F, 0.0F, 20, 12, 1);
		this.field_3601 = new Cuboid(this, 0, 32);
		this.field_3601.mirror = true;
		this.field_3601.addBox(0.0F, 0.0F, 0.0F, 20, 12, 1);
	}

	@Override
	public void render(Entity entity, float f, float g, float h, float i, float j, float k) {
		super.render(entity, f, g, h, i, j, k);
		this.field_3602.render(k);
		this.field_3601.render(k);
	}

	@Override
	public void setRotationAngles(float f, float g, float h, float i, float j, float k, Entity entity) {
		super.setRotationAngles(f, g, h, i, j, k, entity);
		VexEntity vexEntity = (VexEntity)entity;
		if (vexEntity.method_7176()) {
			if (vexEntity.getMainHand() == OptionMainHand.field_6183) {
				this.armRight.pitch = 3.7699115F;
			} else {
				this.armLeft.pitch = 3.7699115F;
			}
		}

		this.legRight.pitch += (float) (Math.PI / 5);
		this.field_3602.rotationPointZ = 2.0F;
		this.field_3601.rotationPointZ = 2.0F;
		this.field_3602.rotationPointY = 1.0F;
		this.field_3601.rotationPointY = 1.0F;
		this.field_3602.yaw = 0.47123894F + MathHelper.cos(h * 0.8F) * (float) Math.PI * 0.05F;
		this.field_3601.yaw = -this.field_3602.yaw;
		this.field_3601.roll = -0.47123894F;
		this.field_3601.pitch = 0.47123894F;
		this.field_3602.pitch = 0.47123894F;
		this.field_3602.roll = 0.47123894F;
	}
}
