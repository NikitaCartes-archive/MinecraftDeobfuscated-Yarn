package net.minecraft.client.render.entity.model;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;

/**
 * Represents the model of a player-like entity.
 * 
 * <div class="fabric">
 * <table border=1>
 * <caption>Model parts of this model</caption>
 * <tr>
 *   <th>Part Name</th><th>Parent</th><th>Corresponding Field</th>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#HAT}</td><td>Root part</td><td>{@link #hat}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#HEAD}</td><td>Root part</td><td>{@link #head}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#BODY}</td><td>Root part</td><td>{@link #body}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_ARM}</td><td>Root part</td><td>{@link #rightArm}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_ARM}</td><td>Root part</td><td>{@link #leftArm}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_LEG}</td><td>Root part</td><td>{@link #rightLeg}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_LEG}</td><td>Root part</td><td>{@link #leftLeg}</td>
 * </tr>
 * <tr>
 *   <td>{@value #EAR}</td><td>Root part</td><td>{@link #ear}</td>
 * </tr>
 * <tr>
 *   <td>{@value #CLOAK}</td><td>Root part</td><td>{@link #cloak}</td>
 * </tr>
 * <tr>
 *   <td>{@value #LEFT_SLEEVE}</td><td>Root part</td><td>{@link #leftSleeve}</td>
 * </tr>
 * <tr>
 *   <td>{@value #RIGHT_SLEEVE}</td><td>Root part</td><td>{@link #rightSleeve}</td>
 * </tr>
 * <tr>
 *   <td>{@value #LEFT_PANTS}</td><td>Root part</td><td>{@link #leftPants}</td>
 * </tr>
 * <tr>
 *   <td>{@value #RIGHT_PANTS}</td><td>Root part</td><td>{@link #rightPants}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#JACKET}</td><td>Root part</td><td>{@link #jacket}</td>
 * </tr>
 * </table>
 * </div>
 */
@Environment(EnvType.CLIENT)
public class PlayerEntityModel extends BipedEntityModel<PlayerEntityRenderState> {
	/**
	 * The key of the left sleeve model part, whose value is {@value}.
	 */
	private static final String LEFT_SLEEVE = "left_sleeve";
	/**
	 * The key of the right sleeve model part, whose value is {@value}.
	 */
	private static final String RIGHT_SLEEVE = "right_sleeve";
	/**
	 * The key of the left pants model part, whose value is {@value}.
	 */
	private static final String LEFT_PANTS = "left_pants";
	/**
	 * The key of the right pants model part, whose value is {@value}.
	 */
	private static final String RIGHT_PANTS = "right_pants";
	/**
	 * All the parts. Used when picking a part to render stuck arrows.
	 */
	private final List<ModelPart> parts;
	public final ModelPart leftSleeve;
	public final ModelPart rightSleeve;
	public final ModelPart leftPants;
	public final ModelPart rightPants;
	public final ModelPart jacket;
	private final boolean thinArms;

	public PlayerEntityModel(ModelPart modelPart, boolean thinArms) {
		super(modelPart, RenderLayer::getEntityTranslucent);
		this.thinArms = thinArms;
		this.leftSleeve = this.leftArm.getChild("left_sleeve");
		this.rightSleeve = this.rightArm.getChild("right_sleeve");
		this.leftPants = this.leftLeg.getChild("left_pants");
		this.rightPants = this.rightLeg.getChild("right_pants");
		this.jacket = this.body.getChild(EntityModelPartNames.JACKET);
		this.parts = List.of(this.head, this.body, this.leftArm, this.rightArm, this.leftLeg, this.rightLeg);
	}

	public static ModelData getTexturedModelData(Dilation dilation, boolean slim) {
		ModelData modelData = BipedEntityModel.getModelData(dilation, 0.0F);
		ModelPartData modelPartData = modelData.getRoot();
		float f = 0.25F;
		if (slim) {
			ModelPartData modelPartData2 = modelPartData.addChild(
				EntityModelPartNames.LEFT_ARM,
				ModelPartBuilder.create().uv(32, 48).cuboid(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, dilation),
				ModelTransform.pivot(5.0F, 2.5F, 0.0F)
			);
			ModelPartData modelPartData3 = modelPartData.addChild(
				EntityModelPartNames.RIGHT_ARM,
				ModelPartBuilder.create().uv(40, 16).cuboid(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, dilation),
				ModelTransform.pivot(-5.0F, 2.5F, 0.0F)
			);
			modelPartData2.addChild(
				"left_sleeve", ModelPartBuilder.create().uv(48, 48).cuboid(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, dilation.add(0.25F)), ModelTransform.NONE
			);
			modelPartData3.addChild(
				"right_sleeve", ModelPartBuilder.create().uv(40, 32).cuboid(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, dilation.add(0.25F)), ModelTransform.NONE
			);
		} else {
			ModelPartData modelPartData2 = modelPartData.addChild(
				EntityModelPartNames.LEFT_ARM,
				ModelPartBuilder.create().uv(32, 48).cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation),
				ModelTransform.pivot(5.0F, 2.0F, 0.0F)
			);
			ModelPartData modelPartData3 = modelPartData.getChild(EntityModelPartNames.RIGHT_ARM);
			modelPartData2.addChild(
				"left_sleeve", ModelPartBuilder.create().uv(48, 48).cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation.add(0.25F)), ModelTransform.NONE
			);
			modelPartData3.addChild(
				"right_sleeve", ModelPartBuilder.create().uv(40, 32).cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation.add(0.25F)), ModelTransform.NONE
			);
		}

		ModelPartData modelPartData2 = modelPartData.addChild(
			EntityModelPartNames.LEFT_LEG,
			ModelPartBuilder.create().uv(16, 48).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation),
			ModelTransform.pivot(1.9F, 12.0F, 0.0F)
		);
		ModelPartData modelPartData3 = modelPartData.getChild(EntityModelPartNames.RIGHT_LEG);
		modelPartData2.addChild(
			"left_pants", ModelPartBuilder.create().uv(0, 48).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation.add(0.25F)), ModelTransform.NONE
		);
		modelPartData3.addChild(
			"right_pants", ModelPartBuilder.create().uv(0, 32).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation.add(0.25F)), ModelTransform.NONE
		);
		ModelPartData modelPartData4 = modelPartData.getChild(EntityModelPartNames.BODY);
		modelPartData4.addChild(
			EntityModelPartNames.JACKET, ModelPartBuilder.create().uv(16, 32).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, dilation.add(0.25F)), ModelTransform.NONE
		);
		return modelData;
	}

	public void setAngles(PlayerEntityRenderState playerEntityRenderState) {
		boolean bl = !playerEntityRenderState.spectator;
		this.body.visible = bl;
		this.rightArm.visible = bl;
		this.leftArm.visible = bl;
		this.rightLeg.visible = bl;
		this.leftLeg.visible = bl;
		this.hat.visible = playerEntityRenderState.hatVisible;
		this.jacket.visible = playerEntityRenderState.jacketVisible;
		this.leftPants.visible = playerEntityRenderState.leftPantsLegVisible;
		this.rightPants.visible = playerEntityRenderState.rightPantsLegVisible;
		this.leftSleeve.visible = playerEntityRenderState.leftSleeveVisible;
		this.rightSleeve.visible = playerEntityRenderState.rightSleeveVisible;
		super.setAngles(playerEntityRenderState);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		this.leftSleeve.visible = visible;
		this.rightSleeve.visible = visible;
		this.leftPants.visible = visible;
		this.rightPants.visible = visible;
		this.jacket.visible = visible;
	}

	@Override
	public void setArmAngle(Arm arm, MatrixStack matrices) {
		this.getPart().rotate(matrices);
		ModelPart modelPart = this.getArm(arm);
		if (this.thinArms) {
			float f = 0.5F * (float)(arm == Arm.RIGHT ? 1 : -1);
			modelPart.pivotX += f;
			modelPart.rotate(matrices);
			modelPart.pivotX -= f;
		} else {
			modelPart.rotate(matrices);
		}
	}

	public ModelPart getRandomPart(Random random) {
		return Util.getRandom(this.parts, random);
	}

	protected BipedEntityModel.ArmPose getArmPose(PlayerEntityRenderState playerEntityRenderState, Arm arm) {
		BipedEntityModel.ArmPose armPose = PlayerEntityRenderer.getArmPose(playerEntityRenderState, playerEntityRenderState.mainHandState, Hand.MAIN_HAND);
		BipedEntityModel.ArmPose armPose2 = PlayerEntityRenderer.getArmPose(playerEntityRenderState, playerEntityRenderState.offHandState, Hand.OFF_HAND);
		if (armPose.isTwoHanded()) {
			armPose2 = playerEntityRenderState.offHandState.empty ? BipedEntityModel.ArmPose.EMPTY : BipedEntityModel.ArmPose.ITEM;
		}

		return playerEntityRenderState.mainArm == arm ? armPose : armPose2;
	}
}
