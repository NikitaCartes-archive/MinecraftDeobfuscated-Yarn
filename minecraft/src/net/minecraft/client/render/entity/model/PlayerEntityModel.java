package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5605;
import net.minecraft.class_5606;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
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
		this.ears = modelPart.method_32086("ear");
		this.cape = modelPart.method_32086("cloak");
		this.leftSleeve = modelPart.method_32086("left_sleeve");
		this.rightSleeve = modelPart.method_32086("right_sleeve");
		this.leftPantLeg = modelPart.method_32086("left_pants");
		this.rightPantLeg = modelPart.method_32086("right_pants");
		this.jacket = modelPart.method_32086("jacket");
		this.field_27466 = (List<ModelPart>)modelPart.method_32088().filter(modelPartx -> !modelPartx.method_32087()).collect(ImmutableList.toImmutableList());
	}

	public static class_5609 method_32028(class_5605 arg, boolean bl) {
		class_5609 lv = BipedEntityModel.method_32011(arg, 0.0F);
		class_5610 lv2 = lv.method_32111();
		lv2.method_32117("ear", class_5606.method_32108().method_32101(24, 0).method_32098(-3.0F, -6.0F, -1.0F, 6.0F, 6.0F, 1.0F, arg), class_5603.field_27701);
		lv2.method_32117(
			"cloak",
			class_5606.method_32108().method_32101(0, 0).method_32099(-5.0F, 0.0F, -1.0F, 10.0F, 16.0F, 1.0F, arg, 1.0F, 0.5F),
			class_5603.method_32090(0.0F, 0.0F, 0.0F)
		);
		float f = 0.25F;
		if (bl) {
			lv2.method_32117(
				"left_arm",
				class_5606.method_32108().method_32101(32, 48).method_32098(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, arg),
				class_5603.method_32090(5.0F, 2.5F, 0.0F)
			);
			lv2.method_32117(
				"right_arm",
				class_5606.method_32108().method_32101(40, 16).method_32098(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, arg),
				class_5603.method_32090(-5.0F, 2.5F, 0.0F)
			);
			lv2.method_32117(
				"left_sleeve",
				class_5606.method_32108().method_32101(48, 48).method_32098(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, arg.method_32094(0.25F)),
				class_5603.method_32090(5.0F, 2.5F, 0.0F)
			);
			lv2.method_32117(
				"right_sleeve",
				class_5606.method_32108().method_32101(40, 32).method_32098(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, arg.method_32094(0.25F)),
				class_5603.method_32090(-5.0F, 2.5F, 0.0F)
			);
		} else {
			lv2.method_32117(
				"left_arm",
				class_5606.method_32108().method_32101(32, 48).method_32098(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, arg),
				class_5603.method_32090(5.0F, 2.0F, 0.0F)
			);
			lv2.method_32117(
				"left_sleeve",
				class_5606.method_32108().method_32101(48, 48).method_32098(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, arg.method_32094(0.25F)),
				class_5603.method_32090(5.0F, 2.0F, 0.0F)
			);
			lv2.method_32117(
				"right_sleeve",
				class_5606.method_32108().method_32101(40, 32).method_32098(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, arg.method_32094(0.25F)),
				class_5603.method_32090(-5.0F, 2.0F, 0.0F)
			);
		}

		lv2.method_32117(
			"left_leg",
			class_5606.method_32108().method_32101(16, 48).method_32098(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, arg),
			class_5603.method_32090(1.9F, 12.0F, 0.0F)
		);
		lv2.method_32117(
			"left_pants",
			class_5606.method_32108().method_32101(0, 48).method_32098(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, arg.method_32094(0.25F)),
			class_5603.method_32090(1.9F, 12.0F, 0.0F)
		);
		lv2.method_32117(
			"right_pants",
			class_5606.method_32108().method_32101(0, 32).method_32098(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, arg.method_32094(0.25F)),
			class_5603.method_32090(-1.9F, 12.0F, 0.0F)
		);
		lv2.method_32117(
			"jacket",
			class_5606.method_32108().method_32101(16, 32).method_32098(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, arg.method_32094(0.25F)),
			class_5603.field_27701
		);
		return lv;
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return Iterables.concat(super.getBodyParts(), ImmutableList.of(this.leftPantLeg, this.rightPantLeg, this.leftSleeve, this.rightSleeve, this.jacket));
	}

	public void renderEars(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
		this.ears.copyPositionAndRotation(this.head);
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
		this.leftPantLeg.copyPositionAndRotation(this.leftLeg);
		this.rightPantLeg.copyPositionAndRotation(this.rightLeg);
		this.leftSleeve.copyPositionAndRotation(this.field_27433);
		this.rightSleeve.copyPositionAndRotation(this.rightArm);
		this.jacket.copyPositionAndRotation(this.torso);
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
