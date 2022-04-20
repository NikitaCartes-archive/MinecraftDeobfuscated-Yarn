package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class UuidArgumentType implements ArgumentType<UUID> {
	public static final SimpleCommandExceptionType INVALID_UUID = new SimpleCommandExceptionType(Text.translatable("argument.uuid.invalid"));
	private static final Collection<String> EXAMPLES = Arrays.asList("dd12be42-52a9-4a91-a8a1-11c01849e498");
	private static final Pattern VALID_CHARACTERS = Pattern.compile("^([-A-Fa-f0-9]+)");

	public static UUID getUuid(CommandContext<ServerCommandSource> context, String name) {
		return context.getArgument(name, UUID.class);
	}

	public static UuidArgumentType uuid() {
		return new UuidArgumentType();
	}

	public UUID parse(StringReader stringReader) throws CommandSyntaxException {
		String string = stringReader.getRemaining();
		Matcher matcher = VALID_CHARACTERS.matcher(string);
		if (matcher.find()) {
			String string2 = matcher.group(1);

			try {
				UUID uUID = UUID.fromString(string2);
				stringReader.setCursor(stringReader.getCursor() + string2.length());
				return uUID;
			} catch (IllegalArgumentException var6) {
			}
		}

		throw INVALID_UUID.create();
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
