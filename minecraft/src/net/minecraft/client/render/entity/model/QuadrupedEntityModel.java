package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5605;
import net.minecraft.class_5606;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class QuadrupedEntityModel<T extends Entity> extends AnimalModel<T> {
	protected final ModelPart head;
	protected final ModelPart torso;
	protected final ModelPart field_27476;
	protected final ModelPart field_27477;
	protected final ModelPart field_27478;
	protected final ModelPart field_27479;

	protected QuadrupedEntityModel(ModelPart modelPart, boolean bl, float f, float g, float h, float i, int j) {
		super(bl, f, g, h, i, (float)j);
		this.head = modelPart.method_32086("head");
		this.torso = modelPart.method_32086("body");
		this.field_27476 = modelPart.method_32086("right_hind_leg");
		this.field_27477 = modelPart.method_32086("left_hind_leg");
		this.field_27478 = modelPart.method_32086("right_front_leg");
		this.field_27479 = modelPart.method_32086("left_front_leg");
	}

	public static class_5609 method_32033(int i, class_5605 arg) {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		lv2.method_32117(
			"head",
			class_5606.method_32108().method_32101(0, 0).method_32098(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, arg),
			class_5603.method_32090(0.0F, (float)(18 - i), -6.0F)
		);
		lv2.method_32117(
			"body",
			class_5606.method_32108().method_32101(28, 8).method_32098(-5.0F, -10.0F, -7.0F, 10.0F, 16.0F, 8.0F, arg),
			class_5603.method_32091(0.0F, (float)(17 - i), 2.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		class_5606 lv3 = class_5606.method_32108().method_32101(0, 16).method_32098(-2.0F, 0.0F, -2.0F, 4.0F, (float)i, 4.0F, arg);
		lv2.method_32117("right_hind_leg", lv3, class_5603.method_32090(-3.0F, (float)(24 - i), 7.0F));
		lv2.method_32117("left_hind_leg", lv3, class_5603.method_32090(3.0F, (float)(24 - i), 7.0F));
		lv2.method_32117("right_front_leg", lv3, class_5603.method_32090(-3.0F, (float)(24 - i), -5.0F));
		lv2.method_32117("left_front_leg", lv3, class_5603.method_32090(3.0F, (float)(24 - i), -5.0F));
		return lv;
	}

	@Override
	protected Iterable<ModelPart> getHeadParts() {
		return ImmutableList.<ModelPart>of(this.head);
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return ImmutableList.<ModelPart>of(this.torso, this.field_27476, this.field_27477, this.field_27478, this.field_27479);
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		this.head.pitch = headPitch * (float) (Math.PI / 180.0);
		this.head.yaw = headYaw * (float) (Math.PI / 180.0);
		this.torso.pitch = (float) (Math.PI / 2);
		this.field_27476.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
		this.field_27477.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 1.4F * limbDistance;
		this.field_27478.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 1.4F * limbDistance;
		this.field_27479.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
	}
}
