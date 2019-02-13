package net.minecraft.resource;

import java.io.Closeable;
import java.io.InputStream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;

public interface Resource extends Closeable {
	@Environment(EnvType.CLIENT)
	Identifier getId();

	InputStream getInputStream();

	@Nullable
	@Environment(EnvType.CLIENT)
	<T> T getMetadata(ResourceMetadataReader<T> resourceMetadataReader);

	String getResourcePackName();
}
