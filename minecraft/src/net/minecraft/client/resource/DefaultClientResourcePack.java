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
		if (resourceType == ResourceType.ASSETS) {
			File file = this.index.method_4630(identifier);
			if (file != null && file.exists()) {
				try {
					return new FileInputStream(file);
				} catch (FileNotFoundException var5) {
				}
			}
		}

		return super.findInputStream(resourceType, identifier);
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
	public Collection<Identifier> method_14408(ResourceType resourceType, String string, int i, Predicate<String> predicate) {
		Collection<Identifier> collection = super.method_14408(resourceType, string, i, predicate);
		collection.addAll((Collection)this.index.getFilesRecursively(string, i, predicate).stream().map(Identifier::new).collect(Collectors.toList()));
		return collection;
	}
}
