/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class BipedEntityRenderer<T extends MobEntity, M extends BipedEntityModel<T>>
extends MobEntityRenderer<T, M> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/steve.png");

    public BipedEntityRenderer(class_5617.class_5618 arg, M model, float f) {
        this(arg, model, f, 1.0f, 1.0f, 1.0f);
    }

    public BipedEntityRenderer(class_5617.class_5618 arg, M bipedEntityModel, float f, float g, float h, float i) {
        super(arg, bipedEntityModel, f);
        this.addFeature(new HeadFeatureRenderer(this, arg.method_32170(), g, h, i));
        this.addFeature(new ElytraFeatureRenderer(this, arg.method_32170()));
        this.addFeature(new HeldItemFeatureRenderer(this));
    }

    @Override
    public Identifier getTexture(T mobEntity) {
        return TEXTURE;
    }
}

