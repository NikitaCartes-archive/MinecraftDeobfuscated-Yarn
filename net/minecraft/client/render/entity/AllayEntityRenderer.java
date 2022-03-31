/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.AllayEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class AllayEntityRenderer
extends MobEntityRenderer<AllayEntity, AllayEntityModel> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/allay/allay.png");
    private static final int field_38462 = 60;
    private static final int field_38463 = 5;

    public AllayEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new AllayEntityModel(context.getPart(EntityModelLayers.ALLAY)), 0.4f);
        this.addFeature(new HeldItemFeatureRenderer<AllayEntity, AllayEntityModel>(this));
    }

    @Override
    public Identifier getTexture(AllayEntity allayEntity) {
        return TEXTURE;
    }

    @Override
    protected int getBlockLight(AllayEntity allayEntity, BlockPos blockPos) {
        long l = allayEntity.getWorld().getTime() + (long)Math.abs(allayEntity.getUuid().hashCode());
        float f = Math.abs(l % 120L - 60L);
        float g = f / 60.0f;
        return (int)MathHelper.lerp(g, 5.0f, 15.0f);
    }
}

