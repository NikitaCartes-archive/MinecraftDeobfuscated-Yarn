package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.Dilation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;

@Environment(EnvType.CLIENT)
public class PlayerEntityModel<T extends LivingEntity> extends BipedEntityModel<T> {
	private final List<ModelPart> field_27466;
	public final ModelPart leftSleeve;
	public final ModelPart rightSleeve;
	public final ModelPart leftPantLeg;
	public final ModelPart rightPantLeg;
	public final ModelPart jacket;
	private final ModelPart cape;
	private final ModelPart ears;
	private final boolean thinArms;

	public PlayerEntityModel(ModelPart modelPart, boolean thinArms) {
		super(modelPart, RenderLayer::getEntityTranslucent);
		this.thinArms = thinArms;
		this.ears = modelPart.getChild("ear");
		this.cape = modelPart.getChild("cloak");
		this.leftSleeve = modelPart.getChild("left_sleeve");
		this.rightSleeve = modelPart.getChild("right_sleeve");
		this.leftPantLeg = modelPart.getChild("left_pants");
		this.rightPantLeg = modelPart.getChild("right_pants");
		this.jacket = modelPart.getChild("jacket");
		this.field_27466 = (List<ModelPart>)modelPart.traverse().filter(modelPartx -> !modelPartx.isEmpty()).collect(ImmutableList.toImmutableList());
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
				"left_arm", ModelPartBuilder.create().uv(32, 48).cuboid(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, dilation), ModelTransform.pivot(5.0F, 2.5F, 0.0F)
			);
			modelPartData.addChild(
				"right_arm", ModelPartBuilder.create().uv(40, 16).cuboid(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, dilation), ModelTransform.pivot(-5.0F, 2.5F, 0.0F)
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
				"left_arm", ModelPartBuilder.create().uv(32, 48).cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation), ModelTransform.pivot(5.0F, 2.0F, 0.0F)
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
			"left_leg", ModelPartBuilder.create().uv(16, 48).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation), ModelTransform.pivot(1.9F, 12.0F, 0.0F)
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
		modelPartData.addChild("jacket", ModelPartBuilder.create().uv(16, 32).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, dilation.add(0.25F)), ModelTransform.NONE);
		return modelData;
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return Iterables.concat(super.getBodyParts(), ImmutableList.of(this.leftPantLeg, this.rightPantLeg, this.leftSleeve, this.rightSleeve, this.jacket));
	}

	public void renderEars(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
		this.ears.copyTransform(this.head);
		this.ears.pivotX = 0.0F;
		this.ears.pivotY = 0.0F;
		this.ears.render(matrices, vertices, light, overlay);
	}

	public void renderCape(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
		this.cape.render(matrices, vertices, light, overlay);
	}

	@Override
	public void setAngles(T livingEntity, float f, float g, float h, float i, float j) {
		super.setAngles(livingEntity, f, g, h, i, j);
		this.leftPantLeg.copyTransform(this.leftLeg);
		this.rightPantLeg.copyTransform(this.rightLeg);
		this.leftSleeve.copyTransform(this.leftArm);
		this.rightSleeve.copyTransform(this.rightArm);
		this.jacket.copyTransform(this.torso);
		if (livingEntity.getEquippedStack(EquipmentSlot.CHEST).isEmpty()) {
			if (livingEntity.isInSneakingPose()) {
				this.cape.pivotZ = 1.4F;
				this.cape.pivotY = 1.85F;
			} else {
				this.cape.pivotZ = 0.0F;
				this.cape.pivotY = 0.0F;
			}
		} else if (livingEntity.isInSneakingPose()) {
			this.cape.pivotZ = 0.3F;
			this.cape.pivotY = 0.8F;
		} else {
			this.cape.pivotZ = -1.1F;
			this.cape.pivotY = -0.85F;
		}
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		this.leftSleeve.visible = visible;
		this.rightSleeve.visible = visible;
		this.leftPantLeg.visible = visible;
		this.rightPantLeg.visible = visible;
		this.jacket.visible = visible;
		this.cape.visible = visible;
		this.ears.visible = visible;
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
		return (ModelPart)this.field_27466.get(random.nextInt(this.field_27466.size()));
	}
}
