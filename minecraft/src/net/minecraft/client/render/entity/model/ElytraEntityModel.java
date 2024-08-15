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
import net.minecraft.client.render.entity.state.BipedEntityRenderState;

/**
 * Represents the model of a worn elytra.
 * 
 * <div class="fabric">
 * <table border=1>
 * <caption>Model parts of this model</caption>
 * <tr>
 *   <th>Part Name</th><th>Parent</th><th>Corresponding Field</th>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_WING}</td><td>Root part</td><td>{@link #leftWing}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_WING}</td><td>Root part</td><td>{@link #rightWing}</td>
 * </tr>
 * </table>
 * </div>
 */
@Environment(EnvType.CLIENT)
public class ElytraEntityModel extends EntityModel<BipedEntityRenderState> {
	public static final ModelTransformer BABY_TRANSFORMER = ModelTransformer.scaling(0.5F);
	private final ModelPart root;
	private final ModelPart rightWing;
	private final ModelPart leftWing;

	public ElytraEntityModel(ModelPart root) {
		this.root = root;
		this.leftWing = root.getChild(EntityModelPartNames.LEFT_WING);
		this.rightWing = root.getChild(EntityModelPartNames.RIGHT_WING);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		Dilation dilation = new Dilation(1.0F);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_WING,
			ModelPartBuilder.create().uv(22, 0).cuboid(-10.0F, 0.0F, 0.0F, 10.0F, 20.0F, 2.0F, dilation),
			ModelTransform.of(5.0F, 0.0F, 0.0F, (float) (Math.PI / 12), 0.0F, (float) (-Math.PI / 12))
		);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_WING,
			ModelPartBuilder.create().uv(22, 0).mirrored().cuboid(0.0F, 0.0F, 0.0F, 10.0F, 20.0F, 2.0F, dilation),
			ModelTransform.of(-5.0F, 0.0F, 0.0F, (float) (Math.PI / 12), 0.0F, (float) (Math.PI / 12))
		);
		return TexturedModelData.of(modelData, 64, 32);
	}

	public void setAngles(BipedEntityRenderState bipedEntityRenderState) {
		this.leftWing.pivotY = bipedEntityRenderState.isInSneakingPose ? 3.0F : 0.0F;
		this.leftWing.pitch = bipedEntityRenderState.leftWingPitch;
		this.leftWing.roll = bipedEntityRenderState.leftWingRoll;
		this.leftWing.yaw = bipedEntityRenderState.leftWingYaw;
		this.rightWing.yaw = -this.leftWing.yaw;
		this.rightWing.pivotY = this.leftWing.pivotY;
		this.rightWing.pitch = this.leftWing.pitch;
		this.rightWing.roll = -this.leftWing.roll;
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}
}
