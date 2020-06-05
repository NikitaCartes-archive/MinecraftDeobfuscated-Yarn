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
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ClientResourcePackProfile extends ResourcePackProfile {
	@Nullable
	private NativeImage icon;
	@Nullable
	private Identifier iconId;

	public ClientResourcePackProfile(
		String string,
		boolean bl,
		Supplier<ResourcePack> supplier,
		ResourcePack resourcePack,
		PackResourceMetadata packResourceMetadata,
		ResourcePackProfile.InsertionPosition insertionPosition,
		ResourcePackSource resourcePackSource
	) {
		super(string, bl, supplier, resourcePack, packResourceMetadata, insertionPosition, resourcePackSource);
		this.icon = method_29713(resourcePack);
	}

	public ClientResourcePackProfile(
		String name,
		boolean alwaysEnabled,
		Supplier<ResourcePack> packFactory,
		Text displayName,
		Text description,
		ResourcePackCompatibility compatibility,
		ResourcePackProfile.InsertionPosition insertionPosition,
		boolean pinned,
		ResourcePackSource resourcePackSource,
		@Nullable NativeImage nativeImage
	) {
		super(name, alwaysEnabled, packFactory, displayName, description, compatibility, insertionPosition, pinned, resourcePackSource);
		this.icon = nativeImage;
	}

	@Nullable
	public static NativeImage method_29713(ResourcePack resourcePack) {
		try {
			InputStream inputStream = resourcePack.openRoot("pack.png");
			Throwable var2 = null;

			NativeImage var3;
			try {
				var3 = NativeImage.read(inputStream);
			} catch (Throwable var13) {
				var2 = var13;
				throw var13;
			} finally {
				if (inputStream != null) {
					if (var2 != null) {
						try {
							inputStream.close();
						} catch (Throwable var12) {
							var2.addSuppressed(var12);
						}
					} else {
						inputStream.close();
					}
				}
			}

			return var3;
		} catch (IllegalArgumentException | IOException var15) {
			return null;
		}
	}

	public void drawIcon(TextureManager manager) {
		if (this.iconId == null) {
			if (this.icon == null) {
				this.iconId = new Identifier("textures/misc/unknown_pack.png");
			} else {
				this.iconId = manager.registerDynamicTexture("texturepackicon", new NativeImageBackedTexture(this.icon));
			}
		}

		manager.bindTexture(this.iconId);
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
