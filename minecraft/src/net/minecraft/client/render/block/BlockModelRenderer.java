package net.minecraft.client.render.block;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.render.item.ItemBuiltinRenderer;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class BlockModelRenderer {
	public void renderBlockModel(Block block, float f) {
		GlStateManager.color4f(f, f, f, 1.0F);
		GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
		ItemBuiltinRenderer.INSTANCE.method_3166(new ItemStack(block));
	}
}
