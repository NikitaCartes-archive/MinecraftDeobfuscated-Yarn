package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.state.PiglinEntityRenderState;
import net.minecraft.entity.mob.PiglinActivity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

/**
 * Represents the model of a piglin-like entity.
 * 
 * <div class="fabric">
 * <table border=1>
 * <caption>Model parts of this model</caption>
 * <tr>
 *   <th>Part Name</th><th>Parent</th><th>Corresponding Field</th>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#HAT} (note: is by default empty)</td><td>Root part</td><td>{@link #hat}</td>
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
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_EAR}</td><td>{@value EntityModelPartNames#HEAD}</td><td>{@link #leftEar}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_EAR}</td><td>{@value EntityModelPartNames#HEAD}</td><td>{@link #rightEar}</td>
 * </tr>
 * </table>
 * </div>
 */
@Environment(EnvType.CLIENT)
public class PiglinEntityModel extends PiglinBaseEntityModel<PiglinEntityRenderState> {
	public PiglinEntityModel(ModelPart modelPart) {
		super(modelPart);
	}

	public void setAngles(PiglinEntityRenderState piglinEntityRenderState) {
		super.setAngles(piglinEntityRenderState);
		float f = (float) (Math.PI / 6);
		float g = piglinEntityRenderState.handSwingProgress;
		PiglinActivity piglinActivity = piglinEntityRenderState.activity;
		if (piglinActivity == PiglinActivity.DANCING) {
			float h = piglinEntityRenderState.age / 60.0F;
			this.rightEar.roll = (float) (Math.PI / 6) + (float) (Math.PI / 180.0) * MathHelper.sin(h * 30.0F) * 10.0F;
			this.leftEar.roll = (float) (-Math.PI / 6) - (float) (Math.PI / 180.0) * MathHelper.cos(h * 30.0F) * 10.0F;
			this.head.pivotX = this.head.pivotX + MathHelper.sin(h * 10.0F);
			this.head.pivotY = this.head.pivotY + MathHelper.sin(h * 40.0F) + 0.4F;
			this.rightArm.roll = (float) (Math.PI / 180.0) * (70.0F + MathHelper.cos(h * 40.0F) * 10.0F);
			this.leftArm.roll = this.rightArm.roll * -1.0F;
			this.rightArm.pivotY = this.rightArm.pivotY + (MathHelper.sin(h * 40.0F) * 0.5F - 0.5F);
			this.leftArm.pivotY = this.leftArm.pivotY + MathHelper.sin(h * 40.0F) * 0.5F + 0.5F;
			this.body.pivotY = this.body.pivotY + MathHelper.sin(h * 40.0F) * 0.35F;
		} else if (piglinActivity == PiglinActivity.ATTACKING_WITH_MELEE_WEAPON && g == 0.0F) {
			this.rotateMainArm(piglinEntityRenderState);
		} else if (piglinActivity == PiglinActivity.CROSSBOW_HOLD) {
			CrossbowPosing.hold(this.rightArm, this.leftArm, this.head, piglinEntityRenderState.mainArm == Arm.RIGHT);
		} else if (piglinActivity == PiglinActivity.CROSSBOW_CHARGE) {
			CrossbowPosing.charge(
				this.rightArm,
				this.leftArm,
				piglinEntityRenderState.piglinCrossbowPullTime,
				piglinEntityRenderState.itemUseTime,
				piglinEntityRenderState.mainArm == Arm.RIGHT
			);
		} else if (piglinActivity == PiglinActivity.ADMIRING_ITEM) {
			this.head.pitch = 0.5F;
			this.head.yaw = 0.0F;
			if (piglinEntityRenderState.mainArm == Arm.LEFT) {
				this.rightArm.yaw = -0.5F;
				this.rightArm.pitch = -0.9F;
			} else {
				this.leftArm.yaw = 0.5F;
				this.leftArm.pitch = -0.9F;
			}
		}
	}

	protected void animateArms(PiglinEntityRenderState piglinEntityRenderState, float f) {
		float g = piglinEntityRenderState.handSwingProgress;
		if (g > 0.0F && piglinEntityRenderState.activity == PiglinActivity.ATTACKING_WITH_MELEE_WEAPON) {
			CrossbowPosing.meleeAttack(this.rightArm, this.leftArm, piglinEntityRenderState.mainArm, g, piglinEntityRenderState.age);
		} else {
			super.animateArms(piglinEntityRenderState, f);
		}
	}

	private void rotateMainArm(PiglinEntityRenderState state) {
		if (state.mainArm == Arm.LEFT) {
			this.leftArm.pitch = -1.8F;
		} else {
			this.rightArm.pitch = -1.8F;
		}
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
}
