/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.SquidEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SquidEntityRenderer
extends MobEntityRenderer<SquidEntity, SquidEntityModel<SquidEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/squid.png");

    public SquidEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new SquidEntityModel(), 0.7f);
    }

    protected Identifier method_4127(SquidEntity squidEntity) {
        return SKIN;
    }

    protected void method_4126(SquidEntity squidEntity, float f, float g, float h) {
        float i = MathHelper.lerp(h, squidEntity.field_6905, squidEntity.field_6907);
        float j = MathHelper.lerp(h, squidEntity.field_6906, squidEntity.field_6903);
        RenderSystem.translatef(0.0f, 0.5f, 0.0f);
        RenderSystem.rotatef(180.0f - g, 0.0f, 1.0f, 0.0f);
        RenderSystem.rotatef(i, 1.0f, 0.0f, 0.0f);
        RenderSystem.rotatef(j, 0.0f, 1.0f, 0.0f);
        RenderSystem.translatef(0.0f, -1.2f, 0.0f);
    }

    protected float method_4125(SquidEntity squidEntity, float f) {
        return MathHelper.lerp(f, squidEntity.field_6900, squidEntity.field_6904);
    }

    @Override
    protected /* synthetic */ float getAge(LivingEntity livingEntity, float f) {
        return this.method_4125((SquidEntity)livingEntity, f);
    }
}

