package net.minecraft.resource;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractFilenameResourcePack implements ResourcePack {
	private static final Logger LOGGER = LogManager.getLogger();
	protected final File base;

	public AbstractFilenameResourcePack(File file) {
		this.base = file;
	}

	private static String getFilename(ResourceType resourceType, Identifier identifier) {
		return String.format("%s/%s/%s", resourceType.getName(), identifier.getNamespace(), identifier.getPath());
	}

	protected static String relativize(File file, File file2) {
		return file.toURI().relativize(file2.toURI()).getPath();
	}

	@Override
	public InputStream open(ResourceType resourceType, Identifier identifier) throws IOException {
		return this.openFilename(getFilename(resourceType, identifier));
	}

	@Override
	public boolean contains(ResourceType resourceType, Identifier identifier) {
		return this.containsFilename(getFilename(resourceType, identifier));
	}

	protected abstract InputStream openFilename(String string) throws IOException;

	@Environment(EnvType.CLIENT)
	@Override
	public InputStream openRoot(String string) throws IOException {
		if (!string.contains("/") && !string.contains("\\")) {
			return this.openFilename(string);
		} else {
			throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
		}
	}

	protected abstract boolean containsFilename(String string);

	protected void warnNonLowercaseNamespace(String string) {
		LOGGER.warn("ResourcePack: ignored non-lowercase namespace: {} in {}", string, this.base);
	}

	@Nullable
	@Override
	public <T> T parseMetadata(ResourceMetadataReader<T> resourceMetadataReader) throws IOException {
		return parseMetadata(resourceMetadataReader, this.openFilename("pack.mcmeta"));
	}

	@Nullable
	public static <T> T parseMetadata(ResourceMetadataReader<T> resourceMetadataReader, InputStream inputStream) {
		JsonObject jsonObject;
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
			Throwable var4 = null;

			try {
				jsonObject = JsonHelper.deserialize(bufferedReader);
			} catch (Throwable var16) {
				var4 = var16;
				throw var16;
			} finally {
				if (bufferedReader != null) {
					if (var4 != null) {
						try {
							bufferedReader.close();
						} catch (Throwable var14) {
							var4.addSuppressed(var14);
						}
					} else {
						bufferedReader.close();
					}
				}
			}
		} catch (JsonParseException | IOException var18) {
			LOGGER.error("Couldn't load {} metadata", resourceMetadataReader.getKey(), var18);
			return null;
		}

		if (!jsonObject.has(resourceMetadataReader.getKey())) {
			return null;
		} else {
			try {
				return resourceMetadataReader.fromJson(JsonHelper.getObject(jsonObject, resourceMetadataReader.getKey()));
			} catch (JsonParseException var15) {
				LOGGER.error("Couldn't load {} metadata", resourceMetadataReader.getKey(), var15);
				return null;
			}
		}
	}

	@Override
	public String getName() {
		return this.base.getName();
	}
}
