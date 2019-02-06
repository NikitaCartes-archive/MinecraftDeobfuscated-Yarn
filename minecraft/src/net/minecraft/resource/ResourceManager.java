package net.minecraft.resource;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

public interface ResourceManager {
	@Environment(EnvType.CLIENT)
	Set<String> getAllNamespaces();

	Resource getResource(Identifier identifier) throws IOException;

	@Environment(EnvType.CLIENT)
	boolean method_18234(Identifier identifier);

	List<Resource> getAllResources(Identifier identifier) throws IOException;

	Collection<Identifier> findResources(String string, Predicate<String> predicate);
}
