/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PandaEntityModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class PandaHeldItemFeatureRenderer
extends FeatureRenderer<PandaEntity, PandaEntityModel<PandaEntity>> {
    private final HeldItemRenderer heldItemRenderer;

    public PandaHeldItemFeatureRenderer(FeatureRendererContext<PandaEntity, PandaEntityModel<PandaEntity>> context, HeldItemRenderer heldItemRenderer) {
        super(context);
        this.heldItemRenderer = heldItemRenderer;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, PandaEntity pandaEntity, float f, float g, float h, float j, float k, float l) {
        ItemStack itemStack = pandaEntity.getEquippedStack(EquipmentSlot.MAINHAND);
        if (!pandaEntity.isSitting() || pandaEntity.isScaredByThunderstorm()) {
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
        this.heldItemRenderer.renderItem(pandaEntity, itemStack, ModelTransformationMode.GROUND, false, matrixStack, vertexConsumerProvider, i);
        matrixStack.pop();
    }
}

