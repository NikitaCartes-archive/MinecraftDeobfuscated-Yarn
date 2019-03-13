package net.minecraft.command.arguments;

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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.state.property.Property;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagManager;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.TagHelper;

public class BlockPredicateArgumentType implements ArgumentType<BlockPredicateArgumentType.BlockPredicateFactory> {
	private static final Collection<String> EXAMPLES = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "#stone", "#stone[foo=bar]{baz=nbt}");
	private static final DynamicCommandExceptionType UNKNOWN_TAG_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("arguments.block.tag.unknown", object)
	);

	public static BlockPredicateArgumentType create() {
		return new BlockPredicateArgumentType();
	}

	public BlockPredicateArgumentType.BlockPredicateFactory method_9642(StringReader stringReader) throws CommandSyntaxException {
		BlockArgumentParser blockArgumentParser = new BlockArgumentParser(stringReader, true).parse(true);
		if (blockArgumentParser.getBlockState() != null) {
			BlockPredicateArgumentType.BlockStatePredicate blockStatePredicate = new BlockPredicateArgumentType.BlockStatePredicate(
				blockArgumentParser.getBlockState(), blockArgumentParser.method_9692().keySet(), blockArgumentParser.method_9694()
			);
			return tagManager -> blockStatePredicate;
		} else {
			Identifier identifier = blockArgumentParser.method_9664();
			return tagManager -> {
				Tag<Block> tag = tagManager.blocks().get(identifier);
				if (tag == null) {
					throw UNKNOWN_TAG_EXCEPTION.create(identifier.toString());
				} else {
					return new BlockPredicateArgumentType.TagPredicate(tag, blockArgumentParser.getProperties(), blockArgumentParser.method_9694());
				}
			};
		}
	}

	public static Predicate<CachedBlockPosition> getPredicateArgument(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		return commandContext.<BlockPredicateArgumentType.BlockPredicateFactory>getArgument(string, BlockPredicateArgumentType.BlockPredicateFactory.class)
			.create(commandContext.getSource().getMinecraftServer().method_3801());
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		StringReader stringReader = new StringReader(suggestionsBuilder.getInput());
		stringReader.setCursor(suggestionsBuilder.getStart());
		BlockArgumentParser blockArgumentParser = new BlockArgumentParser(stringReader, true);

		try {
			blockArgumentParser.parse(true);
		} catch (CommandSyntaxException var6) {
		}

		return blockArgumentParser.getSuggestions(suggestionsBuilder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public interface BlockPredicateFactory {
		Predicate<CachedBlockPosition> create(TagManager tagManager) throws CommandSyntaxException;
	}

	static class BlockStatePredicate implements Predicate<CachedBlockPosition> {
		private final BlockState state;
		private final Set<Property<?>> properties;
		@Nullable
		private final CompoundTag field_10675;

		public BlockStatePredicate(BlockState blockState, Set<Property<?>> set, @Nullable CompoundTag compoundTag) {
			this.state = blockState;
			this.properties = set;
			this.field_10675 = compoundTag;
		}

		public boolean method_9648(CachedBlockPosition cachedBlockPosition) {
			BlockState blockState = cachedBlockPosition.getBlockState();
			if (blockState.getBlock() != this.state.getBlock()) {
				return false;
			} else {
				for (Property<?> property : this.properties) {
					if (blockState.method_11654(property) != this.state.method_11654(property)) {
						return false;
					}
				}

				if (this.field_10675 == null) {
					return true;
				} else {
					BlockEntity blockEntity = cachedBlockPosition.getBlockEntity();
					return blockEntity != null && TagHelper.method_10687(this.field_10675, blockEntity.method_11007(new CompoundTag()), true);
				}
			}
		}
	}

	static class TagPredicate implements Predicate<CachedBlockPosition> {
		private final Tag<Block> field_10676;
		@Nullable
		private final CompoundTag field_10677;
		private final Map<String, String> properties;

		private TagPredicate(Tag<Block> tag, Map<String, String> map, @Nullable CompoundTag compoundTag) {
			this.field_10676 = tag;
			this.properties = map;
			this.field_10677 = compoundTag;
		}

		public boolean method_9649(CachedBlockPosition cachedBlockPosition) {
			BlockState blockState = cachedBlockPosition.getBlockState();
			if (!blockState.method_11602(this.field_10676)) {
				return false;
			} else {
				for (Entry<String, String> entry : this.properties.entrySet()) {
					Property<?> property = blockState.getBlock().method_9595().method_11663((String)entry.getKey());
					if (property == null) {
						return false;
					}

					Comparable<?> comparable = (Comparable<?>)property.getValue((String)entry.getValue()).orElse(null);
					if (comparable == null) {
						return false;
					}

					if (blockState.method_11654(property) != comparable) {
						return false;
					}
				}

				if (this.field_10677 == null) {
					return true;
				} else {
					BlockEntity blockEntity = cachedBlockPosition.getBlockEntity();
					return blockEntity != null && TagHelper.method_10687(this.field_10677, blockEntity.method_11007(new CompoundTag()), true);
				}
			}
		}
	}
}
