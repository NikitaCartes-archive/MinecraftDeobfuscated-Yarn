package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;

@Environment(EnvType.CLIENT)
public class PlayerEntityModel<T extends LivingEntity> extends BipedEntityModel<T> {
	private static final String EAR = "ear";
	private static final String CLOAK = "cloak";
	private static final String LEFT_SLEEVE = "left_sleeve";
	private static final String RIGHT_SLEEVE = "right_sleeve";
	private static final String LEFT_PANTS = "left_pants";
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
	private final ModelPart cloak;
	private final ModelPart ear;
	private final boolean thinArms;

	public PlayerEntityModel(ModelPart root, boolean thinArms) {
		super(root, RenderLayer::getEntityTranslucent);
		this.thinArms = thinArms;
		this.ear = root.getChild("ear");
		this.cloak = root.getChild("cloak");
		this.leftSleeve = root.getChild("left_sleeve");
		this.rightSleeve = root.getChild("right_sleeve");
		this.leftPants = root.getChild("left_pants");
		this.rightPants = root.getChild("right_pants");
		this.jacket = root.getChild(EntityModelPartNames.JACKET);
		this.parts = (List<ModelPart>)root.traverse().filter(part -> !part.isEmpty()).collect(ImmutableList.toImmutableList());
	}

	public static ModelData getTexturedModelData(Dilation dilation, boolean slim) {
		ModelData modelData = BipedEntityModel.getModelData(dilation, 0.0F);
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("ear", ModelPartBuilder.create().uv(24, 0).cuboid(-3.0F, -6.0F, -1.0F, 6.0F, 6.0F, 1.0F, dilation), ModelTransform.NONE);
		modelPartData.addChild(
			"cloak", ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, 0.0F, -1.0F, 10.0F, 16.0F, 1.0F, dilation, 1.0F, 0.5F), ModelTransform.pivot(0.0F, 0.0F, 0.0F)
		);
		float f = 0.25F;
		if (slim) {
			modelPartData.addChild(
				EntityModelPartNames.LEFT_ARM,
				ModelPartBuilder.create().uv(32, 48).cuboid(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, dilation),
				ModelTransform.pivot(5.0F, 2.5F, 0.0F)
			);
			modelPartData.addChild(
				EntityModelPartNames.RIGHT_ARM,
				ModelPartBuilder.create().uv(40, 16).cuboid(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, dilation),
				ModelTransform.pivot(-5.0F, 2.5F, 0.0F)
			);
			modelPartData.addChild(
				"left_sleeve",
				ModelPartBuilder.create().uv(48, 48).cuboid(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, dilation.add(0.25F)),
				ModelTransform.pivot(5.0F, 2.5F, 0.0F)
			);
			modelPartData.addChild(
				"right_sleeve",
				ModelPartBuilder.create().uv(40, 32).cuboid(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, dilation.add(0.25F)),
				ModelTransform.pivot(-5.0F, 2.5F, 0.0F)
			);
		} else {
			modelPartData.addChild(
				EntityModelPartNames.LEFT_ARM,
				ModelPartBuilder.create().uv(32, 48).cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation),
				ModelTransform.pivot(5.0F, 2.0F, 0.0F)
			);
			modelPartData.addChild(
				"left_sleeve",
				ModelPartBuilder.create().uv(48, 48).cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation.add(0.25F)),
				ModelTransform.pivot(5.0F, 2.0F, 0.0F)
			);
			modelPartData.addChild(
				"right_sleeve",
				ModelPartBuilder.create().uv(40, 32).cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation.add(0.25F)),
				ModelTransform.pivot(-5.0F, 2.0F, 0.0F)
			);
		}

		modelPartData.addChild(
			EntityModelPartNames.LEFT_LEG,
			ModelPartBuilder.create().uv(16, 48).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation),
			ModelTransform.pivot(1.9F, 12.0F, 0.0F)
		);
		modelPartData.addChild(
			"left_pants",
			ModelPartBuilder.create().uv(0, 48).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation.add(0.25F)),
			ModelTransform.pivot(1.9F, 12.0F, 0.0F)
		);
		modelPartData.addChild(
			"right_pants",
			ModelPartBuilder.create().uv(0, 32).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation.add(0.25F)),
			ModelTransform.pivot(-1.9F, 12.0F, 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.JACKET, ModelPartBuilder.create().uv(16, 32).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, dilation.add(0.25F)), ModelTransform.NONE
		);
		return modelData;
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return Iterables.concat(super.getBodyParts(), ImmutableList.of(this.leftPants, this.rightPants, this.leftSleeve, this.rightSleeve, this.jacket));
	}

	public void renderEars(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
		this.ear.copyTransform(this.head);
		this.ear.pivotX = 0.0F;
		this.ear.pivotY = 0.0F;
		this.ear.render(matrices, vertices, light, overlay);
	}

	public void renderCape(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
		this.cloak.render(matrices, vertices, light, overlay);
	}

	@Override
	public void setAngles(T livingEntity, float f, float g, float h, float i, float j) {
		super.setAngles(livingEntity, f, g, h, i, j);
		this.leftPants.copyTransform(this.leftLeg);
		this.rightPants.copyTransform(this.rightLeg);
		this.leftSleeve.copyTransform(this.leftArm);
		this.rightSleeve.copyTransform(this.rightArm);
		this.jacket.copyTransform(this.body);
		if (livingEntity.getEquippedStack(EquipmentSlot.CHEST).isEmpty()) {
			if (livingEntity.isInSneakingPose()) {
				this.cloak.pivotZ = 1.4F;
				this.cloak.pivotY = 1.85F;
			} else {
				this.cloak.pivotZ = 0.0F;
				this.cloak.pivotY = 0.0F;
			}
		} else if (livingEntity.isInSneakingPose()) {
			this.cloak.pivotZ = 0.3F;
			this.cloak.pivotY = 0.8F;
		} else {
			this.cloak.pivotZ = -1.1F;
			this.cloak.pivotY = -0.85F;
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
		this.cloak.visible = visible;
		this.ear.visible = visible;
	}

	@Override
	public void setArmAngle(Arm arm, MatrixStack matrices) {
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
		return (ModelPart)this.parts.get(random.nextInt(this.parts.size()));
	}
}
