package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EvilVillagerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.SpellcastingIllagerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EvokerIllagerEntityRenderer<T extends SpellcastingIllagerEntity> extends IllagerEntityRenderer<T> {
	private static final Identifier EVOKER_TEXTURE = new Identifier("textures/entity/illager/evoker.png");

	public EvokerIllagerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new EvilVillagerEntityModel<>(0.0F, 0.0F, 64, 64), 0.5F);
		this.addFeature(
			new HeldItemFeatureRenderer<T, EvilVillagerEntityModel<T>>(this) {
				public void method_23170(
					MatrixStack matrixStack,
					VertexConsumerProvider vertexConsumerProvider,
					int i,
					T spellcastingIllagerEntity,
					float f,
					float g,
					float h,
					float j,
					float k,
					float l,
					float m
				) {
					if (spellcastingIllagerEntity.isSpellcasting()) {
						super.method_17162(matrixStack, vertexConsumerProvider, i, spellcastingIllagerEntity, f, g, h, j, k, l, m);
					}
				}
			}
		);
	}

	public Identifier method_3961(T spellcastingIllagerEntity) {
		return EVOKER_TEXTURE;
	}
}
