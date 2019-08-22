/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class VillagerEntityRenderer
extends MobEntityRenderer<VillagerEntity, VillagerResemblingModel<VillagerEntity>> {
    private static final Identifier VILLAGER_SKIN = new Identifier("textures/entity/villager/villager.png");

    public VillagerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ReloadableResourceManager reloadableResourceManager) {
        super(entityRenderDispatcher, new VillagerResemblingModel(0.0f), 0.5f);
        this.addFeature(new HeadFeatureRenderer<VillagerEntity, VillagerResemblingModel<VillagerEntity>>(this));
        this.addFeature(new VillagerClothingFeatureRenderer<VillagerEntity, VillagerResemblingModel<VillagerEntity>>(this, reloadableResourceManager, "villager"));
        this.addFeature(new VillagerHeldItemFeatureRenderer<VillagerEntity>(this));
    }

    protected Identifier method_4151(VillagerEntity villagerEntity) {
        return VILLAGER_SKIN;
    }

    protected void method_4149(VillagerEntity villagerEntity, float f) {
        float g = 0.9375f;
        if (villagerEntity.isBaby()) {
            g = (float)((double)g * 0.5);
            this.field_4673 = 0.25f;
        } else {
            this.field_4673 = 0.5f;
        }
        RenderSystem.scalef(g, g, g);
    }
}

