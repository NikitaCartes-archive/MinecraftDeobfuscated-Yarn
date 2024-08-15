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
import net.minecraft.client.render.entity.state.WitchEntityRenderState;
import net.minecraft.util.math.MathHelper;

/**
 * Represents the model of a witch resembling entity.
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
 *   <td>{@value EntityModelPartNames#HAT}</td><td>{@value EntityModelPartNames#HEAD}</td><td>{@link #hat}</td>
 * </tr>
 * <tr>
 *   <td>{@code hat2}</td><td>{@value EntityModelPartNames#HAT}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@code hat3}</td><td>{@code hat2}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@code hat4}</td><td>{@code hat3}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#NOSE}</td><td>{@value EntityModelPartNames#HEAD}</td><td>{@link #nose}</td>
 * </tr>
 * <tr>
 *   <td>{@code mole}</td><td>{@value EntityModelPartNames#NOSE}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#BODY}</td><td>{@linkplain #root Root part}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#JACKET}</td><td>{@value EntityModelPartNames#BODY}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#ARMS}</td><td>{@linkplain #root Root part}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_LEG}</td><td>{@linkplain #root Root part}</td><td>{@link #rightLeg}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_LEG}</td><td>{@linkplain #root Root part}</td><td>{@link #leftLeg}</td>
 * </tr>
 * </table>
 * </div>
 */
@Environment(EnvType.CLIENT)
public class WitchEntityModel extends EntityModel<WitchEntityRenderState> implements ModelWithHead, ModelWithHat {
	protected final ModelPart nose;
	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart hat;
	private final ModelPart hatRim;
	private final ModelPart rightLeg;
	private final ModelPart leftLeg;

	public WitchEntityModel(ModelPart root) {
		this.root = root;
		this.head = root.getChild(EntityModelPartNames.HEAD);
		this.hat = this.head.getChild(EntityModelPartNames.HAT);
		this.hatRim = this.hat.getChild(EntityModelPartNames.HAT_RIM);
		this.nose = this.head.getChild(EntityModelPartNames.NOSE);
		this.rightLeg = root.getChild(EntityModelPartNames.RIGHT_LEG);
		this.leftLeg = root.getChild(EntityModelPartNames.LEFT_LEG);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = VillagerResemblingModel.getModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData modelPartData2 = modelPartData.addChild(
			EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F), ModelTransform.NONE
		);
		ModelPartData modelPartData3 = modelPartData2.addChild(
			EntityModelPartNames.HAT, ModelPartBuilder.create().uv(0, 64).cuboid(0.0F, 0.0F, 0.0F, 10.0F, 2.0F, 10.0F), ModelTransform.pivot(-5.0F, -10.03125F, -5.0F)
		);
		ModelPartData modelPartData4 = modelPartData3.addChild(
			"hat2",
			ModelPartBuilder.create().uv(0, 76).cuboid(0.0F, 0.0F, 0.0F, 7.0F, 4.0F, 7.0F),
			ModelTransform.of(1.75F, -4.0F, 2.0F, -0.05235988F, 0.0F, 0.02617994F)
		);
		ModelPartData modelPartData5 = modelPartData4.addChild(
			"hat3",
			ModelPartBuilder.create().uv(0, 87).cuboid(0.0F, 0.0F, 0.0F, 4.0F, 4.0F, 4.0F),
			ModelTransform.of(1.75F, -4.0F, 2.0F, -0.10471976F, 0.0F, 0.05235988F)
		);
		modelPartData5.addChild(
			"hat4",
			ModelPartBuilder.create().uv(0, 95).cuboid(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.25F)),
			ModelTransform.of(1.75F, -2.0F, 2.0F, (float) (-Math.PI / 15), 0.0F, 0.10471976F)
		);
		ModelPartData modelPartData6 = modelPartData2.getChild(EntityModelPartNames.NOSE);
		modelPartData6.addChild(
			"mole", ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, 3.0F, -6.75F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.pivot(0.0F, -2.0F, 0.0F)
		);
		return TexturedModelData.of(modelData, 64, 128);
	}

	public void setAngles(WitchEntityRenderState witchEntityRenderState) {
		this.head.yaw = witchEntityRenderState.yawDegrees * (float) (Math.PI / 180.0);
		this.head.pitch = witchEntityRenderState.pitch * (float) (Math.PI / 180.0);
		this.head.roll = 0.0F;
		this.rightLeg.pitch = MathHelper.cos(witchEntityRenderState.limbFrequency * 0.6662F) * 1.4F * witchEntityRenderState.limbAmplitudeMultiplier * 0.5F;
		this.leftLeg.pitch = MathHelper.cos(witchEntityRenderState.limbFrequency * 0.6662F + (float) Math.PI)
			* 1.4F
			* witchEntityRenderState.limbAmplitudeMultiplier
			* 0.5F;
		this.rightLeg.yaw = 0.0F;
		this.leftLeg.yaw = 0.0F;
		this.nose.setPivot(0.0F, -2.0F, 0.0F);
		float f = 0.01F * (float)(witchEntityRenderState.id % 10);
		this.nose.pitch = MathHelper.sin(witchEntityRenderState.age * f) * 4.5F * (float) (Math.PI / 180.0);
		this.nose.yaw = 0.0F;
		this.nose.roll = MathHelper.cos(witchEntityRenderState.age * f) * 2.5F * (float) (Math.PI / 180.0);
		if (witchEntityRenderState.holdingItem) {
			this.nose.setPivot(0.0F, 1.0F, -1.5F);
			this.nose.pitch = -0.9F;
		}
	}

	public ModelPart getNose() {
		return this.nose;
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}

	@Override
	public ModelPart getHead() {
		return this.head;
	}

	@Override
	public void setHatVisible(boolean visible) {
		this.head.visible = visible;
		this.hat.visible = visible;
		this.hatRim.visible = visible;
	}
}
