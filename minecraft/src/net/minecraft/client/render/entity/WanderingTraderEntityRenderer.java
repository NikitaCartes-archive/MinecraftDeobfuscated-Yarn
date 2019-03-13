package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.VillagerEntityModel;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class WanderingTraderEntityRenderer extends MobEntityRenderer<WanderingTraderEntity, VillagerEntityModel<WanderingTraderEntity>> {
	private static final Identifier field_17739 = new Identifier("textures/entity/wandering_trader.png");

	public WanderingTraderEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new VillagerEntityModel<>(0.0F), 0.5F);
		this.method_4046(new HeadFeatureRenderer<>(this));
		this.method_4046(new VillagerHeldItemFeatureRenderer<>(this));
	}

	protected Identifier method_18045(WanderingTraderEntity wanderingTraderEntity) {
		return field_17739;
	}

	protected void method_18046(WanderingTraderEntity wanderingTraderEntity, float f) {
		float g = 0.9375F;
		GlStateManager.scalef(0.9375F, 0.9375F, 0.9375F);
	}
}
