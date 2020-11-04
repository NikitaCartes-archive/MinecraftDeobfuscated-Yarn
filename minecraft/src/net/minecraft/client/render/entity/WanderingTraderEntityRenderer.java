package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class WanderingTraderEntityRenderer extends MobEntityRenderer<WanderingTraderEntity, VillagerResemblingModel<WanderingTraderEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/wandering_trader.png");

	public WanderingTraderEntityRenderer(class_5617.class_5618 arg) {
		super(arg, new VillagerResemblingModel<>(arg.method_32167(EntityModelLayers.WANDERING_TRADER)), 0.5F);
		this.addFeature(new HeadFeatureRenderer<>(this, arg.method_32170()));
		this.addFeature(new VillagerHeldItemFeatureRenderer<>(this));
	}

	public Identifier getTexture(WanderingTraderEntity wanderingTraderEntity) {
		return TEXTURE;
	}

	protected void scale(WanderingTraderEntity wanderingTraderEntity, MatrixStack matrixStack, float f) {
		float g = 0.9375F;
		matrixStack.scale(0.9375F, 0.9375F, 0.9375F);
	}
}
