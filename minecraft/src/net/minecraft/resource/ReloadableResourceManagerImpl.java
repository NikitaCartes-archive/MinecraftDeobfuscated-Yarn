package net.minecraft.resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
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
import net.minecraft.util.Void;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReloadableResourceManagerImpl implements ReloadableResourceManager {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Map<String, NamespaceResourceManager> namespaceManagers = Maps.<String, NamespaceResourceManager>newHashMap();
	private final List<ResourceReloadListener> listeners = Lists.<ResourceReloadListener>newArrayList();
	private final List<ResourceReloadListener> initialListeners = Lists.<ResourceReloadListener>newArrayList();
	private final Set<String> namespaces = Sets.<String>newLinkedHashSet();
	private final ResourceType type;
	private final Thread field_17937;

	public ReloadableResourceManagerImpl(ResourceType resourceType, Thread thread) {
		this.type = resourceType;
		this.field_17937 = thread;
	}

	@Override
	public void addPack(ResourcePack resourcePack) {
		for (String string : resourcePack.getNamespaces(this.type)) {
			this.namespaces.add(string);
			NamespaceResourceManager namespaceResourceManager = (NamespaceResourceManager)this.namespaceManagers.get(string);
			if (namespaceResourceManager == null) {
				namespaceResourceManager = new NamespaceResourceManager(this.type);
				this.namespaceManagers.put(string, namespaceResourceManager);
			}

			namespaceResourceManager.addPack(resourcePack);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Set<String> getAllNamespaces() {
		return this.namespaces;
	}

	@Override
	public Resource getResource(Identifier identifier) throws IOException {
		ResourceManager resourceManager = (ResourceManager)this.namespaceManagers.get(identifier.getNamespace());
		if (resourceManager != null) {
			return resourceManager.getResource(identifier);
		} else {
			throw new FileNotFoundException(identifier.toString());
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean containsResource(Identifier identifier) {
		ResourceManager resourceManager = (ResourceManager)this.namespaceManagers.get(identifier.getNamespace());
		return resourceManager != null ? resourceManager.containsResource(identifier) : false;
	}

	@Override
	public List<Resource> getAllResources(Identifier identifier) throws IOException {
		ResourceManager resourceManager = (ResourceManager)this.namespaceManagers.get(identifier.getNamespace());
		if (resourceManager != null) {
			return resourceManager.getAllResources(identifier);
		} else {
			throw new FileNotFoundException(identifier.toString());
		}
	}

	@Override
	public Collection<Identifier> findResources(String string, Predicate<String> predicate) {
		Set<Identifier> set = Sets.<Identifier>newHashSet();

		for (NamespaceResourceManager namespaceResourceManager : this.namespaceManagers.values()) {
			set.addAll(namespaceResourceManager.findResources(string, predicate));
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
	public CompletableFuture<Void> beginReload(Executor executor, Executor executor2, List<ResourcePack> list, CompletableFuture<Void> completableFuture) {
		ResourceReloadMonitor resourceReloadMonitor = this.beginMonitoredReload(executor, executor2, completableFuture, list);
		return resourceReloadMonitor.whenComplete();
	}

	@Override
	public void registerListener(ResourceReloadListener resourceReloadListener) {
		this.listeners.add(resourceReloadListener);
		this.initialListeners.add(resourceReloadListener);
	}

	protected ResourceReloadMonitor beginReloadInner(
		Executor executor, Executor executor2, List<ResourceReloadListener> list, CompletableFuture<Void> completableFuture
	) {
		ResourceReloadMonitor resourceReloadMonitor;
		if (LOGGER.isDebugEnabled()) {
			resourceReloadMonitor = new ProfilingResourceReloadHandler(this, new ArrayList(list), executor, executor2, completableFuture);
		} else {
			resourceReloadMonitor = ResourceReloadHandler.create(this, new ArrayList(list), executor, executor2, completableFuture);
		}

		this.initialListeners.clear();
		return resourceReloadMonitor;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ResourceReloadMonitor beginInitialMonitoredReload(Executor executor, Executor executor2, CompletableFuture<Void> completableFuture) {
		return this.beginReloadInner(executor, executor2, this.initialListeners, completableFuture);
	}

	@Override
	public ResourceReloadMonitor beginMonitoredReload(Executor executor, Executor executor2, CompletableFuture<Void> completableFuture, List<ResourcePack> list) {
		this.clear();
		LOGGER.info("Reloading ResourceManager: {}", list.stream().map(ResourcePack::getName).collect(Collectors.joining(", ")));

		for (ResourcePack resourcePack : list) {
			this.addPack(resourcePack);
		}

		return this.beginReloadInner(executor, executor2, this.listeners, completableFuture);
	}
}
