/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class VillagerResemblingModel<T extends Entity>
extends class_5597<T>
implements ModelWithHead,
ModelWithHat {
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
        float f = 0.5f;
        class_5610 lv3 = lv2.method_32117("head", class_5606.method_32108().method_32101(0, 0).method_32097(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f), class_5603.field_27701);
        class_5610 lv4 = lv3.method_32117("hat", class_5606.method_32108().method_32101(32, 0).method_32098(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f, new class_5605(0.5f)), class_5603.field_27701);
        lv4.method_32117("hat_rim", class_5606.method_32108().method_32101(30, 47).method_32097(-8.0f, -8.0f, -6.0f, 16.0f, 16.0f, 1.0f), class_5603.method_32092(-1.5707964f, 0.0f, 0.0f));
        lv3.method_32117("nose", class_5606.method_32108().method_32101(24, 0).method_32097(-1.0f, -1.0f, -6.0f, 2.0f, 4.0f, 2.0f), class_5603.method_32090(0.0f, -2.0f, 0.0f));
        class_5610 lv5 = lv2.method_32117("body", class_5606.method_32108().method_32101(16, 20).method_32097(-4.0f, 0.0f, -3.0f, 8.0f, 12.0f, 6.0f), class_5603.field_27701);
        lv5.method_32117("jacket", class_5606.method_32108().method_32101(0, 38).method_32098(-4.0f, 0.0f, -3.0f, 8.0f, 18.0f, 6.0f, new class_5605(0.5f)), class_5603.field_27701);
        lv2.method_32117("arms", class_5606.method_32108().method_32101(44, 22).method_32097(-8.0f, -2.0f, -2.0f, 4.0f, 8.0f, 4.0f).method_32101(44, 22).method_32100(4.0f, -2.0f, -2.0f, 4.0f, 8.0f, 4.0f, true).method_32101(40, 38).method_32097(-4.0f, 2.0f, -2.0f, 8.0f, 4.0f, 4.0f), class_5603.method_32091(0.0f, 3.0f, -1.0f, -0.75f, 0.0f, 0.0f));
        lv2.method_32117("right_leg", class_5606.method_32108().method_32101(0, 22).method_32097(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f), class_5603.method_32090(-2.0f, 12.0f, 0.0f));
        lv2.method_32117("left_leg", class_5606.method_32108().method_32101(0, 22).method_32096().method_32097(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f), class_5603.method_32090(2.0f, 12.0f, 0.0f));
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
        this.field_27527.yaw = headYaw * ((float)Math.PI / 180);
        this.field_27527.pitch = headPitch * ((float)Math.PI / 180);
        if (bl) {
            this.field_27527.roll = 0.3f * MathHelper.sin(0.45f * animationProgress);
            this.field_27527.pitch = 0.4f;
        } else {
            this.field_27527.roll = 0.0f;
        }
        this.field_27530.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance * 0.5f;
        this.field_27531.pitch = MathHelper.cos(limbAngle * 0.6662f + (float)Math.PI) * 1.4f * limbDistance * 0.5f;
        this.field_27530.yaw = 0.0f;
        this.field_27531.yaw = 0.0f;
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

