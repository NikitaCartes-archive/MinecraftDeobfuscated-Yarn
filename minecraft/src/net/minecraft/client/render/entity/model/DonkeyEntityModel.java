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
import net.minecraft.entity.passive.AbstractDonkeyEntity;

@Environment(EnvType.CLIENT)
public class DonkeyEntityModel<T extends AbstractDonkeyEntity> extends HorseEntityModel<T> {
	private final ModelPart field_27399 = this.torso.method_32086("left_chest");
	private final ModelPart field_27400 = this.torso.method_32086("right_chest");

	public DonkeyEntityModel(ModelPart modelPart) {
		super(modelPart);
	}

	public static class_5607 method_31987() {
		class_5609 lv = HorseEntityModel.method_32010(class_5605.field_27715);
		class_5610 lv2 = lv.method_32111();
		class_5610 lv3 = lv2.method_32116("body");
		class_5606 lv4 = class_5606.method_32108().method_32101(26, 21).method_32097(-4.0F, 0.0F, -2.0F, 8.0F, 8.0F, 3.0F);
		lv3.method_32117("left_chest", lv4, class_5603.method_32091(6.0F, -8.0F, 0.0F, 0.0F, (float) (-Math.PI / 2), 0.0F));
		lv3.method_32117("right_chest", lv4, class_5603.method_32091(-6.0F, -8.0F, 0.0F, 0.0F, (float) (Math.PI / 2), 0.0F));
		class_5610 lv5 = lv2.method_32116("head_parts").method_32116("head");
		class_5606 lv6 = class_5606.method_32108().method_32101(0, 12).method_32097(-1.0F, -7.0F, 0.0F, 2.0F, 7.0F, 1.0F);
		lv5.method_32117("left_ear", lv6, class_5603.method_32091(1.25F, -10.0F, 4.0F, (float) (Math.PI / 12), 0.0F, (float) (Math.PI / 12)));
		lv5.method_32117("right_ear", lv6, class_5603.method_32091(-1.25F, -10.0F, 4.0F, (float) (Math.PI / 12), 0.0F, (float) (-Math.PI / 12)));
		return class_5607.method_32110(lv, 64, 64);
	}

	public void setAngles(T abstractDonkeyEntity, float f, float g, float h, float i, float j) {
		super.setAngles(abstractDonkeyEntity, f, g, h, i, j);
		if (abstractDonkeyEntity.hasChest()) {
			this.field_27399.visible = true;
			this.field_27400.visible = true;
		} else {
			this.field_27399.visible = false;
			this.field_27400.visible = false;
		}
	}
}
