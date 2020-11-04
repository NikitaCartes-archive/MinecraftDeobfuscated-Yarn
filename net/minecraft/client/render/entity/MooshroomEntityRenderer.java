/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
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
    private static final Map<MooshroomEntity.Type, Identifier> TEXTURES = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put(MooshroomEntity.Type.BROWN, new Identifier("textures/entity/cow/brown_mooshroom.png"));
        hashMap.put(MooshroomEntity.Type.RED, new Identifier("textures/entity/cow/red_mooshroom.png"));
    });

    public MooshroomEntityRenderer(class_5617.class_5618 arg) {
        super(arg, new CowEntityModel(arg.method_32167(EntityModelLayers.MOOSHROOM)), 0.7f);
        this.addFeature(new MooshroomMushroomFeatureRenderer<MooshroomEntity>(this));
    }

    @Override
    public Identifier getTexture(MooshroomEntity mooshroomEntity) {
        return TEXTURES.get((Object)mooshroomEntity.getMooshroomType());
    }
}

