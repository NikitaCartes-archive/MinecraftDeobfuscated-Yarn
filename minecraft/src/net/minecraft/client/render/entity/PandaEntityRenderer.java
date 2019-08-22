package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.PandaHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.PandaEntityModel;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PandaEntityRenderer extends MobEntityRenderer<PandaEntity, PandaEntityModel<PandaEntity>> {
	private static final Map<PandaEntity.Gene, Identifier> SKIN_MAP = SystemUtil.consume(Maps.newEnumMap(PandaEntity.Gene.class), enumMap -> {
		enumMap.put(PandaEntity.Gene.NORMAL, new Identifier("textures/entity/panda/panda.png"));
		enumMap.put(PandaEntity.Gene.LAZY, new Identifier("textures/entity/panda/lazy_panda.png"));
		enumMap.put(PandaEntity.Gene.WORRIED, new Identifier("textures/entity/panda/worried_panda.png"));
		enumMap.put(PandaEntity.Gene.PLAYFUL, new Identifier("textures/entity/panda/playful_panda.png"));
		enumMap.put(PandaEntity.Gene.BROWN, new Identifier("textures/entity/panda/brown_panda.png"));
		enumMap.put(PandaEntity.Gene.WEAK, new Identifier("textures/entity/panda/weak_panda.png"));
		enumMap.put(PandaEntity.Gene.AGGRESSIVE, new Identifier("textures/entity/panda/aggressive_panda.png"));
	});

	public PandaEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new PandaEntityModel<>(9, 0.0F), 0.9F);
		this.addFeature(new PandaHeldItemFeatureRenderer(this));
	}

	@Nullable
	protected Identifier method_4083(PandaEntity pandaEntity) {
		return (Identifier)SKIN_MAP.getOrDefault(pandaEntity.getProductGene(), SKIN_MAP.get(PandaEntity.Gene.NORMAL));
	}

	protected void method_4085(PandaEntity pandaEntity, float f, float g, float h) {
		super.setupTransforms(pandaEntity, f, g, h);
		if (pandaEntity.playingTicks > 0) {
			int i = pandaEntity.playingTicks;
			int j = i + 1;
			float k = 7.0F;
			float l = pandaEntity.isBaby() ? 0.3F : 0.8F;
			if (i < 8) {
				float m = (float)(90 * i) / 7.0F;
				float n = (float)(90 * j) / 7.0F;
				float o = this.method_4086(m, n, j, h, 8.0F);
				RenderSystem.translatef(0.0F, (l + 0.2F) * (o / 90.0F), 0.0F);
				RenderSystem.rotatef(-o, 1.0F, 0.0F, 0.0F);
			} else if (i < 16) {
				float m = ((float)i - 8.0F) / 7.0F;
				float n = 90.0F + 90.0F * m;
				float p = 90.0F + 90.0F * ((float)j - 8.0F) / 7.0F;
				float o = this.method_4086(n, p, j, h, 16.0F);
				RenderSystem.translatef(0.0F, l + 0.2F + (l - 0.2F) * (o - 90.0F) / 90.0F, 0.0F);
				RenderSystem.rotatef(-o, 1.0F, 0.0F, 0.0F);
			} else if ((float)i < 24.0F) {
				float m = ((float)i - 16.0F) / 7.0F;
				float n = 180.0F + 90.0F * m;
				float p = 180.0F + 90.0F * ((float)j - 16.0F) / 7.0F;
				float o = this.method_4086(n, p, j, h, 24.0F);
				RenderSystem.translatef(0.0F, l + l * (270.0F - o) / 90.0F, 0.0F);
				RenderSystem.rotatef(-o, 1.0F, 0.0F, 0.0F);
			} else if (i < 32) {
				float m = ((float)i - 24.0F) / 7.0F;
				float n = 270.0F + 90.0F * m;
				float p = 270.0F + 90.0F * ((float)j - 24.0F) / 7.0F;
				float o = this.method_4086(n, p, j, h, 32.0F);
				RenderSystem.translatef(0.0F, l * ((360.0F - o) / 90.0F), 0.0F);
				RenderSystem.rotatef(-o, 1.0F, 0.0F, 0.0F);
			}
		} else {
			RenderSystem.rotatef(0.0F, 1.0F, 0.0F, 0.0F);
		}

		float q = pandaEntity.getScaredAnimationProgress(h);
		if (q > 0.0F) {
			RenderSystem.translatef(0.0F, 0.8F * q, 0.0F);
			RenderSystem.rotatef(MathHelper.lerp(q, pandaEntity.pitch, pandaEntity.pitch + 90.0F), 1.0F, 0.0F, 0.0F);
			RenderSystem.translatef(0.0F, -1.0F * q, 0.0F);
			if (pandaEntity.method_6524()) {
				float r = (float)(Math.cos((double)pandaEntity.age * 1.25) * Math.PI * 0.05F);
				RenderSystem.rotatef(r, 0.0F, 1.0F, 0.0F);
				if (pandaEntity.isBaby()) {
					RenderSystem.translatef(0.0F, 0.8F, 0.55F);
				}
			}
		}

		float r = pandaEntity.getLieOnBackAnimationProgress(h);
		if (r > 0.0F) {
			float k = pandaEntity.isBaby() ? 0.5F : 1.3F;
			RenderSystem.translatef(0.0F, k * r, 0.0F);
			RenderSystem.rotatef(MathHelper.lerp(r, pandaEntity.pitch, pandaEntity.pitch + 180.0F), 1.0F, 0.0F, 0.0F);
		}
	}

	private float method_4086(float f, float g, int i, float h, float j) {
		return (float)i < j ? MathHelper.lerp(h, f, g) : f;
	}
}
