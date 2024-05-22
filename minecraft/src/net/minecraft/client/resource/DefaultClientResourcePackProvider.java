package net.minecraft.client.resource;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.registry.VersionedIdentifier;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.DefaultResourcePackBuilder;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackInfo;
import net.minecraft.resource.ResourcePackPosition;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.VanillaResourcePackProvider;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataMap;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.path.SymlinkFinder;

@Environment(EnvType.CLIENT)
public class DefaultClientResourcePackProvider extends VanillaResourcePackProvider {
	private static final PackResourceMetadata METADATA = new PackResourceMetadata(
		Text.translatable("resourcePack.vanilla.description"), SharedConstants.getGameVersion().getResourceVersion(ResourceType.CLIENT_RESOURCES), Optional.empty()
	);
	private static final ResourceMetadataMap METADATA_MAP = ResourceMetadataMap.of(PackResourceMetadata.SERIALIZER, METADATA);
	public static final String HIGH_CONTRAST_ID = "high_contrast";
	private static final Map<String, Text> PROFILE_NAME_TEXTS = Map.of(
		"programmer_art", Text.translatable("resourcePack.programmer_art.name"), "high_contrast", Text.translatable("resourcePack.high_contrast.name")
	);
	private static final ResourcePackInfo INFO = new ResourcePackInfo(
		"vanilla", Text.translatable("resourcePack.vanilla.name"), ResourcePackSource.BUILTIN, Optional.of(VANILLA_ID)
	);
	private static final ResourcePackPosition REQUIRED_POSITION = new ResourcePackPosition(true, ResourcePackProfile.InsertionPosition.BOTTOM, false);
	private static final ResourcePackPosition OPTIONAL_POSITION = new ResourcePackPosition(false, ResourcePackProfile.InsertionPosition.TOP, false);
	private static final Identifier ID = Identifier.ofVanilla("resourcepacks");
	@Nullable
	private final Path resourcePacksPath;

	public DefaultClientResourcePackProvider(Path assetsPath, SymlinkFinder symlinkFinder) {
		super(ResourceType.CLIENT_RESOURCES, createDefaultPack(assetsPath), ID, symlinkFinder);
		this.resourcePacksPath = this.getResourcePacksPath(assetsPath);
	}

	private static ResourcePackInfo createInfo(String id, Text title) {
		return new ResourcePackInfo(id, title, ResourcePackSource.BUILTIN, Optional.of(VersionedIdentifier.createVanilla(id)));
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
		return defaultResourcePackBuilder.runCallback().withDefaultPaths().withPath(ResourceType.CLIENT_RESOURCES, assetsPath).build(INFO);
	}

	@Override
	protected Text getDisplayName(String id) {
		Text text = (Text)PROFILE_NAME_TEXTS.get(id);
		return (Text)(text != null ? text : Text.literal(id));
	}

	@Nullable
	@Override
	protected ResourcePackProfile createDefault(ResourcePack pack) {
		return ResourcePackProfile.create(INFO, createPackFactory(pack), ResourceType.CLIENT_RESOURCES, REQUIRED_POSITION);
	}

	@Nullable
	@Override
	protected ResourcePackProfile create(String fileName, ResourcePackProfile.PackFactory packFactory, Text displayName) {
		return ResourcePackProfile.create(createInfo(fileName, displayName), packFactory, ResourceType.CLIENT_RESOURCES, OPTIONAL_POSITION);
	}

	@Override
	protected void forEachProfile(BiConsumer<String, Function<String, ResourcePackProfile>> consumer) {
		super.forEachProfile(consumer);
		if (this.resourcePacksPath != null) {
			this.forEachProfile(this.resourcePacksPath, consumer);
		}
	}
}
