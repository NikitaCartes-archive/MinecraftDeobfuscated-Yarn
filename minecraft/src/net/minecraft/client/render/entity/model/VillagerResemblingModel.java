package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5597;
import net.minecraft.class_5603;
import net.minecraft.class_5605;
import net.minecraft.class_5606;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class VillagerResemblingModel<T extends Entity> extends class_5597<T> implements ModelWithHead, ModelWithHat {
	private final ModelPart field_27526;
	private final ModelPart field_27527;
	private final ModelPart field_27528;
	private final ModelPart field_27529;
	private final ModelPart field_27530;
	private final ModelPart field_27531;
	protected final ModelPart field_27525;

	public VillagerResemblingModel(ModelPart modelPart) {
		this.field_27526 = modelPart;
		this.field_27527 = modelPart.method_32086("head");
		this.field_27528 = this.field_27527.method_32086("hat");
		this.field_27529 = this.field_27528.method_32086("hat_rim");
		this.field_27525 = this.field_27527.method_32086("nose");
		this.field_27530 = modelPart.method_32086("right_leg");
		this.field_27531 = modelPart.method_32086("left_leg");
	}

	public static class_5609 method_32064() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		float f = 0.5F;
		class_5610 lv3 = lv2.method_32117(
			"head", class_5606.method_32108().method_32101(0, 0).method_32097(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F), class_5603.field_27701
		);
		class_5610 lv4 = lv3.method_32117(
			"hat", class_5606.method_32108().method_32101(32, 0).method_32098(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new class_5605(0.5F)), class_5603.field_27701
		);
		lv4.method_32117(
			"hat_rim",
			class_5606.method_32108().method_32101(30, 47).method_32097(-8.0F, -8.0F, -6.0F, 16.0F, 16.0F, 1.0F),
			class_5603.method_32092((float) (-Math.PI / 2), 0.0F, 0.0F)
		);
		lv3.method_32117(
			"nose", class_5606.method_32108().method_32101(24, 0).method_32097(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F), class_5603.method_32090(0.0F, -2.0F, 0.0F)
		);
		class_5610 lv5 = lv2.method_32117(
			"body", class_5606.method_32108().method_32101(16, 20).method_32097(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F), class_5603.field_27701
		);
		lv5.method_32117(
			"jacket", class_5606.method_32108().method_32101(0, 38).method_32098(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, new class_5605(0.5F)), class_5603.field_27701
		);
		lv2.method_32117(
			"arms",
			class_5606.method_32108()
				.method_32101(44, 22)
				.method_32097(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F)
				.method_32101(44, 22)
				.method_32100(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, true)
				.method_32101(40, 38)
				.method_32097(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F),
			class_5603.method_32091(0.0F, 3.0F, -1.0F, -0.75F, 0.0F, 0.0F)
		);
		lv2.method_32117(
			"right_leg", class_5606.method_32108().method_32101(0, 22).method_32097(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), class_5603.method_32090(-2.0F, 12.0F, 0.0F)
		);
		lv2.method_32117(
			"left_leg",
			class_5606.method_32108().method_32101(0, 22).method_32096().method_32097(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
			class_5603.method_32090(2.0F, 12.0F, 0.0F)
		);
		return lv;
	}

	@Override
	public ModelPart method_32008() {
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
