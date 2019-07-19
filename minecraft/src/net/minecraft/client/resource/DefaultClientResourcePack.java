package net.minecraft.client.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DefaultClientResourcePack extends DefaultResourcePack {
	private final ResourceIndex index;

	public DefaultClientResourcePack(ResourceIndex resourceIndex) {
		super("minecraft", "realms");
		this.index = resourceIndex;
	}

	@Nullable
	@Override
	protected InputStream findInputStream(ResourceType type, Identifier id) {
		if (type == ResourceType.CLIENT_RESOURCES) {
			File file = this.index.getResource(id);
			if (file != null && file.exists()) {
				try {
					return new FileInputStream(file);
				} catch (FileNotFoundException var5) {
				}
			}
		}

		return super.findInputStream(type, id);
	}

	@Override
	public boolean contains(ResourceType type, Identifier id) {
		if (type == ResourceType.CLIENT_RESOURCES) {
			File file = this.index.getResource(id);
			if (file != null && file.exists()) {
				return true;
			}
		}

		return super.contains(type, id);
	}

	@Nullable
	@Override
	protected InputStream getInputStream(String path) {
		File file = this.index.findFile(path);
		if (file != null && file.exists()) {
			try {
				return new FileInputStream(file);
			} catch (FileNotFoundException var4) {
			}
		}

		return super.getInputStream(path);
	}

	@Override
	public Collection<Identifier> findResources(ResourceType type, String namespace, int maxDepth, Predicate<String> pathFilter) {
		Collection<Identifier> collection = super.findResources(type, namespace, maxDepth, pathFilter);
		collection.addAll((Collection)this.index.getFilesRecursively(namespace, maxDepth, pathFilter).stream().map(Identifier::new).collect(Collectors.toList()));
		return collection;
	}
}
