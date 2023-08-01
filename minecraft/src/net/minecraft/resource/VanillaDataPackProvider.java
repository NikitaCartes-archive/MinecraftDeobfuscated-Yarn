package net.minecraft.resource;

import com.google.common.annotations.VisibleForTesting;
import java.nio.file.Path;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.metadata.PackFeatureSetMetadata;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataMap;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.path.SymlinkFinder;
import net.minecraft.world.level.storage.LevelStorage;

public class VanillaDataPackProvider extends VanillaResourcePackProvider {
	private static final PackResourceMetadata METADATA = new PackResourceMetadata(
		Text.translatable("dataPack.vanilla.description"), SharedConstants.getGameVersion().getResourceVersion(ResourceType.SERVER_DATA), Optional.empty()
	);
	private static final PackFeatureSetMetadata FEATURE_FLAGS = new PackFeatureSetMetadata(FeatureFlags.DEFAULT_ENABLED_FEATURES);
	private static final ResourceMetadataMap METADATA_MAP = ResourceMetadataMap.of(
		PackResourceMetadata.SERIALIZER, METADATA, PackFeatureSetMetadata.SERIALIZER, FEATURE_FLAGS
	);
	private static final Text NAME = Text.translatable("dataPack.vanilla.name");
	private static final Identifier ID = new Identifier("minecraft", "datapacks");

	public VanillaDataPackProvider(SymlinkFinder symlinkFinder) {
		super(ResourceType.SERVER_DATA, createDefaultPack(), ID, symlinkFinder);
	}

	@VisibleForTesting
	public static DefaultResourcePack createDefaultPack() {
		return new DefaultResourcePackBuilder().withMetadataMap(METADATA_MAP).withNamespaces("minecraft").runCallback().withDefaultPaths().build();
	}

	@Override
	protected Text getProfileName(String id) {
		return Text.literal(id);
	}

	@Nullable
	@Override
	protected ResourcePackProfile createDefault(ResourcePack pack) {
		return ResourcePackProfile.create(
			"vanilla", NAME, false, createPackFactory(pack), ResourceType.SERVER_DATA, ResourcePackProfile.InsertionPosition.BOTTOM, ResourcePackSource.BUILTIN
		);
	}

	@Nullable
	@Override
	protected ResourcePackProfile create(String name, ResourcePackProfile.PackFactory packFactory, Text displayName) {
		return ResourcePackProfile.create(
			name, displayName, false, packFactory, ResourceType.SERVER_DATA, ResourcePackProfile.InsertionPosition.TOP, ResourcePackSource.FEATURE
		);
	}

	public static ResourcePackManager createManager(Path dataPacksPath, SymlinkFinder symlinkFinder) {
		return new ResourcePackManager(
			new VanillaDataPackProvider(symlinkFinder), new FileResourcePackProvider(dataPacksPath, ResourceType.SERVER_DATA, ResourcePackSource.WORLD, symlinkFinder)
		);
	}

	public static ResourcePackManager method_52443() {
		return new ResourcePackManager(new VanillaDataPackProvider(new SymlinkFinder(path -> true)));
	}

	public static ResourcePackManager createManager(LevelStorage.Session session) {
		return createManager(session.getDirectory(WorldSavePath.DATAPACKS), session.getLevelStorage().getSymlinkFinder());
	}
}
