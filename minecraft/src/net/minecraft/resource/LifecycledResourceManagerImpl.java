package net.minecraft.resource;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.resource.metadata.ResourceFilter;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

/**
 * A basic implementation of resource manager with a lifecycle.
 * 
 * <p>It handles resources by namespaces, hoping that most namespaces are
 * defined in only few resource packs.
 * 
 * @see NamespaceResourceManager
 */
public class LifecycledResourceManagerImpl implements LifecycledResourceManager {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Map<String, NamespaceResourceManager> subManagers;
	private final List<ResourcePack> packs;

	public LifecycledResourceManagerImpl(ResourceType type, List<ResourcePack> packs) {
		this.packs = List.copyOf(packs);
		Map<String, NamespaceResourceManager> map = new HashMap();
		List<String> list = packs.stream().flatMap(pack -> pack.getNamespaces(type).stream()).distinct().toList();

		for (ResourcePack resourcePack : packs) {
			ResourceFilter resourceFilter = this.parseResourceFilter(resourcePack);
			Set<String> set = resourcePack.getNamespaces(type);
			Predicate<Identifier> predicate = resourceFilter != null ? id -> resourceFilter.isPathBlocked(id.getPath()) : null;

			for (String string : list) {
				boolean bl = set.contains(string);
				boolean bl2 = resourceFilter != null && resourceFilter.isNamespaceBlocked(string);
				if (bl || bl2) {
					NamespaceResourceManager namespaceResourceManager = (NamespaceResourceManager)map.get(string);
					if (namespaceResourceManager == null) {
						namespaceResourceManager = new NamespaceResourceManager(type, string);
						map.put(string, namespaceResourceManager);
					}

					if (bl && bl2) {
						namespaceResourceManager.addPack(resourcePack, predicate);
					} else if (bl) {
						namespaceResourceManager.addPack(resourcePack);
					} else {
						namespaceResourceManager.addPack(resourcePack.getName(), predicate);
					}
				}
			}
		}

		this.subManagers = map;
	}

	@Nullable
	private ResourceFilter parseResourceFilter(ResourcePack pack) {
		try {
			return pack.parseMetadata(ResourceFilter.READER);
		} catch (IOException var3) {
			LOGGER.error("Failed to get filter section from pack {}", pack.getName());
			return null;
		}
	}

	@Override
	public Set<String> getAllNamespaces() {
		return this.subManagers.keySet();
	}

	@Override
	public Optional<Resource> getResource(Identifier identifier) {
		ResourceManager resourceManager = (ResourceManager)this.subManagers.get(identifier.getNamespace());
		return resourceManager != null ? resourceManager.getResource(identifier) : Optional.empty();
	}

	@Override
	public List<Resource> getAllResources(Identifier id) {
		ResourceManager resourceManager = (ResourceManager)this.subManagers.get(id.getNamespace());
		return resourceManager != null ? resourceManager.getAllResources(id) : List.of();
	}

	@Override
	public Map<Identifier, Resource> findResources(String startingPath, Predicate<Identifier> allowedPathPredicate) {
		Map<Identifier, Resource> map = new TreeMap();

		for (NamespaceResourceManager namespaceResourceManager : this.subManagers.values()) {
			map.putAll(namespaceResourceManager.findResources(startingPath, allowedPathPredicate));
		}

		return map;
	}

	@Override
	public Map<Identifier, List<Resource>> findAllResources(String startingPath, Predicate<Identifier> allowedPathPredicate) {
		Map<Identifier, List<Resource>> map = new TreeMap();

		for (NamespaceResourceManager namespaceResourceManager : this.subManagers.values()) {
			map.putAll(namespaceResourceManager.findAllResources(startingPath, allowedPathPredicate));
		}

		return map;
	}

	@Override
	public Stream<ResourcePack> streamResourcePacks() {
		return this.packs.stream();
	}

	@Override
	public void close() {
		this.packs.forEach(ResourcePack::close);
	}
}
