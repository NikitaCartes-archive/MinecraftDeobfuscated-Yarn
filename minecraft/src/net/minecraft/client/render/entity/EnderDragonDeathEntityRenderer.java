package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_308;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;

@Environment(EnvType.CLIENT)
public class EnderDragonDeathEntityRenderer implements LayerEntityRenderer<EnderDragonEntity> {
	public void render(EnderDragonEntity enderDragonEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (enderDragonEntity.field_7031 > 0) {
			Tessellator tessellator = Tessellator.getInstance();
			VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
			class_308.method_1450();
			float m = ((float)enderDragonEntity.field_7031 + h) / 200.0F;
			float n = 0.0F;
			if (m > 0.8F) {
				n = (m - 0.8F) / 0.2F;
			}

			Random random = new Random(432L);
			GlStateManager.disableTexture();
			GlStateManager.shadeModel(7425);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SrcBlendFactor.SRC_ALPHA, GlStateManager.DstBlendFactor.ONE);
			GlStateManager.disableAlphaTest();
			GlStateManager.enableCull();
			GlStateManager.depthMask(false);
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, -1.0F, -2.0F);

			for (int o = 0; (float)o < (m + m * m) / 2.0F * 60.0F; o++) {
				GlStateManager.rotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotatef(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
				GlStateManager.rotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotatef(random.nextFloat() * 360.0F + m * 90.0F, 0.0F, 0.0F, 1.0F);
				float p = random.nextFloat() * 20.0F + 5.0F + n * 10.0F;
				float q = random.nextFloat() * 2.0F + 1.0F + n * 2.0F;
				vertexBuffer.begin(6, VertexFormats.POSITION_COLOR);
				vertexBuffer.vertex(0.0, 0.0, 0.0).color(255, 255, 255, (int)(255.0F * (1.0F - n))).next();
				vertexBuffer.vertex(-0.866 * (double)q, (double)p, (double)(-0.5F * q)).color(255, 0, 255, 0).next();
				vertexBuffer.vertex(0.866 * (double)q, (double)p, (double)(-0.5F * q)).color(255, 0, 255, 0).next();
				vertexBuffer.vertex(0.0, (double)p, (double)(1.0F * q)).color(255, 0, 255, 0).next();
				vertexBuffer.vertex(-0.866 * (double)q, (double)p, (double)(-0.5F * q)).color(255, 0, 255, 0).next();
				tessellator.draw();
			}

			GlStateManager.popMatrix();
			GlStateManager.depthMask(true);
			GlStateManager.disableCull();
			GlStateManager.disableBlend();
			GlStateManager.shadeModel(7424);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableTexture();
			GlStateManager.enableAlphaTest();
			class_308.method_1452();
		}
	}

	@Override
	public boolean shouldMergeTextures() {
		return false;
	}
}
