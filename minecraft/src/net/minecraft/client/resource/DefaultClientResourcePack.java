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
	protected InputStream findInputStream(ResourceType resourceType, Identifier identifier) {
		if (resourceType == ResourceType.field_14188) {
			File file = this.index.getResource(identifier);
			if (file != null && file.exists()) {
				try {
					return new FileInputStream(file);
				} catch (FileNotFoundException var5) {
				}
			}
		}

		return super.findInputStream(resourceType, identifier);
	}

	@Override
	public boolean contains(ResourceType resourceType, Identifier identifier) {
		if (resourceType == ResourceType.field_14188) {
			File file = this.index.getResource(identifier);
			if (file != null && file.exists()) {
				return true;
			}
		}

		return super.contains(resourceType, identifier);
	}

	@Nullable
	@Override
	protected InputStream getInputStream(String string) {
		File file = this.index.findFile(string);
		if (file != null && file.exists()) {
			try {
				return new FileInputStream(file);
			} catch (FileNotFoundException var4) {
			}
		}

		return super.getInputStream(string);
	}

	@Override
	public Collection<Identifier> findResources(ResourceType resourceType, String string, int i, Predicate<String> predicate) {
		Collection<Identifier> collection = super.findResources(resourceType, string, i, predicate);
		collection.addAll((Collection)this.index.getFilesRecursively(string, i, predicate).stream().map(Identifier::new).collect(Collectors.toList()));
		return collection;
	}
}
