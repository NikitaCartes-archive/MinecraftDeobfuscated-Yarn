package net.minecraft.resource.metadata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

public class BlockEntry {
	public static final Codec<BlockEntry> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.REGULAR_EXPRESSION.optionalFieldOf("namespace").forGetter(entry -> entry.namespace),
					Codecs.REGULAR_EXPRESSION.optionalFieldOf("path").forGetter(entry -> entry.path)
				)
				.apply(instance, BlockEntry::new)
	);
	private final Optional<Pattern> namespace;
	private final Predicate<String> namespacePredicate;
	private final Optional<Pattern> path;
	private final Predicate<String> pathPredicate;
	private final Predicate<Identifier> identifierPredicate;

	private BlockEntry(Optional<Pattern> namespace, Optional<Pattern> path) {
		this.namespace = namespace;
		this.namespacePredicate = (Predicate)namespace.map(Pattern::asPredicate).orElse((Predicate)namespace_ -> true);
		this.path = path;
		this.pathPredicate = (Predicate)path.map(Pattern::asPredicate).orElse((Predicate)path_ -> true);
		this.identifierPredicate = id -> this.namespacePredicate.test(id.getNamespace()) && this.pathPredicate.test(id.getPath());
	}

	public Predicate<String> getNamespacePredicate() {
		return this.namespacePredicate;
	}

	public Predicate<String> getPathPredicate() {
		return this.pathPredicate;
	}

	public Predicate<Identifier> getIdentifierPredicate() {
		return this.identifierPredicate;
	}
}
