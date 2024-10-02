package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

public class IdentifierArgumentType implements ArgumentType<Identifier> {
	private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");

	public static IdentifierArgumentType identifier() {
		return new IdentifierArgumentType();
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
