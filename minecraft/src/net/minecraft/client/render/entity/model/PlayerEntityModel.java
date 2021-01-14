package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;

@Environment(EnvType.CLIENT)
public class PlayerEntityModel<T extends LivingEntity> extends BipedEntityModel<T> {
	private List<ModelPart> parts = Lists.<ModelPart>newArrayList();
	public final ModelPart leftSleeve;
	public final ModelPart rightSleeve;
	public final ModelPart leftPants;
	public final ModelPart rightPants;
	public final ModelPart jacket;
	private final ModelPart cloak;
	private final ModelPart ear;
	private final boolean thinArms;

	public PlayerEntityModel(float scale, boolean thinArms) {
		super(RenderLayer::getEntityTranslucent, scale, 0.0F, 64, 64);
		this.thinArms = thinArms;
		this.ear = new ModelPart(this, 24, 0);
		this.ear.addCuboid(-3.0F, -6.0F, -1.0F, 6.0F, 6.0F, 1.0F, scale);
		this.cloak = new ModelPart(this, 0, 0);
		this.cloak.setTextureSize(64, 32);
		this.cloak.addCuboid(-5.0F, 0.0F, -1.0F, 10.0F, 16.0F, 1.0F, scale);
		if (thinArms) {
			this.leftArm = new ModelPart(this, 32, 48);
			this.leftArm.addCuboid(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, scale);
			this.leftArm.setPivot(5.0F, 2.5F, 0.0F);
			this.rightArm = new ModelPart(this, 40, 16);
			this.rightArm.addCuboid(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, scale);
			this.rightArm.setPivot(-5.0F, 2.5F, 0.0F);
			this.leftSleeve = new ModelPart(this, 48, 48);
			this.leftSleeve.addCuboid(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, scale + 0.25F);
			this.leftSleeve.setPivot(5.0F, 2.5F, 0.0F);
			this.rightSleeve = new ModelPart(this, 40, 32);
			this.rightSleeve.addCuboid(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, scale + 0.25F);
			this.rightSleeve.setPivot(-5.0F, 2.5F, 10.0F);
		} else {
			this.leftArm = new ModelPart(this, 32, 48);
			this.leftArm.addCuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale);
			this.leftArm.setPivot(5.0F, 2.0F, 0.0F);
			this.leftSleeve = new ModelPart(this, 48, 48);
			this.leftSleeve.addCuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale + 0.25F);
			this.leftSleeve.setPivot(5.0F, 2.0F, 0.0F);
			this.rightSleeve = new ModelPart(this, 40, 32);
			this.rightSleeve.addCuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale + 0.25F);
			this.rightSleeve.setPivot(-5.0F, 2.0F, 10.0F);
		}

		this.leftLeg = new ModelPart(this, 16, 48);
		this.leftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale);
		this.leftLeg.setPivot(1.9F, 12.0F, 0.0F);
		this.leftPants = new ModelPart(this, 0, 48);
		this.leftPants.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale + 0.25F);
		this.leftPants.setPivot(1.9F, 12.0F, 0.0F);
		this.rightPants = new ModelPart(this, 0, 32);
		this.rightPants.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale + 0.25F);
		this.rightPants.setPivot(-1.9F, 12.0F, 0.0F);
		this.jacket = new ModelPart(this, 16, 32);
		this.jacket.addCuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, scale + 0.25F);
		this.jacket.setPivot(0.0F, 0.0F, 0.0F);
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

	@Override
	public void accept(ModelPart modelPart) {
		if (this.parts == null) {
			this.parts = Lists.<ModelPart>newArrayList();
		}

		this.parts.add(modelPart);
	}
}
