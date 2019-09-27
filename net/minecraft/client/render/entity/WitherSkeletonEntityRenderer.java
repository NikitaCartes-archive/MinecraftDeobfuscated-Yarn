/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
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
    public Identifier method_4119(AbstractSkeletonEntity abstractSkeletonEntity) {
        return SKIN;
    }

    protected void method_4161(AbstractSkeletonEntity abstractSkeletonEntity, class_4587 arg, float f) {
        arg.method_22905(1.2f, 1.2f, 1.2f);
    }
}

