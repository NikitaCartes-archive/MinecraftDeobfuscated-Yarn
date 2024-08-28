package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.state.CreeperEntityRenderState;
import net.minecraft.util.math.MathHelper;

/**
 * Represents the model of a creeper-like entity.
 * This model is not tied to a specific entity.
 * 
 * <div class="fabric">
 * <table border=1>
 * <caption>Model parts of this model</caption>
 * <tr>
 *   <th>Part Name</th><th>Parent</th><th>Corresponding Field</th>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#HEAD}</td><td>{@linkplain #root Root part}</td><td>{@link #head}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#BODY}</td><td>{@linkplain #root Root part}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_HIND_LEG}</td><td>{@linkplain #root Root part}</td><td>{@link #rightHindLeg}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_HIND_LEG}</td><td>{@linkplain #root Root part}</td><td>{@link #leftHindLeg}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_FRONT_LEG}</td><td>{@linkplain #root Root part}</td><td>{@link #rightFrontLeg}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_FRONT_LEG}</td><td>{@linkplain #root Root part}</td><td>{@link #leftFrontLeg}</td>
 * </tr>
 * </table>
 * </div>
 */
@Environment(EnvType.CLIENT)
public class CreeperEntityModel extends EntityModel<CreeperEntityRenderState> {
	private final ModelPart head;
	private final ModelPart leftHindLeg;
	private final ModelPart rightHindLeg;
	private final ModelPart leftFrontLeg;
	private final ModelPart rightFrontLeg;
	private static final int HEAD_AND_BODY_Y_PIVOT = 6;

	public CreeperEntityModel(ModelPart modelPart) {
		super(modelPart);
		this.head = modelPart.getChild(EntityModelPartNames.HEAD);
		this.rightHindLeg = modelPart.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
		this.leftHindLeg = modelPart.getChild(EntityModelPartNames.LEFT_HIND_LEG);
		this.rightFrontLeg = modelPart.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
		this.leftFrontLeg = modelPart.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
	}

	public static TexturedModelData getTexturedModelData(Dilation dilation) {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(
			EntityModelPartNames.HEAD,
			ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, dilation),
			ModelTransform.pivot(0.0F, 6.0F, 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.BODY,
			ModelPartBuilder.create().uv(16, 16).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, dilation),
			ModelTransform.pivot(0.0F, 6.0F, 0.0F)
		);
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, dilation);
		modelPartData.addChild(EntityModelPartNames.RIGHT_HIND_LEG, modelPartBuilder, ModelTransform.pivot(-2.0F, 18.0F, 4.0F));
		modelPartData.addChild(EntityModelPartNames.LEFT_HIND_LEG, modelPartBuilder, ModelTransform.pivot(2.0F, 18.0F, 4.0F));
		modelPartData.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, modelPartBuilder, ModelTransform.pivot(-2.0F, 18.0F, -4.0F));
		modelPartData.addChild(EntityModelPartNames.LEFT_FRONT_LEG, modelPartBuilder, ModelTransform.pivot(2.0F, 18.0F, -4.0F));
		return TexturedModelData.of(modelData, 64, 32);
	}

	public void setAngles(CreeperEntityRenderState creeperEntityRenderState) {
		super.setAngles(creeperEntityRenderState);
		this.head.yaw = creeperEntityRenderState.yawDegrees * (float) (Math.PI / 180.0);
		this.head.pitch = creeperEntityRenderState.pitch * (float) (Math.PI / 180.0);
		float f = creeperEntityRenderState.limbAmplitudeMultiplier;
		float g = creeperEntityRenderState.limbFrequency;
		this.leftHindLeg.pitch = MathHelper.cos(g * 0.6662F) * 1.4F * f;
		this.rightHindLeg.pitch = MathHelper.cos(g * 0.6662F + (float) Math.PI) * 1.4F * f;
		this.leftFrontLeg.pitch = MathHelper.cos(g * 0.6662F + (float) Math.PI) * 1.4F * f;
		this.rightFrontLeg.pitch = MathHelper.cos(g * 0.6662F) * 1.4F * f;
	}
}
