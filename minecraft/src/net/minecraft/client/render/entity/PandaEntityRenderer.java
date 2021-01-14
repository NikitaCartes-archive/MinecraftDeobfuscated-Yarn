package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.PandaHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.PandaEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class PandaEntityRenderer extends MobEntityRenderer<PandaEntity, PandaEntityModel<PandaEntity>> {
	private static final Map<PandaEntity.Gene, Identifier> TEXTURES = Util.make(Maps.newEnumMap(PandaEntity.Gene.class), enumMap -> {
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

	public Identifier getTexture(PandaEntity pandaEntity) {
		return (Identifier)TEXTURES.getOrDefault(pandaEntity.getProductGene(), TEXTURES.get(PandaEntity.Gene.NORMAL));
	}

	protected void setupTransforms(PandaEntity pandaEntity, MatrixStack matrixStack, float f, float g, float h) {
		super.setupTransforms(pandaEntity, matrixStack, f, g, h);
		if (pandaEntity.playingTicks > 0) {
			int i = pandaEntity.playingTicks;
			int j = i + 1;
			float k = 7.0F;
			float l = pandaEntity.isBaby() ? 0.3F : 0.8F;
			if (i < 8) {
				float m = (float)(90 * i) / 7.0F;
				float n = (float)(90 * j) / 7.0F;
				float o = this.method_4086(m, n, j, h, 8.0F);
				matrixStack.translate(0.0, (double)((l + 0.2F) * (o / 90.0F)), 0.0);
				matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-o));
			} else if (i < 16) {
				float m = ((float)i - 8.0F) / 7.0F;
				float n = 90.0F + 90.0F * m;
				float p = 90.0F + 90.0F * ((float)j - 8.0F) / 7.0F;
				float o = this.method_4086(n, p, j, h, 16.0F);
				matrixStack.translate(0.0, (double)(l + 0.2F + (l - 0.2F) * (o - 90.0F) / 90.0F), 0.0);
				matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-o));
			} else if ((float)i < 24.0F) {
				float m = ((float)i - 16.0F) / 7.0F;
				float n = 180.0F + 90.0F * m;
				float p = 180.0F + 90.0F * ((float)j - 16.0F) / 7.0F;
				float o = this.method_4086(n, p, j, h, 24.0F);
				matrixStack.translate(0.0, (double)(l + l * (270.0F - o) / 90.0F), 0.0);
				matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-o));
			} else if (i < 32) {
				float m = ((float)i - 24.0F) / 7.0F;
				float n = 270.0F + 90.0F * m;
				float p = 270.0F + 90.0F * ((float)j - 24.0F) / 7.0F;
				float o = this.method_4086(n, p, j, h, 32.0F);
				matrixStack.translate(0.0, (double)(l * ((360.0F - o) / 90.0F)), 0.0);
				matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-o));
			}
		}

		float q = pandaEntity.getScaredAnimationProgress(h);
		if (q > 0.0F) {
			matrixStack.translate(0.0, (double)(0.8F * q), 0.0);
			matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(MathHelper.lerp(q, pandaEntity.pitch, pandaEntity.pitch + 90.0F)));
			matrixStack.translate(0.0, (double)(-1.0F * q), 0.0);
			if (pandaEntity.isScaredByThunderstorm()) {
				float r = (float)(Math.cos((double)pandaEntity.age * 1.25) * Math.PI * 0.05F);
				matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(r));
				if (pandaEntity.isBaby()) {
					matrixStack.translate(0.0, 0.8F, 0.55F);
				}
			}
		}

		float r = pandaEntity.getLieOnBackAnimationProgress(h);
		if (r > 0.0F) {
			float k = pandaEntity.isBaby() ? 0.5F : 1.3F;
			matrixStack.translate(0.0, (double)(k * r), 0.0);
			matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(MathHelper.lerp(r, pandaEntity.pitch, pandaEntity.pitch + 180.0F)));
		}
	}

	private float method_4086(float f, float g, int i, float h, float j) {
		return (float)i < j ? MathHelper.lerp(h, f, g) : f;
	}
}
