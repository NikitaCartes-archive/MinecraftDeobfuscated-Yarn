package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SmallPufferfishEntityModel<T extends Entity> extends EntityModel<T> {
	private final ModelPart field_3505;
	private final ModelPart field_3507;
	private final ModelPart field_3506;
	private final ModelPart field_3504;
	private final ModelPart field_3503;
	private final ModelPart field_3508;

	public SmallPufferfishEntityModel() {
		this.textureWidth = 32;
		this.textureHeight = 32;
		int i = 23;
		this.field_3505 = new ModelPart(this, 0, 27);
		this.field_3505.addCuboid(-1.5F, -2.0F, -1.5F, 3.0F, 2.0F, 3.0F);
		this.field_3505.setRotationPoint(0.0F, 23.0F, 0.0F);
		this.field_3507 = new ModelPart(this, 24, 6);
		this.field_3507.addCuboid(-1.5F, 0.0F, -1.5F, 1.0F, 1.0F, 1.0F);
		this.field_3507.setRotationPoint(0.0F, 20.0F, 0.0F);
		this.field_3506 = new ModelPart(this, 28, 6);
		this.field_3506.addCuboid(0.5F, 0.0F, -1.5F, 1.0F, 1.0F, 1.0F);
		this.field_3506.setRotationPoint(0.0F, 20.0F, 0.0F);
		this.field_3508 = new ModelPart(this, -3, 0);
		this.field_3508.addCuboid(-1.5F, 0.0F, 0.0F, 3.0F, 0.0F, 3.0F);
		this.field_3508.setRotationPoint(0.0F, 22.0F, 1.5F);
		this.field_3504 = new ModelPart(this, 25, 0);
		this.field_3504.addCuboid(-1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 2.0F);
		this.field_3504.setRotationPoint(-1.5F, 22.0F, -1.5F);
		this.field_3503 = new ModelPart(this, 25, 0);
		this.field_3503.addCuboid(0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 2.0F);
		this.field_3503.setRotationPoint(1.5F, 22.0F, -1.5F);
	}

	@Override
	public void render(T entity, float f, float g, float h, float i, float j, float k) {
		this.setAngles(entity, f, g, h, i, j, k);
		this.field_3505.render(k);
		this.field_3507.render(k);
		this.field_3506.render(k);
		this.field_3508.render(k);
		this.field_3504.render(k);
		this.field_3503.render(k);
	}

	@Override
	public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
		this.field_3504.roll = -0.2F + 0.4F * MathHelper.sin(h * 0.2F);
		this.field_3503.roll = 0.2F - 0.4F * MathHelper.sin(h * 0.2F);
	}
}
