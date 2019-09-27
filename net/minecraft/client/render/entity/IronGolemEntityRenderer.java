/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.IronGolemFlowerFeatureRenderer;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class IronGolemEntityRenderer
extends MobEntityRenderer<IronGolemEntity, IronGolemEntityModel<IronGolemEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/iron_golem.png");

    public IronGolemEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new IronGolemEntityModel(), 0.7f);
        this.addFeature(new IronGolemFlowerFeatureRenderer(this));
    }

    public Identifier method_3987(IronGolemEntity ironGolemEntity) {
        return SKIN;
    }

    protected void method_3986(IronGolemEntity ironGolemEntity, class_4587 arg, float f, float g, float h) {
        super.setupTransforms(ironGolemEntity, arg, f, g, h);
        if ((double)ironGolemEntity.limbDistance < 0.01) {
            return;
        }
        float i = 13.0f;
        float j = ironGolemEntity.limbAngle - ironGolemEntity.limbDistance * (1.0f - h) + 6.0f;
        float k = (Math.abs(j % 13.0f - 6.5f) - 3.25f) / 3.25f;
        arg.method_22907(Vector3f.field_20707.method_23214(6.5f * k, true));
    }
}

