package net.minecraft.resource;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;

public interface ResourcePack extends Closeable {
	@Environment(EnvType.CLIENT)
	InputStream openRoot(String string) throws IOException;

	InputStream method_14405(ResourceType resourceType, Identifier identifier) throws IOException;

	Collection<Identifier> method_14408(ResourceType resourceType, String string, int i, Predicate<String> predicate);

	boolean method_14411(ResourceType resourceType, Identifier identifier);

	Set<String> method_14406(ResourceType resourceType);

	@Nullable
	<T> T method_14407(ResourceMetadataReader<T> resourceMetadataReader) throws IOException;

	String getName();
}
