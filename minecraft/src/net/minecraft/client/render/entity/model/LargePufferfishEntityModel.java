package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class LargePufferfishEntityModel<T extends Entity> extends CompositeEntityModel<T> {
	private final ModelPart field_3493;
	private final ModelPart field_3499;
	private final ModelPart field_3494;
	private final ModelPart field_3490;
	private final ModelPart field_3496;
	private final ModelPart field_3495;
	private final ModelPart field_3489;
	private final ModelPart field_3497;
	private final ModelPart field_3491;
	private final ModelPart field_3487;
	private final ModelPart field_3492;
	private final ModelPart field_3498;
	private final ModelPart field_3488;

	public LargePufferfishEntityModel() {
		this.textureWidth = 32;
		this.textureHeight = 32;
		int i = 22;
		this.field_3493 = new ModelPart(this, 0, 0);
		this.field_3493.addCuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F);
		this.field_3493.setPivot(0.0F, 22.0F, 0.0F);
		this.field_3499 = new ModelPart(this, 24, 0);
		this.field_3499.addCuboid(-2.0F, 0.0F, -1.0F, 2.0F, 1.0F, 2.0F);
		this.field_3499.setPivot(-4.0F, 15.0F, -2.0F);
		this.field_3494 = new ModelPart(this, 24, 3);
		this.field_3494.addCuboid(0.0F, 0.0F, -1.0F, 2.0F, 1.0F, 2.0F);
		this.field_3494.setPivot(4.0F, 15.0F, -2.0F);
		this.field_3490 = new ModelPart(this, 15, 17);
		this.field_3490.addCuboid(-4.0F, -1.0F, 0.0F, 8.0F, 1.0F, 0.0F);
		this.field_3490.setPivot(0.0F, 14.0F, -4.0F);
		this.field_3490.pitch = (float) (Math.PI / 4);
		this.field_3496 = new ModelPart(this, 14, 16);
		this.field_3496.addCuboid(-4.0F, -1.0F, 0.0F, 8.0F, 1.0F, 1.0F);
		this.field_3496.setPivot(0.0F, 14.0F, 0.0F);
		this.field_3495 = new ModelPart(this, 23, 18);
		this.field_3495.addCuboid(-4.0F, -1.0F, 0.0F, 8.0F, 1.0F, 0.0F);
		this.field_3495.setPivot(0.0F, 14.0F, 4.0F);
		this.field_3495.pitch = (float) (-Math.PI / 4);
		this.field_3489 = new ModelPart(this, 5, 17);
		this.field_3489.addCuboid(-1.0F, -8.0F, 0.0F, 1.0F, 8.0F, 0.0F);
		this.field_3489.setPivot(-4.0F, 22.0F, -4.0F);
		this.field_3489.yaw = (float) (-Math.PI / 4);
		this.field_3497 = new ModelPart(this, 1, 17);
		this.field_3497.addCuboid(0.0F, -8.0F, 0.0F, 1.0F, 8.0F, 0.0F);
		this.field_3497.setPivot(4.0F, 22.0F, -4.0F);
		this.field_3497.yaw = (float) (Math.PI / 4);
		this.field_3491 = new ModelPart(this, 15, 20);
		this.field_3491.addCuboid(-4.0F, 0.0F, 0.0F, 8.0F, 1.0F, 0.0F);
		this.field_3491.setPivot(0.0F, 22.0F, -4.0F);
		this.field_3491.pitch = (float) (-Math.PI / 4);
		this.field_3492 = new ModelPart(this, 15, 20);
		this.field_3492.addCuboid(-4.0F, 0.0F, 0.0F, 8.0F, 1.0F, 0.0F);
		this.field_3492.setPivot(0.0F, 22.0F, 0.0F);
		this.field_3487 = new ModelPart(this, 15, 20);
		this.field_3487.addCuboid(-4.0F, 0.0F, 0.0F, 8.0F, 1.0F, 0.0F);
		this.field_3487.setPivot(0.0F, 22.0F, 4.0F);
		this.field_3487.pitch = (float) (Math.PI / 4);
		this.field_3498 = new ModelPart(this, 9, 17);
		this.field_3498.addCuboid(-1.0F, -8.0F, 0.0F, 1.0F, 8.0F, 0.0F);
		this.field_3498.setPivot(-4.0F, 22.0F, 4.0F);
		this.field_3498.yaw = (float) (Math.PI / 4);
		this.field_3488 = new ModelPart(this, 9, 17);
		this.field_3488.addCuboid(0.0F, -8.0F, 0.0F, 1.0F, 8.0F, 0.0F);
		this.field_3488.setPivot(4.0F, 22.0F, 4.0F);
		this.field_3488.yaw = (float) (-Math.PI / 4);
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return ImmutableList.<ModelPart>of(
			this.field_3493,
			this.field_3499,
			this.field_3494,
			this.field_3490,
			this.field_3496,
			this.field_3495,
			this.field_3489,
			this.field_3488,
			this.field_3491,
			this.field_3492,
			this.field_3487,
			this.field_3498,
			this.field_3488
		);
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch) {
		this.field_3499.roll = -0.2F + 0.4F * MathHelper.sin(age * 0.2F);
		this.field_3494.roll = 0.2F - 0.4F * MathHelper.sin(age * 0.2F);
	}
}
