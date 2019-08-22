/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.WitchEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

@Environment(value=EnvType.CLIENT)
public class WitchHeldItemFeatureRenderer<T extends LivingEntity>
extends FeatureRenderer<T, WitchEntityModel<T>> {
    public WitchHeldItemFeatureRenderer(FeatureRendererContext<T, WitchEntityModel<T>> featureRendererContext) {
        super(featureRendererContext);
    }

    public void method_4208(T livingEntity, float f, float g, float h, float i, float j, float k, float l) {
        ItemStack itemStack = ((LivingEntity)livingEntity).getMainHandStack();
        if (itemStack.isEmpty()) {
            return;
        }
        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
        RenderSystem.pushMatrix();
        if (((WitchEntityModel)this.getModel()).isChild) {
            RenderSystem.translatef(0.0f, 0.625f, 0.0f);
            RenderSystem.rotatef(-20.0f, -1.0f, 0.0f, 0.0f);
            float m = 0.5f;
            RenderSystem.scalef(0.5f, 0.5f, 0.5f);
        }
        ((WitchEntityModel)this.getModel()).method_2839().applyTransform(0.0625f);
        RenderSystem.translatef(-0.0625f, 0.53125f, 0.21875f);
        Item item = itemStack.getItem();
        if (Block.getBlockFromItem(item).getDefaultState().getRenderType() == BlockRenderType.ENTITYBLOCK_ANIMATED) {
            RenderSystem.translatef(0.0f, 0.0625f, -0.25f);
            RenderSystem.rotatef(30.0f, 1.0f, 0.0f, 0.0f);
            RenderSystem.rotatef(-5.0f, 0.0f, 1.0f, 0.0f);
            float n = 0.375f;
            RenderSystem.scalef(0.375f, -0.375f, 0.375f);
        } else if (item == Items.BOW) {
            RenderSystem.translatef(0.0f, 0.125f, -0.125f);
            RenderSystem.rotatef(-45.0f, 0.0f, 1.0f, 0.0f);
            float n = 0.625f;
            RenderSystem.scalef(0.625f, -0.625f, 0.625f);
            RenderSystem.rotatef(-100.0f, 1.0f, 0.0f, 0.0f);
            RenderSystem.rotatef(-20.0f, 0.0f, 1.0f, 0.0f);
        } else {
            RenderSystem.translatef(0.1875f, 0.1875f, 0.0f);
            float n = 0.875f;
            RenderSystem.scalef(0.875f, 0.875f, 0.875f);
            RenderSystem.rotatef(-20.0f, 0.0f, 0.0f, 1.0f);
            RenderSystem.rotatef(-60.0f, 1.0f, 0.0f, 0.0f);
            RenderSystem.rotatef(-30.0f, 0.0f, 0.0f, 1.0f);
        }
        RenderSystem.rotatef(-15.0f, 1.0f, 0.0f, 0.0f);
        RenderSystem.rotatef(40.0f, 0.0f, 0.0f, 1.0f);
        MinecraftClient.getInstance().getFirstPersonRenderer().renderItem((LivingEntity)livingEntity, itemStack, ModelTransformation.Type.THIRD_PERSON_RIGHT_HAND);
        RenderSystem.popMatrix();
    }

    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}

