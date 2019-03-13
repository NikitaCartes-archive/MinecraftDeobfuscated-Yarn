package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.VillagerEntityModel;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class VillagerEntityRenderer extends MobEntityRenderer<VillagerEntity, VillagerEntityModel<VillagerEntity>> {
	private static final Identifier field_4807 = new Identifier("textures/entity/villager/villager.png");

	public VillagerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ReloadableResourceManager reloadableResourceManager) {
		super(entityRenderDispatcher, new VillagerEntityModel<>(0.0F), 0.5F);
		this.method_4046(new HeadFeatureRenderer<>(this));
		this.method_4046(new VillagerClothingFeatureRenderer<>(this, reloadableResourceManager, "villager"));
		this.method_4046(new VillagerHeldItemFeatureRenderer<>(this));
	}

	protected Identifier method_4151(VillagerEntity villagerEntity) {
		return field_4807;
	}

	protected void method_4149(VillagerEntity villagerEntity, float f) {
		float g = 0.9375F;
		if (villagerEntity.isChild()) {
			g = (float)((double)g * 0.5);
			this.field_4673 = 0.25F;
		} else {
			this.field_4673 = 0.5F;
		}

		GlStateManager.scalef(g, g, g);
	}
}
