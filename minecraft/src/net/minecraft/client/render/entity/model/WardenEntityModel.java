package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.animation.WardenAnimations;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class WardenEntityModel<T extends WardenEntity> extends SinglePartEntityModel<T> {
	private static final float field_38324 = 13.0F;
	private static final float field_38325 = 1.0F;
	private static final Vec3f field_38326 = new Vec3f();
	private final ModelPart root;
	protected final ModelPart bone;
	protected final ModelPart body;
	protected final ModelPart head;
	protected final ModelPart rightTendril;
	protected final ModelPart leftTendril;
	protected final ModelPart leftLeg;
	protected final ModelPart leftArm;
	protected final ModelPart rightArm;
	protected final ModelPart rightLeg;

	public WardenEntityModel(ModelPart root) {
		super(RenderLayer::getEntityCutoutNoCull);
		this.root = root;
		this.bone = root.getChild("bone");
		this.body = this.bone.getChild(EntityModelPartNames.BODY);
		this.head = this.body.getChild(EntityModelPartNames.HEAD);
		this.rightLeg = this.bone.getChild(EntityModelPartNames.RIGHT_LEG);
		this.leftLeg = this.bone.getChild(EntityModelPartNames.LEFT_LEG);
		this.rightArm = this.body.getChild(EntityModelPartNames.RIGHT_ARM);
		this.leftArm = this.body.getChild(EntityModelPartNames.LEFT_ARM);
		this.rightTendril = this.head.getChild("right_tendril");
		this.leftTendril = this.head.getChild("left_tendril");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData modelPartData2 = modelPartData.addChild("bone", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		ModelPartData modelPartData3 = modelPartData2.addChild(
			EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 0).cuboid(-9.0F, -13.0F, -4.0F, 18.0F, 21.0F, 11.0F), ModelTransform.pivot(0.0F, -21.0F, 0.0F)
		);
		ModelPartData modelPartData4 = modelPartData3.addChild(
			EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 32).cuboid(-8.0F, -16.0F, -5.0F, 16.0F, 16.0F, 10.0F), ModelTransform.pivot(0.0F, -13.0F, 0.0F)
		);
		modelPartData4.addChild(
			"right_tendril", ModelPartBuilder.create().uv(52, 32).cuboid(-16.0F, -13.0F, 0.0F, 16.0F, 16.0F, 0.0F), ModelTransform.pivot(-8.0F, -12.0F, 0.0F)
		);
		modelPartData4.addChild(
			"left_tendril", ModelPartBuilder.create().uv(58, 0).cuboid(0.0F, -13.0F, 0.0F, 16.0F, 16.0F, 0.0F), ModelTransform.pivot(8.0F, -12.0F, 0.0F)
		);
		modelPartData3.addChild(
			EntityModelPartNames.RIGHT_ARM,
			ModelPartBuilder.create().uv(44, 50).cuboid(-4.0F, 0.0F, -4.0F, 8.0F, 28.0F, 8.0F),
			ModelTransform.pivot(-13.0F, -13.0F, 1.0F)
		);
		modelPartData3.addChild(
			EntityModelPartNames.LEFT_ARM, ModelPartBuilder.create().uv(0, 58).cuboid(-4.0F, 0.0F, -4.0F, 8.0F, 28.0F, 8.0F), ModelTransform.pivot(13.0F, -13.0F, 1.0F)
		);
		modelPartData2.addChild(
			EntityModelPartNames.RIGHT_LEG,
			ModelPartBuilder.create().uv(76, 48).cuboid(-3.1F, 0.0F, -3.0F, 6.0F, 13.0F, 6.0F),
			ModelTransform.pivot(-5.9F, -13.0F, 0.0F)
		);
		modelPartData2.addChild(
			EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create().uv(76, 76).cuboid(-2.9F, 0.0F, -3.0F, 6.0F, 13.0F, 6.0F), ModelTransform.pivot(5.9F, -13.0F, 0.0F)
		);
		return TexturedModelData.of(modelData, 128, 128);
	}

	public void setAngles(T wardenEntity, float f, float g, float h, float i, float j) {
		this.getPart().traverse().forEach(ModelPart::resetTransform);
		float k = h - (float)wardenEntity.age;
		float l = Math.min(0.5F, 3.0F * g);
		float m = h * 0.1F;
		float n = f * 0.8662F;
		float o = MathHelper.cos(n);
		float p = MathHelper.sin(n);
		float q = MathHelper.cos(m);
		float r = MathHelper.sin(m);
		float s = Math.min(0.35F, l);
		this.head.pitch = j * (float) (Math.PI / 180.0);
		this.head.yaw = i * (float) (Math.PI / 180.0);
		this.head.roll += 0.3F * p * l;
		this.head.roll += 0.06F * q;
		this.head.pitch = this.head.pitch + 1.2F * MathHelper.cos(n + (float) (Math.PI / 2)) * s;
		this.head.pitch += 0.06F * r;
		this.body.roll = 0.1F * p * l;
		this.body.roll += 0.025F * r;
		this.body.pitch = 1.0F * o * s;
		this.body.pitch += 0.025F * q;
		this.leftLeg.pitch = 1.0F * o * l;
		this.rightLeg.pitch = 1.0F * MathHelper.cos(n + (float) Math.PI) * l;
		this.leftArm.pitch = -(0.8F * o * l);
		this.leftArm.roll = 0.0F;
		this.rightArm.pitch = -(0.8F * p * l);
		this.rightArm.roll = 0.0F;
		this.leftArm.yaw = 0.0F;
		this.leftArm.pivotZ = 1.0F;
		this.leftArm.pivotX = 13.0F;
		this.leftArm.pivotY = -13.0F;
		this.rightArm.yaw = 0.0F;
		this.rightArm.pivotZ = 1.0F;
		this.rightArm.pivotX = -13.0F;
		this.rightArm.pivotY = -13.0F;
		float t = wardenEntity.getEarPitch(k) * (float)(Math.cos((double)h * 2.25) * Math.PI * 0.1F);
		this.leftTendril.pitch = t;
		this.rightTendril.pitch = -t;
		long u = Util.getMeasuringTimeMs();
		this.runAnimation(wardenEntity.attackingAnimationState, WardenAnimations.ATTACKING, u);
		this.runAnimation(wardenEntity.diggingAnimationState, WardenAnimations.DIGGING, u);
		this.runAnimation(wardenEntity.emergingAnimationState, WardenAnimations.EMERGING, u);
		this.runAnimation(wardenEntity.roaringAnimationState, WardenAnimations.ROARING, u);
		this.runAnimation(wardenEntity.sniffingAnimationState, WardenAnimations.SNIFFING, u);
	}

	public void runAnimation(AnimationState animationState, Animation animation, long time) {
		animationState.run(state -> AnimationHelper.animate(this, animation, time - state.getStartTime(), 1.0F, field_38326));
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}
}
