/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.SnowmanEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.item.ItemStack;

@Environment(value=EnvType.CLIENT)
public class SnowmanPumpkinFeatureRenderer
extends FeatureRenderer<SnowGolemEntity, SnowmanEntityModel<SnowGolemEntity>> {
    public SnowmanPumpkinFeatureRenderer(FeatureRendererContext<SnowGolemEntity, SnowmanEntityModel<SnowGolemEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(SnowGolemEntity snowGolemEntity, float f, float g, float h, float i, float j, float k, float l) {
        if (snowGolemEntity.isInvisible() || !snowGolemEntity.hasPumpkin()) {
            return;
        }
        GlStateManager.pushMatrix();
        ((SnowmanEntityModel)this.getContextModel()).method_2834().applyTransform(0.0625f);
        float m = 0.625f;
        GlStateManager.translatef(0.0f, -0.34375f, 0.0f);
        GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.scalef(0.625f, -0.625f, -0.625f);
        MinecraftClient.getInstance().getHeldItemRenderer().renderItem(snowGolemEntity, new ItemStack(Blocks.CARVED_PUMPKIN), ModelTransformation.Type.HEAD);
        GlStateManager.popMatrix();
    }

    @Override
    public boolean hasHurtOverlay() {
        return true;
    }
}

