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
import net.minecraft.entity.mob.ZombieEntity;

@Environment(EnvType.CLIENT)
public class ZombieVillagerEntityModel<T extends ZombieEntity> extends BipedEntityModel<T> implements ModelWithHat {
	private final ModelPart hatRim = this.hat.getChild(EntityModelPartNames.HAT_RIM);

	public ZombieVillagerEntityModel(ModelPart modelPart) {
		super(modelPart);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = BipedEntityModel.getModelData(Dilation.NONE, 0.0F);
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(
			EntityModelPartNames.HEAD,
			new ModelPartBuilder().uv(0, 0).cuboid(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F).uv(24, 0).cuboid(-1.0F, -3.0F, -6.0F, 2.0F, 4.0F, 2.0F),
			ModelTransform.NONE
		);
		ModelPartData modelPartData2 = modelPartData.addChild(
			EntityModelPartNames.HAT, ModelPartBuilder.create().uv(32, 0).cuboid(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new Dilation(0.5F)), ModelTransform.NONE
		);
		modelPartData2.addChild(
			EntityModelPartNames.HAT_RIM,
			ModelPartBuilder.create().uv(30, 47).cuboid(-8.0F, -8.0F, -6.0F, 16.0F, 16.0F, 1.0F),
			ModelTransform.rotation((float) (-Math.PI / 2), 0.0F, 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.BODY,
			ModelPartBuilder.create()
				.uv(16, 20)
				.cuboid(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F)
				.uv(0, 38)
				.cuboid(-4.0F, 0.0F, -3.0F, 8.0F, 20.0F, 6.0F, new Dilation(0.05F)),
			ModelTransform.NONE
		);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_ARM, ModelPartBuilder.create().uv(44, 22).cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F), ModelTransform.pivot(-5.0F, 2.0F, 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_ARM,
			ModelPartBuilder.create().uv(44, 22).mirrored().cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F),
			ModelTransform.pivot(5.0F, 2.0F, 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create().uv(0, 22).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), ModelTransform.pivot(-2.0F, 12.0F, 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_LEG,
			ModelPartBuilder.create().uv(0, 22).mirrored().cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
			ModelTransform.pivot(2.0F, 12.0F, 0.0F)
		);
		return TexturedModelData.of(modelData, 64, 64);
	}

	public static TexturedModelData getArmorTexturedModelData(Dilation dilation) {
		ModelData modelData = BipedEntityModel.getModelData(dilation, 0.0F);
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(
			EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -10.0F, -4.0F, 8.0F, 8.0F, 8.0F, dilation), ModelTransform.NONE
		);
		modelPartData.addChild(
			EntityModelPartNames.BODY, ModelPartBuilder.create().uv(16, 16).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, dilation.add(0.1F)), ModelTransform.NONE
		);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_LEG,
			ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation.add(0.1F)),
			ModelTransform.pivot(-2.0F, 12.0F, 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_LEG,
			ModelPartBuilder.create().uv(0, 16).mirrored().cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation.add(0.1F)),
			ModelTransform.pivot(2.0F, 12.0F, 0.0F)
		);
		modelPartData.getChild(EntityModelPartNames.HAT).addChild(EntityModelPartNames.HAT_RIM, ModelPartBuilder.create(), ModelTransform.NONE);
		return TexturedModelData.of(modelData, 64, 32);
	}

	public void setAngles(T zombieEntity, float f, float g, float h, float i, float j) {
		super.setAngles(zombieEntity, f, g, h, i, j);
		CrossbowPosing.meleeAttack(this.leftArm, this.rightArm, zombieEntity.isAttacking(), this.handSwingProgress, h);
	}

	@Override
	public void setHatVisible(boolean visible) {
		this.head.visible = visible;
		this.hat.visible = visible;
		this.hatRim.visible = visible;
	}
}
