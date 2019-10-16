package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class AbstractTexture {
	protected int glId = -1;
	protected boolean bilinear;
	protected boolean mipmap;

	public void setFilter(boolean bl, boolean bl2) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		this.bilinear = bl;
		this.mipmap = bl2;
		int i;
		int j;
		if (bl) {
			i = bl2 ? 9987 : 9729;
			j = 9729;
		} else {
			i = bl2 ? 9986 : 9728;
			j = 9728;
		}

		GlStateManager.texParameter(3553, 10241, i);
		GlStateManager.texParameter(3553, 10240, j);
	}

	public int getGlId() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
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

	public abstract void load(ResourceManager resourceManager) throws IOException;

	public void bindTexture() {
		if (!RenderSystem.isOnRenderThreadOrInit()) {
			RenderSystem.recordRenderCall(() -> GlStateManager.bindTexture(this.getGlId()));
		} else {
			GlStateManager.bindTexture(this.getGlId());
		}
	}

	public void registerTexture(TextureManager textureManager, ResourceManager resourceManager, Identifier identifier, Executor executor) {
		textureManager.registerTexture(identifier, this);
	}
}
