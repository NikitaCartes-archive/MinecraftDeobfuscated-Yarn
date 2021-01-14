package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.mob.Hoglin;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class HoglinEntityModel<T extends MobEntity & Hoglin> extends AnimalModel<T> {
	private final ModelPart head;
	private final ModelPart rightEar;
	private final ModelPart leftEar;
	private final ModelPart body;
	private final ModelPart field_22231;
	private final ModelPart field_22232;
	private final ModelPart field_22233;
	private final ModelPart field_22234;
	private final ModelPart field_25484;

	public HoglinEntityModel() {
		super(true, 8.0F, 6.0F, 1.9F, 2.0F, 24.0F);
		this.textureWidth = 128;
		this.textureHeight = 64;
		this.body = new ModelPart(this);
		this.body.setPivot(0.0F, 7.0F, 0.0F);
		this.body.setTextureOffset(1, 1).addCuboid(-8.0F, -7.0F, -13.0F, 16.0F, 14.0F, 26.0F);
		this.field_25484 = new ModelPart(this);
		this.field_25484.setPivot(0.0F, -14.0F, -5.0F);
		this.field_25484.setTextureOffset(90, 33).addCuboid(0.0F, 0.0F, -9.0F, 0.0F, 10.0F, 19.0F, 0.001F);
		this.body.addChild(this.field_25484);
		this.head = new ModelPart(this);
		this.head.setPivot(0.0F, 2.0F, -12.0F);
		this.head.setTextureOffset(61, 1).addCuboid(-7.0F, -3.0F, -19.0F, 14.0F, 6.0F, 19.0F);
		this.rightEar = new ModelPart(this);
		this.rightEar.setPivot(-6.0F, -2.0F, -3.0F);
		this.rightEar.setTextureOffset(1, 1).addCuboid(-6.0F, -1.0F, -2.0F, 6.0F, 1.0F, 4.0F);
		this.rightEar.roll = (float) (-Math.PI * 2.0 / 9.0);
		this.head.addChild(this.rightEar);
		this.leftEar = new ModelPart(this);
		this.leftEar.setPivot(6.0F, -2.0F, -3.0F);
		this.leftEar.setTextureOffset(1, 6).addCuboid(0.0F, -1.0F, -2.0F, 6.0F, 1.0F, 4.0F);
		this.leftEar.roll = (float) (Math.PI * 2.0 / 9.0);
		this.head.addChild(this.leftEar);
		ModelPart modelPart = new ModelPart(this);
		modelPart.setPivot(-7.0F, 2.0F, -12.0F);
		modelPart.setTextureOffset(10, 13).addCuboid(-1.0F, -11.0F, -1.0F, 2.0F, 11.0F, 2.0F);
		this.head.addChild(modelPart);
		ModelPart modelPart2 = new ModelPart(this);
		modelPart2.setPivot(7.0F, 2.0F, -12.0F);
		modelPart2.setTextureOffset(1, 13).addCuboid(-1.0F, -11.0F, -1.0F, 2.0F, 11.0F, 2.0F);
		this.head.addChild(modelPart2);
		this.head.pitch = 0.87266463F;
		int i = 14;
		int j = 11;
		this.field_22231 = new ModelPart(this);
		this.field_22231.setPivot(-4.0F, 10.0F, -8.5F);
		this.field_22231.setTextureOffset(66, 42).addCuboid(-3.0F, 0.0F, -3.0F, 6.0F, 14.0F, 6.0F);
		this.field_22232 = new ModelPart(this);
		this.field_22232.setPivot(4.0F, 10.0F, -8.5F);
		this.field_22232.setTextureOffset(41, 42).addCuboid(-3.0F, 0.0F, -3.0F, 6.0F, 14.0F, 6.0F);
		this.field_22233 = new ModelPart(this);
		this.field_22233.setPivot(-5.0F, 13.0F, 10.0F);
		this.field_22233.setTextureOffset(21, 45).addCuboid(-2.5F, 0.0F, -2.5F, 5.0F, 11.0F, 5.0F);
		this.field_22234 = new ModelPart(this);
		this.field_22234.setPivot(5.0F, 13.0F, 10.0F);
		this.field_22234.setTextureOffset(0, 45).addCuboid(-2.5F, 0.0F, -2.5F, 5.0F, 11.0F, 5.0F);
	}

	@Override
	protected Iterable<ModelPart> getHeadParts() {
		return ImmutableList.<ModelPart>of(this.head);
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return ImmutableList.<ModelPart>of(this.body, this.field_22231, this.field_22232, this.field_22233, this.field_22234);
	}

	public void setAngles(T mobEntity, float f, float g, float h, float i, float j) {
		this.rightEar.roll = (float) (-Math.PI * 2.0 / 9.0) - g * MathHelper.sin(f);
		this.leftEar.roll = (float) (Math.PI * 2.0 / 9.0) + g * MathHelper.sin(f);
		this.head.yaw = i * (float) (Math.PI / 180.0);
		int k = mobEntity.getMovementCooldownTicks();
		float l = 1.0F - (float)MathHelper.abs(10 - 2 * k) / 10.0F;
		this.head.pitch = MathHelper.lerp(l, 0.87266463F, (float) (-Math.PI / 9));
		if (mobEntity.isBaby()) {
			this.head.pivotY = MathHelper.lerp(l, 2.0F, 5.0F);
			this.field_25484.pivotZ = -3.0F;
		} else {
			this.head.pivotY = 2.0F;
			this.field_25484.pivotZ = -7.0F;
		}

		float m = 1.2F;
		this.field_22231.pitch = MathHelper.cos(f) * 1.2F * g;
		this.field_22232.pitch = MathHelper.cos(f + (float) Math.PI) * 1.2F * g;
		this.field_22233.pitch = this.field_22232.pitch;
		this.field_22234.pitch = this.field_22231.pitch;
	}
}
