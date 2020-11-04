/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ShulkerEntityModel<T extends ShulkerEntity>
extends CompositeEntityModel<T> {
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
        lv2.method_32117("lid", class_5606.method_32108().method_32101(0, 0).method_32097(-8.0f, -16.0f, -8.0f, 16.0f, 12.0f, 16.0f), class_5603.method_32090(0.0f, 24.0f, 0.0f));
        lv2.method_32117("base", class_5606.method_32108().method_32101(0, 28).method_32097(-8.0f, -8.0f, -8.0f, 16.0f, 8.0f, 16.0f), class_5603.method_32090(0.0f, 24.0f, 0.0f));
        lv2.method_32117("head", class_5606.method_32108().method_32101(0, 52).method_32097(-3.0f, 0.0f, -3.0f, 6.0f, 6.0f, 6.0f), class_5603.method_32090(0.0f, 12.0f, 0.0f));
        return class_5607.method_32110(lv, 64, 64);
    }

    @Override
    public void setAngles(T shulkerEntity, float f, float g, float h, float i, float j) {
        float k = h - (float)((ShulkerEntity)shulkerEntity).age;
        float l = (0.5f + ((ShulkerEntity)shulkerEntity).getOpenProgress(k)) * (float)Math.PI;
        float m = -1.0f + MathHelper.sin(l);
        float n = 0.0f;
        if (l > (float)Math.PI) {
            n = MathHelper.sin(h * 0.1f) * 0.7f;
        }
        this.topShell.setPivot(0.0f, 16.0f + MathHelper.sin(l) * 8.0f + n, 0.0f);
        this.topShell.yaw = ((ShulkerEntity)shulkerEntity).getOpenProgress(k) > 0.3f ? m * m * m * m * (float)Math.PI * 0.125f : 0.0f;
        this.head.pitch = j * ((float)Math.PI / 180);
        this.head.yaw = (((ShulkerEntity)shulkerEntity).headYaw - 180.0f - ((ShulkerEntity)shulkerEntity).bodyYaw) * ((float)Math.PI / 180);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.bottomShell, this.topShell);
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

