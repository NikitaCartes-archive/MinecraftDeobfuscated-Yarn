package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.PandaHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.PandaEntityModel;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PandaEntityRenderer extends MobEntityRenderer<PandaEntity, PandaEntityModel<PandaEntity>> {
	public PandaEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new PandaEntityModel<>(9, 0.0F), 0.5F);
		this.addFeature(new PandaHeldItemFeatureRenderer(this));
	}

	@Nullable
	protected Identifier getTexture(PandaEntity pandaEntity) {
		return pandaEntity.method_6554().method_6562();
	}

	protected void method_4085(PandaEntity pandaEntity, float f, float g, float h) {
		super.setupTransforms(pandaEntity, f, g, h);
		if (pandaEntity.field_6767 > 0) {
			int i = pandaEntity.field_6767;
			int j = i + 1;
			float k = 7.0F;
			float l = pandaEntity.isChild() ? 0.3F : 0.8F;
			if (i < 8) {
				float m = (float)(90 * i) / 7.0F;
				float n = (float)(90 * j) / 7.0F;
				float o = this.method_4086(m, n, j, h, 8.0F);
				GlStateManager.translatef(0.0F, (l + 0.2F) * (o / 90.0F), 0.0F);
				GlStateManager.rotatef(-o, 1.0F, 0.0F, 0.0F);
			} else if (i < 16) {
				float m = ((float)i - 8.0F) / 7.0F;
				float n = 90.0F + 90.0F * m;
				float p = 90.0F + 90.0F * ((float)j - 8.0F) / 7.0F;
				float o = this.method_4086(n, p, j, h, 16.0F);
				GlStateManager.translatef(0.0F, l + 0.2F + (l - 0.2F) * (o - 90.0F) / 90.0F, 0.0F);
				GlStateManager.rotatef(-o, 1.0F, 0.0F, 0.0F);
			} else if ((float)i < 24.0F) {
				float m = ((float)i - 16.0F) / 7.0F;
				float n = 180.0F + 90.0F * m;
				float p = 180.0F + 90.0F * ((float)j - 16.0F) / 7.0F;
				float o = this.method_4086(n, p, j, h, 24.0F);
				GlStateManager.translatef(0.0F, l + l * (270.0F - o) / 90.0F, 0.0F);
				GlStateManager.rotatef(-o, 1.0F, 0.0F, 0.0F);
			} else if (i < 32) {
				float m = ((float)i - 24.0F) / 7.0F;
				float n = 270.0F + 90.0F * m;
				float p = 270.0F + 90.0F * ((float)j - 24.0F) / 7.0F;
				float o = this.method_4086(n, p, j, h, 32.0F);
				GlStateManager.translatef(0.0F, l * ((360.0F - o) / 90.0F), 0.0F);
				GlStateManager.rotatef(-o, 1.0F, 0.0F, 0.0F);
			}
		} else {
			GlStateManager.rotatef(0.0F, 1.0F, 0.0F, 0.0F);
		}

		float q = pandaEntity.method_6534(h);
		if (q > 0.0F) {
			GlStateManager.translatef(0.0F, 0.8F * q, 0.0F);
			GlStateManager.rotatef(MathHelper.lerp(q, pandaEntity.pitch, pandaEntity.pitch + 90.0F), 1.0F, 0.0F, 0.0F);
			GlStateManager.translatef(0.0F, -1.0F * q, 0.0F);
			if (pandaEntity.method_6524()) {
				float r = (float)(Math.cos((double)pandaEntity.age * 1.25) * Math.PI * 0.05F);
				GlStateManager.rotatef(r, 0.0F, 1.0F, 0.0F);
				if (pandaEntity.isChild()) {
					GlStateManager.translatef(0.0F, 0.8F, 0.55F);
				}
			}
		}

		float r = pandaEntity.method_6555(h);
		if (r > 0.0F) {
			float k = pandaEntity.isChild() ? 0.5F : 1.3F;
			GlStateManager.translatef(0.0F, k * r, 0.0F);
			GlStateManager.rotatef(MathHelper.lerp(r, pandaEntity.pitch, pandaEntity.pitch + 180.0F), 1.0F, 0.0F, 0.0F);
		}
	}

	private float method_4086(float f, float g, int i, float h, float j) {
		return (float)i < j ? MathHelper.lerp(h, f, g) : f;
	}
}
