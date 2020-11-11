package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.util.math.Dilation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class VillagerResemblingModel<T extends Entity> extends SinglePartEntityModel<T> implements ModelWithHead, ModelWithHat {
	private final ModelPart field_27526;
	private final ModelPart field_27527;
	private final ModelPart field_27528;
	private final ModelPart field_27529;
	private final ModelPart field_27530;
	private final ModelPart field_27531;
	protected final ModelPart field_27525;

	public VillagerResemblingModel(ModelPart modelPart) {
		this.field_27526 = modelPart;
		this.field_27527 = modelPart.getChild("head");
		this.field_27528 = this.field_27527.getChild("hat");
		this.field_27529 = this.field_27528.getChild("hat_rim");
		this.field_27525 = this.field_27527.getChild("nose");
		this.field_27530 = modelPart.getChild("right_leg");
		this.field_27531 = modelPart.getChild("left_leg");
	}

	public static ModelData getModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		float f = 0.5F;
		ModelPartData modelPartData2 = modelPartData.addChild(
			"head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F), ModelTransform.NONE
		);
		ModelPartData modelPartData3 = modelPartData2.addChild(
			"hat", ModelPartBuilder.create().uv(32, 0).cuboid(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new Dilation(0.5F)), ModelTransform.NONE
		);
		modelPartData3.addChild(
			"hat_rim", ModelPartBuilder.create().uv(30, 47).cuboid(-8.0F, -8.0F, -6.0F, 16.0F, 16.0F, 1.0F), ModelTransform.rotation((float) (-Math.PI / 2), 0.0F, 0.0F)
		);
		modelPartData2.addChild("nose", ModelPartBuilder.create().uv(24, 0).cuboid(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F), ModelTransform.pivot(0.0F, -2.0F, 0.0F));
		ModelPartData modelPartData4 = modelPartData.addChild(
			"body", ModelPartBuilder.create().uv(16, 20).cuboid(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F), ModelTransform.NONE
		);
		modelPartData4.addChild("jacket", ModelPartBuilder.create().uv(0, 38).cuboid(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, new Dilation(0.5F)), ModelTransform.NONE);
		modelPartData.addChild(
			"arms",
			ModelPartBuilder.create()
				.uv(44, 22)
				.cuboid(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F)
				.uv(44, 22)
				.cuboid(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, true)
				.uv(40, 38)
				.cuboid(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F),
			ModelTransform.of(0.0F, 3.0F, -1.0F, -0.75F, 0.0F, 0.0F)
		);
		modelPartData.addChild(
			"right_leg", ModelPartBuilder.create().uv(0, 22).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), ModelTransform.pivot(-2.0F, 12.0F, 0.0F)
		);
		modelPartData.addChild(
			"left_leg", ModelPartBuilder.create().uv(0, 22).mirrored().cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), ModelTransform.pivot(2.0F, 12.0F, 0.0F)
		);
		return modelData;
	}

	@Override
	public ModelPart getPart() {
		return this.field_27526;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		boolean bl = false;
		if (entity instanceof MerchantEntity) {
			bl = ((MerchantEntity)entity).getHeadRollingTimeLeft() > 0;
		}

		this.field_27527.yaw = headYaw * (float) (Math.PI / 180.0);
		this.field_27527.pitch = headPitch * (float) (Math.PI / 180.0);
		if (bl) {
			this.field_27527.roll = 0.3F * MathHelper.sin(0.45F * animationProgress);
			this.field_27527.pitch = 0.4F;
		} else {
			this.field_27527.roll = 0.0F;
		}

		this.field_27530.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance * 0.5F;
		this.field_27531.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 1.4F * limbDistance * 0.5F;
		this.field_27530.yaw = 0.0F;
		this.field_27531.yaw = 0.0F;
	}

	@Override
	public ModelPart getHead() {
		return this.field_27527;
	}

	@Override
	public void setHatVisible(boolean visible) {
		this.field_27527.visible = visible;
		this.field_27528.visible = visible;
		this.field_27529.visible = visible;
	}
}
