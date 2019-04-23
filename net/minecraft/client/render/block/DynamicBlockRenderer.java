/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.render.item.ItemDynamicRenderer;
import net.minecraft.item.ItemStack;

@Environment(value=EnvType.CLIENT)
public class DynamicBlockRenderer {
    public void render(Block block, float f) {
        GlStateManager.color4f(f, f, f, 1.0f);
        GlStateManager.rotatef(90.0f, 0.0f, 1.0f, 0.0f);
        ItemDynamicRenderer.INSTANCE.render(new ItemStack(block));
    }
}

