package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;

public class ResourceLocationArgumentType implements ArgumentType<Identifier> {
	private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
	public static final DynamicCommandExceptionType UNKNOWN_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("argument.id.unknown", object)
	);
	public static final DynamicCommandExceptionType UNKNOWN_ADVANCEMENT_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("advancement.advancementNotFound", object)
	);
	public static final DynamicCommandExceptionType UNKNOWN_RECIPE_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("recipe.notFound", object)
	);

	public static ResourceLocationArgumentType create() {
		return new ResourceLocationArgumentType();
	}

	public static SimpleAdvancement getAdvancementArgument(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		Identifier identifier = commandContext.getArgument(string, Identifier.class);
		SimpleAdvancement simpleAdvancement = commandContext.getSource().getMinecraftServer().getAdvancementManager().get(identifier);
		if (simpleAdvancement == null) {
			throw UNKNOWN_ADVANCEMENT_EXCEPTION.create(identifier);
		} else {
			return simpleAdvancement;
		}
	}

	public static Recipe getRecipeArgument(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		Identifier identifier = commandContext.getArgument(string, Identifier.class);
		Recipe recipe = commandContext.getSource().getMinecraftServer().getRecipeManager().get(identifier);
		if (recipe == null) {
			throw UNKNOWN_RECIPE_EXCEPTION.create(identifier);
		} else {
			return recipe;
		}
	}

	public static Identifier getIdentifierArgument(CommandContext<ServerCommandSource> commandContext, String string) {
		return commandContext.getArgument(string, Identifier.class);
	}

	public Identifier method_9446(StringReader stringReader) throws CommandSyntaxException {
		return Identifier.parse(stringReader);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
