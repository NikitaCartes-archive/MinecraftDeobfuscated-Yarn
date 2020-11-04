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
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WitchEntityModel<T extends Entity>
extends VillagerResemblingModel<T> {
    private boolean liftingNose;

    public WitchEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static class_5607 method_32065() {
        class_5609 lv = VillagerResemblingModel.method_32064();
        class_5610 lv2 = lv.method_32111();
        class_5610 lv3 = lv2.method_32117("head", class_5606.method_32108().method_32101(0, 0).method_32097(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f), class_5603.field_27701);
        class_5610 lv4 = lv3.method_32117("hat", class_5606.method_32108().method_32101(0, 64).method_32097(0.0f, 0.0f, 0.0f, 10.0f, 2.0f, 10.0f), class_5603.method_32090(-5.0f, -10.03125f, -5.0f));
        class_5610 lv5 = lv4.method_32117("hat2", class_5606.method_32108().method_32101(0, 76).method_32097(0.0f, 0.0f, 0.0f, 7.0f, 4.0f, 7.0f), class_5603.method_32091(1.75f, -4.0f, 2.0f, -0.05235988f, 0.0f, 0.02617994f));
        class_5610 lv6 = lv5.method_32117("hat3", class_5606.method_32108().method_32101(0, 87).method_32097(0.0f, 0.0f, 0.0f, 4.0f, 4.0f, 4.0f), class_5603.method_32091(1.75f, -4.0f, 2.0f, -0.10471976f, 0.0f, 0.05235988f));
        lv6.method_32117("hat4", class_5606.method_32108().method_32101(0, 95).method_32098(0.0f, 0.0f, 0.0f, 1.0f, 2.0f, 1.0f, new class_5605(0.25f)), class_5603.method_32091(1.75f, -2.0f, 2.0f, -0.20943952f, 0.0f, 0.10471976f));
        class_5610 lv7 = lv3.method_32116("nose");
        lv7.method_32117("mole", class_5606.method_32108().method_32101(0, 0).method_32098(0.0f, 3.0f, -6.75f, 1.0f, 1.0f, 1.0f, new class_5605(-0.25f)), class_5603.method_32090(0.0f, -2.0f, 0.0f));
        return class_5607.method_32110(lv, 64, 128);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
        this.field_27525.setPivot(0.0f, -2.0f, 0.0f);
        float f = 0.01f * (float)(((Entity)entity).getEntityId() % 10);
        this.field_27525.pitch = MathHelper.sin((float)((Entity)entity).age * f) * 4.5f * ((float)Math.PI / 180);
        this.field_27525.yaw = 0.0f;
        this.field_27525.roll = MathHelper.cos((float)((Entity)entity).age * f) * 2.5f * ((float)Math.PI / 180);
        if (this.liftingNose) {
            this.field_27525.setPivot(0.0f, 1.0f, -1.5f);
            this.field_27525.pitch = -0.9f;
        }
    }

    public ModelPart getNose() {
        return this.field_27525;
    }

    public void setLiftingNose(boolean bl) {
        this.liftingNose = bl;
    }
}

