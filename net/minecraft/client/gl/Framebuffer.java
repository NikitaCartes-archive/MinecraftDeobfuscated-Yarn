/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.platform.FramebufferInfo;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.TextureUtil;

@Environment(value=EnvType.CLIENT)
public class Framebuffer {
    public int textureWidth;
    public int textureHeight;
    public int viewportWidth;
    public int viewportHeight;
    public final boolean useDepthAttachment;
    public int fbo;
    public int colorAttachment;
    public int depthAttachment;
    public final float[] clearColor;
    public int texFilter;

    public Framebuffer(int i, int j, boolean bl, boolean bl2) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        this.useDepthAttachment = bl;
        this.fbo = -1;
        this.colorAttachment = -1;
        this.depthAttachment = -1;
        this.clearColor = new float[4];
        this.clearColor[0] = 1.0f;
        this.clearColor[1] = 1.0f;
        this.clearColor[2] = 1.0f;
        this.clearColor[3] = 0.0f;
        this.resize(i, j, bl2);
    }

    public void resize(int i, int j, boolean bl) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> this.resizeInternal(i, j, bl));
        } else {
            this.resizeInternal(i, j, bl);
        }
    }

    private void resizeInternal(int i, int j, boolean bl) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.enableDepthTest();
        if (this.fbo >= 0) {
            this.delete();
        }
        this.initFbo(i, j, bl);
        GlStateManager.bindFramebuffer(FramebufferInfo.target, 0);
    }

    public void delete() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        this.endRead();
        this.endWrite();
        if (this.depthAttachment > -1) {
            GlStateManager.deleteRenderbuffers(this.depthAttachment);
            this.depthAttachment = -1;
        }
        if (this.colorAttachment > -1) {
            TextureUtil.releaseTextureId(this.colorAttachment);
            this.colorAttachment = -1;
        }
        if (this.fbo > -1) {
            GlStateManager.bindFramebuffer(FramebufferInfo.target, 0);
            GlStateManager.deleteFramebuffers(this.fbo);
            this.fbo = -1;
        }
    }

    public void initFbo(int i, int j, boolean bl) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        this.viewportWidth = i;
        this.viewportHeight = j;
        this.textureWidth = i;
        this.textureHeight = j;
        this.fbo = GlStateManager.genFramebuffers();
        this.colorAttachment = TextureUtil.generateTextureId();
        if (this.useDepthAttachment) {
            this.depthAttachment = GlStateManager.genRenderbuffers();
        }
        this.setTexFilter(9728);
        GlStateManager.bindTexture(this.colorAttachment);
        GlStateManager.texImage2D(3553, 0, 32856, this.textureWidth, this.textureHeight, 0, 6408, 5121, null);
        GlStateManager.bindFramebuffer(FramebufferInfo.target, this.fbo);
        GlStateManager.framebufferTexture2D(FramebufferInfo.target, FramebufferInfo.field_20459, 3553, this.colorAttachment, 0);
        if (this.useDepthAttachment) {
            GlStateManager.bindRenderbuffer(FramebufferInfo.renderBufferTarget, this.depthAttachment);
            GlStateManager.renderbufferStorage(FramebufferInfo.renderBufferTarget, 33190, this.textureWidth, this.textureHeight);
            GlStateManager.framebufferRenderbuffer(FramebufferInfo.target, FramebufferInfo.attachment, FramebufferInfo.renderBufferTarget, this.depthAttachment);
        }
        this.checkFramebufferStatus();
        this.clear(bl);
        this.endRead();
    }

    public void setTexFilter(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        this.texFilter = i;
        GlStateManager.bindTexture(this.colorAttachment);
        GlStateManager.texParameter(3553, 10241, i);
        GlStateManager.texParameter(3553, 10240, i);
        GlStateManager.texParameter(3553, 10242, 10496);
        GlStateManager.texParameter(3553, 10243, 10496);
        GlStateManager.bindTexture(0);
    }

    public void checkFramebufferStatus() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        int i = GlStateManager.checkFramebufferStatus(FramebufferInfo.target);
        if (i == FramebufferInfo.field_20461) {
            return;
        }
        if (i == FramebufferInfo.field_20462) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
        }
        if (i == FramebufferInfo.field_20463) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
        }
        if (i == FramebufferInfo.field_20464) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
        }
        if (i == FramebufferInfo.field_20465) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
        }
        throw new RuntimeException("glCheckFramebufferStatus returned unknown status:" + i);
    }

    public void beginRead() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.bindTexture(this.colorAttachment);
    }

    public void endRead() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.bindTexture(0);
    }

    public void beginWrite(boolean bl) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> this.bind(bl));
        } else {
            this.bind(bl);
        }
    }

    private void bind(boolean bl) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.bindFramebuffer(FramebufferInfo.target, this.fbo);
        if (bl) {
            GlStateManager.viewport(0, 0, this.viewportWidth, this.viewportHeight);
        }
    }

    public void endWrite() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> GlStateManager.bindFramebuffer(FramebufferInfo.target, 0));
        } else {
            GlStateManager.bindFramebuffer(FramebufferInfo.target, 0);
        }
    }

    public void setClearColor(float f, float g, float h, float i) {
        this.clearColor[0] = f;
        this.clearColor[1] = g;
        this.clearColor[2] = h;
        this.clearColor[3] = i;
    }

    public void draw(int i, int j) {
        this.draw(i, j, true);
    }

    public void draw(int i, int j, boolean bl) {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        if (!RenderSystem.isInInitPhase()) {
            RenderSystem.recordRenderCall(() -> this.drawInternal(i, j, bl));
        } else {
            this.drawInternal(i, j, bl);
        }
    }

    private void drawInternal(int i, int j, boolean bl) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.colorMask(true, true, true, false);
        GlStateManager.disableDepthTest();
        GlStateManager.depthMask(false);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0, i, j, 0.0, 1000.0, 3000.0);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translatef(0.0f, 0.0f, -2000.0f);
        GlStateManager.viewport(0, 0, i, j);
        GlStateManager.enableTexture();
        GlStateManager.disableLighting();
        GlStateManager.disableAlphaTest();
        if (bl) {
            GlStateManager.disableBlend();
            GlStateManager.enableColorMaterial();
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.beginRead();
        float f = i;
        float g = j;
        float h = (float)this.viewportWidth / (float)this.textureWidth;
        float k = (float)this.viewportHeight / (float)this.textureHeight;
        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(0.0, g, 0.0).texture(0.0f, 0.0f).color(255, 255, 255, 255).next();
        bufferBuilder.vertex(f, g, 0.0).texture(h, 0.0f).color(255, 255, 255, 255).next();
        bufferBuilder.vertex(f, 0.0, 0.0).texture(h, k).color(255, 255, 255, 255).next();
        bufferBuilder.vertex(0.0, 0.0, 0.0).texture(0.0f, k).color(255, 255, 255, 255).next();
        tessellator.draw();
        this.endRead();
        GlStateManager.depthMask(true);
        GlStateManager.colorMask(true, true, true, true);
    }

    public void clear(boolean bl) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        this.beginWrite(true);
        GlStateManager.clearColor(this.clearColor[0], this.clearColor[1], this.clearColor[2], this.clearColor[3]);
        int i = 16384;
        if (this.useDepthAttachment) {
            GlStateManager.clearDepth(1.0);
            i |= 0x100;
        }
        GlStateManager.clear(i, bl);
        this.endWrite();
    }
}

