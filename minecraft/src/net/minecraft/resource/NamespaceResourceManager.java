package net.minecraft.resource;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
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
	private final ResourceType type;
	private final String namespace;

	public NamespaceResourceManager(ResourceType type, String namespace) {
		this.type = type;
		this.namespace = namespace;
	}

	public void addPack(ResourcePack pack) {
		this.addPack(pack.getId(), pack, null);
	}

	public void addPack(ResourcePack pack, Predicate<Identifier> filter) {
		this.addPack(pack.getId(), pack, filter);
	}

	public void addPack(String id, Predicate<Identifier> filter) {
		this.addPack(id, null, filter);
	}

	private void addPack(String id, @Nullable ResourcePack underlyingPack, @Nullable Predicate<Identifier> filter) {
		this.packList.add(new NamespaceResourceManager.FilterablePack(id, underlyingPack, filter));
	}

	@Override
	public Set<String> getAllNamespaces() {
		return ImmutableSet.of(this.namespace);
	}

	@Override
	public Optional<Resource> getResource(Identifier identifier) {
		for (int i = this.packList.size() - 1; i >= 0; i--) {
			NamespaceResourceManager.FilterablePack filterablePack = (NamespaceResourceManager.FilterablePack)this.packList.get(i);
			ResourcePack resourcePack = filterablePack.underlying;
			if (resourcePack != null) {
				InputSupplier<InputStream> inputSupplier = resourcePack.open(this.type, identifier);
				if (inputSupplier != null) {
					InputSupplier<ResourceMetadata> inputSupplier2 = this.createMetadataSupplier(identifier, i);
					return Optional.of(createResource(resourcePack, identifier, inputSupplier, inputSupplier2));
				}
			}

			if (filterablePack.isFiltered(identifier)) {
				LOGGER.warn("Resource {} not found, but was filtered by pack {}", identifier, filterablePack.name);
				return Optional.empty();
			}
		}

		return Optional.empty();
	}

	private static Resource createResource(ResourcePack pack, Identifier id, InputSupplier<InputStream> supplier, InputSupplier<ResourceMetadata> metadataSupplier) {
		return new Resource(pack, wrapForDebug(id, pack, supplier), metadataSupplier);
	}

	private static InputSupplier<InputStream> wrapForDebug(Identifier id, ResourcePack pack, InputSupplier<InputStream> supplier) {
		return LOGGER.isDebugEnabled() ? () -> new NamespaceResourceManager.DebugInputStream(supplier.get(), id, pack.getId()) : supplier;
	}

	@Override
	public List<Resource> getAllResources(Identifier id) {
		Identifier identifier = getMetadataPath(id);
		List<Resource> list = new ArrayList();
		boolean bl = false;
		String string = null;

		for (int i = this.packList.size() - 1; i >= 0; i--) {
			NamespaceResourceManager.FilterablePack filterablePack = (NamespaceResourceManager.FilterablePack)this.packList.get(i);
			ResourcePack resourcePack = filterablePack.underlying;
			if (resourcePack != null) {
				InputSupplier<InputStream> inputSupplier = resourcePack.open(this.type, id);
				if (inputSupplier != null) {
					InputSupplier<ResourceMetadata> inputSupplier2;
					if (bl) {
						inputSupplier2 = ResourceMetadata.NONE_SUPPLIER;
					} else {
						inputSupplier2 = () -> {
							InputSupplier<InputStream> inputSupplierx = resourcePack.open(this.type, identifier);
							return inputSupplierx != null ? loadMetadata(inputSupplierx) : ResourceMetadata.NONE;
						};
					}

					list.add(new Resource(resourcePack, inputSupplier, inputSupplier2));
				}
			}

			if (filterablePack.isFiltered(id)) {
				string = filterablePack.name;
				break;
			}

			if (filterablePack.isFiltered(identifier)) {
				bl = true;
			}
		}

		if (list.isEmpty() && string != null) {
			LOGGER.warn("Resource {} not found, but was filtered by pack {}", id, string);
		}

		return Lists.reverse(list);
	}

	private static boolean isMcmeta(Identifier id) {
		return id.getPath().endsWith(".mcmeta");
	}

	private static Identifier getMetadataFileName(Identifier id) {
		String string = id.getPath().substring(0, id.getPath().length() - ".mcmeta".length());
		return id.withPath(string);
	}

	static Identifier getMetadataPath(Identifier id) {
		return id.withPath(id.getPath() + ".mcmeta");
	}

	@Override
	public Map<Identifier, Resource> findResources(String startingPath, Predicate<Identifier> allowedPathPredicate) {
		record Result(ResourcePack pack, InputSupplier<InputStream> supplier, int packIndex) {
		}

		Map<Identifier, Result> map = new HashMap();
		Map<Identifier, Result> map2 = new HashMap();
		int i = this.packList.size();

		for (int j = 0; j < i; j++) {
			NamespaceResourceManager.FilterablePack filterablePack = (NamespaceResourceManager.FilterablePack)this.packList.get(j);
			filterablePack.removeFiltered(map.keySet());
			filterablePack.removeFiltered(map2.keySet());
			ResourcePack resourcePack = filterablePack.underlying;
			if (resourcePack != null) {
				int k = j;
				resourcePack.findResources(this.type, this.namespace, startingPath, (id, supplier) -> {
					if (isMcmeta(id)) {
						if (allowedPathPredicate.test(getMetadataFileName(id))) {
							map2.put(id, new Result(resourcePack, supplier, k));
						}
					} else if (allowedPathPredicate.test(id)) {
						map.put(id, new Result(resourcePack, supplier, k));
					}
				});
			}
		}

		Map<Identifier, Resource> map3 = Maps.<Identifier, Resource>newTreeMap();
		map.forEach((id, result) -> {
			Identifier identifier = getMetadataPath(id);
			Result result2 = (Result)map2.get(identifier);
			InputSupplier<ResourceMetadata> inputSupplier;
			if (result2 != null && result2.packIndex >= result.packIndex) {
				inputSupplier = getMetadataSupplier(result2.supplier);
			} else {
				inputSupplier = ResourceMetadata.NONE_SUPPLIER;
			}

			map3.put(id, createResource(result.pack, id, result.supplier, inputSupplier));
		});
		return map3;
	}

	private InputSupplier<ResourceMetadata> createMetadataSupplier(Identifier id, int index) {
		return () -> {
			Identifier identifier2 = getMetadataPath(id);

			for (int j = this.packList.size() - 1; j >= index; j--) {
				NamespaceResourceManager.FilterablePack filterablePack = (NamespaceResourceManager.FilterablePack)this.packList.get(j);
				ResourcePack resourcePack = filterablePack.underlying;
				if (resourcePack != null) {
					InputSupplier<InputStream> inputSupplier = resourcePack.open(this.type, identifier2);
					if (inputSupplier != null) {
						return loadMetadata(inputSupplier);
					}
				}

				if (filterablePack.isFiltered(identifier2)) {
					break;
				}
			}

			return ResourceMetadata.NONE;
		};
	}

	private static InputSupplier<ResourceMetadata> getMetadataSupplier(InputSupplier<InputStream> supplier) {
		return () -> loadMetadata(supplier);
	}

	private static ResourceMetadata loadMetadata(InputSupplier<InputStream> supplier) throws IOException {
		InputStream inputStream = supplier.get();

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
	}

	private static void applyFilter(NamespaceResourceManager.FilterablePack pack, Map<Identifier, NamespaceResourceManager.EntryList> idToEntryList) {
		for (NamespaceResourceManager.EntryList entryList : idToEntryList.values()) {
			if (pack.isFiltered(entryList.id)) {
				entryList.fileSources.clear();
			} else if (pack.isFiltered(entryList.metadataId())) {
				entryList.metaSources.clear();
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
			resourcePack.findResources(
				this.type,
				this.namespace,
				startingPath,
				(id, supplier) -> {
					if (isMcmeta(id)) {
						Identifier identifier = getMetadataFileName(id);
						if (!allowedPathPredicate.test(identifier)) {
							return;
						}
	
						((NamespaceResourceManager.EntryList)idToEntryList.computeIfAbsent(identifier, NamespaceResourceManager.EntryList::new))
							.metaSources
							.put(resourcePack, supplier);
					} else {
						if (!allowedPathPredicate.test(id)) {
							return;
						}
	
						((NamespaceResourceManager.EntryList)idToEntryList.computeIfAbsent(id, NamespaceResourceManager.EntryList::new))
							.fileSources
							.add(new NamespaceResourceManager.FileSource(resourcePack, supplier));
					}
				}
			);
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

		for (NamespaceResourceManager.EntryList entryList : map.values()) {
			if (!entryList.fileSources.isEmpty()) {
				List<Resource> list = new ArrayList();

				for (NamespaceResourceManager.FileSource fileSource : entryList.fileSources) {
					ResourcePack resourcePack = fileSource.sourcePack;
					InputSupplier<InputStream> inputSupplier = (InputSupplier<InputStream>)entryList.metaSources.get(resourcePack);
					InputSupplier<ResourceMetadata> inputSupplier2 = inputSupplier != null ? getMetadataSupplier(inputSupplier) : ResourceMetadata.NONE_SUPPLIER;
					list.add(createResource(resourcePack, entryList.id, fileSource.supplier, inputSupplier2));
				}

				treeMap.put(entryList.id, list);
			}
		}

		return treeMap;
	}

	@Override
	public Stream<ResourcePack> streamResourcePacks() {
		return this.packList.stream().map(pack -> pack.underlying).filter(Objects::nonNull);
	}

	static class DebugInputStream extends FilterInputStream {
		private final Supplier<String> leakMessage;
		private boolean closed;

		public DebugInputStream(InputStream parent, Identifier id, String packId) {
			super(parent);
			Exception exception = new Exception("Stacktrace");
			this.leakMessage = () -> {
				StringWriter stringWriter = new StringWriter();
				exception.printStackTrace(new PrintWriter(stringWriter));
				return "Leaked resource: '" + id + "' loaded from pack: '" + packId + "'\n" + stringWriter;
			};
		}

		public void close() throws IOException {
			super.close();
			this.closed = true;
		}

		protected void finalize() throws Throwable {
			if (!this.closed) {
				NamespaceResourceManager.LOGGER.warn("{}", this.leakMessage.get());
			}

			super.finalize();
		}
	}

	static record EntryList(
		Identifier id, Identifier metadataId, List<NamespaceResourceManager.FileSource> fileSources, Map<ResourcePack, InputSupplier<InputStream>> metaSources
	) {

		EntryList(Identifier id) {
			this(id, NamespaceResourceManager.getMetadataPath(id), new ArrayList(), new Object2ObjectArrayMap<>());
		}
	}

	static record FileSource(ResourcePack sourcePack, InputSupplier<InputStream> supplier) {
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
