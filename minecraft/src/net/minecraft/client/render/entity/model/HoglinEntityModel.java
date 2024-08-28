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
import net.minecraft.client.render.entity.state.HoglinEntityRenderState;
import net.minecraft.util.math.MathHelper;

/**
 * Represents the model of a hoglin-like entity.
 * 
 * <div class="fabric">
 * <table border=1>
 * <caption>Model parts of this model</caption>
 * <tr>
 *   <th>Part Name</th><th>Parent</th><th>Corresponding Field</th>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#BODY}</td><td>Root part</td><td>{@link #body}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#MANE}</td><td>{@value EntityModelPartNames#BODY}</td><td>{@link #mane}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#HEAD}</td><td>Root part</td><td>{@link #head}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_EAR}</td><td>{@value EntityModelPartNames#HEAD}</td><td>{@link #rightEar}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_EAR}</td><td>{@value EntityModelPartNames#HEAD}</td><td>{@link #leftEar}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_HORN}</td><td>{@value EntityModelPartNames#HEAD}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_HORN}</td><td>{@value EntityModelPartNames#HEAD}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_FRONT_LEG}</td><td>Root part</td><td>{@link #rightFrontLeg}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_FRONT_LEG}</td><td>Root part</td><td>{@link #leftFrontLeg}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_HIND_LEG}</td><td>Root part</td><td>{@link #rightHindLeg}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_HIND_LEG}</td><td>Root part</td><td>{@link #leftHindLeg}</td>
 * </tr>
 * </table>
 * </div>
 */
@Environment(EnvType.CLIENT)
public class HoglinEntityModel extends EntityModel<HoglinEntityRenderState> {
	public static final ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(true, 8.0F, 6.0F, 1.9F, 2.0F, 24.0F, Set.of("head"));
	private static final float HEAD_PITCH_START = 0.87266463F;
	private static final float HEAD_PITCH_END = (float) (-Math.PI / 9);
	private final ModelPart head;
	private final ModelPart rightEar;
	private final ModelPart leftEar;
	private final ModelPart body;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;
	private final ModelPart rightHindLeg;
	private final ModelPart leftHindLeg;
	private final ModelPart mane;

	public HoglinEntityModel(ModelPart modelPart) {
		super(modelPart);
		this.body = modelPart.getChild(EntityModelPartNames.BODY);
		this.mane = this.body.getChild(EntityModelPartNames.MANE);
		this.head = modelPart.getChild(EntityModelPartNames.HEAD);
		this.rightEar = this.head.getChild(EntityModelPartNames.RIGHT_EAR);
		this.leftEar = this.head.getChild(EntityModelPartNames.LEFT_EAR);
		this.rightFrontLeg = modelPart.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
		this.leftFrontLeg = modelPart.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
		this.rightHindLeg = modelPart.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
		this.leftHindLeg = modelPart.getChild(EntityModelPartNames.LEFT_HIND_LEG);
	}

	private static ModelData getModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData modelPartData2 = modelPartData.addChild(
			EntityModelPartNames.BODY, ModelPartBuilder.create().uv(1, 1).cuboid(-8.0F, -7.0F, -13.0F, 16.0F, 14.0F, 26.0F), ModelTransform.pivot(0.0F, 7.0F, 0.0F)
		);
		modelPartData2.addChild(
			EntityModelPartNames.MANE,
			ModelPartBuilder.create().uv(90, 33).cuboid(0.0F, 0.0F, -9.0F, 0.0F, 10.0F, 19.0F, new Dilation(0.001F)),
			ModelTransform.pivot(0.0F, -14.0F, -7.0F)
		);
		ModelPartData modelPartData3 = modelPartData.addChild(
			EntityModelPartNames.HEAD,
			ModelPartBuilder.create().uv(61, 1).cuboid(-7.0F, -3.0F, -19.0F, 14.0F, 6.0F, 19.0F),
			ModelTransform.of(0.0F, 2.0F, -12.0F, 0.87266463F, 0.0F, 0.0F)
		);
		modelPartData3.addChild(
			EntityModelPartNames.RIGHT_EAR,
			ModelPartBuilder.create().uv(1, 1).cuboid(-6.0F, -1.0F, -2.0F, 6.0F, 1.0F, 4.0F),
			ModelTransform.of(-6.0F, -2.0F, -3.0F, 0.0F, 0.0F, (float) (-Math.PI * 2.0 / 9.0))
		);
		modelPartData3.addChild(
			EntityModelPartNames.LEFT_EAR,
			ModelPartBuilder.create().uv(1, 6).cuboid(0.0F, -1.0F, -2.0F, 6.0F, 1.0F, 4.0F),
			ModelTransform.of(6.0F, -2.0F, -3.0F, 0.0F, 0.0F, (float) (Math.PI * 2.0 / 9.0))
		);
		modelPartData3.addChild(
			EntityModelPartNames.RIGHT_HORN,
			ModelPartBuilder.create().uv(10, 13).cuboid(-1.0F, -11.0F, -1.0F, 2.0F, 11.0F, 2.0F),
			ModelTransform.pivot(-7.0F, 2.0F, -12.0F)
		);
		modelPartData3.addChild(
			EntityModelPartNames.LEFT_HORN,
			ModelPartBuilder.create().uv(1, 13).cuboid(-1.0F, -11.0F, -1.0F, 2.0F, 11.0F, 2.0F),
			ModelTransform.pivot(7.0F, 2.0F, -12.0F)
		);
		int i = 14;
		int j = 11;
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_FRONT_LEG,
			ModelPartBuilder.create().uv(66, 42).cuboid(-3.0F, 0.0F, -3.0F, 6.0F, 14.0F, 6.0F),
			ModelTransform.pivot(-4.0F, 10.0F, -8.5F)
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_FRONT_LEG,
			ModelPartBuilder.create().uv(41, 42).cuboid(-3.0F, 0.0F, -3.0F, 6.0F, 14.0F, 6.0F),
			ModelTransform.pivot(4.0F, 10.0F, -8.5F)
		);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_HIND_LEG,
			ModelPartBuilder.create().uv(21, 45).cuboid(-2.5F, 0.0F, -2.5F, 5.0F, 11.0F, 5.0F),
			ModelTransform.pivot(-5.0F, 13.0F, 10.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_HIND_LEG,
			ModelPartBuilder.create().uv(0, 45).cuboid(-2.5F, 0.0F, -2.5F, 5.0F, 11.0F, 5.0F),
			ModelTransform.pivot(5.0F, 13.0F, 10.0F)
		);
		return modelData;
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = getModelData();
		return TexturedModelData.of(modelData, 128, 64);
	}

	public static TexturedModelData getBabyTexturedModelData() {
		ModelData modelData = getModelData();
		ModelPartData modelPartData = modelData.getRoot().getChild(EntityModelPartNames.BODY);
		modelPartData.addChild(
			EntityModelPartNames.MANE,
			ModelPartBuilder.create().uv(90, 33).cuboid(0.0F, 0.0F, -9.0F, 0.0F, 10.0F, 19.0F, new Dilation(0.001F)),
			ModelTransform.pivot(0.0F, -14.0F, -3.0F)
		);
		return TexturedModelData.of(modelData, 128, 64).transform(BABY_TRANSFORMER);
	}

	public void setAngles(HoglinEntityRenderState hoglinEntityRenderState) {
		super.setAngles(hoglinEntityRenderState);
		float f = hoglinEntityRenderState.limbAmplitudeMultiplier;
		float g = hoglinEntityRenderState.limbFrequency;
		this.rightEar.roll = (float) (-Math.PI * 2.0 / 9.0) - f * MathHelper.sin(g);
		this.leftEar.roll = (float) (Math.PI * 2.0 / 9.0) + f * MathHelper.sin(g);
		this.head.yaw = hoglinEntityRenderState.yawDegrees * (float) (Math.PI / 180.0);
		float h = 1.0F - (float)MathHelper.abs(10 - 2 * hoglinEntityRenderState.movementCooldownTicks) / 10.0F;
		this.head.pitch = MathHelper.lerp(h, 0.87266463F, (float) (-Math.PI / 9));
		if (hoglinEntityRenderState.baby) {
			this.head.pivotY += h * 2.5F;
		}

		float i = 1.2F;
		this.rightFrontLeg.pitch = MathHelper.cos(g) * 1.2F * f;
		this.leftFrontLeg.pitch = MathHelper.cos(g + (float) Math.PI) * 1.2F * f;
		this.rightHindLeg.pitch = this.leftFrontLeg.pitch;
		this.leftHindLeg.pitch = this.rightFrontLeg.pitch;
	}
}
