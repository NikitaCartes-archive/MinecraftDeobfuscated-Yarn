package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class HoglinEntityModel extends AnimalModel<HoglinEntity> {
	private final ModelPart head;
	private final ModelPart rightEar;
	private final ModelPart leftEar;
	private final ModelPart torso;
	private final ModelPart field_22231;
	private final ModelPart field_22232;
	private final ModelPart field_22233;
	private final ModelPart field_22234;

	public HoglinEntityModel() {
		super(true, 8.0F, 6.0F, 1.9F, 2.0F, 24.0F);
		this.textureWidth = 128;
		this.textureHeight = 128;
		this.torso = new ModelPart(this);
		this.torso.setPivot(0.0F, 7.0F, 0.0F);
		this.torso.setTextureOffset(1, 1).addCuboid(-8.0F, -7.0F, -13.0F, 16.0F, 14.0F, 26.0F);
		ModelPart modelPart = new ModelPart(this);
		modelPart.setPivot(0.0F, -14.0F, -7.0F);
		modelPart.setTextureOffset(5, 67).addCuboid(0.0F, 0.0F, -9.0F, 0.0F, 10.0F, 19.0F, 0.001F);
		this.torso.addChild(modelPart);
		this.head = new ModelPart(this);
		this.head.setPivot(0.0F, 2.0F, -12.0F);
		this.head.setTextureOffset(1, 42).addCuboid(-7.0F, -3.0F, -19.0F, 14.0F, 6.0F, 19.0F);
		this.rightEar = new ModelPart(this);
		this.rightEar.setPivot(-6.0F, -2.0F, -3.0F);
		this.rightEar.setTextureOffset(4, 16).addCuboid(-6.0F, -1.0F, -2.0F, 6.0F, 1.0F, 4.0F);
		this.rightEar.roll = (float) (-Math.PI * 2.0 / 9.0);
		this.head.addChild(this.rightEar);
		this.leftEar = new ModelPart(this);
		this.leftEar.setPivot(6.0F, -2.0F, -3.0F);
		this.leftEar.setTextureOffset(4, 21).addCuboid(0.0F, -1.0F, -2.0F, 6.0F, 1.0F, 4.0F);
		this.leftEar.roll = (float) (Math.PI * 2.0 / 9.0);
		this.head.addChild(this.leftEar);
		ModelPart modelPart2 = new ModelPart(this);
		modelPart2.setPivot(-7.0F, 2.0F, -12.0F);
		modelPart2.setTextureOffset(6, 45).addCuboid(-1.0F, -11.0F, -1.0F, 2.0F, 11.0F, 2.0F);
		this.head.addChild(modelPart2);
		ModelPart modelPart3 = new ModelPart(this);
		modelPart3.setPivot(7.0F, 2.0F, -12.0F);
		modelPart3.setTextureOffset(6, 45).addCuboid(-1.0F, -11.0F, -1.0F, 2.0F, 11.0F, 2.0F);
		this.head.addChild(modelPart3);
		this.head.pitch = 0.87266463F;
		int i = 14;
		int j = 11;
		this.field_22231 = new ModelPart(this);
		this.field_22231.setPivot(-4.0F, 10.0F, -8.5F);
		this.field_22231.setTextureOffset(46, 75).addCuboid(-3.0F, 0.0F, -3.0F, 6.0F, 14.0F, 6.0F);
		this.field_22232 = new ModelPart(this);
		this.field_22232.setPivot(4.0F, 10.0F, -8.5F);
		this.field_22232.setTextureOffset(71, 75).addCuboid(-3.0F, 0.0F, -3.0F, 6.0F, 14.0F, 6.0F);
		this.field_22233 = new ModelPart(this);
		this.field_22233.setPivot(-5.0F, 13.0F, 10.0F);
		this.field_22233.setTextureOffset(51, 43).addCuboid(-2.5F, 0.0F, -2.5F, 5.0F, 11.0F, 5.0F);
		this.field_22234 = new ModelPart(this);
		this.field_22234.setPivot(5.0F, 13.0F, 10.0F);
		this.field_22234.setTextureOffset(72, 43).addCuboid(-2.5F, 0.0F, -2.5F, 5.0F, 11.0F, 5.0F);
	}

	@Override
	protected Iterable<ModelPart> getHeadParts() {
		return ImmutableList.<ModelPart>of(this.head);
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return ImmutableList.<ModelPart>of(this.torso, this.field_22231, this.field_22232, this.field_22233, this.field_22234);
	}

	public void setAngles(HoglinEntity hoglinEntity, float f, float g, float h, float i, float j) {
		this.rightEar.roll = (float) (-Math.PI * 2.0 / 9.0) - g * MathHelper.sin(f);
		this.leftEar.roll = (float) (Math.PI * 2.0 / 9.0) + g * MathHelper.sin(f);
		this.head.yaw = i * (float) (Math.PI / 180.0);
		float k = 1.0F - (float)MathHelper.abs(10 - 2 * hoglinEntity.getMovementCooldownTicks()) / 10.0F;
		this.head.pitch = MathHelper.lerp(k, 0.87266463F, (float) (-Math.PI / 9));
		float l = 1.2F;
		this.field_22231.pitch = MathHelper.cos(f) * 1.2F * g;
		this.field_22232.pitch = MathHelper.cos(f + (float) Math.PI) * 1.2F * g;
		this.field_22233.pitch = this.field_22232.pitch;
		this.field_22234.pitch = this.field_22231.pitch;
	}
}
