package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class ShulkerBulletEntityModel<T extends Entity> extends CompositeEntityModel<T> {
	private final ModelPart bullet;

	public ShulkerBulletEntityModel() {
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.bullet = new ModelPart(this);
		this.bullet.setTextureOffset(0, 0).addCuboid(-4.0F, -4.0F, -1.0F, 8.0F, 8.0F, 2.0F, 0.0F);
		this.bullet.setTextureOffset(0, 10).addCuboid(-1.0F, -4.0F, -4.0F, 2.0F, 8.0F, 8.0F, 0.0F);
		this.bullet.setTextureOffset(20, 0).addCuboid(-4.0F, -1.0F, -4.0F, 8.0F, 2.0F, 8.0F, 0.0F);
		this.bullet.setPivot(0.0F, 0.0F, 0.0F);
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return ImmutableList.<ModelPart>of(this.bullet);
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float customAngle, float headYaw, float headPitch) {
		this.bullet.yaw = headYaw * (float) (Math.PI / 180.0);
		this.bullet.pitch = headPitch * (float) (Math.PI / 180.0);
	}
}
