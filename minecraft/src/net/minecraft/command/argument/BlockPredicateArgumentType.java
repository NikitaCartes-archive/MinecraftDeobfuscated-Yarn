package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.state.property.Property;

public class BlockPredicateArgumentType implements ArgumentType<BlockPredicateArgumentType.BlockPredicate> {
	private static final Collection<String> EXAMPLES = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "#stone", "#stone[foo=bar]{baz=nbt}");
	private final RegistryWrapper<Block> registryWrapper;

	public BlockPredicateArgumentType(CommandRegistryAccess commandRegistryAccess) {
		this.registryWrapper = commandRegistryAccess.getOrThrow(RegistryKeys.BLOCK);
	}

	public static BlockPredicateArgumentType blockPredicate(CommandRegistryAccess commandRegistryAccess) {
		return new BlockPredicateArgumentType(commandRegistryAccess);
	}

	public BlockPredicateArgumentType.BlockPredicate parse(StringReader stringReader) throws CommandSyntaxException {
		return parse(this.registryWrapper, stringReader);
	}

	public static BlockPredicateArgumentType.BlockPredicate parse(RegistryWrapper<Block> registryWrapper, StringReader reader) throws CommandSyntaxException {
		return BlockArgumentParser.blockOrTag(registryWrapper, reader, true)
			.map(
				result -> new BlockPredicateArgumentType.StatePredicate(result.blockState(), result.properties().keySet(), result.nbt()),
				result -> new BlockPredicateArgumentType.TagPredicate(result.tag(), result.vagueProperties(), result.nbt())
			);
	}

	public static Predicate<CachedBlockPosition> getBlockPredicate(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		return context.getArgument(name, BlockPredicateArgumentType.BlockPredicate.class);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return BlockArgumentParser.getSuggestions(this.registryWrapper, builder, true, true);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public interface BlockPredicate extends Predicate<CachedBlockPosition> {
		boolean hasNbt();
	}

	static class StatePredicate implements BlockPredicateArgumentType.BlockPredicate {
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
					return blockEntity != null
						&& NbtHelper.matches(this.nbt, blockEntity.createNbtWithIdentifyingData(cachedBlockPosition.getWorld().getRegistryManager()), true);
				}
			}
		}

		@Override
		public boolean hasNbt() {
			return this.nbt != null;
		}
	}

	static class TagPredicate implements BlockPredicateArgumentType.BlockPredicate {
		private final RegistryEntryList<Block> tag;
		@Nullable
		private final NbtCompound nbt;
		private final Map<String, String> properties;

		TagPredicate(RegistryEntryList<Block> tag, Map<String, String> properties, @Nullable NbtCompound nbt) {
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
					return blockEntity != null
						&& NbtHelper.matches(this.nbt, blockEntity.createNbtWithIdentifyingData(cachedBlockPosition.getWorld().getRegistryManager()), true);
				}
			}
		}

		@Override
		public boolean hasNbt() {
			return this.nbt != null;
		}
	}
}
