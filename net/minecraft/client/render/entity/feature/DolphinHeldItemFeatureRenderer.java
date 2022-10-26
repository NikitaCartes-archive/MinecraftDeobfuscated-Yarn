/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.DolphinEntityModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class DolphinHeldItemFeatureRenderer
extends FeatureRenderer<DolphinEntity, DolphinEntityModel<DolphinEntity>> {
    private final HeldItemRenderer heldItemRenderer;

    public DolphinHeldItemFeatureRenderer(FeatureRendererContext<DolphinEntity, DolphinEntityModel<DolphinEntity>> context, HeldItemRenderer heldItemRenderer) {
        super(context);
        this.heldItemRenderer = heldItemRenderer;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, DolphinEntity dolphinEntity, float f, float g, float h, float j, float k, float l) {
        boolean bl = dolphinEntity.getMainArm() == Arm.RIGHT;
        matrixStack.push();
        float m = 1.0f;
        float n = -1.0f;
        float o = MathHelper.abs(dolphinEntity.getPitch()) / 60.0f;
        if (dolphinEntity.getPitch() < 0.0f) {
            matrixStack.translate(0.0f, 1.0f - o * 0.5f, -1.0f + o * 0.5f);
        } else {
            matrixStack.translate(0.0f, 1.0f + o * 0.8f, -1.0f + o * 0.2f);
        }
        ItemStack itemStack = bl ? dolphinEntity.getMainHandStack() : dolphinEntity.getOffHandStack();
        this.heldItemRenderer.renderItem(dolphinEntity, itemStack, ModelTransformation.Mode.GROUND, false, matrixStack, vertexConsumerProvider, i);
        matrixStack.pop();
    }
}

