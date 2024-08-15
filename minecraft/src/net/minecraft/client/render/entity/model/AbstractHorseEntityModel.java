package net.minecraft.client.render.entity.model;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.entity.state.LivingHorseEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class AbstractHorseEntityModel<T extends LivingHorseEntityRenderState> extends EntityModel<T> {
	private static final float EATING_GRASS_ANIMATION_HEAD_BASE_PITCH = 2.1816616F;
	private static final float ANGRY_ANIMATION_FRONT_LEG_PITCH_MULTIPLIER = (float) (Math.PI / 3);
	private static final float ANGRY_ANIMATION_BODY_PITCH_MULTIPLIER = (float) (Math.PI / 4);
	private static final float HEAD_TAIL_BASE_PITCH = (float) (Math.PI / 6);
	private static final float ANGRY_ANIMATION_HIND_LEG_PITCH_MULTIPLIER = (float) (Math.PI / 12);
	/**
	 * The key of the model part containing the head model parts, whose value is {@value}.
	 */
	protected static final String HEAD_PARTS = "head_parts";
	/**
	 * The key of the saddle mane model part, whose value is {@value}.
	 */
	private static final String SADDLE = "saddle";
	/**
	 * The key of the left saddle mouth model part, whose value is {@value}.
	 */
	private static final String LEFT_SADDLE_MOUTH = "left_saddle_mouth";
	/**
	 * The key of the left saddle line model part, whose value is {@value}.
	 */
	private static final String LEFT_SADDLE_LINE = "left_saddle_line";
	/**
	 * The key of the right saddle mouth model part, whose value is {@value}.
	 */
	private static final String RIGHT_SADDLE_MOUTH = "right_saddle_mouth";
	/**
	 * The key of the right saddle line model part, whose value is {@value}.
	 */
	private static final String RIGHT_SADDLE_LINE = "right_saddle_line";
	/**
	 * The key of the head saddle model part, whose value is {@value}.
	 */
	private static final String HEAD_SADDLE = "head_saddle";
	/**
	 * The key of the mouth saddle wrap mane model part, whose value is {@value}.
	 */
	private static final String MOUTH_SADDLE_WRAP = "mouth_saddle_wrap";
	protected static final ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(true, 16.2F, 1.36F, 2.7272F, 2.0F, 20.0F, Set.of("head_parts"));
	private final ModelPart root;
	protected final ModelPart body;
	protected final ModelPart head;
	private final ModelPart rightHindLeg;
	private final ModelPart leftHindLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;
	private final ModelPart tail;
	private final ModelPart[] saddle;
	private final ModelPart[] straps;

	public AbstractHorseEntityModel(ModelPart root) {
		this.root = root;
		this.body = root.getChild(EntityModelPartNames.BODY);
		this.head = root.getChild("head_parts");
		this.rightHindLeg = root.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
		this.leftHindLeg = root.getChild(EntityModelPartNames.LEFT_HIND_LEG);
		this.rightFrontLeg = root.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
		this.leftFrontLeg = root.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
		this.tail = this.body.getChild(EntityModelPartNames.TAIL);
		ModelPart modelPart = this.body.getChild("saddle");
		ModelPart modelPart2 = this.head.getChild("left_saddle_mouth");
		ModelPart modelPart3 = this.head.getChild("right_saddle_mouth");
		ModelPart modelPart4 = this.head.getChild("left_saddle_line");
		ModelPart modelPart5 = this.head.getChild("right_saddle_line");
		ModelPart modelPart6 = this.head.getChild("head_saddle");
		ModelPart modelPart7 = this.head.getChild("mouth_saddle_wrap");
		this.saddle = new ModelPart[]{modelPart, modelPart2, modelPart3, modelPart6, modelPart7};
		this.straps = new ModelPart[]{modelPart4, modelPart5};
	}

	public static ModelData getModelData(Dilation dilation) {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData modelPartData2 = modelPartData.addChild(
			EntityModelPartNames.BODY,
			ModelPartBuilder.create().uv(0, 32).cuboid(-5.0F, -8.0F, -17.0F, 10.0F, 10.0F, 22.0F, new Dilation(0.05F)),
			ModelTransform.pivot(0.0F, 11.0F, 5.0F)
		);
		ModelPartData modelPartData3 = modelPartData.addChild(
			"head_parts",
			ModelPartBuilder.create().uv(0, 35).cuboid(-2.05F, -6.0F, -2.0F, 4.0F, 12.0F, 7.0F),
			ModelTransform.of(0.0F, 4.0F, -12.0F, (float) (Math.PI / 6), 0.0F, 0.0F)
		);
		ModelPartData modelPartData4 = modelPartData3.addChild(
			EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 13).cuboid(-3.0F, -11.0F, -2.0F, 6.0F, 5.0F, 7.0F, dilation), ModelTransform.NONE
		);
		modelPartData3.addChild(
			EntityModelPartNames.MANE, ModelPartBuilder.create().uv(56, 36).cuboid(-1.0F, -11.0F, 5.01F, 2.0F, 16.0F, 2.0F, dilation), ModelTransform.NONE
		);
		modelPartData3.addChild("upper_mouth", ModelPartBuilder.create().uv(0, 25).cuboid(-2.0F, -11.0F, -7.0F, 4.0F, 5.0F, 5.0F, dilation), ModelTransform.NONE);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_HIND_LEG,
			ModelPartBuilder.create().uv(48, 21).mirrored().cuboid(-3.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, dilation),
			ModelTransform.pivot(4.0F, 14.0F, 7.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_HIND_LEG,
			ModelPartBuilder.create().uv(48, 21).cuboid(-1.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, dilation),
			ModelTransform.pivot(-4.0F, 14.0F, 7.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_FRONT_LEG,
			ModelPartBuilder.create().uv(48, 21).mirrored().cuboid(-3.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, dilation),
			ModelTransform.pivot(4.0F, 14.0F, -10.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_FRONT_LEG,
			ModelPartBuilder.create().uv(48, 21).cuboid(-1.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, dilation),
			ModelTransform.pivot(-4.0F, 14.0F, -10.0F)
		);
		modelPartData2.addChild(
			EntityModelPartNames.TAIL,
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
			ModelPartBuilder.create().uv(32, 2).cuboid(3.1F, -6.0F, -8.0F, 0.0F, 3.0F, 16.0F),
			ModelTransform.rotation((float) (-Math.PI / 6), 0.0F, 0.0F)
		);
		modelPartData3.addChild(
			"right_saddle_line",
			ModelPartBuilder.create().uv(32, 2).cuboid(-3.1F, -6.0F, -8.0F, 0.0F, 3.0F, 16.0F),
			ModelTransform.rotation((float) (-Math.PI / 6), 0.0F, 0.0F)
		);
		modelPartData3.addChild(
			"head_saddle", ModelPartBuilder.create().uv(1, 1).cuboid(-3.0F, -11.0F, -1.9F, 6.0F, 5.0F, 6.0F, new Dilation(0.22F)), ModelTransform.NONE
		);
		modelPartData3.addChild(
			"mouth_saddle_wrap", ModelPartBuilder.create().uv(19, 0).cuboid(-2.0F, -11.0F, -4.0F, 4.0F, 5.0F, 2.0F, new Dilation(0.2F)), ModelTransform.NONE
		);
		modelPartData4.addChild(
			EntityModelPartNames.LEFT_EAR,
			ModelPartBuilder.create().uv(19, 16).cuboid(0.55F, -13.0F, 4.0F, 2.0F, 3.0F, 1.0F, new Dilation(-0.001F)),
			ModelTransform.NONE
		);
		modelPartData4.addChild(
			EntityModelPartNames.RIGHT_EAR,
			ModelPartBuilder.create().uv(19, 16).cuboid(-2.55F, -13.0F, 4.0F, 2.0F, 3.0F, 1.0F, new Dilation(-0.001F)),
			ModelTransform.NONE
		);
		return modelData;
	}

	public static ModelData getBabyHorseModelData(Dilation dilation) {
		return BABY_TRANSFORMER.apply(getBabyModelData(dilation));
	}

	protected static ModelData getBabyModelData(Dilation dilation) {
		ModelData modelData = getModelData(dilation);
		ModelPartData modelPartData = modelData.getRoot();
		Dilation dilation2 = dilation.add(0.0F, 5.5F, 0.0F);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_HIND_LEG,
			ModelPartBuilder.create().uv(48, 21).mirrored().cuboid(-3.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, dilation2),
			ModelTransform.pivot(4.0F, 14.0F, 7.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_HIND_LEG,
			ModelPartBuilder.create().uv(48, 21).cuboid(-1.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, dilation2),
			ModelTransform.pivot(-4.0F, 14.0F, 7.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_FRONT_LEG,
			ModelPartBuilder.create().uv(48, 21).mirrored().cuboid(-3.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, dilation2),
			ModelTransform.pivot(4.0F, 14.0F, -10.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_FRONT_LEG,
			ModelPartBuilder.create().uv(48, 21).cuboid(-1.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, dilation2),
			ModelTransform.pivot(-4.0F, 14.0F, -10.0F)
		);
		return modelData;
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}

	public void setAngles(T livingHorseEntityRenderState) {
		boolean bl = livingHorseEntityRenderState.saddled;
		boolean bl2 = livingHorseEntityRenderState.hasPassengers;

		for (ModelPart modelPart : this.saddle) {
			modelPart.visible = bl;
		}

		for (ModelPart modelPart : this.straps) {
			modelPart.visible = bl2 && bl;
		}

		float f = MathHelper.clamp(livingHorseEntityRenderState.yawDegrees, -20.0F, 20.0F);
		float g = livingHorseEntityRenderState.pitch * (float) (Math.PI / 180.0);
		float h = livingHorseEntityRenderState.limbAmplitudeMultiplier;
		float i = livingHorseEntityRenderState.limbFrequency;
		if (h > 0.2F) {
			g += MathHelper.cos(i * 0.8F) * 0.15F * h;
		}

		float j = livingHorseEntityRenderState.eatingGrassAnimationProgress;
		float k = livingHorseEntityRenderState.angryAnimationProgress;
		float l = 1.0F - k;
		float m = livingHorseEntityRenderState.eatingAnimationProgress;
		boolean bl3 = livingHorseEntityRenderState.waggingTail;
		this.head.resetTransform();
		this.body.pitch = 0.0F;
		this.head.pitch = (float) (Math.PI / 6) + g;
		this.head.yaw = f * (float) (Math.PI / 180.0);
		float n = livingHorseEntityRenderState.touchingWater ? 0.2F : 1.0F;
		float o = MathHelper.cos(n * i * 0.6662F + (float) Math.PI);
		float p = o * 0.8F * h;
		float q = (1.0F - Math.max(k, j)) * ((float) (Math.PI / 6) + g + m * MathHelper.sin(livingHorseEntityRenderState.age) * 0.05F);
		this.head.pitch = k * ((float) (Math.PI / 12) + g) + j * (2.1816616F + MathHelper.sin(livingHorseEntityRenderState.age) * 0.05F) + q;
		this.head.yaw = k * f * (float) (Math.PI / 180.0) + (1.0F - Math.max(k, j)) * this.head.yaw;
		float r = livingHorseEntityRenderState.ageScale;
		this.head.pivotY = this.head.pivotY + MathHelper.lerp(j, MathHelper.lerp(k, 0.0F, -8.0F * r), 7.0F * r);
		this.head.pivotZ = MathHelper.lerp(k, this.head.pivotZ, -4.0F * r);
		this.body.pitch = k * (float) (-Math.PI / 4) + l * this.body.pitch;
		float s = (float) (Math.PI / 12) * k;
		float t = MathHelper.cos(livingHorseEntityRenderState.age * 0.6F + (float) Math.PI);
		this.leftFrontLeg.resetTransform();
		this.leftFrontLeg.pivotY -= 12.0F * r * k;
		this.leftFrontLeg.pivotZ += 4.0F * r * k;
		this.rightFrontLeg.resetTransform();
		this.rightFrontLeg.pivotY = this.leftFrontLeg.pivotY;
		this.rightFrontLeg.pivotZ = this.leftFrontLeg.pivotZ;
		float u = ((float) (-Math.PI / 3) + t) * k + p * l;
		float v = ((float) (-Math.PI / 3) - t) * k - p * l;
		this.leftHindLeg.pitch = s - o * 0.5F * h * l;
		this.rightHindLeg.pitch = s + o * 0.5F * h * l;
		this.leftFrontLeg.pitch = u;
		this.rightFrontLeg.pitch = v;
		this.tail.resetTransform();
		this.tail.pitch = (float) (Math.PI / 6) + h * 0.75F;
		this.tail.pivotY += h * r;
		this.tail.pivotZ += h * 2.0F * r;
		if (bl3) {
			this.tail.yaw = MathHelper.cos(livingHorseEntityRenderState.age * 0.7F);
		} else {
			this.tail.yaw = 0.0F;
		}
	}
}
