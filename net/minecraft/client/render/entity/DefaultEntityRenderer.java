/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class DefaultEntityRenderer
extends EntityRenderer<Entity> {
    public DefaultEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Override
    public void render(Entity entity, double d, double e, double f, float g, float h) {
        GlStateManager.pushMatrix();
        DefaultEntityRenderer.renderBox(entity.getBoundingBox(), d - entity.lastRenderX, e - entity.lastRenderY, f - entity.lastRenderZ);
        GlStateManager.popMatrix();
        super.render(entity, d, e, f, g, h);
    }

    @Override
    @Nullable
    protected Identifier getTexture(Entity entity) {
        return null;
    }
}

