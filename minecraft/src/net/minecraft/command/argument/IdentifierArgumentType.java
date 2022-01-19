package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.advancement.Advancement;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionManager;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.StructureFeature;

public class IdentifierArgumentType implements ArgumentType<Identifier> {
	private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
	private static final DynamicCommandExceptionType UNKNOWN_ADVANCEMENT_EXCEPTION = new DynamicCommandExceptionType(
		id -> new TranslatableText("advancement.advancementNotFound", id)
	);
	private static final DynamicCommandExceptionType UNKNOWN_RECIPE_EXCEPTION = new DynamicCommandExceptionType(id -> new TranslatableText("recipe.notFound", id));
	private static final DynamicCommandExceptionType UNKNOWN_PREDICATE_EXCEPTION = new DynamicCommandExceptionType(
		id -> new TranslatableText("predicate.unknown", id)
	);
	private static final DynamicCommandExceptionType UNKNOWN_ATTRIBUTE_EXCEPTION = new DynamicCommandExceptionType(
		id -> new TranslatableText("attribute.unknown", id)
	);
	private static final DynamicCommandExceptionType UNKNOWN_ITEM_MODIFIER_EXCEPTION = new DynamicCommandExceptionType(
		id -> new TranslatableText("item_modifier.unknown", id)
	);
	private static final DynamicCommandExceptionType UNKNOWN_BIOME_EXCEPTION = new DynamicCommandExceptionType(
		id -> new TranslatableText("commands.locatebiome.invalid", id)
	);
	private static final DynamicCommandExceptionType UNKNOWN_FEATURE_EXCEPTION = new DynamicCommandExceptionType(
		id -> new TranslatableText("commands.placefeature.invalid", id)
	);
	private static final DynamicCommandExceptionType UNKNOWN_STRUCTURE_EXCEPTION = new DynamicCommandExceptionType(
		id -> new TranslatableText("commands.locate.invalid", id)
	);

	public static IdentifierArgumentType identifier() {
		return new IdentifierArgumentType();
	}

	public static Advancement getAdvancementArgument(CommandContext<ServerCommandSource> context, String argumentName) throws CommandSyntaxException {
		Identifier identifier = getIdentifier(context, argumentName);
		Advancement advancement = context.getSource().getServer().getAdvancementLoader().get(identifier);
		if (advancement == null) {
			throw UNKNOWN_ADVANCEMENT_EXCEPTION.create(identifier);
		} else {
			return advancement;
		}
	}

	public static Recipe<?> getRecipeArgument(CommandContext<ServerCommandSource> context, String argumentName) throws CommandSyntaxException {
		RecipeManager recipeManager = context.getSource().getServer().getRecipeManager();
		Identifier identifier = getIdentifier(context, argumentName);
		return (Recipe<?>)recipeManager.get(identifier).orElseThrow(() -> UNKNOWN_RECIPE_EXCEPTION.create(identifier));
	}

	public static LootCondition getPredicateArgument(CommandContext<ServerCommandSource> context, String argumentName) throws CommandSyntaxException {
		Identifier identifier = getIdentifier(context, argumentName);
		LootConditionManager lootConditionManager = context.getSource().getServer().getPredicateManager();
		LootCondition lootCondition = lootConditionManager.get(identifier);
		if (lootCondition == null) {
			throw UNKNOWN_PREDICATE_EXCEPTION.create(identifier);
		} else {
			return lootCondition;
		}
	}

	public static LootFunction getItemModifierArgument(CommandContext<ServerCommandSource> context, String argumentName) throws CommandSyntaxException {
		Identifier identifier = getIdentifier(context, argumentName);
		LootFunctionManager lootFunctionManager = context.getSource().getServer().getItemModifierManager();
		LootFunction lootFunction = lootFunctionManager.get(identifier);
		if (lootFunction == null) {
			throw UNKNOWN_ITEM_MODIFIER_EXCEPTION.create(identifier);
		} else {
			return lootFunction;
		}
	}

	public static EntityAttribute getAttributeArgument(CommandContext<ServerCommandSource> context, String argumentName) throws CommandSyntaxException {
		Identifier identifier = getIdentifier(context, argumentName);
		return (EntityAttribute)Registry.ATTRIBUTE.getOrEmpty(identifier).orElseThrow(() -> UNKNOWN_ATTRIBUTE_EXCEPTION.create(identifier));
	}

	private static <T> IdentifierArgumentType.RegistryEntry<T> getFromRegistry(
		CommandContext<ServerCommandSource> context, String argumentName, RegistryKey<Registry<T>> registryRef, DynamicCommandExceptionType exceptionType
	) throws CommandSyntaxException {
		Identifier identifier = getIdentifier(context, argumentName);
		T object = (T)context.getSource()
			.getServer()
			.getRegistryManager()
			.get(registryRef)
			.getOrEmpty(identifier)
			.orElseThrow(() -> exceptionType.create(identifier));
		return new IdentifierArgumentType.RegistryEntry<>(identifier, object);
	}

	public static IdentifierArgumentType.RegistryEntry<Biome> getBiomeEntry(CommandContext<ServerCommandSource> context, String argumentName) throws CommandSyntaxException {
		return getFromRegistry(context, argumentName, Registry.BIOME_KEY, UNKNOWN_BIOME_EXCEPTION);
	}

	public static IdentifierArgumentType.RegistryEntry<ConfiguredFeature<?, ?>> getConfiguredFeatureEntry(
		CommandContext<ServerCommandSource> context, String argumentName
	) throws CommandSyntaxException {
		return getFromRegistry(context, argumentName, Registry.CONFIGURED_FEATURE_KEY, UNKNOWN_FEATURE_EXCEPTION);
	}

	public static IdentifierArgumentType.RegistryEntry<StructureFeature<?>> getStructureFeatureEntry(
		CommandContext<ServerCommandSource> context, String argumentName
	) throws CommandSyntaxException {
		return getFromRegistry(context, argumentName, Registry.STRUCTURE_FEATURE_KEY, UNKNOWN_STRUCTURE_EXCEPTION);
	}

	public static Identifier getIdentifier(CommandContext<ServerCommandSource> context, String name) {
		return context.getArgument(name, Identifier.class);
	}

	public Identifier parse(StringReader stringReader) throws CommandSyntaxException {
		return Identifier.fromCommandInput(stringReader);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public static record RegistryEntry<T>(Identifier id, T resource) {
	}
}
