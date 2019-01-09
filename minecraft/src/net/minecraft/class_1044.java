package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_1044 implements class_1062 {
	protected int field_5204 = -1;
	protected boolean field_5205;
	protected boolean field_5203;
	protected boolean field_5202;
	protected boolean field_5201;

	public void method_4527(boolean bl, boolean bl2) {
		this.field_5205 = bl;
		this.field_5203 = bl2;
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

	@Override
	public void method_4626(boolean bl, boolean bl2) {
		this.field_5202 = this.field_5205;
		this.field_5201 = this.field_5203;
		this.method_4527(bl, bl2);
	}

	@Override
	public void method_4627() {
		this.method_4527(this.field_5202, this.field_5201);
	}

	@Override
	public int method_4624() {
		if (this.field_5204 == -1) {
			this.field_5204 = TextureUtil.generateTextureId();
		}

		return this.field_5204;
	}

	public void method_4528() {
		if (this.field_5204 != -1) {
			TextureUtil.releaseTextureId(this.field_5204);
			this.field_5204 = -1;
		}
	}
}
