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
import net.minecraft.client.render.entity.model.PandaEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class PandaHeldItemFeatureRenderer
extends FeatureRenderer<PandaEntity, PandaEntityModel<PandaEntity>> {
    public PandaHeldItemFeatureRenderer(FeatureRendererContext<PandaEntity, PandaEntityModel<PandaEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, PandaEntity pandaEntity, float f, float g, float h, float j, float k, float l) {
        ItemStack itemStack = pandaEntity.getEquippedStack(EquipmentSlot.MAINHAND);
        if (!pandaEntity.isScared() || pandaEntity.method_6524()) {
            return;
        }
        float m = -0.6f;
        float n = 1.4f;
        if (pandaEntity.isEating()) {
            m -= 0.2f * MathHelper.sin(j * 0.6f) + 0.2f;
            n -= 0.09f * MathHelper.sin(j * 0.6f);
        }
        matrixStack.push();
        matrixStack.translate(0.1f, n, m);
        MinecraftClient.getInstance().getFirstPersonRenderer().renderItem(pandaEntity, itemStack, ModelTransformation.Type.GROUND, false, matrixStack, vertexConsumerProvider, i);
        matrixStack.pop();
    }
}

