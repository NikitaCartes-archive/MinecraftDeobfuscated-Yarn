package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ShulkerEntityModel<T extends ShulkerEntity> extends CompositeEntityModel<T> {
	private final ModelPart bottomShell;
	private final ModelPart topShell;
	private final ModelPart head;

	public ShulkerEntityModel(ModelPart modelPart) {
		super(RenderLayer::getEntityCutoutNoCullZOffset);
		this.topShell = modelPart.method_32086("lid");
		this.bottomShell = modelPart.method_32086("base");
		this.head = modelPart.method_32086("head");
	}

	public static class_5607 method_32041() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		lv2.method_32117(
			"lid", class_5606.method_32108().method_32101(0, 0).method_32097(-8.0F, -16.0F, -8.0F, 16.0F, 12.0F, 16.0F), class_5603.method_32090(0.0F, 24.0F, 0.0F)
		);
		lv2.method_32117(
			"base", class_5606.method_32108().method_32101(0, 28).method_32097(-8.0F, -8.0F, -8.0F, 16.0F, 8.0F, 16.0F), class_5603.method_32090(0.0F, 24.0F, 0.0F)
		);
		lv2.method_32117(
			"head", class_5606.method_32108().method_32101(0, 52).method_32097(-3.0F, 0.0F, -3.0F, 6.0F, 6.0F, 6.0F), class_5603.method_32090(0.0F, 12.0F, 0.0F)
		);
		return class_5607.method_32110(lv, 64, 64);
	}

	public void setAngles(T shulkerEntity, float f, float g, float h, float i, float j) {
		float k = h - (float)shulkerEntity.age;
		float l = (0.5F + shulkerEntity.getOpenProgress(k)) * (float) Math.PI;
		float m = -1.0F + MathHelper.sin(l);
		float n = 0.0F;
		if (l > (float) Math.PI) {
			n = MathHelper.sin(h * 0.1F) * 0.7F;
		}

		this.topShell.setPivot(0.0F, 16.0F + MathHelper.sin(l) * 8.0F + n, 0.0F);
		if (shulkerEntity.getOpenProgress(k) > 0.3F) {
			this.topShell.yaw = m * m * m * m * (float) Math.PI * 0.125F;
		} else {
			this.topShell.yaw = 0.0F;
		}

		this.head.pitch = j * (float) (Math.PI / 180.0);
		this.head.yaw = (shulkerEntity.headYaw - 180.0F - shulkerEntity.bodyYaw) * (float) (Math.PI / 180.0);
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return ImmutableList.<ModelPart>of(this.bottomShell, this.topShell);
	}

	public ModelPart getBottomShell() {
		return this.bottomShell;
	}

	public ModelPart getTopShell() {
		return this.topShell;
	}

	public ModelPart getHead() {
		return this.head;
	}
}
