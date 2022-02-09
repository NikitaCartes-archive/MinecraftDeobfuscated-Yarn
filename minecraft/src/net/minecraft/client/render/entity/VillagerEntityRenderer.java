package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class VillagerEntityRenderer extends MobEntityRenderer<VillagerEntity, VillagerResemblingModel<VillagerEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/villager/villager.png");

	public VillagerEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new VillagerResemblingModel<>(context.getPart(EntityModelLayers.VILLAGER)), 0.5F);
		this.addFeature(new HeadFeatureRenderer<>(this, context.getModelLoader()));
		this.addFeature(new VillagerClothingFeatureRenderer<>(this, context.getResourceManager(), "villager"));
		this.addFeature(new VillagerHeldItemFeatureRenderer<>(this));
	}

	public Identifier getTexture(VillagerEntity villagerEntity) {
		return TEXTURE;
	}

	protected void scale(VillagerEntity villagerEntity, MatrixStack matrixStack, float f) {
		float g = 0.9375F;
		if (villagerEntity.isBaby()) {
			g *= 0.5F;
			this.shadowRadius = 0.25F;
		} else {
			this.shadowRadius = 0.5F;
		}

		matrixStack.scale(g, g, g);
	}
}
