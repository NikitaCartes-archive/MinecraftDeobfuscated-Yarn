package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.render.entity.state.EvokerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.SpellcastingIllagerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EvokerEntityRenderer<T extends SpellcastingIllagerEntity> extends IllagerEntityRenderer<T, EvokerEntityRenderState> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/illager/evoker.png");

	public EvokerEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new IllagerEntityModel<>(context.getPart(EntityModelLayers.EVOKER)), 0.5F);
		this.addFeature(
			new HeldItemFeatureRenderer<EvokerEntityRenderState, IllagerEntityModel<EvokerEntityRenderState>>(this, context.getItemRenderer()) {
				public void render(
					MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, EvokerEntityRenderState evokerEntityRenderState, float f, float g
				) {
					if (evokerEntityRenderState.spellcasting) {
						super.render(matrixStack, vertexConsumerProvider, i, evokerEntityRenderState, f, g);
					}
				}
			}
		);
	}

	public Identifier getTexture(EvokerEntityRenderState evokerEntityRenderState) {
		return TEXTURE;
	}

	public EvokerEntityRenderState createRenderState() {
		return new EvokerEntityRenderState();
	}

	public void updateRenderState(T spellcastingIllagerEntity, EvokerEntityRenderState evokerEntityRenderState, float f) {
		super.updateRenderState(spellcastingIllagerEntity, evokerEntityRenderState, f);
		evokerEntityRenderState.spellcasting = spellcastingIllagerEntity.isSpellcasting();
	}
}
