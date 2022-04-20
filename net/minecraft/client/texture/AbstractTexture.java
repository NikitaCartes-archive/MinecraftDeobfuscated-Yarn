/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractTexture
implements AutoCloseable {
    public static final int DEFAULT_ID = -1;
    protected int glId = -1;
    protected boolean bilinear;
    protected boolean mipmap;

    public void setFilter(boolean bilinear, boolean mipmap) {
        int j;
        int i;
        RenderSystem.assertOnRenderThreadOrInit();
        this.bilinear = bilinear;
        this.mipmap = mipmap;
        if (bilinear) {
            i = mipmap ? 9987 : 9729;
            j = 9729;
        } else {
            i = mipmap ? GlConst.GL_NEAREST_MIPMAP_LINEAR : GlConst.GL_NEAREST;
            j = GlConst.GL_NEAREST;
        }
        this.bindTexture();
        GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MIN_FILTER, i);
        GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MAG_FILTER, j);
    }

    public int getGlId() {
        RenderSystem.assertOnRenderThreadOrInit();
        if (this.glId == -1) {
            this.glId = TextureUtil.generateTextureId();
        }
        return this.glId;
    }

    public void clearGlId() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
                if (this.glId != -1) {
                    TextureUtil.releaseTextureId(this.glId);
                    this.glId = -1;
                }
            });
        } else if (this.glId != -1) {
            TextureUtil.releaseTextureId(this.glId);
            this.glId = -1;
        }
    }

    public abstract void load(ResourceManager var1) throws IOException;

    public void bindTexture() {
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> GlStateManager._bindTexture(this.getGlId()));
        } else {
            GlStateManager._bindTexture(this.getGlId());
        }
    }

    public void registerTexture(TextureManager textureManager, ResourceManager resourceManager, Identifier id, Executor executor) {
        textureManager.registerTexture(id, this);
    }

    @Override
    public void close() {
    }
}

