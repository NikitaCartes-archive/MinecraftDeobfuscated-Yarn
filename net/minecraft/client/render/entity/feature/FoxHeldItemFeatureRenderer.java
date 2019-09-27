/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.FoxEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.item.ItemStack;

@Environment(value=EnvType.CLIENT)
public class FoxHeldItemFeatureRenderer
extends FeatureRenderer<FoxEntity, FoxEntityModel<FoxEntity>> {
    public FoxHeldItemFeatureRenderer(FeatureRendererContext<FoxEntity, FoxEntityModel<FoxEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    public void method_18335(class_4587 arg, class_4597 arg2, int i, FoxEntity foxEntity, float f, float g, float h, float j, float k, float l, float m) {
        float n;
        boolean bl = foxEntity.isSleeping();
        boolean bl2 = foxEntity.isBaby();
        arg.method_22903();
        if (bl2) {
            n = 0.75f;
            arg.method_22905(0.75f, 0.75f, 0.75f);
            arg.method_22904(0.0, 8.0f * m, 3.35f * m);
        }
        arg.method_22904(((FoxEntityModel)this.getModel()).head.rotationPointX / 16.0f, ((FoxEntityModel)this.getModel()).head.rotationPointY / 16.0f, ((FoxEntityModel)this.getModel()).head.rotationPointZ / 16.0f);
        n = foxEntity.getHeadRoll(h);
        arg.method_22907(Vector3f.field_20707.method_23214(n, false));
        arg.method_22907(Vector3f.field_20705.method_23214(k, true));
        arg.method_22907(Vector3f.field_20703.method_23214(l, true));
        if (foxEntity.isBaby()) {
            if (bl) {
                arg.method_22904(0.4f, 0.26f, 0.15f);
            } else {
                arg.method_22904(0.06f, 0.26f, -0.5);
            }
        } else if (bl) {
            arg.method_22904(0.46f, 0.26f, 0.22f);
        } else {
            arg.method_22904(0.06f, 0.27f, -0.5);
        }
        arg.method_22907(Vector3f.field_20703.method_23214(90.0f, true));
        if (bl) {
            arg.method_22907(Vector3f.field_20707.method_23214(90.0f, true));
        }
        ItemStack itemStack = foxEntity.getEquippedStack(EquipmentSlot.MAINHAND);
        MinecraftClient.getInstance().getFirstPersonRenderer().renderItem(foxEntity, itemStack, ModelTransformation.Type.GROUND, false, arg, arg2);
        arg.method_22909();
    }
}

