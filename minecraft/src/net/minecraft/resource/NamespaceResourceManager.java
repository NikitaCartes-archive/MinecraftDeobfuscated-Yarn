package net.minecraft.resource;

import com.google.common.collect.Lists;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NamespaceResourceManager implements ResourceManager {
	private static final Logger LOGGER = LogManager.getLogger();
	protected final List<ResourcePack> packList = Lists.<ResourcePack>newArrayList();
	private final ResourceType type;

	public NamespaceResourceManager(ResourceType resourceType) {
		this.type = resourceType;
	}

	public void add(ResourcePack resourcePack) {
		this.packList.add(resourcePack);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Set<String> getAllNamespaces() {
		return Collections.emptySet();
	}

	@Override
	public Resource getResource(Identifier identifier) throws IOException {
		this.validate(identifier);
		ResourcePack resourcePack = null;
		Identifier identifier2 = getMetadataPath(identifier);

		for (int i = this.packList.size() - 1; i >= 0; i--) {
			ResourcePack resourcePack2 = (ResourcePack)this.packList.get(i);
			if (resourcePack == null && resourcePack2.contains(this.type, identifier2)) {
				resourcePack = resourcePack2;
			}

			if (resourcePack2.contains(this.type, identifier)) {
				InputStream inputStream = null;
				if (resourcePack != null) {
					inputStream = this.open(identifier2, resourcePack);
				}

				return new ResourceImpl(resourcePack2.getName(), identifier, this.open(identifier, resourcePack2), inputStream);
			}
		}

		throw new FileNotFoundException(identifier.toString());
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_18234(Identifier identifier) {
		if (!this.method_18221(identifier)) {
			return false;
		} else {
			for (int i = this.packList.size() - 1; i >= 0; i--) {
				ResourcePack resourcePack = (ResourcePack)this.packList.get(i);
				if (resourcePack.contains(this.type, identifier)) {
					return true;
				}
			}

			return false;
		}
	}

	protected InputStream open(Identifier identifier, ResourcePack resourcePack) throws IOException {
		InputStream inputStream = resourcePack.open(this.type, identifier);
		return (InputStream)(LOGGER.isDebugEnabled() ? new NamespaceResourceManager.DebugInputStream(inputStream, identifier, resourcePack.getName()) : inputStream);
	}

	private void validate(Identifier identifier) throws IOException {
		if (!this.method_18221(identifier)) {
			throw new IOException("Invalid relative path to resource: " + identifier);
		}
	}

	private boolean method_18221(Identifier identifier) {
		return !identifier.getPath().contains("..");
	}

	@Override
	public List<Resource> getAllResources(Identifier identifier) throws IOException {
		this.validate(identifier);
		List<Resource> list = Lists.<Resource>newArrayList();
		Identifier identifier2 = getMetadataPath(identifier);

		for (ResourcePack resourcePack : this.packList) {
			if (resourcePack.contains(this.type, identifier)) {
				InputStream inputStream = resourcePack.contains(this.type, identifier2) ? this.open(identifier2, resourcePack) : null;
				list.add(new ResourceImpl(resourcePack.getName(), identifier, this.open(identifier, resourcePack), inputStream));
			}
		}

		if (list.isEmpty()) {
			throw new FileNotFoundException(identifier.toString());
		} else {
			return list;
		}
	}

	@Override
	public Collection<Identifier> findResources(String string, Predicate<String> predicate) {
		List<Identifier> list = Lists.<Identifier>newArrayList();

		for (ResourcePack resourcePack : this.packList) {
			list.addAll(resourcePack.findResources(this.type, string, Integer.MAX_VALUE, predicate));
		}

		Collections.sort(list);
		return list;
	}

	static Identifier getMetadataPath(Identifier identifier) {
		return new Identifier(identifier.getNamespace(), identifier.getPath() + ".mcmeta");
	}

	static class DebugInputStream extends InputStream {
		private final InputStream parent;
		private final String leakMessage;
		private boolean closed;

		public DebugInputStream(InputStream inputStream, Identifier identifier, String string) {
			this.parent = inputStream;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			new Exception().printStackTrace(new PrintStream(byteArrayOutputStream));
			this.leakMessage = "Leaked resource: '" + identifier + "' loaded from pack: '" + string + "'\n" + byteArrayOutputStream;
		}

		public void close() throws IOException {
			this.parent.close();
			this.closed = true;
		}

		protected void finalize() throws Throwable {
			if (!this.closed) {
				NamespaceResourceManager.LOGGER.warn(this.leakMessage);
			}

			super.finalize();
		}

		public int read() throws IOException {
			return this.parent.read();
		}
	}
}
