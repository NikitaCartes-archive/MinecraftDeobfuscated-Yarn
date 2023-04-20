package net.minecraft.client.resource;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.DefaultResourcePackBuilder;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.VanillaResourcePackProvider;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataMap;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DefaultClientResourcePackProvider extends VanillaResourcePackProvider {
	private static final PackResourceMetadata METADATA = new PackResourceMetadata(
		Text.translatable("resourcePack.vanilla.description"), SharedConstants.getGameVersion().getResourceVersion(ResourceType.CLIENT_RESOURCES)
	);
	private static final ResourceMetadataMap METADATA_MAP = ResourceMetadataMap.of(PackResourceMetadata.SERIALIZER, METADATA);
	private static final Text VANILLA_NAME_TEXT = Text.translatable("resourcePack.vanilla.name");
	public static final String HIGH_CONTRAST_NAME = "high_contrast";
	private static final Map<String, Text> PROFILE_NAME_TEXTS = Map.of(
		"programmer_art", Text.translatable("resourcePack.programmer_art.name"), "high_contrast", Text.translatable("resourcePack.high_contrast.name")
	);
	private static final Identifier ID = new Identifier("minecraft", "resourcepacks");
	@Nullable
	private final Path resourcePacksPath;

	public DefaultClientResourcePackProvider(Path assetsPath) {
		super(ResourceType.CLIENT_RESOURCES, createDefaultPack(assetsPath), ID);
		this.resourcePacksPath = this.getResourcePacksPath(assetsPath);
	}

	@Nullable
	private Path getResourcePacksPath(Path path) {
		if (SharedConstants.isDevelopment && path.getFileSystem() == FileSystems.getDefault()) {
			Path path2 = path.getParent().resolve("resourcepacks");
			if (Files.isDirectory(path2, new LinkOption[0])) {
				return path2;
			}
		}

		return null;
	}

	private static DefaultResourcePack createDefaultPack(Path assetsPath) {
		DefaultResourcePackBuilder defaultResourcePackBuilder = new DefaultResourcePackBuilder().withMetadataMap(METADATA_MAP).withNamespaces("minecraft", "realms");
		return defaultResourcePackBuilder.runCallback().withDefaultPaths().withPath(ResourceType.CLIENT_RESOURCES, assetsPath).build();
	}

	@Override
	protected Text getProfileName(String id) {
		Text text = (Text)PROFILE_NAME_TEXTS.get(id);
		return (Text)(text != null ? text : Text.literal(id));
	}

	@Nullable
	@Override
	protected ResourcePackProfile createDefault(ResourcePack pack) {
		return ResourcePackProfile.create(
			"vanilla", VANILLA_NAME_TEXT, true, name -> pack, ResourceType.CLIENT_RESOURCES, ResourcePackProfile.InsertionPosition.BOTTOM, ResourcePackSource.BUILTIN
		);
	}

	@Nullable
	@Override
	protected ResourcePackProfile create(String name, ResourcePackProfile.PackFactory packFactory, Text displayName) {
		return ResourcePackProfile.create(
			name, displayName, false, packFactory, ResourceType.CLIENT_RESOURCES, ResourcePackProfile.InsertionPosition.TOP, ResourcePackSource.BUILTIN
		);
	}

	@Override
	protected void forEachProfile(BiConsumer<String, Function<String, ResourcePackProfile>> consumer) {
		super.forEachProfile(consumer);
		if (this.resourcePacksPath != null) {
			this.forEachProfile(this.resourcePacksPath, consumer);
		}
	}
}
