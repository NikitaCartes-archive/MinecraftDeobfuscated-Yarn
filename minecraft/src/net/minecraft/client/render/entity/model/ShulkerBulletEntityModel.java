package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4595;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class ShulkerBulletEntityModel<T extends Entity> extends class_4595<T> {
	private final ModelPart field_3556;

	public ShulkerBulletEntityModel() {
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.field_3556 = new ModelPart(this);
		this.field_3556.setTextureOffset(0, 0).addCuboid(-4.0F, -4.0F, -1.0F, 8.0F, 8.0F, 2.0F, 0.0F);
		this.field_3556.setTextureOffset(0, 10).addCuboid(-1.0F, -4.0F, -4.0F, 2.0F, 8.0F, 8.0F, 0.0F);
		this.field_3556.setTextureOffset(20, 0).addCuboid(-4.0F, -1.0F, -4.0F, 8.0F, 2.0F, 8.0F, 0.0F);
		this.field_3556.setRotationPoint(0.0F, 0.0F, 0.0F);
	}

	@Override
	public Iterable<ModelPart> method_22960() {
		return ImmutableList.<ModelPart>of(this.field_3556);
	}

	@Override
	public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
		this.field_3556.yaw = i * (float) (Math.PI / 180.0);
		this.field_3556.pitch = j * (float) (Math.PI / 180.0);
	}
}
