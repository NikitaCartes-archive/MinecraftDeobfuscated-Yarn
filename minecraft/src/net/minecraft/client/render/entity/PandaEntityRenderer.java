package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.PandaHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PandaEntityModel;
import net.minecraft.client.render.entity.state.PandaEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class PandaEntityRenderer extends AgeableMobEntityRenderer<PandaEntity, PandaEntityRenderState, PandaEntityModel> {
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
		super(context, new PandaEntityModel(context.getPart(EntityModelLayers.PANDA)), new PandaEntityModel(context.getPart(EntityModelLayers.PANDA_BABY)), 0.9F);
		this.addFeature(new PandaHeldItemFeatureRenderer(this, context.getItemRenderer()));
	}

	public Identifier getTexture(PandaEntityRenderState pandaEntityRenderState) {
		return (Identifier)TEXTURES.getOrDefault(pandaEntityRenderState.gene, (Identifier)TEXTURES.get(PandaEntity.Gene.NORMAL));
	}

	public PandaEntityRenderState createRenderState() {
		return new PandaEntityRenderState();
	}

	public void updateRenderState(PandaEntity pandaEntity, PandaEntityRenderState pandaEntityRenderState, float f) {
		super.updateRenderState(pandaEntity, pandaEntityRenderState, f);
		pandaEntityRenderState.gene = pandaEntity.getProductGene();
		pandaEntityRenderState.askingForBamboo = pandaEntity.getAskForBambooTicks() > 0;
		pandaEntityRenderState.sneezing = pandaEntity.isSneezing();
		pandaEntityRenderState.sneezeProgress = pandaEntity.getSneezeProgress();
		pandaEntityRenderState.eating = pandaEntity.isEating();
		pandaEntityRenderState.scaredByThunderstorm = pandaEntity.isScaredByThunderstorm();
		pandaEntityRenderState.sitting = pandaEntity.isSitting();
		pandaEntityRenderState.sittingAnimationProgress = pandaEntity.getSittingAnimationProgress(f);
		pandaEntityRenderState.lieOnBackAnimationProgress = pandaEntity.getLieOnBackAnimationProgress(f);
		pandaEntityRenderState.rollOverAnimationProgress = pandaEntity.isBaby() ? 0.0F : pandaEntity.getRollOverAnimationProgress(f);
		pandaEntityRenderState.playingTicks = pandaEntity.playingTicks > 0 ? (float)pandaEntity.playingTicks + f : 0.0F;
	}

	protected void setupTransforms(PandaEntityRenderState pandaEntityRenderState, MatrixStack matrixStack, float f, float g) {
		super.setupTransforms(pandaEntityRenderState, matrixStack, f, g);
		if (pandaEntityRenderState.playingTicks > 0.0F) {
			float h = MathHelper.fractionalPart(pandaEntityRenderState.playingTicks);
			int i = MathHelper.floor(pandaEntityRenderState.playingTicks);
			int j = i + 1;
			float k = 7.0F;
			float l = pandaEntityRenderState.baby ? 0.3F : 0.8F;
			if ((float)i < 8.0F) {
				float m = 90.0F * (float)i / 7.0F;
				float n = 90.0F * (float)j / 7.0F;
				float o = this.getAngle(m, n, j, h, 8.0F);
				matrixStack.translate(0.0F, (l + 0.2F) * (o / 90.0F), 0.0F);
				matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-o));
			} else if ((float)i < 16.0F) {
				float m = ((float)i - 8.0F) / 7.0F;
				float n = 90.0F + 90.0F * m;
				float p = 90.0F + 90.0F * ((float)j - 8.0F) / 7.0F;
				float o = this.getAngle(n, p, j, h, 16.0F);
				matrixStack.translate(0.0F, l + 0.2F + (l - 0.2F) * (o - 90.0F) / 90.0F, 0.0F);
				matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-o));
			} else if ((float)i < 24.0F) {
				float m = ((float)i - 16.0F) / 7.0F;
				float n = 180.0F + 90.0F * m;
				float p = 180.0F + 90.0F * ((float)j - 16.0F) / 7.0F;
				float o = this.getAngle(n, p, j, h, 24.0F);
				matrixStack.translate(0.0F, l + l * (270.0F - o) / 90.0F, 0.0F);
				matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-o));
			} else if (i < 32) {
				float m = ((float)i - 24.0F) / 7.0F;
				float n = 270.0F + 90.0F * m;
				float p = 270.0F + 90.0F * ((float)j - 24.0F) / 7.0F;
				float o = this.getAngle(n, p, j, h, 32.0F);
				matrixStack.translate(0.0F, l * ((360.0F - o) / 90.0F), 0.0F);
				matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-o));
			}
		}

		float h = pandaEntityRenderState.sittingAnimationProgress;
		if (h > 0.0F) {
			matrixStack.translate(0.0F, 0.8F * h, 0.0F);
			matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.lerp(h, pandaEntityRenderState.pitch, pandaEntityRenderState.pitch + 90.0F)));
			matrixStack.translate(0.0F, -1.0F * h, 0.0F);
			if (pandaEntityRenderState.scaredByThunderstorm) {
				float q = (float)(Math.cos((double)(pandaEntityRenderState.age * 1.25F)) * Math.PI * 0.05F);
				matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(q));
				if (pandaEntityRenderState.baby) {
					matrixStack.translate(0.0F, 0.8F, 0.55F);
				}
			}
		}

		float q = pandaEntityRenderState.lieOnBackAnimationProgress;
		if (q > 0.0F) {
			float r = pandaEntityRenderState.baby ? 0.5F : 1.3F;
			matrixStack.translate(0.0F, r * q, 0.0F);
			matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.lerp(q, pandaEntityRenderState.pitch, pandaEntityRenderState.pitch + 180.0F)));
		}
	}

	private float getAngle(float f, float g, int i, float h, float j) {
		return (float)i < j ? MathHelper.lerp(h, f, g) : f;
	}
}
