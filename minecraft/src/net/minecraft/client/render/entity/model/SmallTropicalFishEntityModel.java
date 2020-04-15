package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SmallTropicalFishEntityModel<T extends Entity> extends TintableCompositeModel<T> {
	private final ModelPart field_3589;
	private final ModelPart field_3591;
	private final ModelPart field_3590;
	private final ModelPart field_3588;
	private final ModelPart field_3587;

	public SmallTropicalFishEntityModel(float scale) {
		this.textureWidth = 32;
		this.textureHeight = 32;
		int i = 22;
		this.field_3589 = new ModelPart(this, 0, 0);
		this.field_3589.addCuboid(-1.0F, -1.5F, -3.0F, 2.0F, 3.0F, 6.0F, scale);
		this.field_3589.setPivot(0.0F, 22.0F, 0.0F);
		this.field_3591 = new ModelPart(this, 22, -6);
		this.field_3591.addCuboid(0.0F, -1.5F, 0.0F, 0.0F, 3.0F, 6.0F, scale);
		this.field_3591.setPivot(0.0F, 22.0F, 3.0F);
		this.field_3590 = new ModelPart(this, 2, 16);
		this.field_3590.addCuboid(-2.0F, -1.0F, 0.0F, 2.0F, 2.0F, 0.0F, scale);
		this.field_3590.setPivot(-1.0F, 22.5F, 0.0F);
		this.field_3590.yaw = (float) (Math.PI / 4);
		this.field_3588 = new ModelPart(this, 2, 12);
		this.field_3588.addCuboid(0.0F, -1.0F, 0.0F, 2.0F, 2.0F, 0.0F, scale);
		this.field_3588.setPivot(1.0F, 22.5F, 0.0F);
		this.field_3588.yaw = (float) (-Math.PI / 4);
		this.field_3587 = new ModelPart(this, 10, -5);
		this.field_3587.addCuboid(0.0F, -3.0F, 0.0F, 0.0F, 3.0F, 6.0F, scale);
		this.field_3587.setPivot(0.0F, 20.5F, -3.0F);
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return ImmutableList.<ModelPart>of(this.field_3589, this.field_3591, this.field_3590, this.field_3588, this.field_3587);
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float customAngle, float headYaw, float headPitch) {
		float f = 1.0F;
		if (!entity.isTouchingWater()) {
			f = 1.5F;
		}

		this.field_3591.yaw = -f * 0.45F * MathHelper.sin(0.6F * customAngle);
	}
}