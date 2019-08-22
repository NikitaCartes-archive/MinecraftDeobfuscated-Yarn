package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4493;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.model.DragonEntityModel;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;

@Environment(EnvType.CLIENT)
public class EnderDragonDeathFeatureRenderer extends FeatureRenderer<EnderDragonEntity, DragonEntityModel> {
	public EnderDragonDeathFeatureRenderer(FeatureRendererContext<EnderDragonEntity, DragonEntityModel> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4184(EnderDragonEntity enderDragonEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (enderDragonEntity.field_7031 > 0) {
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
			GuiLighting.disable();
			float m = ((float)enderDragonEntity.field_7031 + h) / 200.0F;
			float n = 0.0F;
			if (m > 0.8F) {
				n = (m - 0.8F) / 0.2F;
			}

			Random random = new Random(432L);
			RenderSystem.disableTexture();
			RenderSystem.shadeModel(7425);
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(class_4493.class_4535.SRC_ALPHA, class_4493.class_4534.ONE);
			RenderSystem.disableAlphaTest();
			RenderSystem.enableCull();
			RenderSystem.depthMask(false);
			RenderSystem.pushMatrix();
			RenderSystem.translatef(0.0F, -1.0F, -2.0F);

			for (int o = 0; (float)o < (m + m * m) / 2.0F * 60.0F; o++) {
				RenderSystem.rotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
				RenderSystem.rotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
				RenderSystem.rotatef(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
				RenderSystem.rotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
				RenderSystem.rotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
				RenderSystem.rotatef(random.nextFloat() * 360.0F + m * 90.0F, 0.0F, 0.0F, 1.0F);
				float p = random.nextFloat() * 20.0F + 5.0F + n * 10.0F;
				float q = random.nextFloat() * 2.0F + 1.0F + n * 2.0F;
				bufferBuilder.begin(6, VertexFormats.POSITION_COLOR);
				bufferBuilder.vertex(0.0, 0.0, 0.0).color(255, 255, 255, (int)(255.0F * (1.0F - n))).next();
				bufferBuilder.vertex(-0.866 * (double)q, (double)p, (double)(-0.5F * q)).color(255, 0, 255, 0).next();
				bufferBuilder.vertex(0.866 * (double)q, (double)p, (double)(-0.5F * q)).color(255, 0, 255, 0).next();
				bufferBuilder.vertex(0.0, (double)p, (double)(1.0F * q)).color(255, 0, 255, 0).next();
				bufferBuilder.vertex(-0.866 * (double)q, (double)p, (double)(-0.5F * q)).color(255, 0, 255, 0).next();
				tessellator.draw();
			}

			RenderSystem.popMatrix();
			RenderSystem.depthMask(true);
			RenderSystem.disableCull();
			RenderSystem.disableBlend();
			RenderSystem.shadeModel(7424);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.enableTexture();
			RenderSystem.enableAlphaTest();
			GuiLighting.enable();
		}
	}

	@Override
	public boolean hasHurtOverlay() {
		return false;
	}
}
