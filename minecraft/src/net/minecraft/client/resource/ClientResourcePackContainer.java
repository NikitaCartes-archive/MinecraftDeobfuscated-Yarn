package net.minecraft.client.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.resource.ResourcePackContainer;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ClientResourcePackContainer extends ResourcePackContainer {
	@Nullable
	private NativeImage icon;
	@Nullable
	private Identifier iconId;

	public ClientResourcePackContainer(
		String string,
		boolean bl,
		Supplier<ResourcePack> supplier,
		ResourcePack resourcePack,
		PackResourceMetadata packResourceMetadata,
		ResourcePackContainer.InsertionPosition insertionPosition
	) {
		super(string, bl, supplier, resourcePack, packResourceMetadata, insertionPosition);
		NativeImage nativeImage = null;

		try {
			InputStream inputStream = resourcePack.openRoot("pack.png");
			Throwable var9 = null;

			try {
				nativeImage = NativeImage.read(inputStream);
			} catch (Throwable var19) {
				var9 = var19;
				throw var19;
			} finally {
				if (inputStream != null) {
					if (var9 != null) {
						try {
							inputStream.close();
						} catch (Throwable var18) {
							var9.addSuppressed(var18);
						}
					} else {
						inputStream.close();
					}
				}
			}
		} catch (IllegalArgumentException | IOException var21) {
		}

		this.icon = nativeImage;
	}

	public ClientResourcePackContainer(
		String string,
		boolean bl,
		Supplier<ResourcePack> supplier,
		Text text,
		Text text2,
		ResourcePackCompatibility resourcePackCompatibility,
		ResourcePackContainer.InsertionPosition insertionPosition,
		boolean bl2,
		@Nullable NativeImage nativeImage
	) {
		super(string, bl, supplier, text, text2, resourcePackCompatibility, insertionPosition, bl2);
		this.icon = nativeImage;
	}

	public void drawIcon(TextureManager textureManager) {
		if (this.iconId == null) {
			if (this.icon == null) {
				this.iconId = new Identifier("textures/misc/unknown_pack.png");
			} else {
				this.iconId = textureManager.registerDynamicTexture("texturepackicon", new NativeImageBackedTexture(this.icon));
			}
		}

		textureManager.bindTexture(this.iconId);
	}

	@Override
	public void close() {
		super.close();
		if (this.icon != null) {
			this.icon.close();
			this.icon = null;
		}
	}
}
