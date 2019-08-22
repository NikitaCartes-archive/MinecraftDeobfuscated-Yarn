/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.SkeletonEntityRenderer;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class WitherSkeletonEntityRenderer
extends SkeletonEntityRenderer {
    private static final Identifier SKIN = new Identifier("textures/entity/skeleton/wither_skeleton.png");

    public WitherSkeletonEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Override
    protected Identifier method_4119(AbstractSkeletonEntity abstractSkeletonEntity) {
        return SKIN;
    }

    protected void method_4161(AbstractSkeletonEntity abstractSkeletonEntity, float f) {
        RenderSystem.scalef(1.2f, 1.2f, 1.2f);
    }
}

