package net.minecraft.resource;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.resource.metadata.ResourceMetadata;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

/**
 * A resource manager that only loads resources for a specific namespace.
 * This is an implementation detail for {@link LifecycledResourceManagerImpl}, based
 * on the heuristic that most namespaces are only defined in few resource
 * packs, making loading in those namespaces faster.
 * 
 * @see LifecycledResourceManagerImpl
 */
public class NamespaceResourceManager implements ResourceManager {
	static final Logger LOGGER = LogUtils.getLogger();
	protected final List<NamespaceResourceManager.FilterablePack> packList = Lists.<NamespaceResourceManager.FilterablePack>newArrayList();
	final ResourceType type;
	private final String namespace;

	public NamespaceResourceManager(ResourceType type, String namespace) {
		this.type = type;
		this.namespace = namespace;
	}

	public void addPack(ResourcePack pack) {
		this.addPack(pack.getName(), pack, null);
	}

	public void addPack(ResourcePack pack, Predicate<Identifier> filter) {
		this.addPack(pack.getName(), pack, filter);
	}

	public void addPack(String name, Predicate<Identifier> filter) {
		this.addPack(name, null, filter);
	}

	private void addPack(String name, @Nullable ResourcePack underlyingPack, @Nullable Predicate<Identifier> filter) {
		this.packList.add(new NamespaceResourceManager.FilterablePack(name, underlyingPack, filter));
	}

	@Override
	public Set<String> getAllNamespaces() {
		return ImmutableSet.of(this.namespace);
	}

	@Override
	public Optional<Resource> getResource(Identifier identifier) {
		if (!this.isPathAbsolute(identifier)) {
			return Optional.empty();
		} else {
			for (int i = this.packList.size() - 1; i >= 0; i--) {
				NamespaceResourceManager.FilterablePack filterablePack = (NamespaceResourceManager.FilterablePack)this.packList.get(i);
				ResourcePack resourcePack = filterablePack.underlying;
				if (resourcePack != null && resourcePack.contains(this.type, identifier)) {
					return Optional.of(new Resource(resourcePack.getName(), this.createOpener(identifier, resourcePack), this.createMetadataSupplier(identifier, i)));
				}

				if (filterablePack.isFiltered(identifier)) {
					LOGGER.warn("Resource {} not found, but was filtered by pack {}", identifier, filterablePack.name);
					return Optional.empty();
				}
			}

			return Optional.empty();
		}
	}

	Resource.InputSupplier<InputStream> createOpener(Identifier id, ResourcePack pack) {
		return LOGGER.isDebugEnabled() ? () -> {
			InputStream inputStream = pack.open(this.type, id);
			return new NamespaceResourceManager.DebugInputStream(inputStream, id, pack.getName());
		} : () -> pack.open(this.type, id);
	}

	private boolean isPathAbsolute(Identifier id) {
		return !id.getPath().contains("..");
	}

	@Override
	public List<Resource> getAllResources(Identifier id) {
		if (!this.isPathAbsolute(id)) {
			return List.of();
		} else {
			List<NamespaceResourceManager.Entry> list = Lists.<NamespaceResourceManager.Entry>newArrayList();
			Identifier identifier = getMetadataPath(id);
			String string = null;

			for (NamespaceResourceManager.FilterablePack filterablePack : this.packList) {
				if (filterablePack.isFiltered(id)) {
					if (!list.isEmpty()) {
						string = filterablePack.name;
					}

					list.clear();
				} else if (filterablePack.isFiltered(identifier)) {
					list.forEach(NamespaceResourceManager.Entry::ignoreMetadata);
				}

				ResourcePack resourcePack = filterablePack.underlying;
				if (resourcePack != null && resourcePack.contains(this.type, id)) {
					list.add(new NamespaceResourceManager.Entry(id, identifier, resourcePack));
				}
			}

			if (list.isEmpty() && string != null) {
				LOGGER.info("Resource {} was filtered by pack {}", id, string);
			}

			return list.stream().map(NamespaceResourceManager.Entry::toReference).toList();
		}
	}

	@Override
	public Map<Identifier, Resource> findResources(String startingPath, Predicate<Identifier> allowedPathPredicate) {
		Object2IntMap<Identifier> object2IntMap = new Object2IntOpenHashMap<>();
		int i = this.packList.size();

		for (int j = 0; j < i; j++) {
			NamespaceResourceManager.FilterablePack filterablePack = (NamespaceResourceManager.FilterablePack)this.packList.get(j);
			filterablePack.removeFiltered(object2IntMap.keySet());
			if (filterablePack.underlying != null) {
				for (Identifier identifier : filterablePack.underlying.findResources(this.type, this.namespace, startingPath, allowedPathPredicate)) {
					object2IntMap.put(identifier, j);
				}
			}
		}

		Map<Identifier, Resource> map = Maps.<Identifier, Resource>newTreeMap();

		for (Object2IntMap.Entry<Identifier> entry : Object2IntMaps.fastIterable(object2IntMap)) {
			int k = entry.getIntValue();
			Identifier identifier2 = (Identifier)entry.getKey();
			ResourcePack resourcePack = ((NamespaceResourceManager.FilterablePack)this.packList.get(k)).underlying;
			map.put(identifier2, new Resource(resourcePack.getName(), this.createOpener(identifier2, resourcePack), this.createMetadataSupplier(identifier2, k)));
		}

		return map;
	}

	private Resource.InputSupplier<ResourceMetadata> createMetadataSupplier(Identifier id, int index) {
		return () -> {
			Identifier identifier2 = getMetadataPath(id);

			for (int j = this.packList.size() - 1; j >= index; j--) {
				NamespaceResourceManager.FilterablePack filterablePack = (NamespaceResourceManager.FilterablePack)this.packList.get(j);
				ResourcePack resourcePack = filterablePack.underlying;
				if (resourcePack != null && resourcePack.contains(this.type, identifier2)) {
					InputStream inputStream = resourcePack.open(this.type, identifier2);

					ResourceMetadata var8;
					try {
						var8 = ResourceMetadata.create(inputStream);
					} catch (Throwable var11) {
						if (inputStream != null) {
							try {
								inputStream.close();
							} catch (Throwable var10) {
								var11.addSuppressed(var10);
							}
						}

						throw var11;
					}

					if (inputStream != null) {
						inputStream.close();
					}

					return var8;
				}

				if (filterablePack.isFiltered(identifier2)) {
					break;
				}
			}

			return ResourceMetadata.NONE;
		};
	}

	private static void applyFilter(NamespaceResourceManager.FilterablePack pack, Map<Identifier, NamespaceResourceManager.EntryList> idToEntryList) {
		Iterator<java.util.Map.Entry<Identifier, NamespaceResourceManager.EntryList>> iterator = idToEntryList.entrySet().iterator();

		while (iterator.hasNext()) {
			java.util.Map.Entry<Identifier, NamespaceResourceManager.EntryList> entry = (java.util.Map.Entry<Identifier, NamespaceResourceManager.EntryList>)iterator.next();
			Identifier identifier = (Identifier)entry.getKey();
			NamespaceResourceManager.EntryList entryList = (NamespaceResourceManager.EntryList)entry.getValue();
			if (pack.isFiltered(identifier)) {
				iterator.remove();
			} else if (pack.isFiltered(entryList.metadataId())) {
				entryList.entries.forEach(NamespaceResourceManager.Entry::ignoreMetadata);
			}
		}
	}

	private void findAndAdd(
		NamespaceResourceManager.FilterablePack pack,
		String startingPath,
		Predicate<Identifier> allowedPathPredicate,
		Map<Identifier, NamespaceResourceManager.EntryList> idToEntryList
	) {
		ResourcePack resourcePack = pack.underlying;
		if (resourcePack != null) {
			for (Identifier identifier : resourcePack.findResources(this.type, this.namespace, startingPath, allowedPathPredicate)) {
				Identifier identifier2 = getMetadataPath(identifier);
				((NamespaceResourceManager.EntryList)idToEntryList.computeIfAbsent(
						identifier, id -> new NamespaceResourceManager.EntryList(identifier2, Lists.<NamespaceResourceManager.Entry>newArrayList())
					))
					.entries()
					.add(new NamespaceResourceManager.Entry(identifier, identifier2, resourcePack));
			}
		}
	}

	@Override
	public Map<Identifier, List<Resource>> findAllResources(String startingPath, Predicate<Identifier> allowedPathPredicate) {
		Map<Identifier, NamespaceResourceManager.EntryList> map = Maps.<Identifier, NamespaceResourceManager.EntryList>newHashMap();

		for (NamespaceResourceManager.FilterablePack filterablePack : this.packList) {
			applyFilter(filterablePack, map);
			this.findAndAdd(filterablePack, startingPath, allowedPathPredicate, map);
		}

		TreeMap<Identifier, List<Resource>> treeMap = Maps.newTreeMap();
		map.forEach((id, entryList) -> treeMap.put(id, entryList.toReferenceList()));
		return treeMap;
	}

	@Override
	public Stream<ResourcePack> streamResourcePacks() {
		return this.packList.stream().map(pack -> pack.underlying).filter(Objects::nonNull);
	}

	static Identifier getMetadataPath(Identifier id) {
		return new Identifier(id.getNamespace(), id.getPath() + ".mcmeta");
	}

	static class DebugInputStream extends FilterInputStream {
		private final String leakMessage;
		private boolean closed;

		public DebugInputStream(InputStream parent, Identifier id, String packName) {
			super(parent);
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			new Exception().printStackTrace(new PrintStream(byteArrayOutputStream));
			this.leakMessage = "Leaked resource: '" + id + "' loaded from pack: '" + packName + "'\n" + byteArrayOutputStream;
		}

		public void close() throws IOException {
			super.close();
			this.closed = true;
		}

		protected void finalize() throws Throwable {
			if (!this.closed) {
				NamespaceResourceManager.LOGGER.warn(this.leakMessage);
			}

			super.finalize();
		}
	}

	class Entry {
		private final Identifier id;
		private final Identifier metadataId;
		private final ResourcePack pack;
		private boolean checksMetadata = true;

		Entry(Identifier id, Identifier metadataId, ResourcePack pack) {
			this.pack = pack;
			this.id = id;
			this.metadataId = metadataId;
		}

		public void ignoreMetadata() {
			this.checksMetadata = false;
		}

		public Resource toReference() {
			String string = this.pack.getName();
			return this.checksMetadata ? new Resource(string, NamespaceResourceManager.this.createOpener(this.id, this.pack), () -> {
				if (this.pack.contains(NamespaceResourceManager.this.type, this.metadataId)) {
					InputStream inputStream = this.pack.open(NamespaceResourceManager.this.type, this.metadataId);

					ResourceMetadata var2;
					try {
						var2 = ResourceMetadata.create(inputStream);
					} catch (Throwable var5) {
						if (inputStream != null) {
							try {
								inputStream.close();
							} catch (Throwable var4) {
								var5.addSuppressed(var4);
							}
						}

						throw var5;
					}

					if (inputStream != null) {
						inputStream.close();
					}

					return var2;
				} else {
					return ResourceMetadata.NONE;
				}
			}) : new Resource(string, NamespaceResourceManager.this.createOpener(this.id, this.pack));
		}
	}

	static record EntryList(Identifier metadataId, List<NamespaceResourceManager.Entry> entries) {

		List<Resource> toReferenceList() {
			return this.entries().stream().map(NamespaceResourceManager.Entry::toReference).toList();
		}
	}

	static record FilterablePack(String name, @Nullable ResourcePack underlying, @Nullable Predicate<Identifier> filter) {

		public void removeFiltered(Collection<Identifier> ids) {
			if (this.filter != null) {
				ids.removeIf(this.filter);
			}
		}

		public boolean isFiltered(Identifier id) {
			return this.filter != null && this.filter.test(id);
		}
	}
}
