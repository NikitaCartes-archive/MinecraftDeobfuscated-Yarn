package net.minecraft;

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
import net.minecraft.text.TranslatableText;

public class class_5242 implements ArgumentType<UUID> {
	public static final SimpleCommandExceptionType field_24318 = new SimpleCommandExceptionType(new TranslatableText("argument.uuid.invalid"));
	private static final Collection<String> field_24319 = Arrays.asList("dd12be42-52a9-4a91-a8a1-11c01849e498");
	private static final Pattern field_24320 = Pattern.compile("^([-A-Fa-f0-9]+)");

	public static UUID method_27645(CommandContext<ServerCommandSource> commandContext, String string) {
		return commandContext.getArgument(string, UUID.class);
	}

	public static class_5242 method_27643() {
		return new class_5242();
	}

	public UUID parse(StringReader stringReader) throws CommandSyntaxException {
		String string = stringReader.getRemaining();
		Matcher matcher = field_24320.matcher(string);
		if (matcher.find()) {
			String string2 = matcher.group(1);

			try {
				UUID uUID = UUID.fromString(string2);
				stringReader.setCursor(stringReader.getCursor() + string2.length());
				return uUID;
			} catch (IllegalArgumentException var6) {
			}
		}

		throw field_24318.create();
	}

	@Override
	public Collection<String> getExamples() {
		return field_24319;
	}
}
