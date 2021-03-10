package net.minecraft;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GlShader;

@Environment(EnvType.CLIENT)
public class class_5937 extends GlShader {
	private static final class_5913 field_29329 = new class_5913() {
		@Override
		public String method_34233(boolean bl, String string) {
			return "#error Import statement not supported";
		}
	};
	private int field_29330;

	private class_5937(GlShader.Type type, int i, String string) {
		super(type, i, string);
	}

	public void method_34414(class_5936 arg) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		this.field_29330++;
		this.attachTo(arg);
	}

	@Override
	public void release() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		this.field_29330--;
		if (this.field_29330 <= 0) {
			super.release();
		}
	}

	public static class_5937 method_34415(GlShader.Type type, String string, InputStream inputStream, String string2) throws IOException {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		int i = method_34416(type, string, inputStream, string2, field_29329);
		class_5937 lv = new class_5937(type, i, string);
		type.getLoadedShaders().put(string, lv);
		return lv;
	}
}
