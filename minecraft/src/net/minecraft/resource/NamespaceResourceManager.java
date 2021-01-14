package net.minecraft.resource;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NamespaceResourceManager implements ResourceManager {
	private static final Logger LOGGER = LogManager.getLogger();
	protected final List<ResourcePack> packList = Lists.<ResourcePack>newArrayList();
	private final ResourceType type;
	private final String namespace;

	public NamespaceResourceManager(ResourceType type, String namespace) {
		this.type = type;
		this.namespace = namespace;
	}

	public void addPack(ResourcePack pack) {
		this.packList.add(pack);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Set<String> getAllNamespaces() {
		return ImmutableSet.of(this.namespace);
	}

	@Override
	public Resource getResource(Identifier id) throws IOException {
		this.validate(id);
		ResourcePack resourcePack = null;
		Identifier identifier = getMetadataPath(id);

		for (int i = this.packList.size() - 1; i >= 0; i--) {
			ResourcePack resourcePack2 = (ResourcePack)this.packList.get(i);
			if (resourcePack == null && resourcePack2.contains(this.type, identifier)) {
				resourcePack = resourcePack2;
			}

			if (resourcePack2.contains(this.type, id)) {
				InputStream inputStream = null;
				if (resourcePack != null) {
					inputStream = this.open(identifier, resourcePack);
				}

				return new ResourceImpl(resourcePack2.getName(), id, this.open(id, resourcePack2), inputStream);
			}
		}

		throw new FileNotFoundException(id.toString());
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean containsResource(Identifier id) {
		if (!this.isPathAbsolute(id)) {
			return false;
		} else {
			for (int i = this.packList.size() - 1; i >= 0; i--) {
				ResourcePack resourcePack = (ResourcePack)this.packList.get(i);
				if (resourcePack.contains(this.type, id)) {
					return true;
				}
			}

			return false;
		}
	}

	protected InputStream open(Identifier id, ResourcePack pack) throws IOException {
		InputStream inputStream = pack.open(this.type, id);
		return (InputStream)(LOGGER.isDebugEnabled() ? new NamespaceResourceManager.DebugInputStream(inputStream, id, pack.getName()) : inputStream);
	}

	private void validate(Identifier id) throws IOException {
		if (!this.isPathAbsolute(id)) {
			throw new IOException("Invalid relative path to resource: " + id);
		}
	}

	private boolean isPathAbsolute(Identifier id) {
		return !id.getPath().contains("..");
	}

	@Override
	public List<Resource> getAllResources(Identifier id) throws IOException {
		this.validate(id);
		List<Resource> list = Lists.<Resource>newArrayList();
		Identifier identifier = getMetadataPath(id);

		for (ResourcePack resourcePack : this.packList) {
			if (resourcePack.contains(this.type, id)) {
				InputStream inputStream = resourcePack.contains(this.type, identifier) ? this.open(identifier, resourcePack) : null;
				list.add(new ResourceImpl(resourcePack.getName(), id, this.open(id, resourcePack), inputStream));
			}
		}

		if (list.isEmpty()) {
			throw new FileNotFoundException(id.toString());
		} else {
			return list;
		}
	}

	@Override
	public Collection<Identifier> findResources(String startingPath, Predicate<String> pathPredicate) {
		List<Identifier> list = Lists.<Identifier>newArrayList();

		for (ResourcePack resourcePack : this.packList) {
			list.addAll(resourcePack.findResources(this.type, this.namespace, startingPath, Integer.MAX_VALUE, pathPredicate));
		}

		Collections.sort(list);
		return list;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Stream<ResourcePack> streamResourcePacks() {
		return this.packList.stream();
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
}
