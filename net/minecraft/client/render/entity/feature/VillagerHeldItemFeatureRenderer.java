/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Environment(value=EnvType.CLIENT)
public class VillagerHeldItemFeatureRenderer<T extends LivingEntity>
extends FeatureRenderer<T, VillagerResemblingModel<T>> {
    private final ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();

    public VillagerHeldItemFeatureRenderer(FeatureRendererContext<T, VillagerResemblingModel<T>> featureRendererContext) {
        super(featureRendererContext);
    }

    public void method_18147(T livingEntity, float f, float g, float h, float i, float j, float k, float l) {
        boolean bl;
        ItemStack itemStack = ((LivingEntity)livingEntity).getEquippedStack(EquipmentSlot.MAINHAND);
        if (itemStack.isEmpty()) {
            return;
        }
        Item item = itemStack.getItem();
        Block block = Block.getBlockFromItem(item);
        RenderSystem.pushMatrix();
        boolean bl2 = bl = this.itemRenderer.hasDepthInGui(itemStack) && BlockRenderLayer.method_22715(block.getDefaultState()) == BlockRenderLayer.field_9179;
        if (bl) {
            RenderSystem.depthMask(false);
        }
        RenderSystem.translatef(0.0f, 0.4f, -0.4f);
        RenderSystem.rotatef(180.0f, 1.0f, 0.0f, 0.0f);
        this.itemRenderer.renderHeldItem(itemStack, (LivingEntity)livingEntity, ModelTransformation.Type.GROUND, false);
        if (bl) {
            RenderSystem.depthMask(true);
        }
        RenderSystem.popMatrix();
    }

    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}

