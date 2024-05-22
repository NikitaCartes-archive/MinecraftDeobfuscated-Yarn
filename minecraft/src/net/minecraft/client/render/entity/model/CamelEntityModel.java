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
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.CamelAnimations;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.CamelEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CamelEntityModel<T extends CamelEntity> extends SinglePartEntityModel<T> {
	private static final float LIMB_ANGLE_SCALE = 2.0F;
	private static final float LIMB_DISTANCE_SCALE = 2.5F;
	private static final float field_43083 = 0.45F;
	private static final float field_43084 = 29.35F;
	private static final String SADDLE = "saddle";
	private static final String BRIDLE = "bridle";
	private static final String REINS = "reins";
	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart[] saddleAndBridle;
	private final ModelPart[] reins;

	public CamelEntityModel(ModelPart root) {
		this.root = root;
		ModelPart modelPart = root.getChild(EntityModelPartNames.BODY);
		this.head = modelPart.getChild(EntityModelPartNames.HEAD);
		this.saddleAndBridle = new ModelPart[]{modelPart.getChild("saddle"), this.head.getChild("bridle")};
		this.reins = new ModelPart[]{this.head.getChild("reins")};
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		Dilation dilation = new Dilation(0.05F);
		ModelPartData modelPartData2 = modelPartData.addChild(
			EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 25).cuboid(-7.5F, -12.0F, -23.5F, 15.0F, 12.0F, 27.0F), ModelTransform.pivot(0.0F, 4.0F, 9.5F)
		);
		modelPartData2.addChild(
			"hump", ModelPartBuilder.create().uv(74, 0).cuboid(-4.5F, -5.0F, -5.5F, 9.0F, 5.0F, 11.0F), ModelTransform.pivot(0.0F, -12.0F, -10.0F)
		);
		modelPartData2.addChild(
			EntityModelPartNames.TAIL, ModelPartBuilder.create().uv(122, 0).cuboid(-1.5F, 0.0F, 0.0F, 3.0F, 14.0F, 0.0F), ModelTransform.pivot(0.0F, -9.0F, 3.5F)
		);
		ModelPartData modelPartData3 = modelPartData2.addChild(
			EntityModelPartNames.HEAD,
			ModelPartBuilder.create()
				.uv(60, 24)
				.cuboid(-3.5F, -7.0F, -15.0F, 7.0F, 8.0F, 19.0F)
				.uv(21, 0)
				.cuboid(-3.5F, -21.0F, -15.0F, 7.0F, 14.0F, 7.0F)
				.uv(50, 0)
				.cuboid(-2.5F, -21.0F, -21.0F, 5.0F, 5.0F, 6.0F),
			ModelTransform.pivot(0.0F, -3.0F, -19.5F)
		);
		modelPartData3.addChild(
			EntityModelPartNames.LEFT_EAR, ModelPartBuilder.create().uv(45, 0).cuboid(-0.5F, 0.5F, -1.0F, 3.0F, 1.0F, 2.0F), ModelTransform.pivot(2.5F, -21.0F, -9.5F)
		);
		modelPartData3.addChild(
			EntityModelPartNames.RIGHT_EAR, ModelPartBuilder.create().uv(67, 0).cuboid(-2.5F, 0.5F, -1.0F, 3.0F, 1.0F, 2.0F), ModelTransform.pivot(-2.5F, -21.0F, -9.5F)
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_HIND_LEG,
			ModelPartBuilder.create().uv(58, 16).cuboid(-2.5F, 2.0F, -2.5F, 5.0F, 21.0F, 5.0F),
			ModelTransform.pivot(4.9F, 1.0F, 9.5F)
		);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_HIND_LEG,
			ModelPartBuilder.create().uv(94, 16).cuboid(-2.5F, 2.0F, -2.5F, 5.0F, 21.0F, 5.0F),
			ModelTransform.pivot(-4.9F, 1.0F, 9.5F)
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_FRONT_LEG,
			ModelPartBuilder.create().uv(0, 0).cuboid(-2.5F, 2.0F, -2.5F, 5.0F, 21.0F, 5.0F),
			ModelTransform.pivot(4.9F, 1.0F, -10.5F)
		);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_FRONT_LEG,
			ModelPartBuilder.create().uv(0, 26).cuboid(-2.5F, 2.0F, -2.5F, 5.0F, 21.0F, 5.0F),
			ModelTransform.pivot(-4.9F, 1.0F, -10.5F)
		);
		modelPartData2.addChild(
			"saddle",
			ModelPartBuilder.create()
				.uv(74, 64)
				.cuboid(-4.5F, -17.0F, -15.5F, 9.0F, 5.0F, 11.0F, dilation)
				.uv(92, 114)
				.cuboid(-3.5F, -20.0F, -15.5F, 7.0F, 3.0F, 11.0F, dilation)
				.uv(0, 89)
				.cuboid(-7.5F, -12.0F, -23.5F, 15.0F, 12.0F, 27.0F, dilation),
			ModelTransform.pivot(0.0F, 0.0F, 0.0F)
		);
		modelPartData3.addChild(
			"reins",
			ModelPartBuilder.create()
				.uv(98, 42)
				.cuboid(3.51F, -18.0F, -17.0F, 0.0F, 7.0F, 15.0F)
				.uv(84, 57)
				.cuboid(-3.5F, -18.0F, -2.0F, 7.0F, 7.0F, 0.0F)
				.uv(98, 42)
				.cuboid(-3.51F, -18.0F, -17.0F, 0.0F, 7.0F, 15.0F),
			ModelTransform.pivot(0.0F, 0.0F, 0.0F)
		);
		modelPartData3.addChild(
			"bridle",
			ModelPartBuilder.create()
				.uv(60, 87)
				.cuboid(-3.5F, -7.0F, -15.0F, 7.0F, 8.0F, 19.0F, dilation)
				.uv(21, 64)
				.cuboid(-3.5F, -21.0F, -15.0F, 7.0F, 14.0F, 7.0F, dilation)
				.uv(50, 64)
				.cuboid(-2.5F, -21.0F, -21.0F, 5.0F, 5.0F, 6.0F, dilation)
				.uv(74, 70)
				.cuboid(2.5F, -19.0F, -18.0F, 1.0F, 2.0F, 2.0F)
				.uv(74, 70)
				.mirrored()
				.cuboid(-3.5F, -19.0F, -18.0F, 1.0F, 2.0F, 2.0F),
			ModelTransform.pivot(0.0F, 0.0F, 0.0F)
		);
		return TexturedModelData.of(modelData, 128, 128);
	}

	public void setAngles(T camelEntity, float f, float g, float h, float i, float j) {
		this.getPart().traverse().forEach(ModelPart::resetTransform);
		this.setHeadAngles(camelEntity, i, j, h);
		this.updateVisibleParts(camelEntity);
		this.animateMovement(CamelAnimations.WALKING, f, g, 2.0F, 2.5F);
		this.updateAnimation(camelEntity.sittingTransitionAnimationState, CamelAnimations.SITTING_TRANSITION, h, 1.0F);
		this.updateAnimation(camelEntity.sittingAnimationState, CamelAnimations.SITTING, h, 1.0F);
		this.updateAnimation(camelEntity.standingTransitionAnimationState, CamelAnimations.STANDING_TRANSITION, h, 1.0F);
		this.updateAnimation(camelEntity.idlingAnimationState, CamelAnimations.IDLING, h, 1.0F);
		this.updateAnimation(camelEntity.dashingAnimationState, CamelAnimations.DASHING, h, 1.0F);
	}

	private void setHeadAngles(T entity, float headYaw, float headPitch, float animationProgress) {
		headYaw = MathHelper.clamp(headYaw, -30.0F, 30.0F);
		headPitch = MathHelper.clamp(headPitch, -25.0F, 45.0F);
		if (entity.getJumpCooldown() > 0) {
			float f = animationProgress - (float)entity.age;
			float g = 45.0F * ((float)entity.getJumpCooldown() - f) / 55.0F;
			headPitch = MathHelper.clamp(headPitch + g, -25.0F, 70.0F);
		}

		this.head.yaw = headYaw * (float) (Math.PI / 180.0);
		this.head.pitch = headPitch * (float) (Math.PI / 180.0);
	}

	private void updateVisibleParts(T camel) {
		boolean bl = camel.isSaddled();
		boolean bl2 = camel.hasPassengers();

		for (ModelPart modelPart : this.saddleAndBridle) {
			modelPart.visible = bl;
		}

		for (ModelPart modelPart : this.reins) {
			modelPart.visible = bl2 && bl;
		}
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		if (this.child) {
			matrices.push();
			matrices.scale(0.45F, 0.45F, 0.45F);
			matrices.translate(0.0F, 1.834375F, 0.0F);
			this.getPart().render(matrices, vertices, light, overlay, color);
			matrices.pop();
		} else {
			this.getPart().render(matrices, vertices, light, overlay, color);
		}
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}
}
