package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.state.property.Property;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockPredicateArgumentType implements ArgumentType<BlockPredicateArgumentType.BlockPredicate> {
	private static final Collection<String> EXAMPLES = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "#stone", "#stone[foo=bar]{baz=nbt}");
	static final DynamicCommandExceptionType UNKNOWN_TAG_EXCEPTION = new DynamicCommandExceptionType(id -> new TranslatableText("arguments.block.tag.unknown", id));

	public static BlockPredicateArgumentType blockPredicate() {
		return new BlockPredicateArgumentType();
	}

	public BlockPredicateArgumentType.BlockPredicate parse(StringReader stringReader) throws CommandSyntaxException {
		final BlockArgumentParser blockArgumentParser = new BlockArgumentParser(stringReader, true).parse(true);
		if (blockArgumentParser.getBlockState() != null) {
			final BlockPredicateArgumentType.StatePredicate statePredicate = new BlockPredicateArgumentType.StatePredicate(
				blockArgumentParser.getBlockState(), blockArgumentParser.getBlockProperties().keySet(), blockArgumentParser.getNbtData()
			);
			return new BlockPredicateArgumentType.BlockPredicate() {
				@Override
				public Predicate<CachedBlockPosition> create(TagManager manager) {
					return statePredicate;
				}

				@Override
				public boolean hasNbt() {
					return statePredicate.hasNbt();
				}
			};
		} else {
			final Identifier identifier = blockArgumentParser.getTagId();
			return new BlockPredicateArgumentType.BlockPredicate() {
				@Override
				public Predicate<CachedBlockPosition> create(TagManager manager) throws CommandSyntaxException {
					Tag<Block> tag = manager.getTag(
						Registry.BLOCK_KEY, identifier, identifierx -> BlockPredicateArgumentType.UNKNOWN_TAG_EXCEPTION.create(identifierx.toString())
					);
					return new BlockPredicateArgumentType.TagPredicate(tag, blockArgumentParser.getProperties(), blockArgumentParser.getNbtData());
				}

				@Override
				public boolean hasNbt() {
					return blockArgumentParser.getNbtData() != null;
				}
			};
		}
	}

	public static Predicate<CachedBlockPosition> getBlockPredicate(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		return context.<BlockPredicateArgumentType.BlockPredicate>getArgument(name, BlockPredicateArgumentType.BlockPredicate.class)
			.create(context.getSource().getServer().getTagManager());
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		StringReader stringReader = new StringReader(builder.getInput());
		stringReader.setCursor(builder.getStart());
		BlockArgumentParser blockArgumentParser = new BlockArgumentParser(stringReader, true);

		try {
			blockArgumentParser.parse(true);
		} catch (CommandSyntaxException var6) {
		}

		return blockArgumentParser.getSuggestions(builder, BlockTags.getTagGroup());
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public interface BlockPredicate {
		Predicate<CachedBlockPosition> create(TagManager manager) throws CommandSyntaxException;

		boolean hasNbt();
	}

	static class StatePredicate implements Predicate<CachedBlockPosition> {
		private final BlockState state;
		private final Set<Property<?>> properties;
		@Nullable
		private final NbtCompound nbt;

		public StatePredicate(BlockState state, Set<Property<?>> properties, @Nullable NbtCompound nbt) {
			this.state = state;
			this.properties = properties;
			this.nbt = nbt;
		}

		public boolean test(CachedBlockPosition cachedBlockPosition) {
			BlockState blockState = cachedBlockPosition.getBlockState();
			if (!blockState.isOf(this.state.getBlock())) {
				return false;
			} else {
				for (Property<?> property : this.properties) {
					if (blockState.get(property) != this.state.get(property)) {
						return false;
					}
				}

				if (this.nbt == null) {
					return true;
				} else {
					BlockEntity blockEntity = cachedBlockPosition.getBlockEntity();
					return blockEntity != null && NbtHelper.matches(this.nbt, blockEntity.createNbtWithIdentifyingData(), true);
				}
			}
		}

		public boolean hasNbt() {
			return this.nbt != null;
		}
	}

	static class TagPredicate implements Predicate<CachedBlockPosition> {
		private final Tag<Block> tag;
		@Nullable
		private final NbtCompound nbt;
		private final Map<String, String> properties;

		TagPredicate(Tag<Block> tag, Map<String, String> properties, @Nullable NbtCompound nbt) {
			this.tag = tag;
			this.properties = properties;
			this.nbt = nbt;
		}

		public boolean test(CachedBlockPosition cachedBlockPosition) {
			BlockState blockState = cachedBlockPosition.getBlockState();
			if (!blockState.isIn(this.tag)) {
				return false;
			} else {
				for (Entry<String, String> entry : this.properties.entrySet()) {
					Property<?> property = blockState.getBlock().getStateManager().getProperty((String)entry.getKey());
					if (property == null) {
						return false;
					}

					Comparable<?> comparable = (Comparable<?>)property.parse((String)entry.getValue()).orElse(null);
					if (comparable == null) {
						return false;
					}

					if (blockState.get(property) != comparable) {
						return false;
					}
				}

				if (this.nbt == null) {
					return true;
				} else {
					BlockEntity blockEntity = cachedBlockPosition.getBlockEntity();
					return blockEntity != null && NbtHelper.matches(this.nbt, blockEntity.createNbtWithIdentifyingData(), true);
				}
			}
		}
	}
}
