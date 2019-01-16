package net.minecraft.resource;

import com.google.common.base.Stopwatch;
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
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReloadableResourceManagerImpl implements ReloadableResourceManager {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Map<String, NamespaceResourceManager> namespaceManagers = Maps.<String, NamespaceResourceManager>newHashMap();
	private final List<ResourceReloadListener> listeners = Lists.<ResourceReloadListener>newArrayList();
	private final Set<String> namespaces = Sets.<String>newLinkedHashSet();
	private final ResourceType type;

	public ReloadableResourceManagerImpl(ResourceType resourceType) {
		this.type = resourceType;
	}

	public void load(ResourcePack resourcePack) {
		for (String string : resourcePack.getNamespaces(this.type)) {
			this.namespaces.add(string);
			NamespaceResourceManager namespaceResourceManager = (NamespaceResourceManager)this.namespaceManagers.get(string);
			if (namespaceResourceManager == null) {
				namespaceResourceManager = new NamespaceResourceManager(this.type);
				this.namespaceManagers.put(string, namespaceResourceManager);
			}

			namespaceResourceManager.add(resourcePack);
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
	public void reload(List<ResourcePack> list) {
		this.clear();
		LOGGER.info("Reloading ResourceManager: {}", list.stream().map(ResourcePack::getName).collect(Collectors.joining(", ")));

		for (ResourcePack resourcePack : list) {
			this.load(resourcePack);
		}

		if (LOGGER.isDebugEnabled()) {
			this.reloadAll();
		} else {
			this.emitReloadAll();
		}
	}

	@Override
	public void addListener(ResourceReloadListener resourceReloadListener) {
		this.listeners.add(resourceReloadListener);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(this.emitReloadTimed(resourceReloadListener));
		} else {
			resourceReloadListener.onResourceReload(this);
		}
	}

	private void emitReloadAll() {
		for (ResourceReloadListener resourceReloadListener : this.listeners) {
			resourceReloadListener.onResourceReload(this);
		}
	}

	private void reloadAll() {
		LOGGER.info("Reloading all resources! {} listeners to update.", this.listeners.size());
		List<String> list = Lists.<String>newArrayList();
		Stopwatch stopwatch = Stopwatch.createStarted();

		for (ResourceReloadListener resourceReloadListener : this.listeners) {
			list.add(this.emitReloadTimed(resourceReloadListener));
		}

		stopwatch.stop();
		LOGGER.info("----");
		LOGGER.info("Complete resource reload took {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));

		for (String string : list) {
			LOGGER.info(string);
		}

		LOGGER.info("----");
	}

	private String emitReloadTimed(ResourceReloadListener resourceReloadListener) {
		Stopwatch stopwatch = Stopwatch.createStarted();
		resourceReloadListener.onResourceReload(this);
		stopwatch.stop();
		return "Resource reload for " + resourceReloadListener.getClass().getSimpleName() + " took " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms";
	}
}
