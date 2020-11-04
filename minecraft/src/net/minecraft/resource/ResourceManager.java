package net.minecraft.resource;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.io.FileNotFoundException;
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
	Stream<ResourcePack> streamResourcePacks();

	public static enum Empty implements ResourceManager {
		INSTANCE;

		@Environment(EnvType.CLIENT)
		@Override
		public Set<String> getAllNamespaces() {
			return ImmutableSet.of();
		}

		@Override
		public Resource getResource(Identifier id) throws IOException {
			throw new FileNotFoundException(id.toString());
		}

		@Environment(EnvType.CLIENT)
		@Override
		public boolean containsResource(Identifier id) {
			return false;
		}

		@Override
		public List<Resource> getAllResources(Identifier id) {
			return ImmutableList.of();
		}

		@Override
		public Collection<Identifier> findResources(String resourceType, Predicate<String> pathPredicate) {
			return ImmutableSet.<Identifier>of();
		}

		@Environment(EnvType.CLIENT)
		@Override
		public Stream<ResourcePack> streamResourcePacks() {
			return Stream.of();
		}
	}
}
