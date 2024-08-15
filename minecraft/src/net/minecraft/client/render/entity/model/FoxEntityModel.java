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
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.state.FoxEntityRenderState;
import net.minecraft.util.math.MathHelper;

/**
 * Represents the model of a {@linkplain FoxEntity}.
 * 
 * <div class="fabric">
 * <table border=1>
 * <caption>Model parts of this model</caption>
 * <tr>
 *   <th>Part Name</th><th>Parent</th><th>Corresponding Field</th>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#HEAD}</td><td>Root part</td><td>{@link #head}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_EAR}</td><td>{@value EntityModelPartNames#HEAD}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_EAR}</td><td>{@value EntityModelPartNames#HEAD}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#NOSE}</td><td>{@value EntityModelPartNames#HEAD}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#BODY}</td><td>Root part</td><td>{@link #body}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#TAIL}</td><td>{@value EntityModelPartNames#BODY}</td><td>{@link #tail}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_HIND_LEG}</td><td>Root part</td><td>{@link #rightHindLeg}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_HIND_LEG}</td><td>Root part</td><td>{@link #leftHindLeg}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_FRONT_LEG}</td><td>Root part</td><td>{@link #rightFrontLeg}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_FRONT_LEG}</td><td>Root part</td><td>{@link #leftFrontLeg}</td>
 * </tr>
 * </table>
 * </div>
 */
@Environment(EnvType.CLIENT)
public class FoxEntityModel extends EntityModel<FoxEntityRenderState> {
	public static final ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(true, 8.0F, 3.35F, Set.of("head"));
	private final ModelPart root;
	public final ModelPart head;
	private final ModelPart body;
	private final ModelPart rightHindLeg;
	private final ModelPart leftHindLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;
	private final ModelPart tail;
	private static final int field_32477 = 6;
	private static final float HEAD_Y_PIVOT = 16.5F;
	private static final float LEG_Y_PIVOT = 17.5F;
	private float legPitchModifier;

	public FoxEntityModel(ModelPart root) {
		this.root = root;
		this.head = root.getChild(EntityModelPartNames.HEAD);
		this.body = root.getChild(EntityModelPartNames.BODY);
		this.rightHindLeg = root.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
		this.leftHindLeg = root.getChild(EntityModelPartNames.LEFT_HIND_LEG);
		this.rightFrontLeg = root.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
		this.leftFrontLeg = root.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
		this.tail = this.body.getChild(EntityModelPartNames.TAIL);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData modelPartData2 = modelPartData.addChild(
			EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(1, 5).cuboid(-3.0F, -2.0F, -5.0F, 8.0F, 6.0F, 6.0F), ModelTransform.pivot(-1.0F, 16.5F, -3.0F)
		);
		modelPartData2.addChild(EntityModelPartNames.RIGHT_EAR, ModelPartBuilder.create().uv(8, 1).cuboid(-3.0F, -4.0F, -4.0F, 2.0F, 2.0F, 1.0F), ModelTransform.NONE);
		modelPartData2.addChild(EntityModelPartNames.LEFT_EAR, ModelPartBuilder.create().uv(15, 1).cuboid(3.0F, -4.0F, -4.0F, 2.0F, 2.0F, 1.0F), ModelTransform.NONE);
		modelPartData2.addChild(EntityModelPartNames.NOSE, ModelPartBuilder.create().uv(6, 18).cuboid(-1.0F, 2.01F, -8.0F, 4.0F, 2.0F, 3.0F), ModelTransform.NONE);
		ModelPartData modelPartData3 = modelPartData.addChild(
			EntityModelPartNames.BODY,
			ModelPartBuilder.create().uv(24, 15).cuboid(-3.0F, 3.999F, -3.5F, 6.0F, 11.0F, 6.0F),
			ModelTransform.of(0.0F, 16.0F, -6.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		Dilation dilation = new Dilation(0.001F);
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(4, 24).cuboid(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, dilation);
		ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().uv(13, 24).cuboid(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, dilation);
		modelPartData.addChild(EntityModelPartNames.RIGHT_HIND_LEG, modelPartBuilder2, ModelTransform.pivot(-5.0F, 17.5F, 7.0F));
		modelPartData.addChild(EntityModelPartNames.LEFT_HIND_LEG, modelPartBuilder, ModelTransform.pivot(-1.0F, 17.5F, 7.0F));
		modelPartData.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, modelPartBuilder2, ModelTransform.pivot(-5.0F, 17.5F, 0.0F));
		modelPartData.addChild(EntityModelPartNames.LEFT_FRONT_LEG, modelPartBuilder, ModelTransform.pivot(-1.0F, 17.5F, 0.0F));
		modelPartData3.addChild(
			EntityModelPartNames.TAIL,
			ModelPartBuilder.create().uv(30, 0).cuboid(2.0F, 0.0F, -1.0F, 4.0F, 9.0F, 5.0F),
			ModelTransform.of(-4.0F, 15.0F, -1.0F, -0.05235988F, 0.0F, 0.0F)
		);
		return TexturedModelData.of(modelData, 48, 32);
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}

	public void setAngles(FoxEntityRenderState foxEntityRenderState) {
		this.body.resetTransform();
		this.head.resetTransform();
		this.tail.resetTransform();
		this.rightHindLeg.resetTransform();
		this.leftHindLeg.resetTransform();
		float f = foxEntityRenderState.limbAmplitudeMultiplier;
		float g = foxEntityRenderState.limbFrequency;
		this.rightHindLeg.pitch = MathHelper.cos(g * 0.6662F) * 1.4F * f;
		this.leftHindLeg.pitch = MathHelper.cos(g * 0.6662F + (float) Math.PI) * 1.4F * f;
		this.rightFrontLeg.pitch = MathHelper.cos(g * 0.6662F + (float) Math.PI) * 1.4F * f;
		this.leftFrontLeg.pitch = MathHelper.cos(g * 0.6662F) * 1.4F * f;
		this.head.roll = foxEntityRenderState.headRoll;
		this.rightHindLeg.visible = true;
		this.leftHindLeg.visible = true;
		this.rightFrontLeg.visible = true;
		this.leftFrontLeg.visible = true;
		float h = foxEntityRenderState.ageScale;
		if (foxEntityRenderState.inSneakingPose) {
			this.body.pitch += 0.10471976F;
			float i = foxEntityRenderState.bodyRotationHeightOffset;
			this.body.pivotY += i * h;
			this.head.pivotY += i * h;
		} else if (foxEntityRenderState.sleeping) {
			this.body.roll = (float) (-Math.PI / 2);
			this.body.pivotY += 5.0F * h;
			this.tail.pitch = (float) (-Math.PI * 5.0 / 6.0);
			if (foxEntityRenderState.baby) {
				this.tail.pitch = -2.1816616F;
				this.body.pivotZ += 2.0F;
			}

			this.head.pivotX += 2.0F * h;
			this.head.pivotY += 2.99F * h;
			this.head.yaw = (float) (-Math.PI * 2.0 / 3.0);
			this.head.roll = 0.0F;
			this.rightHindLeg.visible = false;
			this.leftHindLeg.visible = false;
			this.rightFrontLeg.visible = false;
			this.leftFrontLeg.visible = false;
		} else if (foxEntityRenderState.sitting) {
			this.body.pitch = (float) (Math.PI / 6);
			this.body.pivotY -= 7.0F * h;
			this.body.pivotZ += 3.0F * h;
			this.tail.pitch = (float) (Math.PI / 4);
			this.tail.pivotZ -= 1.0F * h;
			this.head.pitch = 0.0F;
			this.head.yaw = 0.0F;
			if (foxEntityRenderState.baby) {
				this.head.pivotY--;
				this.head.pivotZ -= 0.375F;
			} else {
				this.head.pivotY -= 6.5F;
				this.head.pivotZ += 2.75F;
			}

			this.rightHindLeg.pitch = (float) (-Math.PI * 5.0 / 12.0);
			this.rightHindLeg.pivotY += 4.0F * h;
			this.rightHindLeg.pivotZ -= 0.25F * h;
			this.leftHindLeg.pitch = (float) (-Math.PI * 5.0 / 12.0);
			this.leftHindLeg.pivotY += 4.0F * h;
			this.leftHindLeg.pivotZ -= 0.25F * h;
			this.rightFrontLeg.pitch = (float) (-Math.PI / 12);
			this.leftFrontLeg.pitch = (float) (-Math.PI / 12);
		}

		if (!foxEntityRenderState.sleeping && !foxEntityRenderState.walking && !foxEntityRenderState.inSneakingPose) {
			this.head.pitch = foxEntityRenderState.pitch * (float) (Math.PI / 180.0);
			this.head.yaw = foxEntityRenderState.yawDegrees * (float) (Math.PI / 180.0);
		}

		if (foxEntityRenderState.sleeping) {
			this.head.pitch = 0.0F;
			this.head.yaw = (float) (-Math.PI * 2.0 / 3.0);
			this.head.roll = MathHelper.cos(foxEntityRenderState.age * 0.027F) / 22.0F;
		}

		if (foxEntityRenderState.inSneakingPose) {
			float i = MathHelper.cos(foxEntityRenderState.age) * 0.01F;
			this.body.yaw = i;
			this.rightHindLeg.roll = i;
			this.leftHindLeg.roll = i;
			this.rightFrontLeg.roll = i / 2.0F;
			this.leftFrontLeg.roll = i / 2.0F;
		}

		if (foxEntityRenderState.walking) {
			float i = 0.1F;
			this.legPitchModifier += 0.67F;
			this.rightHindLeg.pitch = MathHelper.cos(this.legPitchModifier * 0.4662F) * 0.1F;
			this.leftHindLeg.pitch = MathHelper.cos(this.legPitchModifier * 0.4662F + (float) Math.PI) * 0.1F;
			this.rightFrontLeg.pitch = MathHelper.cos(this.legPitchModifier * 0.4662F + (float) Math.PI) * 0.1F;
			this.leftFrontLeg.pitch = MathHelper.cos(this.legPitchModifier * 0.4662F) * 0.1F;
		}
	}
}
