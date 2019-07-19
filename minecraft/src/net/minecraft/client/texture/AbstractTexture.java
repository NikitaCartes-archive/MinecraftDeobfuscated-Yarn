package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class AbstractTexture implements Texture {
	protected int glId = -1;
	protected boolean bilinear;
	protected boolean mipmap;
	protected boolean oldBilinear;
	protected boolean oldMipmap;

	public void setFilter(boolean bilinear, boolean mipmap) {
		this.bilinear = bilinear;
		this.mipmap = mipmap;
		int i;
		int j;
		if (bilinear) {
			i = mipmap ? 9987 : 9729;
			j = 9729;
		} else {
			i = mipmap ? 9986 : 9728;
			j = 9728;
		}

		GlStateManager.texParameter(3553, 10241, i);
		GlStateManager.texParameter(3553, 10240, j);
	}

	@Override
	public void pushFilter(boolean bilinear, boolean mipmap) {
		this.oldBilinear = this.bilinear;
		this.oldMipmap = this.mipmap;
		this.setFilter(bilinear, mipmap);
	}

	@Override
	public void popFilter() {
		this.setFilter(this.oldBilinear, this.oldMipmap);
	}

	@Override
	public int getGlId() {
		if (this.glId == -1) {
			this.glId = TextureUtil.generateTextureId();
		}

		return this.glId;
	}

	public void clearGlId() {
		if (this.glId != -1) {
			TextureUtil.releaseTextureId(this.glId);
			this.glId = -1;
		}
	}
}
