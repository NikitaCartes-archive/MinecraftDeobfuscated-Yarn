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
	private final List<ResourceReloadListener> initialListeners = Lists.<ResourceReloadListener>newArrayList();
	private final Set<String> namespaces = Sets.<String>newLinkedHashSet();
	private final ResourceType type;
	private final Thread mainThread;

	public ReloadableResourceManagerImpl(ResourceType type, Thread mainThread) {
		this.type = type;
		this.mainThread = mainThread;
	}

	@Override
	public void addPack(ResourcePack pack) {
		for (String string : pack.getNamespaces(this.type)) {
			this.namespaces.add(string);
			NamespaceResourceManager namespaceResourceManager = (NamespaceResourceManager)this.namespaceManagers.get(string);
			if (namespaceResourceManager == null) {
				namespaceResourceManager = new NamespaceResourceManager(this.type);
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
	public Collection<Identifier> findResources(String resourceType, Predicate<String> pathPredicate) {
		Set<Identifier> set = Sets.<Identifier>newHashSet();

		for (NamespaceResourceManager namespaceResourceManager : this.namespaceManagers.values()) {
			set.addAll(namespaceResourceManager.findResources(resourceType, pathPredicate));
		}

		List<Identifier> list = Lists.<Identifier>newArrayList(set);
		Collections.sort(list);
		return list;
	}

	private void clear() {
		this.namespaceManagers.clear();
		this.namespaces.clear();
	}

	@Override
	public CompletableFuture<Unit> beginReload(Executor prepareExecutor, Executor applyExecutor, List<ResourcePack> packs, CompletableFuture<Unit> initialStage) {
		ResourceReloadMonitor resourceReloadMonitor = this.beginMonitoredReload(prepareExecutor, applyExecutor, initialStage, packs);
		return resourceReloadMonitor.whenComplete();
	}

	@Override
	public void registerListener(ResourceReloadListener listener) {
		this.listeners.add(listener);
		this.initialListeners.add(listener);
	}

	protected ResourceReloadMonitor beginReloadInner(
		Executor prepareExecutor, Executor applyExecutor, List<ResourceReloadListener> listeners, CompletableFuture<Unit> initialStage
	) {
		ResourceReloadMonitor resourceReloadMonitor;
		if (LOGGER.isDebugEnabled()) {
			resourceReloadMonitor = new ProfilingResourceReloader(
				this, Lists.<ResourceReloadListener>newArrayList(listeners), prepareExecutor, applyExecutor, initialStage
			);
		} else {
			resourceReloadMonitor = ResourceReloader.create(this, Lists.<ResourceReloadListener>newArrayList(listeners), prepareExecutor, applyExecutor, initialStage);
		}

		this.initialListeners.clear();
		return resourceReloadMonitor;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ResourceReloadMonitor beginInitialMonitoredReload(Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage) {
		return this.beginReloadInner(prepareExecutor, applyExecutor, this.initialListeners, initialStage);
	}

	@Override
	public ResourceReloadMonitor beginMonitoredReload(
		Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, List<ResourcePack> packs
	) {
		this.clear();
		LOGGER.info("Reloading ResourceManager: {}", packs.stream().map(ResourcePack::getName).collect(Collectors.joining(", ")));

		for (ResourcePack resourcePack : packs) {
			this.addPack(resourcePack);
		}

		return this.beginReloadInner(prepareExecutor, applyExecutor, this.listeners, initialStage);
	}
}
