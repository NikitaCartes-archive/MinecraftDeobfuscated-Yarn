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
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4010;
import net.minecraft.class_4014;
import net.minecraft.client.resource.ResourceLoadProgressProvider;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReloadableResourceManagerImpl implements ReloadableResourceManager {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Map<String, NamespaceResourceManager> namespaceManagers = Maps.<String, NamespaceResourceManager>newHashMap();
	private final List<ResourceReloadListener<?>> field_17935 = Lists.<ResourceReloadListener<?>>newArrayList();
	private final List<ResourceReloadListener<?>> field_17936 = Lists.<ResourceReloadListener<?>>newArrayList();
	private final Set<String> namespaces = Sets.<String>newLinkedHashSet();
	private final ResourceType type;
	private final Thread field_17937;

	public ReloadableResourceManagerImpl(ResourceType resourceType, Thread thread) {
		this.type = resourceType;
		this.field_17937 = thread;
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

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_18234(Identifier identifier) {
		ResourceManager resourceManager = (ResourceManager)this.namespaceManagers.get(identifier.getNamespace());
		return resourceManager != null ? resourceManager.method_18234(identifier) : false;
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

		ResourceLoadProgressProvider resourceLoadProgressProvider = this.method_18233();
		resourceLoadProgressProvider.method_18226();
	}

	@Override
	public void addListener(ResourceReloadListener<?> resourceReloadListener) {
		this.field_17935.add(resourceReloadListener);
		this.field_17936.add(resourceReloadListener);
	}

	protected ResourceLoadProgressProvider method_18240(List<ResourceReloadListener<?>> list, @Nullable CompletableFuture<Void> completableFuture) {
		ResourceLoadProgressProvider resourceLoadProgressProvider;
		if (LOGGER.isDebugEnabled()) {
			resourceLoadProgressProvider = new class_4010(this, new ArrayList(list), completableFuture);
		} else {
			resourceLoadProgressProvider = new class_4014(this, new ArrayList(list), completableFuture);
		}

		this.field_17936.clear();
		return resourceLoadProgressProvider;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ResourceLoadProgressProvider method_18230(@Nullable CompletableFuture<Void> completableFuture) {
		return this.method_18240(this.field_17936, completableFuture);
	}

	@Override
	public ResourceLoadProgressProvider method_18232(@Nullable CompletableFuture<Void> completableFuture) {
		return this.method_18240(this.field_17935, completableFuture);
	}
}
