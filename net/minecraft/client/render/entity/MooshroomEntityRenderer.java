/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.MooshroomMushroomFeatureRenderer;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class MooshroomEntityRenderer
extends MobEntityRenderer<MooshroomEntity, CowEntityModel<MooshroomEntity>> {
    private static final Map<MooshroomEntity.Type, Identifier> TEXTURES = Util.make(Maps.newHashMap(), map -> {
        map.put(MooshroomEntity.Type.BROWN, new Identifier("textures/entity/cow/brown_mooshroom.png"));
        map.put(MooshroomEntity.Type.RED, new Identifier("textures/entity/cow/red_mooshroom.png"));
    });

    public MooshroomEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new CowEntityModel(context.getPart(EntityModelLayers.MOOSHROOM)), 0.7f);
        this.addFeature(new MooshroomMushroomFeatureRenderer<MooshroomEntity>(this, context.getBlockRenderManager()));
    }

    @Override
    public Identifier getTexture(MooshroomEntity mooshroomEntity) {
        return TEXTURES.get(mooshroomEntity.getVariant());
    }
}

