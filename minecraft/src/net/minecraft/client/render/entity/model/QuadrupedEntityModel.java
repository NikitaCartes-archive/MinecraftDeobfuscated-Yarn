package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.math.MathHelper;

/**
 * Represents the model of a quadruped entity.
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
public class QuadrupedEntityModel<T extends LivingEntityRenderState> extends EntityModel<T> {
	protected final ModelPart root;
	protected final ModelPart head;
	protected final ModelPart body;
	protected final ModelPart rightHindLeg;
	protected final ModelPart leftHindLeg;
	protected final ModelPart rightFrontLeg;
	protected final ModelPart leftFrontLeg;

	protected QuadrupedEntityModel(ModelPart root) {
		this.root = root;
		this.head = root.getChild(EntityModelPartNames.HEAD);
		this.body = root.getChild(EntityModelPartNames.BODY);
		this.rightHindLeg = root.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
		this.leftHindLeg = root.getChild(EntityModelPartNames.LEFT_HIND_LEG);
		this.rightFrontLeg = root.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
		this.leftFrontLeg = root.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
	}

	public static ModelData getModelData(int stanceWidth, Dilation dilation) {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(
			EntityModelPartNames.HEAD,
			ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, dilation),
			ModelTransform.pivot(0.0F, (float)(18 - stanceWidth), -6.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.BODY,
			ModelPartBuilder.create().uv(28, 8).cuboid(-5.0F, -10.0F, -7.0F, 10.0F, 16.0F, 8.0F, dilation),
			ModelTransform.of(0.0F, (float)(17 - stanceWidth), 2.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, (float)stanceWidth, 4.0F, dilation);
		modelPartData.addChild(EntityModelPartNames.RIGHT_HIND_LEG, modelPartBuilder, ModelTransform.pivot(-3.0F, (float)(24 - stanceWidth), 7.0F));
		modelPartData.addChild(EntityModelPartNames.LEFT_HIND_LEG, modelPartBuilder, ModelTransform.pivot(3.0F, (float)(24 - stanceWidth), 7.0F));
		modelPartData.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, modelPartBuilder, ModelTransform.pivot(-3.0F, (float)(24 - stanceWidth), -5.0F));
		modelPartData.addChild(EntityModelPartNames.LEFT_FRONT_LEG, modelPartBuilder, ModelTransform.pivot(3.0F, (float)(24 - stanceWidth), -5.0F));
		return modelData;
	}

	public void setAngles(T livingEntityRenderState) {
		this.head.pitch = livingEntityRenderState.pitch * (float) (Math.PI / 180.0);
		this.head.yaw = livingEntityRenderState.yawDegrees * (float) (Math.PI / 180.0);
		float f = livingEntityRenderState.limbFrequency;
		float g = livingEntityRenderState.limbAmplitudeMultiplier;
		this.rightHindLeg.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		this.leftHindLeg.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.rightFrontLeg.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.leftFrontLeg.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}
}
