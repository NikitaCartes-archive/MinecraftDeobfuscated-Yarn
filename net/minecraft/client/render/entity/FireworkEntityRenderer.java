/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.FireworkEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class FireworkEntityRenderer
extends EntityRenderer<FireworkEntity> {
    private final ItemRenderer itemRenderer;

    public FireworkEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer) {
        super(entityRenderDispatcher);
        this.itemRenderer = itemRenderer;
    }

    public void method_3968(FireworkEntity fireworkEntity, double d, double e, double f, float g, float h) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)d, (float)e, (float)f);
        GlStateManager.enableRescaleNormal();
        GlStateManager.rotatef(-this.renderManager.cameraYaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef((float)(this.renderManager.gameOptions.perspective == 2 ? -1 : 1) * this.renderManager.cameraPitch, 1.0f, 0.0f, 0.0f);
        if (fireworkEntity.wasShotAtAngle()) {
            GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
        } else {
            GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
        }
        this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        if (this.field_4674) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(fireworkEntity));
        }
        this.itemRenderer.renderItem(fireworkEntity.getStack(), ModelTransformation.Type.GROUND);
        if (this.field_4674) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.render(fireworkEntity, d, e, f, g, h);
    }

    protected Identifier method_3969(FireworkEntity fireworkEntity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
    }
}

