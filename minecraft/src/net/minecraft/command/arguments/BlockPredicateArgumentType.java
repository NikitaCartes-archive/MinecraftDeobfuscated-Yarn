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
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.state.property.Property;
import net.minecraft.tag.RegistryTagManager;
import net.minecraft.tag.Tag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class BlockPredicateArgumentType implements ArgumentType<BlockPredicateArgumentType.BlockPredicate> {
	private static final Collection<String> EXAMPLES = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "#stone", "#stone[foo=bar]{baz=nbt}");
	private static final DynamicCommandExceptionType UNKNOWN_TAG_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("arguments.block.tag.unknown", object)
	);

	public static BlockPredicateArgumentType blockPredicate() {
		return new BlockPredicateArgumentType();
	}

	public BlockPredicateArgumentType.BlockPredicate method_9642(StringReader stringReader) throws CommandSyntaxException {
		BlockArgumentParser blockArgumentParser = new BlockArgumentParser(stringReader, true).parse(true);
		if (blockArgumentParser.getBlockState() != null) {
			BlockPredicateArgumentType.StatePredicate statePredicate = new BlockPredicateArgumentType.StatePredicate(
				blockArgumentParser.getBlockState(), blockArgumentParser.getBlockProperties().keySet(), blockArgumentParser.getNbtData()
			);
			return registryTagManager -> statePredicate;
		} else {
			Identifier identifier = blockArgumentParser.getTagId();
			return registryTagManager -> {
				Tag<Block> tag = registryTagManager.blocks().get(identifier);
				if (tag == null) {
					throw UNKNOWN_TAG_EXCEPTION.create(identifier.toString());
				} else {
					return new BlockPredicateArgumentType.TagPredicate(tag, blockArgumentParser.getProperties(), blockArgumentParser.getNbtData());
				}
			};
		}
	}

	public static Predicate<CachedBlockPosition> getBlockPredicate(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		return context.<BlockPredicateArgumentType.BlockPredicate>getArgument(name, BlockPredicateArgumentType.BlockPredicate.class)
			.create(context.getSource().getMinecraftServer().getTagManager());
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

		return blockArgumentParser.getSuggestions(builder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public interface BlockPredicate {
		Predicate<CachedBlockPosition> create(RegistryTagManager registryTagManager) throws CommandSyntaxException;
	}

	static class StatePredicate implements Predicate<CachedBlockPosition> {
		private final BlockState state;
		private final Set<Property<?>> properties;
		@Nullable
		private final CompoundTag nbt;

		public StatePredicate(BlockState state, Set<Property<?>> properties, @Nullable CompoundTag nbt) {
			this.state = state;
			this.properties = properties;
			this.nbt = nbt;
		}

		public boolean method_9648(CachedBlockPosition pos) {
			BlockState blockState = pos.getBlockState();
			if (blockState.getBlock() != this.state.getBlock()) {
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
					BlockEntity blockEntity = pos.getBlockEntity();
					return blockEntity != null && NbtHelper.matches(this.nbt, blockEntity.toTag(new CompoundTag()), true);
				}
			}
		}
	}

	static class TagPredicate implements Predicate<CachedBlockPosition> {
		private final Tag<Block> tag;
		@Nullable
		private final CompoundTag nbt;
		private final Map<String, String> properties;

		private TagPredicate(Tag<Block> tag, Map<String, String> map, @Nullable CompoundTag nbt) {
			this.tag = tag;
			this.properties = map;
			this.nbt = nbt;
		}

		public boolean method_9649(CachedBlockPosition cachedBlockPosition) {
			BlockState blockState = cachedBlockPosition.getBlockState();
			if (!blockState.matches(this.tag)) {
				return false;
			} else {
				for (Entry<String, String> entry : this.properties.entrySet()) {
					Property<?> property = blockState.getBlock().getStateFactory().getProperty((String)entry.getKey());
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
					return blockEntity != null && NbtHelper.matches(this.nbt, blockEntity.toTag(new CompoundTag()), true);
				}
			}
		}
	}
}
