package net.minecraft.resource;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

public interface ResourceManager {
	@Environment(EnvType.CLIENT)
	Set<String> getAllNamespaces();

	Resource getResource(Identifier id) throws IOException;

	@Environment(EnvType.CLIENT)
	boolean containsResource(Identifier id);

	List<Resource> getAllResources(Identifier id) throws IOException;

	Collection<Identifier> findResources(String resourceType, Predicate<String> pathPredicate);

	@Environment(EnvType.CLIENT)
	Stream<ResourcePack> method_29213();
}
