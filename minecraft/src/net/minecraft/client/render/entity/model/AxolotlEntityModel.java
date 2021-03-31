package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class AxolotlEntityModel<T extends AxolotlEntity> extends AnimalModel<T> {
	private final ModelPart tail;
	private final ModelPart leftHindLeg;
	private final ModelPart rightHindLeg;
	private final ModelPart leftFrontLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart topGills;
	private final ModelPart leftGills;
	private final ModelPart rightGills;
	public static final float MOVING_IN_WATER_LEG_PITCH = 1.8849558F;

	public AxolotlEntityModel(ModelPart root) {
		super(true, 8.0F, 3.35F);
		this.body = root.getChild(EntityModelPartNames.BODY);
		this.head = this.body.getChild(EntityModelPartNames.HEAD);
		this.rightHindLeg = this.body.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
		this.leftHindLeg = this.body.getChild(EntityModelPartNames.LEFT_HIND_LEG);
		this.rightFrontLeg = this.body.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
		this.leftFrontLeg = this.body.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
		this.tail = this.body.getChild(EntityModelPartNames.TAIL);
		this.topGills = this.head.getChild(EntityModelPartNames.TOP_GILLS);
		this.leftGills = this.head.getChild(EntityModelPartNames.LEFT_GILLS);
		this.rightGills = this.head.getChild(EntityModelPartNames.RIGHT_GILLS);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData modelPartData2 = modelPartData.addChild(
			EntityModelPartNames.BODY,
			ModelPartBuilder.create().uv(0, 11).cuboid(-4.0F, -2.0F, -9.0F, 8.0F, 4.0F, 10.0F).uv(2, 17).cuboid(0.0F, -3.0F, -8.0F, 0.0F, 5.0F, 9.0F),
			ModelTransform.pivot(0.0F, 20.0F, 5.0F)
		);
		Dilation dilation = new Dilation(0.001F);
		ModelPartData modelPartData3 = modelPartData2.addChild(
			EntityModelPartNames.HEAD,
			ModelPartBuilder.create().uv(0, 1).cuboid(-4.0F, -3.0F, -5.0F, 8.0F, 5.0F, 5.0F, dilation),
			ModelTransform.pivot(0.0F, 0.0F, -9.0F)
		);
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(3, 37).cuboid(-4.0F, -3.0F, 0.0F, 8.0F, 3.0F, 0.0F, dilation);
		ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().uv(0, 40).cuboid(-3.0F, -5.0F, 0.0F, 3.0F, 7.0F, 0.0F, dilation);
		ModelPartBuilder modelPartBuilder3 = ModelPartBuilder.create().uv(11, 40).cuboid(0.0F, -5.0F, 0.0F, 3.0F, 7.0F, 0.0F, dilation);
		modelPartData3.addChild(EntityModelPartNames.TOP_GILLS, modelPartBuilder, ModelTransform.pivot(0.0F, -3.0F, -1.0F));
		modelPartData3.addChild(EntityModelPartNames.LEFT_GILLS, modelPartBuilder2, ModelTransform.pivot(-4.0F, 0.0F, -1.0F));
		modelPartData3.addChild(EntityModelPartNames.RIGHT_GILLS, modelPartBuilder3, ModelTransform.pivot(4.0F, 0.0F, -1.0F));
		ModelPartBuilder modelPartBuilder4 = ModelPartBuilder.create().uv(2, 13).cuboid(-1.0F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, dilation);
		ModelPartBuilder modelPartBuilder5 = ModelPartBuilder.create().uv(2, 13).cuboid(-2.0F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, dilation);
		modelPartData2.addChild(EntityModelPartNames.RIGHT_HIND_LEG, modelPartBuilder5, ModelTransform.pivot(-3.5F, 1.0F, -1.0F));
		modelPartData2.addChild(EntityModelPartNames.LEFT_HIND_LEG, modelPartBuilder4, ModelTransform.pivot(3.5F, 1.0F, -1.0F));
		modelPartData2.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, modelPartBuilder5, ModelTransform.pivot(-3.5F, 1.0F, -8.0F));
		modelPartData2.addChild(EntityModelPartNames.LEFT_FRONT_LEG, modelPartBuilder4, ModelTransform.pivot(3.5F, 1.0F, -8.0F));
		modelPartData2.addChild(
			EntityModelPartNames.TAIL, ModelPartBuilder.create().uv(2, 19).cuboid(0.0F, -3.0F, 0.0F, 0.0F, 5.0F, 12.0F), ModelTransform.pivot(0.0F, 0.0F, 1.0F)
		);
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	protected Iterable<ModelPart> getHeadParts() {
		return ImmutableList.<ModelPart>of();
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return ImmutableList.<ModelPart>of(this.body);
	}

	public void setAngles(T axolotlEntity, float f, float g, float h, float i, float j) {
		this.resetAngles(i, j);
		if (axolotlEntity.isPlayingDead()) {
			this.setPlayingDeadAngles();
		} else {
			boolean bl = Entity.squaredHorizontalLength(axolotlEntity.getVelocity()) > 1.0E-7;
			if (axolotlEntity.isInsideWaterOrBubbleColumn()) {
				if (bl) {
					this.setMovingInWaterAngles(h, j);
				} else {
					this.setStandingInWaterAngles(h);
				}
			} else {
				if (axolotlEntity.isOnGround()) {
					if (bl) {
						this.setMovingOnGroundAngles(h);
					} else {
						this.setStandingOnGroundAngles(h);
					}
				}
			}
		}
	}

	/**
	 * Resets the angles of the axolotl model.
	 * 
	 * @param headYaw the axolotl head yaw
	 * @param headPitch the axolotl head pitch
	 */
	private void resetAngles(float headYaw, float headPitch) {
		this.body.pivotX = 0.0F;
		this.head.pivotY = 0.0F;
		this.body.pivotY = 20.0F;
		this.body.setAngles(headPitch * (float) (Math.PI / 180.0), headYaw * (float) (Math.PI / 180.0), 0.0F);
		this.head.setAngles(0.0F, 0.0F, 0.0F);
		this.leftHindLeg.setAngles(0.0F, 0.0F, 0.0F);
		this.rightHindLeg.setAngles(0.0F, 0.0F, 0.0F);
		this.leftFrontLeg.setAngles(0.0F, 0.0F, 0.0F);
		this.rightFrontLeg.setAngles(0.0F, 0.0F, 0.0F);
		this.leftGills.setAngles(0.0F, 0.0F, 0.0F);
		this.rightGills.setAngles(0.0F, 0.0F, 0.0F);
		this.topGills.setAngles(0.0F, 0.0F, 0.0F);
		this.tail.setAngles(0.0F, 0.0F, 0.0F);
	}

	private void setStandingOnGroundAngles(float animationProgress) {
		float f = animationProgress * 0.09F;
		float g = MathHelper.sin(f);
		float h = MathHelper.cos(f);
		float i = g * g - 2.0F * g;
		float j = h * h - 3.0F * g;
		this.head.pitch = -0.09F * i;
		this.head.roll = -0.2F;
		this.tail.yaw = -0.1F + 0.1F * i;
		this.topGills.pitch = 0.6F + 0.05F * j;
		this.leftGills.yaw = -this.topGills.pitch;
		this.rightGills.yaw = -this.leftGills.yaw;
		this.leftHindLeg.setAngles(1.1F, 1.0F, 0.0F);
		this.leftFrontLeg.setAngles(0.8F, 2.3F, -0.5F);
		this.copyLegAngles();
	}

	private void setMovingOnGroundAngles(float animationProgress) {
		float f = animationProgress * 0.11F;
		float g = MathHelper.cos(f);
		float h = (g * g - 2.0F * g) / 5.0F;
		float i = 0.7F * g;
		this.head.yaw = 0.09F * g;
		this.tail.yaw = this.head.yaw;
		this.topGills.pitch = 0.6F - 0.08F * (g * g + 2.0F * MathHelper.sin(f));
		this.leftGills.yaw = -this.topGills.pitch;
		this.rightGills.yaw = -this.leftGills.yaw;
		this.leftHindLeg.setAngles(0.9424779F, 1.5F - h, -0.1F);
		this.leftFrontLeg.setAngles(1.0995574F, (float) (Math.PI / 2) - i, 0.0F);
		this.rightHindLeg.setAngles(this.leftHindLeg.pitch, -1.0F - h, 0.0F);
		this.rightFrontLeg.setAngles(this.leftFrontLeg.pitch, (float) (-Math.PI / 2) - i, 0.0F);
	}

	private void setStandingInWaterAngles(float animationProgress) {
		float f = animationProgress * 0.075F;
		float g = MathHelper.cos(f);
		float h = MathHelper.sin(f) * 0.15F;
		this.body.pitch = -0.15F + 0.075F * g;
		this.body.pivotY -= h;
		this.head.pitch = -this.body.pitch;
		this.topGills.pitch = 0.2F * g;
		this.leftGills.yaw = -0.3F * g - 0.19F;
		this.rightGills.yaw = -this.leftGills.yaw;
		this.leftHindLeg.setAngles((float) (Math.PI * 3.0 / 4.0) - g * 0.11F, 0.47123894F, 1.7278761F);
		this.leftFrontLeg.setAngles((float) (Math.PI / 4) - g * 0.2F, 2.042035F, 0.0F);
		this.copyLegAngles();
		this.tail.yaw = 0.5F * g;
	}

	private void setMovingInWaterAngles(float animationProgress, float headPitch) {
		float f = animationProgress * 0.33F;
		float g = MathHelper.sin(f);
		float h = MathHelper.cos(f);
		float i = 0.13F * g;
		this.body.pitch = headPitch * (float) (Math.PI / 180.0) + i;
		this.head.pitch = -i * 1.8F;
		this.body.pivotY -= 0.45F * h;
		this.topGills.pitch = -0.5F * g - 0.8F;
		this.leftGills.yaw = 0.3F * g + 0.9F;
		this.rightGills.yaw = -this.leftGills.yaw;
		this.tail.yaw = 0.3F * MathHelper.cos(f * 0.9F);
		this.leftHindLeg.setAngles(1.8849558F, -0.4F * g, (float) (Math.PI / 2));
		this.leftFrontLeg.setAngles(1.8849558F, -0.2F * h - 0.1F, (float) (Math.PI / 2));
		this.copyLegAngles();
	}

	private void setPlayingDeadAngles() {
		this.leftHindLeg.setAngles(1.4137167F, 1.0995574F, (float) (Math.PI / 4));
		this.leftFrontLeg.setAngles((float) (Math.PI / 4), 2.042035F, 0.0F);
		this.body.pitch = -0.15F;
		this.body.roll = 0.35F;
		this.copyLegAngles();
	}

	/**
	 * Copies and mirrors the left leg angles to the right leg angles.
	 */
	private void copyLegAngles() {
		this.rightHindLeg.setAngles(this.leftHindLeg.pitch, -this.leftHindLeg.yaw, -this.leftHindLeg.roll);
		this.rightFrontLeg.setAngles(this.leftFrontLeg.pitch, -this.leftFrontLeg.yaw, -this.leftFrontLeg.roll);
	}
}
