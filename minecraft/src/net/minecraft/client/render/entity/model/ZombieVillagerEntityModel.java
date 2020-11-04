package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5605;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.mob.ZombieEntity;

@Environment(EnvType.CLIENT)
public class ZombieVillagerEntityModel<T extends ZombieEntity> extends BipedEntityModel<T> implements ModelWithHat {
	private final ModelPart hat = this.helmet.method_32086("hat_rim");

	public ZombieVillagerEntityModel(ModelPart modelPart) {
		super(modelPart);
	}

	public static class_5607 method_32070() {
		class_5609 lv = BipedEntityModel.method_32011(class_5605.field_27715, 0.0F);
		class_5610 lv2 = lv.method_32111();
		lv2.method_32117(
			"head",
			new class_5606()
				.method_32101(0, 0)
				.method_32097(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F)
				.method_32101(24, 0)
				.method_32097(-1.0F, -3.0F, -6.0F, 2.0F, 4.0F, 2.0F),
			class_5603.field_27701
		);
		class_5610 lv3 = lv2.method_32117(
			"hat", class_5606.method_32108().method_32101(32, 0).method_32098(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new class_5605(0.5F)), class_5603.field_27701
		);
		lv3.method_32117(
			"hat_rim",
			class_5606.method_32108().method_32101(30, 47).method_32097(-8.0F, -8.0F, -6.0F, 16.0F, 16.0F, 1.0F),
			class_5603.method_32092((float) (-Math.PI / 2), 0.0F, 0.0F)
		);
		lv2.method_32117(
			"body",
			class_5606.method_32108()
				.method_32101(16, 20)
				.method_32097(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F)
				.method_32101(0, 38)
				.method_32098(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, new class_5605(0.05F)),
			class_5603.field_27701
		);
		lv2.method_32117(
			"right_arm", class_5606.method_32108().method_32101(44, 22).method_32097(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F), class_5603.method_32090(-5.0F, 2.0F, 0.0F)
		);
		lv2.method_32117(
			"left_arm",
			class_5606.method_32108().method_32101(44, 22).method_32096().method_32097(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F),
			class_5603.method_32090(5.0F, 2.0F, 0.0F)
		);
		lv2.method_32117(
			"right_leg", class_5606.method_32108().method_32101(0, 22).method_32097(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), class_5603.method_32090(-2.0F, 12.0F, 0.0F)
		);
		lv2.method_32117(
			"left_leg",
			class_5606.method_32108().method_32101(0, 22).method_32096().method_32097(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
			class_5603.method_32090(2.0F, 12.0F, 0.0F)
		);
		return class_5607.method_32110(lv, 64, 64);
	}

	public static class_5607 method_32069(class_5605 arg) {
		class_5609 lv = BipedEntityModel.method_32011(arg, 0.0F);
		class_5610 lv2 = lv.method_32111();
		lv2.method_32117("head", class_5606.method_32108().method_32101(0, 0).method_32098(-4.0F, -10.0F, -4.0F, 8.0F, 8.0F, 8.0F, arg), class_5603.field_27701);
		lv2.method_32117(
			"body", class_5606.method_32108().method_32101(16, 16).method_32098(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, arg.method_32094(0.1F)), class_5603.field_27701
		);
		lv2.method_32117(
			"right_leg",
			class_5606.method_32108().method_32101(0, 16).method_32098(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, arg.method_32094(0.1F)),
			class_5603.method_32090(-2.0F, 12.0F, 0.0F)
		);
		lv2.method_32117(
			"left_leg",
			class_5606.method_32108().method_32101(0, 16).method_32096().method_32098(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, arg.method_32094(0.1F)),
			class_5603.method_32090(2.0F, 12.0F, 0.0F)
		);
		lv2.method_32116("hat").method_32117("hat_rim", class_5606.method_32108(), class_5603.field_27701);
		return class_5607.method_32110(lv, 64, 32);
	}

	public void setAngles(T zombieEntity, float f, float g, float h, float i, float j) {
		super.setAngles(zombieEntity, f, g, h, i, j);
		CrossbowPosing.method_29352(this.field_27433, this.rightArm, zombieEntity.isAttacking(), this.handSwingProgress, h);
	}

	@Override
	public void setHatVisible(boolean visible) {
		this.head.visible = visible;
		this.helmet.visible = visible;
		this.hat.visible = visible;
	}
}
