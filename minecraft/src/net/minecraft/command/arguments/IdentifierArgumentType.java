package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.advancement.Advancement;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class IdentifierArgumentType implements ArgumentType<Identifier> {
	private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
	public static final DynamicCommandExceptionType UNKNOWN_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("argument.id.unknown", object)
	);
	public static final DynamicCommandExceptionType UNKNOWN_ADVANCEMENT_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("advancement.advancementNotFound", object)
	);
	public static final DynamicCommandExceptionType UNKNOWN_RECIPE_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("recipe.notFound", object)
	);

	public static IdentifierArgumentType create() {
		return new IdentifierArgumentType();
	}

	public static Advancement getAdvancementArgument(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		Identifier identifier = commandContext.getArgument(string, Identifier.class);
		Advancement advancement = commandContext.getSource().getMinecraftServer().getAdvancementManager().get(identifier);
		if (advancement == null) {
			throw UNKNOWN_ADVANCEMENT_EXCEPTION.create(identifier);
		} else {
			return advancement;
		}
	}

	public static Recipe<?> getRecipeArgument(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		RecipeManager recipeManager = commandContext.getSource().getMinecraftServer().getRecipeManager();
		Identifier identifier = commandContext.getArgument(string, Identifier.class);
		return (Recipe<?>)recipeManager.get(identifier).orElseThrow(() -> UNKNOWN_RECIPE_EXCEPTION.create(identifier));
	}

	public static Identifier getIdentifier(CommandContext<ServerCommandSource> commandContext, String string) {
		return commandContext.getArgument(string, Identifier.class);
	}

	public Identifier method_9446(StringReader stringReader) throws CommandSyntaxException {
		return Identifier.fromCommandInput(stringReader);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
