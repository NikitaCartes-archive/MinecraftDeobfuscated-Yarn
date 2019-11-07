/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.FoxEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
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

    public void method_18335(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, FoxEntity foxEntity, float f, float g, float h, float j, float k, float l) {
        float m;
        boolean bl = foxEntity.isSleeping();
        boolean bl2 = foxEntity.isBaby();
        matrixStack.push();
        if (bl2) {
            m = 0.75f;
            matrixStack.scale(0.75f, 0.75f, 0.75f);
            matrixStack.translate(0.0, 0.5, 0.209375f);
        }
        matrixStack.translate(((FoxEntityModel)this.getModel()).head.pivotX / 16.0f, ((FoxEntityModel)this.getModel()).head.pivotY / 16.0f, ((FoxEntityModel)this.getModel()).head.pivotZ / 16.0f);
        m = foxEntity.getHeadRoll(h);
        matrixStack.multiply(Vector3f.POSITIVE_Z.method_23626(m));
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(k));
        matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(l));
        if (foxEntity.isBaby()) {
            if (bl) {
                matrixStack.translate(0.4f, 0.26f, 0.15f);
            } else {
                matrixStack.translate(0.06f, 0.26f, -0.5);
            }
        } else if (bl) {
            matrixStack.translate(0.46f, 0.26f, 0.22f);
        } else {
            matrixStack.translate(0.06f, 0.27f, -0.5);
        }
        matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(90.0f));
        if (bl) {
            matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(90.0f));
        }
        ItemStack itemStack = foxEntity.getEquippedStack(EquipmentSlot.MAINHAND);
        MinecraftClient.getInstance().getFirstPersonRenderer().renderItem(foxEntity, itemStack, ModelTransformation.Type.GROUND, false, matrixStack, vertexConsumerProvider, i);
        matrixStack.pop();
    }
}

