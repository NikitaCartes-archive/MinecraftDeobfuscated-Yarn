package net.minecraft.client.render.block;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.render.item.ItemDynamicRenderer;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class DynamicBlockRenderer {
	public void render(Block block, float f) {
		RenderSystem.color4f(f, f, f, 1.0F);
		RenderSystem.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
		ItemDynamicRenderer.INSTANCE.render(new ItemStack(block));
	}
}
