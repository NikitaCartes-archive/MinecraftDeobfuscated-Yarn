package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.entity.EntityType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EntitySummonArgumentType implements ArgumentType<Identifier> {
	private static final Collection<String> EXAMPLES = Arrays.asList("minecraft:pig", "cow");
	public static final DynamicCommandExceptionType NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("entity.notFound", object)
	);

	public static EntitySummonArgumentType entitySummon() {
		return new EntitySummonArgumentType();
	}

	public static Identifier getEntitySummon(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		return validate(context.getArgument(name, Identifier.class));
	}

	private static Identifier validate(Identifier identifier) throws CommandSyntaxException {
		Registry.ENTITY_TYPE.getOrEmpty(identifier).filter(EntityType::isSummonable).orElseThrow(() -> NOT_FOUND_EXCEPTION.create(identifier));
		return identifier;
	}

	public Identifier method_9325(StringReader stringReader) throws CommandSyntaxException {
		return validate(Identifier.fromCommandInput(stringReader));
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
