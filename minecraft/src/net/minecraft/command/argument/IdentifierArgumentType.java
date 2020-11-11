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

public class IdentifierArgumentType implements ArgumentType<Identifier> {
	private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
	private static final DynamicCommandExceptionType UNKNOWN_ADVANCEMENT_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("advancement.advancementNotFound", object)
	);
	private static final DynamicCommandExceptionType UNKNOWN_RECIPE_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("recipe.notFound", object)
	);
	private static final DynamicCommandExceptionType UNKNOWN_PREDICATE_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("predicate.unknown", object)
	);
	private static final DynamicCommandExceptionType UNKNOWN_ATTRIBUTE_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("attribute.unknown", object)
	);
	private static final DynamicCommandExceptionType UNKNOWN_ITEM_MODIFIER_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("item_modifier.unknown", object)
	);

	public static IdentifierArgumentType identifier() {
		return new IdentifierArgumentType();
	}

	public static Advancement getAdvancementArgument(CommandContext<ServerCommandSource> context, String argumentName) throws CommandSyntaxException {
		Identifier identifier = context.getArgument(argumentName, Identifier.class);
		Advancement advancement = context.getSource().getMinecraftServer().getAdvancementLoader().get(identifier);
		if (advancement == null) {
			throw UNKNOWN_ADVANCEMENT_EXCEPTION.create(identifier);
		} else {
			return advancement;
		}
	}

	public static Recipe<?> getRecipeArgument(CommandContext<ServerCommandSource> context, String argumentName) throws CommandSyntaxException {
		RecipeManager recipeManager = context.getSource().getMinecraftServer().getRecipeManager();
		Identifier identifier = context.getArgument(argumentName, Identifier.class);
		return (Recipe<?>)recipeManager.get(identifier).orElseThrow(() -> UNKNOWN_RECIPE_EXCEPTION.create(identifier));
	}

	public static LootCondition getPredicateArgument(CommandContext<ServerCommandSource> context, String argumentName) throws CommandSyntaxException {
		Identifier identifier = context.getArgument(argumentName, Identifier.class);
		LootConditionManager lootConditionManager = context.getSource().getMinecraftServer().getPredicateManager();
		LootCondition lootCondition = lootConditionManager.get(identifier);
		if (lootCondition == null) {
			throw UNKNOWN_PREDICATE_EXCEPTION.create(identifier);
		} else {
			return lootCondition;
		}
	}

	public static LootFunction getItemModifierArgument(CommandContext<ServerCommandSource> context, String argumentName) throws CommandSyntaxException {
		Identifier identifier = context.getArgument(argumentName, Identifier.class);
		LootFunctionManager lootFunctionManager = context.getSource().getMinecraftServer().getItemModifierManager();
		LootFunction lootFunction = lootFunctionManager.get(identifier);
		if (lootFunction == null) {
			throw UNKNOWN_ITEM_MODIFIER_EXCEPTION.create(identifier);
		} else {
			return lootFunction;
		}
	}

	public static EntityAttribute getAttributeArgument(CommandContext<ServerCommandSource> context, String argumentName) throws CommandSyntaxException {
		Identifier identifier = context.getArgument(argumentName, Identifier.class);
		return (EntityAttribute)Registry.ATTRIBUTE.getOrEmpty(identifier).orElseThrow(() -> UNKNOWN_ATTRIBUTE_EXCEPTION.create(identifier));
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
}
