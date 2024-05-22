package net.minecraft.command.argument;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.packrat.ArgumentParser;
import net.minecraft.command.argument.packrat.PackratParsing;
import net.minecraft.component.ComponentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.ItemSubPredicate;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class ItemPredicateArgumentType implements ArgumentType<ItemPredicateArgumentType.ItemStackPredicateArgument> {
	private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "#stick", "#stick{foo:'bar'}");
	static final DynamicCommandExceptionType INVALID_ITEM_ID_EXCEPTION = new DynamicCommandExceptionType(
		id -> Text.stringifiedTranslatable("argument.item.id.invalid", id)
	);
	static final DynamicCommandExceptionType UNKNOWN_ITEM_TAG_EXCEPTION = new DynamicCommandExceptionType(
		tag -> Text.stringifiedTranslatable("arguments.item.tag.unknown", tag)
	);
	static final DynamicCommandExceptionType UNKNOWN_ITEM_COMPONENT_EXCEPTION = new DynamicCommandExceptionType(
		component -> Text.stringifiedTranslatable("arguments.item.component.unknown", component)
	);
	static final Dynamic2CommandExceptionType MALFORMED_ITEM_COMPONENT_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> Text.stringifiedTranslatable("arguments.item.component.malformed", object, object2)
	);
	static final DynamicCommandExceptionType UNKNOWN_ITEM_PREDICATE_EXCEPTION = new DynamicCommandExceptionType(
		predicate -> Text.stringifiedTranslatable("arguments.item.predicate.unknown", predicate)
	);
	static final Dynamic2CommandExceptionType MALFORMED_ITEM_PREDICATE_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> Text.stringifiedTranslatable("arguments.item.predicate.malformed", object, object2)
	);
	private static final Identifier COUNT_ID = Identifier.ofVanilla("count");
	static final Map<Identifier, ItemPredicateArgumentType.ComponentCheck> SPECIAL_COMPONENT_CHECKS = (Map<Identifier, ItemPredicateArgumentType.ComponentCheck>)Stream.of(
			new ItemPredicateArgumentType.ComponentCheck(COUNT_ID, stack -> true, NumberRange.IntRange.CODEC.map(range -> stack -> range.test(stack.getCount())))
		)
		.collect(Collectors.toUnmodifiableMap(ItemPredicateArgumentType.ComponentCheck::id, check -> check));
	static final Map<Identifier, ItemPredicateArgumentType.SubPredicateCheck> SPECIAL_SUB_PREDICATE_CHECKS = (Map<Identifier, ItemPredicateArgumentType.SubPredicateCheck>)Stream.of(
			new ItemPredicateArgumentType.SubPredicateCheck(COUNT_ID, NumberRange.IntRange.CODEC.map(range -> stack -> range.test(stack.getCount())))
		)
		.collect(Collectors.toUnmodifiableMap(ItemPredicateArgumentType.SubPredicateCheck::id, check -> check));
	private final ArgumentParser<List<Predicate<ItemStack>>> parser;

	public ItemPredicateArgumentType(CommandRegistryAccess commandRegistryAccess) {
		ItemPredicateArgumentType.Context context = new ItemPredicateArgumentType.Context(commandRegistryAccess);
		this.parser = PackratParsing.createParser(context);
	}

	public static ItemPredicateArgumentType itemPredicate(CommandRegistryAccess commandRegistryAccess) {
		return new ItemPredicateArgumentType(commandRegistryAccess);
	}

	public ItemPredicateArgumentType.ItemStackPredicateArgument parse(StringReader stringReader) throws CommandSyntaxException {
		return Util.allOf(this.parser.parse(stringReader))::test;
	}

	public static ItemPredicateArgumentType.ItemStackPredicateArgument getItemStackPredicate(CommandContext<ServerCommandSource> context, String name) {
		return context.getArgument(name, ItemPredicateArgumentType.ItemStackPredicateArgument.class);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return this.parser.listSuggestions(builder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	static record ComponentCheck(Identifier id, Predicate<ItemStack> presenceChecker, Decoder<? extends Predicate<ItemStack>> valueChecker) {

		public static <T> ItemPredicateArgumentType.ComponentCheck read(ImmutableStringReader reader, Identifier id, ComponentType<T> type) throws CommandSyntaxException {
			Codec<T> codec = type.getCodec();
			if (codec == null) {
				throw ItemPredicateArgumentType.UNKNOWN_ITEM_COMPONENT_EXCEPTION.createWithContext(reader, id);
			} else {
				return new ItemPredicateArgumentType.ComponentCheck(id, stack -> stack.contains(type), codec.map(expected -> stack -> {
						T object2 = stack.get(type);
						return Objects.equals(expected, object2);
					}));
			}
		}

		public Predicate<ItemStack> createPredicate(ImmutableStringReader reader, RegistryOps<NbtElement> ops, NbtElement nbt) throws CommandSyntaxException {
			DataResult<? extends Predicate<ItemStack>> dataResult = this.valueChecker.parse(ops, nbt);
			return (Predicate<ItemStack>)dataResult.getOrThrow(
				error -> ItemPredicateArgumentType.MALFORMED_ITEM_COMPONENT_EXCEPTION.createWithContext(reader, this.id.toString(), error)
			);
		}
	}

	static class Context
		implements PackratParsing.Callbacks<Predicate<ItemStack>, ItemPredicateArgumentType.ComponentCheck, ItemPredicateArgumentType.SubPredicateCheck> {
		private final RegistryWrapper.Impl<Item> itemRegistryWrapper;
		private final RegistryWrapper.Impl<ComponentType<?>> dataComponentTypeRegistryWrapper;
		private final RegistryWrapper.Impl<ItemSubPredicate.Type<?>> itemSubPredicateTypeRegistryWrapper;
		private final RegistryOps<NbtElement> nbtOps;

		Context(RegistryWrapper.WrapperLookup registryLookup) {
			this.itemRegistryWrapper = registryLookup.getWrapperOrThrow(RegistryKeys.ITEM);
			this.dataComponentTypeRegistryWrapper = registryLookup.getWrapperOrThrow(RegistryKeys.DATA_COMPONENT_TYPE);
			this.itemSubPredicateTypeRegistryWrapper = registryLookup.getWrapperOrThrow(RegistryKeys.ITEM_SUB_PREDICATE_TYPE);
			this.nbtOps = registryLookup.getOps(NbtOps.INSTANCE);
		}

		public Predicate<ItemStack> itemMatchPredicate(ImmutableStringReader immutableStringReader, Identifier identifier) throws CommandSyntaxException {
			RegistryEntry.Reference<Item> reference = (RegistryEntry.Reference<Item>)this.itemRegistryWrapper
				.getOptional(RegistryKey.of(RegistryKeys.ITEM, identifier))
				.orElseThrow(() -> ItemPredicateArgumentType.INVALID_ITEM_ID_EXCEPTION.createWithContext(immutableStringReader, identifier));
			return stack -> stack.itemMatches(reference);
		}

		public Predicate<ItemStack> tagMatchPredicate(ImmutableStringReader immutableStringReader, Identifier identifier) throws CommandSyntaxException {
			RegistryEntryList<Item> registryEntryList = (RegistryEntryList<Item>)this.itemRegistryWrapper
				.getOptional(TagKey.of(RegistryKeys.ITEM, identifier))
				.orElseThrow(() -> ItemPredicateArgumentType.UNKNOWN_ITEM_TAG_EXCEPTION.createWithContext(immutableStringReader, identifier));
			return stack -> stack.isIn(registryEntryList);
		}

		public ItemPredicateArgumentType.ComponentCheck componentCheck(ImmutableStringReader immutableStringReader, Identifier identifier) throws CommandSyntaxException {
			ItemPredicateArgumentType.ComponentCheck componentCheck = (ItemPredicateArgumentType.ComponentCheck)ItemPredicateArgumentType.SPECIAL_COMPONENT_CHECKS
				.get(identifier);
			if (componentCheck != null) {
				return componentCheck;
			} else {
				ComponentType<?> componentType = (ComponentType<?>)this.dataComponentTypeRegistryWrapper
					.getOptional(RegistryKey.of(RegistryKeys.DATA_COMPONENT_TYPE, identifier))
					.map(RegistryEntry::value)
					.orElseThrow(() -> ItemPredicateArgumentType.UNKNOWN_ITEM_COMPONENT_EXCEPTION.createWithContext(immutableStringReader, identifier));
				return ItemPredicateArgumentType.ComponentCheck.read(immutableStringReader, identifier, componentType);
			}
		}

		public Predicate<ItemStack> componentMatchPredicate(
			ImmutableStringReader immutableStringReader, ItemPredicateArgumentType.ComponentCheck componentCheck, NbtElement nbtElement
		) throws CommandSyntaxException {
			return componentCheck.createPredicate(immutableStringReader, this.nbtOps, nbtElement);
		}

		public Predicate<ItemStack> componentPresencePredicate(ImmutableStringReader immutableStringReader, ItemPredicateArgumentType.ComponentCheck componentCheck) {
			return componentCheck.presenceChecker;
		}

		public ItemPredicateArgumentType.SubPredicateCheck subPredicateCheck(ImmutableStringReader immutableStringReader, Identifier identifier) throws CommandSyntaxException {
			ItemPredicateArgumentType.SubPredicateCheck subPredicateCheck = (ItemPredicateArgumentType.SubPredicateCheck)ItemPredicateArgumentType.SPECIAL_SUB_PREDICATE_CHECKS
				.get(identifier);
			return subPredicateCheck != null
				? subPredicateCheck
				: (ItemPredicateArgumentType.SubPredicateCheck)this.itemSubPredicateTypeRegistryWrapper
					.getOptional(RegistryKey.of(RegistryKeys.ITEM_SUB_PREDICATE_TYPE, identifier))
					.map(ItemPredicateArgumentType.SubPredicateCheck::new)
					.orElseThrow(() -> ItemPredicateArgumentType.UNKNOWN_ITEM_PREDICATE_EXCEPTION.createWithContext(immutableStringReader, identifier));
		}

		public Predicate<ItemStack> subPredicatePredicate(
			ImmutableStringReader immutableStringReader, ItemPredicateArgumentType.SubPredicateCheck subPredicateCheck, NbtElement nbtElement
		) throws CommandSyntaxException {
			return subPredicateCheck.createPredicate(immutableStringReader, this.nbtOps, nbtElement);
		}

		@Override
		public Stream<Identifier> streamItemIds() {
			return this.itemRegistryWrapper.streamKeys().map(RegistryKey::getValue);
		}

		@Override
		public Stream<Identifier> streamTags() {
			return this.itemRegistryWrapper.streamTagKeys().map(TagKey::id);
		}

		@Override
		public Stream<Identifier> streamComponentIds() {
			return Stream.concat(
				ItemPredicateArgumentType.SPECIAL_COMPONENT_CHECKS.keySet().stream(),
				this.dataComponentTypeRegistryWrapper
					.streamEntries()
					.filter(entry -> !((ComponentType)entry.value()).shouldSkipSerialization())
					.map(entry -> entry.registryKey().getValue())
			);
		}

		@Override
		public Stream<Identifier> streamSubPredicateIds() {
			return Stream.concat(
				ItemPredicateArgumentType.SPECIAL_SUB_PREDICATE_CHECKS.keySet().stream(), this.itemSubPredicateTypeRegistryWrapper.streamKeys().map(RegistryKey::getValue)
			);
		}

		public Predicate<ItemStack> negate(Predicate<ItemStack> predicate) {
			return predicate.negate();
		}

		public Predicate<ItemStack> anyOf(List<Predicate<ItemStack>> list) {
			return Util.anyOf(list);
		}
	}

	public interface ItemStackPredicateArgument extends Predicate<ItemStack> {
	}

	static record SubPredicateCheck(Identifier id, Decoder<? extends Predicate<ItemStack>> type) {
		public SubPredicateCheck(RegistryEntry.Reference<ItemSubPredicate.Type<?>> type) {
			this(type.registryKey().getValue(), type.value().codec().map(predicate -> predicate::test));
		}

		public Predicate<ItemStack> createPredicate(ImmutableStringReader reader, RegistryOps<NbtElement> ops, NbtElement nbt) throws CommandSyntaxException {
			DataResult<? extends Predicate<ItemStack>> dataResult = this.type.parse(ops, nbt);
			return (Predicate<ItemStack>)dataResult.getOrThrow(
				error -> ItemPredicateArgumentType.MALFORMED_ITEM_PREDICATE_EXCEPTION.createWithContext(reader, this.id.toString(), error)
			);
		}
	}
}
