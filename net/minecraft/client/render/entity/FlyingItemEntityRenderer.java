/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class FlyingItemEntityRenderer<T extends Entity>
extends EntityRenderer<T> {
    private final ItemRenderer item;
    private final float scale;

    public FlyingItemEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer, float f) {
        super(entityRenderDispatcher);
        this.item = itemRenderer;
        this.scale = f;
    }

    public FlyingItemEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer) {
        this(entityRenderDispatcher, itemRenderer, 1.0f);
    }

    @Override
    public void render(T entity, double d, double e, double f, float g, float h) {
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float)d, (float)e, (float)f);
        RenderSystem.enableRescaleNormal();
        RenderSystem.scalef(this.scale, this.scale, this.scale);
        RenderSystem.rotatef(-this.renderManager.cameraYaw, 0.0f, 1.0f, 0.0f);
        RenderSystem.rotatef((float)(this.renderManager.gameOptions.perspective == 2 ? -1 : 1) * this.renderManager.cameraPitch, 1.0f, 0.0f, 0.0f);
        RenderSystem.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
        this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        if (this.renderOutlines) {
            RenderSystem.enableColorMaterial();
            RenderSystem.setupSolidRenderingTextureCombine(this.getOutlineColor(entity));
        }
        this.item.renderItem(((FlyingItemEntity)entity).getStack(), ModelTransformation.Type.GROUND);
        if (this.renderOutlines) {
            RenderSystem.tearDownSolidRenderingTextureCombine();
            RenderSystem.disableColorMaterial();
        }
        RenderSystem.disableRescaleNormal();
        RenderSystem.popMatrix();
        super.render(entity, d, e, f, g, h);
    }

    @Override
    protected Identifier getTexture(Entity entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
    }
}

