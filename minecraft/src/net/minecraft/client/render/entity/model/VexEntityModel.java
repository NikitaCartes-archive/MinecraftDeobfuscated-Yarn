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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.state.VexEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

/**
 * Represents the model of a {@linkplain VexEntity}.
 * 
 * <div class="fabric">
 * <table border=1>
 * <caption>Model parts of this model</caption>
 * <tr>
 *   <th>Part Name</th><th>Parent</th><th>Corresponding Field</th>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#HEAD}</td><td>Root part</td><td></td>
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
 *   <td>{@value EntityModelPartNames#RIGHT_WING}</td><td>Root part</td><td>{@link #rightWing}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_WING}</td><td>Root part</td><td>{@link #leftWing}</td>
 * </tr>
 * </table>
 * </div>
 */
@Environment(EnvType.CLIENT)
public class VexEntityModel extends EntityModel<VexEntityRenderState> implements ModelWithArms {
	private final ModelPart body = this.root.getChild(EntityModelPartNames.BODY);
	private final ModelPart rightArm = this.body.getChild(EntityModelPartNames.RIGHT_ARM);
	private final ModelPart leftArm = this.body.getChild(EntityModelPartNames.LEFT_ARM);
	private final ModelPart rightWing = this.body.getChild(EntityModelPartNames.RIGHT_WING);
	private final ModelPart leftWing = this.body.getChild(EntityModelPartNames.LEFT_WING);
	private final ModelPart head = this.root.getChild(EntityModelPartNames.HEAD);

	public VexEntityModel(ModelPart modelPart) {
		super(modelPart.getChild(EntityModelPartNames.ROOT), RenderLayer::getEntityTranslucent);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.ROOT, ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -2.5F, 0.0F));
		modelPartData2.addChild(
			EntityModelPartNames.HEAD,
			ModelPartBuilder.create().uv(0, 0).cuboid(-2.5F, -5.0F, -2.5F, 5.0F, 5.0F, 5.0F, new Dilation(0.0F)),
			ModelTransform.pivot(0.0F, 20.0F, 0.0F)
		);
		ModelPartData modelPartData3 = modelPartData2.addChild(
			EntityModelPartNames.BODY,
			ModelPartBuilder.create()
				.uv(0, 10)
				.cuboid(-1.5F, 0.0F, -1.0F, 3.0F, 4.0F, 2.0F, new Dilation(0.0F))
				.uv(0, 16)
				.cuboid(-1.5F, 1.0F, -1.0F, 3.0F, 5.0F, 2.0F, new Dilation(-0.2F)),
			ModelTransform.pivot(0.0F, 20.0F, 0.0F)
		);
		modelPartData3.addChild(
			EntityModelPartNames.RIGHT_ARM,
			ModelPartBuilder.create().uv(23, 0).cuboid(-1.25F, -0.5F, -1.0F, 2.0F, 4.0F, 2.0F, new Dilation(-0.1F)),
			ModelTransform.pivot(-1.75F, 0.25F, 0.0F)
		);
		modelPartData3.addChild(
			EntityModelPartNames.LEFT_ARM,
			ModelPartBuilder.create().uv(23, 6).cuboid(-0.75F, -0.5F, -1.0F, 2.0F, 4.0F, 2.0F, new Dilation(-0.1F)),
			ModelTransform.pivot(1.75F, 0.25F, 0.0F)
		);
		modelPartData3.addChild(
			EntityModelPartNames.LEFT_WING,
			ModelPartBuilder.create().uv(16, 14).mirrored().cuboid(0.0F, 0.0F, 0.0F, 0.0F, 5.0F, 8.0F, new Dilation(0.0F)).mirrored(false),
			ModelTransform.pivot(0.5F, 1.0F, 1.0F)
		);
		modelPartData3.addChild(
			EntityModelPartNames.RIGHT_WING,
			ModelPartBuilder.create().uv(16, 14).cuboid(0.0F, 0.0F, 0.0F, 0.0F, 5.0F, 8.0F, new Dilation(0.0F)),
			ModelTransform.pivot(-0.5F, 1.0F, 1.0F)
		);
		return TexturedModelData.of(modelData, 32, 32);
	}

	public void setAngles(VexEntityRenderState vexEntityRenderState) {
		super.setAngles(vexEntityRenderState);
		this.head.yaw = vexEntityRenderState.yawDegrees * (float) (Math.PI / 180.0);
		this.head.pitch = vexEntityRenderState.pitch * (float) (Math.PI / 180.0);
		float f = MathHelper.cos(vexEntityRenderState.age * 5.5F * (float) (Math.PI / 180.0)) * 0.1F;
		this.rightArm.roll = (float) (Math.PI / 5) + f;
		this.leftArm.roll = -((float) (Math.PI / 5) + f);
		if (vexEntityRenderState.charging) {
			this.body.pitch = 0.0F;
			this.setChargingArmAngles(!vexEntityRenderState.rightHandStack.isEmpty(), !vexEntityRenderState.leftHandStack.isEmpty(), f);
		} else {
			this.body.pitch = (float) (Math.PI / 20);
		}

		this.leftWing.yaw = 1.0995574F + MathHelper.cos(vexEntityRenderState.age * 45.836624F * (float) (Math.PI / 180.0)) * (float) (Math.PI / 180.0) * 16.2F;
		this.rightWing.yaw = -this.leftWing.yaw;
		this.leftWing.pitch = 0.47123888F;
		this.leftWing.roll = -0.47123888F;
		this.rightWing.pitch = 0.47123888F;
		this.rightWing.roll = 0.47123888F;
	}

	private void setChargingArmAngles(boolean bl, boolean bl2, float f) {
		if (!bl && !bl2) {
			this.rightArm.pitch = -1.2217305F;
			this.rightArm.yaw = (float) (Math.PI / 12);
			this.rightArm.roll = -0.47123888F - f;
			this.leftArm.pitch = -1.2217305F;
			this.leftArm.yaw = (float) (-Math.PI / 12);
			this.leftArm.roll = 0.47123888F + f;
		} else {
			if (bl) {
				this.rightArm.pitch = (float) (Math.PI * 7.0 / 6.0);
				this.rightArm.yaw = (float) (Math.PI / 12);
				this.rightArm.roll = -0.47123888F - f;
			}

			if (bl2) {
				this.leftArm.pitch = (float) (Math.PI * 7.0 / 6.0);
				this.leftArm.yaw = (float) (-Math.PI / 12);
				this.leftArm.roll = 0.47123888F + f;
			}
		}
	}

	@Override
	public void setArmAngle(Arm arm, MatrixStack matrices) {
		boolean bl = arm == Arm.RIGHT;
		ModelPart modelPart = bl ? this.rightArm : this.leftArm;
		this.root.rotate(matrices);
		this.body.rotate(matrices);
		modelPart.rotate(matrices);
		matrices.scale(0.55F, 0.55F, 0.55F);
		this.translateForHand(matrices, bl);
	}

	private void translateForHand(MatrixStack matrices, boolean mainHand) {
		if (mainHand) {
			matrices.translate(0.046875, -0.15625, 0.078125);
		} else {
			matrices.translate(-0.046875, -0.15625, 0.078125);
		}
	}
}
