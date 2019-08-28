package net.minecraft.client.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class AbstractTexture implements Texture {
	protected int glId = -1;
	protected boolean bilinear;
	protected boolean mipmap;
	protected boolean oldBilinear;
	protected boolean oldMipmap;

	public void setFilter(boolean bl, boolean bl2) {
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

		RenderSystem.texParameter(3553, 10241, i);
		RenderSystem.texParameter(3553, 10240, j);
	}

	@Override
	public void pushFilter(boolean bl, boolean bl2) {
		this.oldBilinear = this.bilinear;
		this.oldMipmap = this.mipmap;
		this.setFilter(bl, bl2);
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
