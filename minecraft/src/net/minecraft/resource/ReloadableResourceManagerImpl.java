package net.minecraft.resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReloadableResourceManagerImpl implements ReloadableResourceManager {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Map<String, NamespaceResourceManager> namespaceManagers = Maps.<String, NamespaceResourceManager>newHashMap();
	private final List<ResourceReloadListener> listeners = Lists.<ResourceReloadListener>newArrayList();
	private final Set<String> namespaces = Sets.<String>newLinkedHashSet();
	private final List<ResourcePack> packs = Lists.<ResourcePack>newArrayList();
	private final ResourceType type;

	public ReloadableResourceManagerImpl(ResourceType type) {
		this.type = type;
	}

	public void addPack(ResourcePack pack) {
		this.packs.add(pack);

		for (String string : pack.getNamespaces(this.type)) {
			this.namespaces.add(string);
			NamespaceResourceManager namespaceResourceManager = (NamespaceResourceManager)this.namespaceManagers.get(string);
			if (namespaceResourceManager == null) {
				namespaceResourceManager = new NamespaceResourceManager(this.type, string);
				this.namespaceManagers.put(string, namespaceResourceManager);
			}

			namespaceResourceManager.addPack(pack);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Set<String> getAllNamespaces() {
		return this.namespaces;
	}

	@Override
	public Resource getResource(Identifier id) throws IOException {
		ResourceManager resourceManager = (ResourceManager)this.namespaceManagers.get(id.getNamespace());
		if (resourceManager != null) {
			return resourceManager.getResource(id);
		} else {
			throw new FileNotFoundException(id.toString());
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean containsResource(Identifier id) {
		ResourceManager resourceManager = (ResourceManager)this.namespaceManagers.get(id.getNamespace());
		return resourceManager != null ? resourceManager.containsResource(id) : false;
	}

	@Override
	public List<Resource> getAllResources(Identifier id) throws IOException {
		ResourceManager resourceManager = (ResourceManager)this.namespaceManagers.get(id.getNamespace());
		if (resourceManager != null) {
			return resourceManager.getAllResources(id);
		} else {
			throw new FileNotFoundException(id.toString());
		}
	}

	@Override
	public Collection<Identifier> findResources(String startingPath, Predicate<String> pathPredicate) {
		Set<Identifier> set = Sets.<Identifier>newHashSet();

		for (NamespaceResourceManager namespaceResourceManager : this.namespaceManagers.values()) {
			set.addAll(namespaceResourceManager.findResources(startingPath, pathPredicate));
		}

		List<Identifier> list = Lists.<Identifier>newArrayList(set);
		Collections.sort(list);
		return list;
	}

	private void clear() {
		this.namespaceManagers.clear();
		this.namespaces.clear();
		this.packs.forEach(ResourcePack::close);
		this.packs.clear();
	}

	@Override
	public void close() {
		this.clear();
	}

	@Override
	public void registerListener(ResourceReloadListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public ResourceReloadMonitor beginMonitoredReload(
		Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, List<ResourcePack> packs
	) {
		LOGGER.info("Reloading ResourceManager: {}", () -> (String)packs.stream().map(ResourcePack::getName).collect(Collectors.joining(", ")));
		this.clear();

		for (ResourcePack resourcePack : packs) {
			try {
				this.addPack(resourcePack);
			} catch (Exception var8) {
				LOGGER.error("Failed to add resource pack {}", resourcePack.getName(), var8);
				return new ReloadableResourceManagerImpl.FailedResourceReloadMonitor(new ReloadableResourceManagerImpl.PackAdditionFailedException(resourcePack, var8));
			}
		}

		return (ResourceReloadMonitor)(LOGGER.isDebugEnabled()
			? new ProfilingResourceReloader(this, Lists.<ResourceReloadListener>newArrayList(this.listeners), prepareExecutor, applyExecutor, initialStage)
			: ResourceReloader.create(this, Lists.<ResourceReloadListener>newArrayList(this.listeners), prepareExecutor, applyExecutor, initialStage));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Stream<ResourcePack> streamResourcePacks() {
		return this.packs.stream();
	}

	static class FailedResourceReloadMonitor implements ResourceReloadMonitor {
		private final ReloadableResourceManagerImpl.PackAdditionFailedException exception;
		private final CompletableFuture<Unit> future;

		public FailedResourceReloadMonitor(ReloadableResourceManagerImpl.PackAdditionFailedException exception) {
			this.exception = exception;
			this.future = new CompletableFuture();
			this.future.completeExceptionally(exception);
		}

		@Override
		public CompletableFuture<Unit> whenComplete() {
			return this.future;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public float getProgress() {
			return 0.0F;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public boolean isPrepareStageComplete() {
			return false;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public boolean isApplyStageComplete() {
			return true;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public void throwExceptions() {
			throw this.exception;
		}
	}

	public static class PackAdditionFailedException extends RuntimeException {
		private final ResourcePack pack;

		public PackAdditionFailedException(ResourcePack pack, Throwable cause) {
			super(pack.getName(), cause);
			this.pack = pack;
		}

		@Environment(EnvType.CLIENT)
		public ResourcePack getPack() {
			return this.pack;
		}
	}
}
