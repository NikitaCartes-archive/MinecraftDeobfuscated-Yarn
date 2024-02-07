package net.minecraft.resource;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.registry.VersionedIdentifier;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.path.SymlinkFinder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public abstract class VanillaResourcePackProvider implements ResourcePackProvider {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final String VANILLA_KEY = "vanilla";
	public static final VersionedIdentifier VANILLA_ID = VersionedIdentifier.createVanilla("core");
	private final ResourceType type;
	private final DefaultResourcePack resourcePack;
	private final Identifier id;
	private final SymlinkFinder symlinkFinder;

	public VanillaResourcePackProvider(ResourceType type, DefaultResourcePack resourcePack, Identifier id, SymlinkFinder symlinkFinder) {
		this.type = type;
		this.resourcePack = resourcePack;
		this.id = id;
		this.symlinkFinder = symlinkFinder;
	}

	@Override
	public void register(Consumer<ResourcePackProfile> profileAdder) {
		ResourcePackProfile resourcePackProfile = this.createDefault(this.resourcePack);
		if (resourcePackProfile != null) {
			profileAdder.accept(resourcePackProfile);
		}

		this.forEachProfile(profileAdder);
	}

	@Nullable
	protected abstract ResourcePackProfile createDefault(ResourcePack pack);

	protected abstract Text getDisplayName(String id);

	public DefaultResourcePack getResourcePack() {
		return this.resourcePack;
	}

	private void forEachProfile(Consumer<ResourcePackProfile> consumer) {
		Map<String, Function<String, ResourcePackProfile>> map = new HashMap();
		this.forEachProfile(map::put);
		map.forEach((id, packFactory) -> {
			ResourcePackProfile resourcePackProfile = (ResourcePackProfile)packFactory.apply(id);
			if (resourcePackProfile != null) {
				consumer.accept(resourcePackProfile);
			}
		});
	}

	protected void forEachProfile(BiConsumer<String, Function<String, ResourcePackProfile>> consumer) {
		this.resourcePack.forEachNamespacedPath(this.type, this.id, namespacedPath -> this.forEachProfile(namespacedPath, consumer));
	}

	protected void forEachProfile(@Nullable Path namespacedPath, BiConsumer<String, Function<String, ResourcePackProfile>> consumer) {
		if (namespacedPath != null && Files.isDirectory(namespacedPath, new LinkOption[0])) {
			try {
				FileResourcePackProvider.forEachProfile(
					namespacedPath,
					this.symlinkFinder,
					(profilePath, factory) -> consumer.accept(getFileName(profilePath), (Function)id -> this.create(id, factory, this.getDisplayName(id)))
				);
			} catch (IOException var4) {
				LOGGER.warn("Failed to discover packs in {}", namespacedPath, var4);
			}
		}
	}

	private static String getFileName(Path path) {
		return StringUtils.removeEnd(path.getFileName().toString(), ".zip");
	}

	@Nullable
	protected abstract ResourcePackProfile create(String fileName, ResourcePackProfile.PackFactory packFactory, Text displayName);

	protected static ResourcePackProfile.PackFactory createPackFactory(ResourcePack pack) {
		return new ResourcePackProfile.PackFactory() {
			@Override
			public ResourcePack open(ResourcePackInfo info) {
				return pack;
			}

			@Override
			public ResourcePack openWithOverlays(ResourcePackInfo info, ResourcePackProfile.Metadata metadata) {
				return pack;
			}
		};
	}
}
