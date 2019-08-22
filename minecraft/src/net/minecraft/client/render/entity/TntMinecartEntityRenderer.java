package net.minecraft.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4493;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TntMinecartEntityRenderer extends MinecartEntityRenderer<TntMinecartEntity> {
	public TntMinecartEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	protected void method_4137(TntMinecartEntity tntMinecartEntity, float f, BlockState blockState) {
		int i = tntMinecartEntity.getFuseTicks();
		if (i > -1 && (float)i - f + 1.0F < 10.0F) {
			float g = 1.0F - ((float)i - f + 1.0F) / 10.0F;
			g = MathHelper.clamp(g, 0.0F, 1.0F);
			g *= g;
			g *= g;
			float h = 1.0F + g * 0.3F;
			RenderSystem.scalef(h, h, h);
		}

		super.renderBlock(tntMinecartEntity, f, blockState);
		if (i > -1 && i / 5 % 2 == 0) {
			BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
			RenderSystem.disableTexture();
			RenderSystem.disableLighting();
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(class_4493.class_4535.SRC_ALPHA, class_4493.class_4534.DST_ALPHA);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, (1.0F - ((float)i - f + 1.0F) / 100.0F) * 0.8F);
			RenderSystem.pushMatrix();
			blockRenderManager.renderDynamic(Blocks.TNT.getDefaultState(), 1.0F);
			RenderSystem.popMatrix();
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.disableBlend();
			RenderSystem.enableLighting();
			RenderSystem.enableTexture();
		}
	}
}
