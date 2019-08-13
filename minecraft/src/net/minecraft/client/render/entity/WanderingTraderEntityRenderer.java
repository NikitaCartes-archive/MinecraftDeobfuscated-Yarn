package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class WanderingTraderEntityRenderer extends MobEntityRenderer<WanderingTraderEntity, VillagerResemblingModel<WanderingTraderEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/wandering_trader.png");

	public WanderingTraderEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new VillagerResemblingModel<>(0.0F), 0.5F);
		this.addFeature(new HeadFeatureRenderer<>(this));
		this.addFeature(new VillagerHeldItemFeatureRenderer<>(this));
	}

	protected Identifier method_18045(WanderingTraderEntity wanderingTraderEntity) {
		return TEXTURE;
	}

	protected void method_18046(WanderingTraderEntity wanderingTraderEntity, float f) {
		float g = 0.9375F;
		GlStateManager.scalef(0.9375F, 0.9375F, 0.9375F);
	}
}
