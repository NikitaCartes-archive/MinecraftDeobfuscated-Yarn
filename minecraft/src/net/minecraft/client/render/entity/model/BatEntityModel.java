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
import net.minecraft.client.render.entity.animation.BatAnimations;
import net.minecraft.client.render.entity.state.BatEntityRenderState;

/**
 * Represents the model of a {@linkplain BatEntity}.
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
 *   <td>{@value EntityModelPartNames#RIGHT_EAR}</td><td>{@value EntityModelPartNames#HEAD}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_EAR}</td><td>{@value EntityModelPartNames#HEAD}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#BODY}</td><td>{@linkplain #root Root part}</td><td>{@link #body}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_WING}</td><td>{@value EntityModelPartNames#BODY}</td><td>{@link #rightWing}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_WING_TIP}</td><td>{@value EntityModelPartNames#RIGHT_WING}</td><td>{@link #rightWingTip}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_WING}</td><td>{@value EntityModelPartNames#BODY}</td><td>{@link #leftWing}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_WING_TIP}</td><td>{@value EntityModelPartNames#LEFT_WING}</td><td>{@link #leftWingTip}</td>
 * </tr>
 * </table>
 * </div>
 */
@Environment(EnvType.CLIENT)
public class BatEntityModel extends EntityModel<BatEntityRenderState> {
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart rightWing;
	private final ModelPart leftWing;
	private final ModelPart rightWingTip;
	private final ModelPart leftWingTip;
	private final ModelPart feet;

	public BatEntityModel(ModelPart modelPart) {
		super(modelPart, RenderLayer::getEntityCutout);
		this.body = modelPart.getChild(EntityModelPartNames.BODY);
		this.head = modelPart.getChild(EntityModelPartNames.HEAD);
		this.rightWing = this.body.getChild(EntityModelPartNames.RIGHT_WING);
		this.rightWingTip = this.rightWing.getChild(EntityModelPartNames.RIGHT_WING_TIP);
		this.leftWing = this.body.getChild(EntityModelPartNames.LEFT_WING);
		this.leftWingTip = this.leftWing.getChild(EntityModelPartNames.LEFT_WING_TIP);
		this.feet = this.body.getChild(EntityModelPartNames.FEET);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData modelPartData2 = modelPartData.addChild(
			EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 0).cuboid(-1.5F, 0.0F, -1.0F, 3.0F, 5.0F, 2.0F), ModelTransform.pivot(0.0F, 17.0F, 0.0F)
		);
		ModelPartData modelPartData3 = modelPartData.addChild(
			EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 7).cuboid(-2.0F, -3.0F, -1.0F, 4.0F, 3.0F, 2.0F), ModelTransform.pivot(0.0F, 17.0F, 0.0F)
		);
		modelPartData3.addChild(
			EntityModelPartNames.RIGHT_EAR, ModelPartBuilder.create().uv(1, 15).cuboid(-2.5F, -4.0F, 0.0F, 3.0F, 5.0F, 0.0F), ModelTransform.pivot(-1.5F, -2.0F, 0.0F)
		);
		modelPartData3.addChild(
			EntityModelPartNames.LEFT_EAR, ModelPartBuilder.create().uv(8, 15).cuboid(-0.1F, -3.0F, 0.0F, 3.0F, 5.0F, 0.0F), ModelTransform.pivot(1.1F, -3.0F, 0.0F)
		);
		ModelPartData modelPartData4 = modelPartData2.addChild(
			EntityModelPartNames.RIGHT_WING, ModelPartBuilder.create().uv(12, 0).cuboid(-2.0F, -2.0F, 0.0F, 2.0F, 7.0F, 0.0F), ModelTransform.pivot(-1.5F, 0.0F, 0.0F)
		);
		modelPartData4.addChild(
			EntityModelPartNames.RIGHT_WING_TIP,
			ModelPartBuilder.create().uv(16, 0).cuboid(-6.0F, -2.0F, 0.0F, 6.0F, 8.0F, 0.0F),
			ModelTransform.pivot(-2.0F, 0.0F, 0.0F)
		);
		ModelPartData modelPartData5 = modelPartData2.addChild(
			EntityModelPartNames.LEFT_WING, ModelPartBuilder.create().uv(12, 7).cuboid(0.0F, -2.0F, 0.0F, 2.0F, 7.0F, 0.0F), ModelTransform.pivot(1.5F, 0.0F, 0.0F)
		);
		modelPartData5.addChild(
			EntityModelPartNames.LEFT_WING_TIP, ModelPartBuilder.create().uv(16, 8).cuboid(0.0F, -2.0F, 0.0F, 6.0F, 8.0F, 0.0F), ModelTransform.pivot(2.0F, 0.0F, 0.0F)
		);
		modelPartData2.addChild(
			EntityModelPartNames.FEET, ModelPartBuilder.create().uv(16, 16).cuboid(-1.5F, 0.0F, 0.0F, 3.0F, 2.0F, 0.0F), ModelTransform.pivot(0.0F, 5.0F, 0.0F)
		);
		return TexturedModelData.of(modelData, 32, 32);
	}

	public void setAngles(BatEntityRenderState batEntityRenderState) {
		super.setAngles(batEntityRenderState);
		if (batEntityRenderState.roosting) {
			this.setRoostingHeadAngles(batEntityRenderState.yawDegrees);
		}

		this.animate(batEntityRenderState.flyingAnimationState, BatAnimations.FLYING, batEntityRenderState.age, 1.0F);
		this.animate(batEntityRenderState.roostingAnimationState, BatAnimations.ROOSTING, batEntityRenderState.age, 1.0F);
	}

	private void setRoostingHeadAngles(float yaw) {
		this.head.yaw = yaw * (float) (Math.PI / 180.0);
	}
}
