/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.entity.passive.AbstractDonkeyEntity;

@Environment(value=EnvType.CLIENT)
public class DonkeyEntityModel<T extends AbstractDonkeyEntity>
extends HorseEntityModel<T> {
    private final ModelPart field_27399;
    private final ModelPart field_27400;

    public DonkeyEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.field_27399 = this.torso.method_32086("left_chest");
        this.field_27400 = this.torso.method_32086("right_chest");
    }

    public static class_5607 method_31987() {
        class_5609 lv = HorseEntityModel.method_32010(class_5605.field_27715);
        class_5610 lv2 = lv.method_32111();
        class_5610 lv3 = lv2.method_32116("body");
        class_5606 lv4 = class_5606.method_32108().method_32101(26, 21).method_32097(-4.0f, 0.0f, -2.0f, 8.0f, 8.0f, 3.0f);
        lv3.method_32117("left_chest", lv4, class_5603.method_32091(6.0f, -8.0f, 0.0f, 0.0f, -1.5707964f, 0.0f));
        lv3.method_32117("right_chest", lv4, class_5603.method_32091(-6.0f, -8.0f, 0.0f, 0.0f, 1.5707964f, 0.0f));
        class_5610 lv5 = lv2.method_32116("head_parts").method_32116("head");
        class_5606 lv6 = class_5606.method_32108().method_32101(0, 12).method_32097(-1.0f, -7.0f, 0.0f, 2.0f, 7.0f, 1.0f);
        lv5.method_32117("left_ear", lv6, class_5603.method_32091(1.25f, -10.0f, 4.0f, 0.2617994f, 0.0f, 0.2617994f));
        lv5.method_32117("right_ear", lv6, class_5603.method_32091(-1.25f, -10.0f, 4.0f, 0.2617994f, 0.0f, -0.2617994f));
        return class_5607.method_32110(lv, 64, 64);
    }

    @Override
    public void setAngles(T abstractDonkeyEntity, float f, float g, float h, float i, float j) {
        super.setAngles(abstractDonkeyEntity, f, g, h, i, j);
        if (((AbstractDonkeyEntity)abstractDonkeyEntity).hasChest()) {
            this.field_27399.visible = true;
            this.field_27400.visible = true;
        } else {
            this.field_27399.visible = false;
            this.field_27400.visible = false;
        }
    }
}

