package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.advancement.Advancement;
import net.minecraft.loot.LootDataType;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class IdentifierArgumentType implements ArgumentType<Identifier> {
	private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
	private static final DynamicCommandExceptionType UNKNOWN_ADVANCEMENT_EXCEPTION = new DynamicCommandExceptionType(
		id -> Text.translatable("advancement.advancementNotFound", id)
	);
	private static final DynamicCommandExceptionType UNKNOWN_RECIPE_EXCEPTION = new DynamicCommandExceptionType(id -> Text.translatable("recipe.notFound", id));
	private static final DynamicCommandExceptionType UNKNOWN_PREDICATE_EXCEPTION = new DynamicCommandExceptionType(
		id -> Text.translatable("predicate.unknown", id)
	);
	private static final DynamicCommandExceptionType UNKNOWN_ITEM_MODIFIER_EXCEPTION = new DynamicCommandExceptionType(
		id -> Text.translatable("item_modifier.unknown", id)
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
		LootManager lootManager = context.getSource().getServer().getLootManager();
		LootCondition lootCondition = lootManager.getElement(LootDataType.PREDICATES, identifier);
		if (lootCondition == null) {
			throw UNKNOWN_PREDICATE_EXCEPTION.create(identifier);
		} else {
			return lootCondition;
		}
	}

	public static LootFunction getItemModifierArgument(CommandContext<ServerCommandSource> context, String argumentName) throws CommandSyntaxException {
		Identifier identifier = getIdentifier(context, argumentName);
		LootManager lootManager = context.getSource().getServer().getLootManager();
		LootFunction lootFunction = lootManager.getElement(LootDataType.ITEM_MODIFIERS, identifier);
		if (lootFunction == null) {
			throw UNKNOWN_ITEM_MODIFIER_EXCEPTION.create(identifier);
		} else {
			return lootFunction;
		}
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
