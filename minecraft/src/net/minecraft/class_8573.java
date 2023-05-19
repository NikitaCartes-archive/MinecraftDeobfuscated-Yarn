package net.minecraft;

import com.google.common.hash.Hashing;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class class_8573 implements AutoCloseable {
	private static final Identifier field_44933 = new Identifier("textures/misc/unknown_server.png");
	private static final int field_44934 = 64;
	private static final int field_44935 = 64;
	private final TextureManager field_44936;
	private final Identifier field_44937;
	@Nullable
	private NativeImageBackedTexture field_44938;
	private boolean field_44939;

	private class_8573(TextureManager textureManager, Identifier identifier) {
		this.field_44936 = textureManager;
		this.field_44937 = identifier;
	}

	public static class_8573 method_52200(TextureManager textureManager, String string) {
		return new class_8573(
			textureManager,
			new Identifier(
				"minecraft", "worlds/" + Util.replaceInvalidChars(string, Identifier::isPathCharacterValid) + "/" + Hashing.sha1().hashUnencodedChars(string) + "/icon"
			)
		);
	}

	public static class_8573 method_52202(TextureManager textureManager, String string) {
		return new class_8573(textureManager, new Identifier("minecraft", "servers/" + Hashing.sha1().hashUnencodedChars(string) + "/icon"));
	}

	public void method_52199(NativeImage nativeImage) {
		if (nativeImage.getWidth() == 64 && nativeImage.getHeight() == 64) {
			try {
				this.method_52203();
				if (this.field_44938 == null) {
					this.field_44938 = new NativeImageBackedTexture(nativeImage);
				} else {
					this.field_44938.setImage(nativeImage);
					this.field_44938.upload();
				}

				this.field_44936.registerTexture(this.field_44937, this.field_44938);
			} catch (Throwable var3) {
				nativeImage.close();
				this.method_52198();
				throw var3;
			}
		} else {
			nativeImage.close();
			throw new IllegalArgumentException("Icon must be 64x64, but was " + nativeImage.getWidth() + "x" + nativeImage.getHeight());
		}
	}

	public void method_52198() {
		this.method_52203();
		if (this.field_44938 != null) {
			this.field_44936.destroyTexture(this.field_44937);
			this.field_44938.close();
			this.field_44938 = null;
		}
	}

	public Identifier method_52201() {
		return this.field_44938 != null ? this.field_44937 : field_44933;
	}

	public void close() {
		this.method_52198();
		this.field_44939 = true;
	}

	private void method_52203() {
		if (this.field_44939) {
			throw new IllegalStateException("Icon already closed");
		}
	}
}
