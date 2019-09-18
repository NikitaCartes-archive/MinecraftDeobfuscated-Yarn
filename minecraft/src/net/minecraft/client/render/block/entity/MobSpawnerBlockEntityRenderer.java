package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.MobSpawnerLogic;

@Environment(EnvType.CLIENT)
public class MobSpawnerBlockEntityRenderer extends BlockEntityRenderer<MobSpawnerBlockEntity> {
	public void method_3590(MobSpawnerBlockEntity mobSpawnerBlockEntity, double d, double e, double f, float g, int i, BlockRenderLayer blockRenderLayer) {
		RenderSystem.pushMatrix();
		RenderSystem.translatef((float)d + 0.5F, (float)e, (float)f + 0.5F);
		method_3589(mobSpawnerBlockEntity.getLogic(), d, e, f, g);
		RenderSystem.popMatrix();
	}

	public static void method_3589(MobSpawnerLogic mobSpawnerLogic, double d, double e, double f, float g) {
		Entity entity = mobSpawnerLogic.getRenderedEntity();
		if (entity != null) {
			float h = 0.53125F;
			float i = Math.max(entity.getWidth(), entity.getHeight());
			if ((double)i > 1.0) {
				h /= i;
			}

			RenderSystem.translatef(0.0F, 0.4F, 0.0F);
			RenderSystem.rotatef((float)MathHelper.lerp((double)g, mobSpawnerLogic.method_8279(), mobSpawnerLogic.method_8278()) * 10.0F, 0.0F, 1.0F, 0.0F);
			RenderSystem.translatef(0.0F, -0.2F, 0.0F);
			RenderSystem.rotatef(-30.0F, 1.0F, 0.0F, 0.0F);
			RenderSystem.scalef(h, h, h);
			entity.setPositionAndAngles(d, e, f, 0.0F, 0.0F);
			MinecraftClient.getInstance().getEntityRenderManager().render(entity, 0.0, 0.0, 0.0, 0.0F, g, false);
		}
	}
}
