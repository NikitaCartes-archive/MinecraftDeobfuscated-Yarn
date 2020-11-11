package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.util.math.Dilation;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class HorseEntityModel<T extends HorseBaseEntity> extends AnimalModel<T> {
	protected final ModelPart torso;
	protected final ModelPart head;
	private final ModelPart field_27425;
	private final ModelPart field_27426;
	private final ModelPart field_27427;
	private final ModelPart field_27428;
	private final ModelPart field_27429;
	private final ModelPart field_27430;
	private final ModelPart field_27431;
	private final ModelPart field_27432;
	private final ModelPart tail;
	private final ModelPart[] field_3304;
	private final ModelPart[] field_3301;

	public HorseEntityModel(ModelPart modelPart) {
		super(true, 16.2F, 1.36F, 2.7272F, 2.0F, 20.0F);
		this.torso = modelPart.getChild("body");
		this.head = modelPart.getChild("head_parts");
		this.field_27425 = modelPart.getChild("right_hind_leg");
		this.field_27426 = modelPart.getChild("left_hind_leg");
		this.field_27427 = modelPart.getChild("right_front_leg");
		this.field_27428 = modelPart.getChild("left_front_leg");
		this.field_27429 = modelPart.getChild("right_hind_baby_leg");
		this.field_27430 = modelPart.getChild("left_hind_baby_leg");
		this.field_27431 = modelPart.getChild("right_front_baby_leg");
		this.field_27432 = modelPart.getChild("left_front_baby_leg");
		this.tail = this.torso.getChild("tail");
		ModelPart modelPart2 = this.torso.getChild("saddle");
		ModelPart modelPart3 = this.head.getChild("left_saddle_mouth");
		ModelPart modelPart4 = this.head.getChild("right_saddle_mouth");
		ModelPart modelPart5 = this.head.getChild("left_saddle_line");
		ModelPart modelPart6 = this.head.getChild("right_saddle_line");
		ModelPart modelPart7 = this.head.getChild("head_saddle");
		ModelPart modelPart8 = this.head.getChild("mouth_saddle_wrap");
		this.field_3304 = new ModelPart[]{modelPart2, modelPart3, modelPart4, modelPart7, modelPart8};
		this.field_3301 = new ModelPart[]{modelPart5, modelPart6};
	}

	public static ModelData getModelData(Dilation dilation) {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData modelPartData2 = modelPartData.addChild(
			"body", ModelPartBuilder.create().uv(0, 32).cuboid(-5.0F, -8.0F, -17.0F, 10.0F, 10.0F, 22.0F, new Dilation(0.05F)), ModelTransform.pivot(0.0F, 11.0F, 5.0F)
		);
		ModelPartData modelPartData3 = modelPartData.addChild(
			"head_parts",
			ModelPartBuilder.create().uv(0, 35).cuboid(-2.05F, -6.0F, -2.0F, 4.0F, 12.0F, 7.0F),
			ModelTransform.of(0.0F, 4.0F, -12.0F, (float) (Math.PI / 6), 0.0F, 0.0F)
		);
		ModelPartData modelPartData4 = modelPartData3.addChild(
			"head", ModelPartBuilder.create().uv(0, 13).cuboid(-3.0F, -11.0F, -2.0F, 6.0F, 5.0F, 7.0F, dilation), ModelTransform.NONE
		);
		modelPartData3.addChild("mane", ModelPartBuilder.create().uv(56, 36).cuboid(-1.0F, -11.0F, 5.01F, 2.0F, 16.0F, 2.0F, dilation), ModelTransform.NONE);
		modelPartData3.addChild("upper_mouth", ModelPartBuilder.create().uv(0, 25).cuboid(-2.0F, -11.0F, -7.0F, 4.0F, 5.0F, 5.0F, dilation), ModelTransform.NONE);
		modelPartData.addChild(
			"left_hind_leg",
			ModelPartBuilder.create().uv(48, 21).mirrored().cuboid(-3.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, dilation),
			ModelTransform.pivot(4.0F, 14.0F, 7.0F)
		);
		modelPartData.addChild(
			"right_hind_leg", ModelPartBuilder.create().uv(48, 21).cuboid(-1.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, dilation), ModelTransform.pivot(-4.0F, 14.0F, 7.0F)
		);
		modelPartData.addChild(
			"left_front_leg",
			ModelPartBuilder.create().uv(48, 21).mirrored().cuboid(-3.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, dilation),
			ModelTransform.pivot(4.0F, 14.0F, -12.0F)
		);
		modelPartData.addChild(
			"right_front_leg",
			ModelPartBuilder.create().uv(48, 21).cuboid(-1.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, dilation),
			ModelTransform.pivot(-4.0F, 14.0F, -12.0F)
		);
		Dilation dilation2 = dilation.add(0.0F, 5.5F, 0.0F);
		modelPartData.addChild(
			"left_hind_baby_leg",
			ModelPartBuilder.create().uv(48, 21).mirrored().cuboid(-3.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, dilation2),
			ModelTransform.pivot(4.0F, 14.0F, 7.0F)
		);
		modelPartData.addChild(
			"right_hind_baby_leg",
			ModelPartBuilder.create().uv(48, 21).cuboid(-1.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, dilation2),
			ModelTransform.pivot(-4.0F, 14.0F, 7.0F)
		);
		modelPartData.addChild(
			"left_front_baby_leg",
			ModelPartBuilder.create().uv(48, 21).mirrored().cuboid(-3.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, dilation2),
			ModelTransform.pivot(4.0F, 14.0F, -12.0F)
		);
		modelPartData.addChild(
			"right_front_baby_leg",
			ModelPartBuilder.create().uv(48, 21).cuboid(-1.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, dilation2),
			ModelTransform.pivot(-4.0F, 14.0F, -12.0F)
		);
		modelPartData2.addChild(
			"tail",
			ModelPartBuilder.create().uv(42, 36).cuboid(-1.5F, 0.0F, 0.0F, 3.0F, 14.0F, 4.0F, dilation),
			ModelTransform.of(0.0F, -5.0F, 2.0F, (float) (Math.PI / 6), 0.0F, 0.0F)
		);
		modelPartData2.addChild("saddle", ModelPartBuilder.create().uv(26, 0).cuboid(-5.0F, -8.0F, -9.0F, 10.0F, 9.0F, 9.0F, new Dilation(0.5F)), ModelTransform.NONE);
		modelPartData3.addChild("left_saddle_mouth", ModelPartBuilder.create().uv(29, 5).cuboid(2.0F, -9.0F, -6.0F, 1.0F, 2.0F, 2.0F, dilation), ModelTransform.NONE);
		modelPartData3.addChild(
			"right_saddle_mouth", ModelPartBuilder.create().uv(29, 5).cuboid(-3.0F, -9.0F, -6.0F, 1.0F, 2.0F, 2.0F, dilation), ModelTransform.NONE
		);
		modelPartData3.addChild(
			"left_saddle_line",
			ModelPartBuilder.create().uv(32, 2).cuboid(3.1F, -6.0F, -8.0F, 0.0F, 3.0F, 16.0F, dilation),
			ModelTransform.rotation((float) (-Math.PI / 6), 0.0F, 0.0F)
		);
		modelPartData3.addChild(
			"right_saddle_line",
			ModelPartBuilder.create().uv(32, 2).cuboid(-3.1F, -6.0F, -8.0F, 0.0F, 3.0F, 16.0F, dilation),
			ModelTransform.rotation((float) (-Math.PI / 6), 0.0F, 0.0F)
		);
		modelPartData3.addChild(
			"head_saddle", ModelPartBuilder.create().uv(1, 1).cuboid(-3.0F, -11.0F, -1.9F, 6.0F, 5.0F, 6.0F, new Dilation(0.2F)), ModelTransform.NONE
		);
		modelPartData3.addChild(
			"mouth_saddle_wrap", ModelPartBuilder.create().uv(19, 0).cuboid(-2.0F, -11.0F, -4.0F, 4.0F, 5.0F, 2.0F, new Dilation(0.2F)), ModelTransform.NONE
		);
		modelPartData4.addChild(
			"left_ear", ModelPartBuilder.create().uv(19, 16).cuboid(0.55F, -13.0F, 4.0F, 2.0F, 3.0F, 1.0F, new Dilation(-0.001F)), ModelTransform.NONE
		);
		modelPartData4.addChild(
			"right_ear", ModelPartBuilder.create().uv(19, 16).cuboid(-2.55F, -13.0F, 4.0F, 2.0F, 3.0F, 1.0F, new Dilation(-0.001F)), ModelTransform.NONE
		);
		return modelData;
	}

	public void setAngles(T horseBaseEntity, float f, float g, float h, float i, float j) {
		boolean bl = horseBaseEntity.isSaddled();
		boolean bl2 = horseBaseEntity.hasPassengers();

		for (ModelPart modelPart : this.field_3304) {
			modelPart.visible = bl;
		}

		for (ModelPart modelPart : this.field_3301) {
			modelPart.visible = bl2 && bl;
		}

		this.torso.pivotY = 11.0F;
	}

	@Override
	public Iterable<ModelPart> getHeadParts() {
		return ImmutableList.<ModelPart>of(this.head);
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return ImmutableList.<ModelPart>of(
			this.torso, this.field_27425, this.field_27426, this.field_27427, this.field_27428, this.field_27429, this.field_27430, this.field_27431, this.field_27432
		);
	}

	public void animateModel(T horseBaseEntity, float f, float g, float h) {
		super.animateModel(horseBaseEntity, f, g, h);
		float i = MathHelper.lerpAngle(horseBaseEntity.prevBodyYaw, horseBaseEntity.bodyYaw, h);
		float j = MathHelper.lerpAngle(horseBaseEntity.prevHeadYaw, horseBaseEntity.headYaw, h);
		float k = MathHelper.lerp(h, horseBaseEntity.prevPitch, horseBaseEntity.pitch);
		float l = j - i;
		float m = k * (float) (Math.PI / 180.0);
		if (l > 20.0F) {
			l = 20.0F;
		}

		if (l < -20.0F) {
			l = -20.0F;
		}

		if (g > 0.2F) {
			m += MathHelper.cos(f * 0.4F) * 0.15F * g;
		}

		float n = horseBaseEntity.getEatingGrassAnimationProgress(h);
		float o = horseBaseEntity.getAngryAnimationProgress(h);
		float p = 1.0F - o;
		float q = horseBaseEntity.getEatingAnimationProgress(h);
		boolean bl = horseBaseEntity.tailWagTicks != 0;
		float r = (float)horseBaseEntity.age + h;
		this.head.pivotY = 4.0F;
		this.head.pivotZ = -12.0F;
		this.torso.pitch = 0.0F;
		this.head.pitch = (float) (Math.PI / 6) + m;
		this.head.yaw = l * (float) (Math.PI / 180.0);
		float s = horseBaseEntity.isTouchingWater() ? 0.2F : 1.0F;
		float t = MathHelper.cos(s * f * 0.6662F + (float) Math.PI);
		float u = t * 0.8F * g;
		float v = (1.0F - Math.max(o, n)) * ((float) (Math.PI / 6) + m + q * MathHelper.sin(r) * 0.05F);
		this.head.pitch = o * ((float) (Math.PI / 12) + m) + n * (2.1816616F + MathHelper.sin(r) * 0.05F) + v;
		this.head.yaw = o * l * (float) (Math.PI / 180.0) + (1.0F - Math.max(o, n)) * this.head.yaw;
		this.head.pivotY = o * -4.0F + n * 11.0F + (1.0F - Math.max(o, n)) * this.head.pivotY;
		this.head.pivotZ = o * -4.0F + n * -12.0F + (1.0F - Math.max(o, n)) * this.head.pivotZ;
		this.torso.pitch = o * (float) (-Math.PI / 4) + p * this.torso.pitch;
		float w = (float) (Math.PI / 12) * o;
		float x = MathHelper.cos(r * 0.6F + (float) Math.PI);
		this.field_27428.pivotY = 2.0F * o + 14.0F * p;
		this.field_27428.pivotZ = -6.0F * o - 10.0F * p;
		this.field_27427.pivotY = this.field_27428.pivotY;
		this.field_27427.pivotZ = this.field_27428.pivotZ;
		float y = ((float) (-Math.PI / 3) + x) * o + u * p;
		float z = ((float) (-Math.PI / 3) - x) * o - u * p;
		this.field_27426.pitch = w - t * 0.5F * g * p;
		this.field_27425.pitch = w + t * 0.5F * g * p;
		this.field_27428.pitch = y;
		this.field_27427.pitch = z;
		this.tail.pitch = (float) (Math.PI / 6) + g * 0.75F;
		this.tail.pivotY = -5.0F + g;
		this.tail.pivotZ = 2.0F + g * 2.0F;
		if (bl) {
			this.tail.yaw = MathHelper.cos(r * 0.7F);
		} else {
			this.tail.yaw = 0.0F;
		}

		this.field_27429.pivotY = this.field_27425.pivotY;
		this.field_27429.pivotZ = this.field_27425.pivotZ;
		this.field_27429.pitch = this.field_27425.pitch;
		this.field_27430.pivotY = this.field_27426.pivotY;
		this.field_27430.pivotZ = this.field_27426.pivotZ;
		this.field_27430.pitch = this.field_27426.pitch;
		this.field_27431.pivotY = this.field_27427.pivotY;
		this.field_27431.pivotZ = this.field_27427.pivotZ;
		this.field_27431.pitch = this.field_27427.pitch;
		this.field_27432.pivotY = this.field_27428.pivotY;
		this.field_27432.pivotZ = this.field_27428.pivotZ;
		this.field_27432.pitch = this.field_27428.pitch;
		boolean bl2 = horseBaseEntity.isBaby();
		this.field_27425.visible = !bl2;
		this.field_27426.visible = !bl2;
		this.field_27427.visible = !bl2;
		this.field_27428.visible = !bl2;
		this.field_27429.visible = bl2;
		this.field_27430.visible = bl2;
		this.field_27431.visible = bl2;
		this.field_27432.visible = bl2;
		this.torso.pivotY = bl2 ? 10.8F : 0.0F;
	}
}
