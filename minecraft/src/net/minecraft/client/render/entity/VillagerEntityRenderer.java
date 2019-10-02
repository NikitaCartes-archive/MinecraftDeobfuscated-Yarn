package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class VillagerEntityRenderer extends MobEntityRenderer<VillagerEntity, VillagerResemblingModel<VillagerEntity>> {
	private static final Identifier VILLAGER_SKIN = new Identifier("textures/entity/villager/villager.png");

	public VillagerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ReloadableResourceManager reloadableResourceManager) {
		super(entityRenderDispatcher, new VillagerResemblingModel<>(0.0F), 0.5F);
		this.addFeature(new HeadFeatureRenderer<>(this));
		this.addFeature(new VillagerClothingFeatureRenderer<>(this, reloadableResourceManager, "villager"));
		this.addFeature(new VillagerHeldItemFeatureRenderer<>(this));
	}

	public Identifier method_4151(VillagerEntity villagerEntity) {
		return VILLAGER_SKIN;
	}

	protected void method_4149(VillagerEntity villagerEntity, MatrixStack matrixStack, float f) {
		float g = 0.9375F;
		if (villagerEntity.isBaby()) {
			g = (float)((double)g * 0.5);
			this.field_4673 = 0.25F;
		} else {
			this.field_4673 = 0.5F;
		}

		matrixStack.scale(g, g, g);
	}
}
