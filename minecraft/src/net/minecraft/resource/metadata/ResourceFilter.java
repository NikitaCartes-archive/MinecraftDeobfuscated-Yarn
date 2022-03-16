package net.minecraft.resource.metadata;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import org.slf4j.Logger;

public class ResourceFilter {
	static final Logger LOGGER = LogUtils.getLogger();
	static final Codec<ResourceFilter> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(Codec.list(ResourceFilter.BlockEntry.CODEC).fieldOf("block").forGetter(filter -> filter.blocks))
				.apply(instance, ResourceFilter::new)
	);
	public static final ResourceMetadataReader<ResourceFilter> READER = new ResourceMetadataReader<ResourceFilter>() {
		@Override
		public String getKey() {
			return "filter";
		}

		public ResourceFilter fromJson(JsonObject jsonObject) {
			return ResourceFilter.CODEC.parse(JsonOps.INSTANCE, jsonObject).getOrThrow(false, ResourceFilter.LOGGER::error);
		}
	};
	/**
	 * The list of block rules, named {@code block} in the JSON format.
	 */
	private final List<ResourceFilter.BlockEntry> blocks;

	public ResourceFilter(List<ResourceFilter.BlockEntry> blocks) {
		this.blocks = List.copyOf(blocks);
	}

	public boolean isNamespaceBlocked(String namespace) {
		return this.blocks.stream().anyMatch(block -> block.namespacePredicate.test(namespace));
	}

	public boolean isPathBlocked(String namespace) {
		return this.blocks.stream().anyMatch(block -> block.pathPredicate.test(namespace));
	}

	static class BlockEntry implements Predicate<Identifier> {
		static final Codec<ResourceFilter.BlockEntry> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codecs.REGULAR_EXPRESSION.optionalFieldOf("namespace").forGetter(entry -> entry.namespace),
						Codecs.REGULAR_EXPRESSION.optionalFieldOf("path").forGetter(entry -> entry.path)
					)
					.apply(instance, ResourceFilter.BlockEntry::new)
		);
		private final Optional<Pattern> namespace;
		final Predicate<String> namespacePredicate;
		private final Optional<Pattern> path;
		final Predicate<String> pathPredicate;

		private BlockEntry(Optional<Pattern> namespace, Optional<Pattern> path) {
			this.namespace = namespace;
			this.namespacePredicate = (Predicate<String>)namespace.map(Pattern::asPredicate).orElse((Predicate)namespace_ -> true);
			this.path = path;
			this.pathPredicate = (Predicate<String>)path.map(Pattern::asPredicate).orElse((Predicate)path_ -> true);
		}

		public boolean test(Identifier identifier) {
			return this.namespacePredicate.test(identifier.getNamespace()) && this.pathPredicate.test(identifier.getPath());
		}
	}
}
