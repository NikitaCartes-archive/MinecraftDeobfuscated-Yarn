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
import net.minecraft.client.render.entity.state.FelineEntityRenderState;
import net.minecraft.util.math.MathHelper;

/**
 * Represents the model of an ocelot-like entity.
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
 *   <td>{@value EntityModelPartNames#BODY}</td><td>Root part</td><td>{@link #body}</td>
 * </tr>
 * <tr>
 *   <td>{@value #TAIL1}</td><td>Root part</td><td>{@link #upperTail}</td>
 * </tr>
 * <tr>
 *   <td>{@value #TAIL2}</td><td>Root part</td><td>{@link #lowerTail}</td>
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
public class FelineEntityModel<T extends FelineEntityRenderState> extends EntityModel<T> {
	public static final ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(true, 10.0F, 4.0F, Set.of("head"));
	public static final ModelTransformer field_52910 = ModelTransformer.scaling(0.8F);
	private static final float field_32527 = 0.0F;
	private static final float BODY_SIZE_Y = 16.0F;
	private static final float field_32529 = -9.0F;
	protected static final float HIND_LEG_PIVOT_Y = 18.0F;
	protected static final float HIND_LEG_PIVOT_Z = 5.0F;
	protected static final float FRONT_LEG_PIVOT_Y = 14.1F;
	private static final float FRONT_LEG_PIVOT_Z = -5.0F;
	/**
	 * The key of the upper tail model part, whose value is {@value}.
	 */
	private static final String TAIL1 = "tail1";
	/**
	 * The key of the lower tail model part, whose value is {@value}.
	 */
	private static final String TAIL2 = "tail2";
	protected final ModelPart leftHindLeg;
	protected final ModelPart rightHindLeg;
	protected final ModelPart leftFrontLeg;
	protected final ModelPart rightFrontLeg;
	protected final ModelPart upperTail;
	protected final ModelPart lowerTail;
	protected final ModelPart head;
	protected final ModelPart body;

	public FelineEntityModel(ModelPart modelPart) {
		super(modelPart);
		this.head = modelPart.getChild(EntityModelPartNames.HEAD);
		this.body = modelPart.getChild(EntityModelPartNames.BODY);
		this.upperTail = modelPart.getChild("tail1");
		this.lowerTail = modelPart.getChild("tail2");
		this.leftHindLeg = modelPart.getChild(EntityModelPartNames.LEFT_HIND_LEG);
		this.rightHindLeg = modelPart.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
		this.leftFrontLeg = modelPart.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
		this.rightFrontLeg = modelPart.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
	}

	public static ModelData getModelData(Dilation dilation) {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		Dilation dilation2 = new Dilation(-0.02F);
		modelPartData.addChild(
			EntityModelPartNames.HEAD,
			ModelPartBuilder.create()
				.cuboid("main", -2.5F, -2.0F, -3.0F, 5.0F, 4.0F, 5.0F, dilation)
				.cuboid(EntityModelPartNames.NOSE, -1.5F, -0.001F, -4.0F, 3, 2, 2, dilation, 0, 24)
				.cuboid("ear1", -2.0F, -3.0F, 0.0F, 1, 1, 2, dilation, 0, 10)
				.cuboid("ear2", 1.0F, -3.0F, 0.0F, 1, 1, 2, dilation, 6, 10),
			ModelTransform.pivot(0.0F, 15.0F, -9.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.BODY,
			ModelPartBuilder.create().uv(20, 0).cuboid(-2.0F, 3.0F, -8.0F, 4.0F, 16.0F, 6.0F, dilation),
			ModelTransform.of(0.0F, 12.0F, -10.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		modelPartData.addChild(
			"tail1", ModelPartBuilder.create().uv(0, 15).cuboid(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F, dilation), ModelTransform.of(0.0F, 15.0F, 8.0F, 0.9F, 0.0F, 0.0F)
		);
		modelPartData.addChild(
			"tail2", ModelPartBuilder.create().uv(4, 15).cuboid(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F, dilation2), ModelTransform.pivot(0.0F, 20.0F, 14.0F)
		);
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(8, 13).cuboid(-1.0F, 0.0F, 1.0F, 2.0F, 6.0F, 2.0F, dilation);
		modelPartData.addChild(EntityModelPartNames.LEFT_HIND_LEG, modelPartBuilder, ModelTransform.pivot(1.1F, 18.0F, 5.0F));
		modelPartData.addChild(EntityModelPartNames.RIGHT_HIND_LEG, modelPartBuilder, ModelTransform.pivot(-1.1F, 18.0F, 5.0F));
		ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().uv(40, 0).cuboid(-1.0F, 0.0F, 0.0F, 2.0F, 10.0F, 2.0F, dilation);
		modelPartData.addChild(EntityModelPartNames.LEFT_FRONT_LEG, modelPartBuilder2, ModelTransform.pivot(1.2F, 14.1F, -5.0F));
		modelPartData.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, modelPartBuilder2, ModelTransform.pivot(-1.2F, 14.1F, -5.0F));
		return modelData;
	}

	public void setAngles(T felineEntityRenderState) {
		super.setAngles(felineEntityRenderState);
		if (felineEntityRenderState.inSneakingPose) {
			this.body.pivotY++;
			this.head.pivotY += 2.0F;
			this.upperTail.pivotY++;
			this.lowerTail.pivotY += -4.0F;
			this.lowerTail.pivotZ += 2.0F;
			this.upperTail.pitch = (float) (Math.PI / 2);
			this.lowerTail.pitch = (float) (Math.PI / 2);
		} else if (felineEntityRenderState.sprinting) {
			this.lowerTail.pivotY = this.upperTail.pivotY;
			this.lowerTail.pivotZ += 2.0F;
			this.upperTail.pitch = (float) (Math.PI / 2);
			this.lowerTail.pitch = (float) (Math.PI / 2);
		}

		this.head.pitch = felineEntityRenderState.pitch * (float) (Math.PI / 180.0);
		this.head.yaw = felineEntityRenderState.yawDegrees * (float) (Math.PI / 180.0);
		if (!felineEntityRenderState.inSittingPose) {
			this.body.pitch = (float) (Math.PI / 2);
			float f = felineEntityRenderState.limbAmplitudeMultiplier;
			float g = felineEntityRenderState.limbFrequency;
			if (felineEntityRenderState.sprinting) {
				this.leftHindLeg.pitch = MathHelper.cos(g * 0.6662F) * f;
				this.rightHindLeg.pitch = MathHelper.cos(g * 0.6662F + 0.3F) * f;
				this.leftFrontLeg.pitch = MathHelper.cos(g * 0.6662F + (float) Math.PI + 0.3F) * f;
				this.rightFrontLeg.pitch = MathHelper.cos(g * 0.6662F + (float) Math.PI) * f;
				this.lowerTail.pitch = 1.7278761F + (float) (Math.PI / 10) * MathHelper.cos(g) * f;
			} else {
				this.leftHindLeg.pitch = MathHelper.cos(g * 0.6662F) * f;
				this.rightHindLeg.pitch = MathHelper.cos(g * 0.6662F + (float) Math.PI) * f;
				this.leftFrontLeg.pitch = MathHelper.cos(g * 0.6662F + (float) Math.PI) * f;
				this.rightFrontLeg.pitch = MathHelper.cos(g * 0.6662F) * f;
				if (!felineEntityRenderState.inSneakingPose) {
					this.lowerTail.pitch = 1.7278761F + (float) (Math.PI / 4) * MathHelper.cos(g) * f;
				} else {
					this.lowerTail.pitch = 1.7278761F + 0.47123894F * MathHelper.cos(g) * f;
				}
			}
		}

		float f = felineEntityRenderState.ageScale;
		if (felineEntityRenderState.inSittingPose) {
			this.body.pitch = (float) (Math.PI / 4);
			this.body.pivotY += -4.0F * f;
			this.body.pivotZ += 5.0F * f;
			this.head.pivotY += -3.3F * f;
			this.head.pivotZ += 1.0F * f;
			this.upperTail.pivotY += 8.0F * f;
			this.upperTail.pivotZ += -2.0F * f;
			this.lowerTail.pivotY += 2.0F * f;
			this.lowerTail.pivotZ += -0.8F * f;
			this.upperTail.pitch = 1.7278761F;
			this.lowerTail.pitch = 2.670354F;
			this.leftFrontLeg.pitch = (float) (-Math.PI / 20);
			this.leftFrontLeg.pivotY += 2.0F * f;
			this.leftFrontLeg.pivotZ -= 2.0F * f;
			this.rightFrontLeg.pitch = (float) (-Math.PI / 20);
			this.rightFrontLeg.pivotY += 2.0F * f;
			this.rightFrontLeg.pivotZ -= 2.0F * f;
			this.leftHindLeg.pitch = (float) (-Math.PI / 2);
			this.leftHindLeg.pivotY += 3.0F * f;
			this.leftHindLeg.pivotZ -= 4.0F * f;
			this.rightHindLeg.pitch = (float) (-Math.PI / 2);
			this.rightHindLeg.pivotY += 3.0F * f;
			this.rightHindLeg.pivotZ -= 4.0F * f;
		}

		if (felineEntityRenderState.sleepAnimationProgress > 0.0F) {
			this.head.roll = MathHelper.lerpAngleDegrees(felineEntityRenderState.sleepAnimationProgress, this.head.roll, -1.2707963F);
			this.head.yaw = MathHelper.lerpAngleDegrees(felineEntityRenderState.sleepAnimationProgress, this.head.yaw, 1.2707963F);
			this.leftFrontLeg.pitch = -1.2707963F;
			this.rightFrontLeg.pitch = -0.47079635F;
			this.rightFrontLeg.roll = -0.2F;
			this.rightFrontLeg.pivotX += f;
			this.leftHindLeg.pitch = -0.4F;
			this.rightHindLeg.pitch = 0.5F;
			this.rightHindLeg.roll = -0.5F;
			this.rightHindLeg.pivotX += 0.8F * f;
			this.rightHindLeg.pivotY += 2.0F * f;
			this.upperTail.pitch = MathHelper.lerpAngleDegrees(felineEntityRenderState.tailCurlAnimationProgress, this.upperTail.pitch, 0.8F);
			this.lowerTail.pitch = MathHelper.lerpAngleDegrees(felineEntityRenderState.tailCurlAnimationProgress, this.lowerTail.pitch, -0.4F);
		}

		if (felineEntityRenderState.headDownAnimationProgress > 0.0F) {
			this.head.pitch = MathHelper.lerpAngleDegrees(felineEntityRenderState.headDownAnimationProgress, this.head.pitch, -0.58177644F);
		}
	}
}
