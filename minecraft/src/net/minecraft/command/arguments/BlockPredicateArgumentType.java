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
import net.minecraft.class_2259;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.BlockProxy;
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
		class_2259 lv = new class_2259(stringReader, true).method_9678(true);
		if (lv.method_9669() != null) {
			BlockPredicateArgumentType.BlockStatePredicate blockStatePredicate = new BlockPredicateArgumentType.BlockStatePredicate(
				lv.method_9669(), lv.method_9692().keySet(), lv.method_9694()
			);
			return tagManager -> blockStatePredicate;
		} else {
			Identifier identifier = lv.method_9664();
			return tagManager -> {
				Tag<Block> tag = tagManager.blocks().get(identifier);
				if (tag == null) {
					throw UNKNOWN_TAG_EXCEPTION.create(identifier.toString());
				} else {
					return new BlockPredicateArgumentType.TagPredicate(tag, lv.method_9688(), lv.method_9694());
				}
			};
		}
	}

	public static Predicate<BlockProxy> getPredicateArgument(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		return commandContext.<BlockPredicateArgumentType.BlockPredicateFactory>getArgument(string, BlockPredicateArgumentType.BlockPredicateFactory.class)
			.create(commandContext.getSource().getMinecraftServer().getTagManager());
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		StringReader stringReader = new StringReader(suggestionsBuilder.getInput());
		stringReader.setCursor(suggestionsBuilder.getStart());
		class_2259 lv = new class_2259(stringReader, true);

		try {
			lv.method_9678(true);
		} catch (CommandSyntaxException var6) {
		}

		return lv.method_9666(suggestionsBuilder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public interface BlockPredicateFactory {
		Predicate<BlockProxy> create(TagManager tagManager) throws CommandSyntaxException;
	}

	static class BlockStatePredicate implements Predicate<BlockProxy> {
		private final BlockState state;
		private final Set<Property<?>> properties;
		@Nullable
		private final CompoundTag compound;

		public BlockStatePredicate(BlockState blockState, Set<Property<?>> set, @Nullable CompoundTag compoundTag) {
			this.state = blockState;
			this.properties = set;
			this.compound = compoundTag;
		}

		public boolean method_9648(BlockProxy blockProxy) {
			BlockState blockState = blockProxy.getBlockState();
			if (blockState.getBlock() != this.state.getBlock()) {
				return false;
			} else {
				for (Property<?> property : this.properties) {
					if (blockState.get(property) != this.state.get(property)) {
						return false;
					}
				}

				if (this.compound == null) {
					return true;
				} else {
					BlockEntity blockEntity = blockProxy.getBlockEntity();
					return blockEntity != null && TagHelper.areTagsEqual(this.compound, blockEntity.toTag(new CompoundTag()), true);
				}
			}
		}
	}

	static class TagPredicate implements Predicate<BlockProxy> {
		private final Tag<Block> tag;
		@Nullable
		private final CompoundTag compound;
		private final Map<String, String> properties;

		private TagPredicate(Tag<Block> tag, Map<String, String> map, @Nullable CompoundTag compoundTag) {
			this.tag = tag;
			this.properties = map;
			this.compound = compoundTag;
		}

		public boolean method_9649(BlockProxy blockProxy) {
			BlockState blockState = blockProxy.getBlockState();
			if (!blockState.matches(this.tag)) {
				return false;
			} else {
				for (Entry<String, String> entry : this.properties.entrySet()) {
					Property<?> property = blockState.getBlock().getStateFactory().getProperty((String)entry.getKey());
					if (property == null) {
						return false;
					}

					Comparable<?> comparable = (Comparable<?>)property.getValue((String)entry.getValue()).orElse(null);
					if (comparable == null) {
						return false;
					}

					if (blockState.get(property) != comparable) {
						return false;
					}
				}

				if (this.compound == null) {
					return true;
				} else {
					BlockEntity blockEntity = blockProxy.getBlockEntity();
					return blockEntity != null && TagHelper.areTagsEqual(this.compound, blockEntity.toTag(new CompoundTag()), true);
				}
			}
		}
	}
}
