package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.PandaHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PandaEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class PandaEntityRenderer extends MobEntityRenderer<PandaEntity, PandaEntityModel<PandaEntity>> {
	private static final Map<PandaEntity.Gene, Identifier> TEXTURES = Util.make(Maps.newEnumMap(PandaEntity.Gene.class), map -> {
		map.put(PandaEntity.Gene.NORMAL, Identifier.ofVanilla("textures/entity/panda/panda.png"));
		map.put(PandaEntity.Gene.LAZY, Identifier.ofVanilla("textures/entity/panda/lazy_panda.png"));
		map.put(PandaEntity.Gene.WORRIED, Identifier.ofVanilla("textures/entity/panda/worried_panda.png"));
		map.put(PandaEntity.Gene.PLAYFUL, Identifier.ofVanilla("textures/entity/panda/playful_panda.png"));
		map.put(PandaEntity.Gene.BROWN, Identifier.ofVanilla("textures/entity/panda/brown_panda.png"));
		map.put(PandaEntity.Gene.WEAK, Identifier.ofVanilla("textures/entity/panda/weak_panda.png"));
		map.put(PandaEntity.Gene.AGGRESSIVE, Identifier.ofVanilla("textures/entity/panda/aggressive_panda.png"));
	});

	public PandaEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new PandaEntityModel<>(context.getPart(EntityModelLayers.PANDA)), 0.9F);
		this.addFeature(new PandaHeldItemFeatureRenderer(this, context.getHeldItemRenderer()));
	}

	public Identifier getTexture(PandaEntity pandaEntity) {
		return (Identifier)TEXTURES.getOrDefault(pandaEntity.getProductGene(), (Identifier)TEXTURES.get(PandaEntity.Gene.NORMAL));
	}

	protected void setupTransforms(PandaEntity pandaEntity, MatrixStack matrixStack, float f, float g, float h, float i) {
		super.setupTransforms(pandaEntity, matrixStack, f, g, h, i);
		if (pandaEntity.playingTicks > 0) {
			int j = pandaEntity.playingTicks;
			int k = j + 1;
			float l = 7.0F;
			float m = pandaEntity.isBaby() ? 0.3F : 0.8F;
			if (j < 8) {
				float n = (float)(90 * j) / 7.0F;
				float o = (float)(90 * k) / 7.0F;
				float p = this.getAngle(n, o, k, h, 8.0F);
				matrixStack.translate(0.0F, (m + 0.2F) * (p / 90.0F), 0.0F);
				matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-p));
			} else if (j < 16) {
				float n = ((float)j - 8.0F) / 7.0F;
				float o = 90.0F + 90.0F * n;
				float q = 90.0F + 90.0F * ((float)k - 8.0F) / 7.0F;
				float p = this.getAngle(o, q, k, h, 16.0F);
				matrixStack.translate(0.0F, m + 0.2F + (m - 0.2F) * (p - 90.0F) / 90.0F, 0.0F);
				matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-p));
			} else if ((float)j < 24.0F) {
				float n = ((float)j - 16.0F) / 7.0F;
				float o = 180.0F + 90.0F * n;
				float q = 180.0F + 90.0F * ((float)k - 16.0F) / 7.0F;
				float p = this.getAngle(o, q, k, h, 24.0F);
				matrixStack.translate(0.0F, m + m * (270.0F - p) / 90.0F, 0.0F);
				matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-p));
			} else if (j < 32) {
				float n = ((float)j - 24.0F) / 7.0F;
				float o = 270.0F + 90.0F * n;
				float q = 270.0F + 90.0F * ((float)k - 24.0F) / 7.0F;
				float p = this.getAngle(o, q, k, h, 32.0F);
				matrixStack.translate(0.0F, m * ((360.0F - p) / 90.0F), 0.0F);
				matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-p));
			}
		}

		float r = pandaEntity.getSittingAnimationProgress(h);
		if (r > 0.0F) {
			matrixStack.translate(0.0F, 0.8F * r, 0.0F);
			matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.lerp(r, pandaEntity.getPitch(), pandaEntity.getPitch() + 90.0F)));
			matrixStack.translate(0.0F, -1.0F * r, 0.0F);
			if (pandaEntity.isScaredByThunderstorm()) {
				float s = (float)(Math.cos((double)pandaEntity.age * 1.25) * Math.PI * 0.05F);
				matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(s));
				if (pandaEntity.isBaby()) {
					matrixStack.translate(0.0F, 0.8F, 0.55F);
				}
			}
		}

		float s = pandaEntity.getLieOnBackAnimationProgress(h);
		if (s > 0.0F) {
			float l = pandaEntity.isBaby() ? 0.5F : 1.3F;
			matrixStack.translate(0.0F, l * s, 0.0F);
			matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.lerp(s, pandaEntity.getPitch(), pandaEntity.getPitch() + 180.0F)));
		}
	}

	private float getAngle(float f, float g, int i, float h, float j) {
		return (float)i < j ? MathHelper.lerp(h, f, g) : f;
	}
}
