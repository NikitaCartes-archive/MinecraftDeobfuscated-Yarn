package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5605;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class LlamaEntityModel<T extends AbstractDonkeyEntity> extends EntityModel<T> {
	private final ModelPart field_27443;
	private final ModelPart field_27444;
	private final ModelPart field_27445;
	private final ModelPart field_27446;
	private final ModelPart field_27447;
	private final ModelPart field_27448;
	private final ModelPart field_27449;
	private final ModelPart field_27450;

	public LlamaEntityModel(ModelPart modelPart) {
		this.field_27443 = modelPart.method_32086("head");
		this.field_27444 = modelPart.method_32086("body");
		this.field_27449 = modelPart.method_32086("right_chest");
		this.field_27450 = modelPart.method_32086("left_chest");
		this.field_27445 = modelPart.method_32086("right_hind_leg");
		this.field_27446 = modelPart.method_32086("left_hind_leg");
		this.field_27447 = modelPart.method_32086("right_front_leg");
		this.field_27448 = modelPart.method_32086("left_front_leg");
	}

	public static class_5607 method_32018(class_5605 arg) {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		lv2.method_32117(
			"head",
			class_5606.method_32108()
				.method_32101(0, 0)
				.method_32098(-2.0F, -14.0F, -10.0F, 4.0F, 4.0F, 9.0F, arg)
				.method_32101(0, 14)
				.method_32103("neck", -4.0F, -16.0F, -6.0F, 8.0F, 18.0F, 6.0F, arg)
				.method_32101(17, 0)
				.method_32103("ear", -4.0F, -19.0F, -4.0F, 3.0F, 3.0F, 2.0F, arg)
				.method_32101(17, 0)
				.method_32103("ear", 1.0F, -19.0F, -4.0F, 3.0F, 3.0F, 2.0F, arg),
			class_5603.method_32090(0.0F, 7.0F, -6.0F)
		);
		lv2.method_32117(
			"body",
			class_5606.method_32108().method_32101(29, 0).method_32098(-6.0F, -10.0F, -7.0F, 12.0F, 18.0F, 10.0F, arg),
			class_5603.method_32091(0.0F, 5.0F, 2.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		lv2.method_32117(
			"right_chest",
			class_5606.method_32108().method_32101(45, 28).method_32098(-3.0F, 0.0F, 0.0F, 8.0F, 8.0F, 3.0F, arg),
			class_5603.method_32091(-8.5F, 3.0F, 3.0F, 0.0F, (float) (Math.PI / 2), 0.0F)
		);
		lv2.method_32117(
			"left_chest",
			class_5606.method_32108().method_32101(45, 41).method_32098(-3.0F, 0.0F, 0.0F, 8.0F, 8.0F, 3.0F, arg),
			class_5603.method_32091(5.5F, 3.0F, 3.0F, 0.0F, (float) (Math.PI / 2), 0.0F)
		);
		int i = 4;
		int j = 14;
		class_5606 lv3 = class_5606.method_32108().method_32101(29, 29).method_32098(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, arg);
		lv2.method_32117("right_hind_leg", lv3, class_5603.method_32090(-3.5F, 10.0F, 6.0F));
		lv2.method_32117("left_hind_leg", lv3, class_5603.method_32090(3.5F, 10.0F, 6.0F));
		lv2.method_32117("right_front_leg", lv3, class_5603.method_32090(-3.5F, 10.0F, -5.0F));
		lv2.method_32117("left_front_leg", lv3, class_5603.method_32090(3.5F, 10.0F, -5.0F));
		return class_5607.method_32110(lv, 128, 64);
	}

	public void setAngles(T abstractDonkeyEntity, float f, float g, float h, float i, float j) {
		this.field_27443.pitch = j * (float) (Math.PI / 180.0);
		this.field_27443.yaw = i * (float) (Math.PI / 180.0);
		this.field_27445.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		this.field_27446.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.field_27447.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.field_27448.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		boolean bl = !abstractDonkeyEntity.isBaby() && abstractDonkeyEntity.hasChest();
		this.field_27449.visible = bl;
		this.field_27450.visible = bl;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		if (this.child) {
			float f = 2.0F;
			matrices.push();
			float g = 0.7F;
			matrices.scale(0.71428573F, 0.64935064F, 0.7936508F);
			matrices.translate(0.0, 1.3125, 0.22F);
			this.field_27443.render(matrices, vertices, light, overlay, red, green, blue, alpha);
			matrices.pop();
			matrices.push();
			float h = 1.1F;
			matrices.scale(0.625F, 0.45454544F, 0.45454544F);
			matrices.translate(0.0, 2.0625, 0.0);
			this.field_27444.render(matrices, vertices, light, overlay, red, green, blue, alpha);
			matrices.pop();
			matrices.push();
			matrices.scale(0.45454544F, 0.41322312F, 0.45454544F);
			matrices.translate(0.0, 2.0625, 0.0);
			ImmutableList.of(this.field_27445, this.field_27446, this.field_27447, this.field_27448, this.field_27449, this.field_27450)
				.forEach(modelPart -> modelPart.render(matrices, vertices, light, overlay, red, green, blue, alpha));
			matrices.pop();
		} else {
			ImmutableList.of(
					this.field_27443, this.field_27444, this.field_27445, this.field_27446, this.field_27447, this.field_27448, this.field_27449, this.field_27450
				)
				.forEach(modelPart -> modelPart.render(matrices, vertices, light, overlay, red, green, blue, alpha));
		}
	}
}
