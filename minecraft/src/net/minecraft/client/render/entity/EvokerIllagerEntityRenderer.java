package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EvilVillagerEntityModel;
import net.minecraft.entity.mob.SpellcastingIllagerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EvokerIllagerEntityRenderer<T extends SpellcastingIllagerEntity> extends IllagerEntityRenderer<T> {
	private static final Identifier EVOKER_TEXTURE = new Identifier("textures/entity/illager/evoker.png");

	public EvokerIllagerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new EvilVillagerEntityModel<>(0.0F, 0.0F, 64, 64), 0.5F);
		this.method_4046(new HeldItemFeatureRenderer<T, EvilVillagerEntityModel<T>>(this) {
			public void method_17147(T spellcastingIllagerEntity, float f, float g, float h, float i, float j, float k, float l) {
				if (spellcastingIllagerEntity.isSpellcasting()) {
					super.method_17162(spellcastingIllagerEntity, f, g, h, i, j, k, l);
				}
			}
		});
	}

	protected Identifier method_3961(T spellcastingIllagerEntity) {
		return EVOKER_TEXTURE;
	}
}
