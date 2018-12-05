package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.entity.EntityType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EntitySummonArgumentType implements ArgumentType<Identifier> {
	private static final Collection<String> EXAMPLES = Arrays.asList("minecraft:pig", "cow");
	public static final DynamicCommandExceptionType NOTFOUND_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("entity.notFound", object)
	);

	public static EntitySummonArgumentType create() {
		return new EntitySummonArgumentType();
	}

	public static Identifier getSummonArgument(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		return validate(commandContext.getArgument(string, Identifier.class));
	}

	private static final Identifier validate(Identifier identifier) throws CommandSyntaxException {
		EntityType<?> entityType = Registry.ENTITY_TYPE.get(identifier);
		if (entityType != null && entityType.isSummonable()) {
			return identifier;
		} else {
			throw NOTFOUND_EXCEPTION.create(identifier);
		}
	}

	public Identifier method_9325(StringReader stringReader) throws CommandSyntaxException {
		return validate(Identifier.parse(stringReader));
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
