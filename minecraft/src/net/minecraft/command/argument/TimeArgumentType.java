package net.minecraft.command.argument;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

public class TimeArgumentType implements ArgumentType<Integer> {
	private static final Collection<String> EXAMPLES = Arrays.asList("0d", "0s", "0t", "0");
	private static final SimpleCommandExceptionType INVALID_UNIT_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.time.invalid_unit"));
	private static final Dynamic2CommandExceptionType TICK_COUNT_TOO_LOW_EXCEPTION = new Dynamic2CommandExceptionType(
		(value, minimum) -> Text.translatable("argument.time.tick_count_too_low", minimum, value)
	);
	private static final Object2IntMap<String> UNITS = new Object2IntOpenHashMap<>();
	final int minimum;

	private TimeArgumentType(int minimum) {
		this.minimum = minimum;
	}

	public static TimeArgumentType time() {
		return new TimeArgumentType(0);
	}

	public static TimeArgumentType time(int minimum) {
		return new TimeArgumentType(minimum);
	}

	public Integer parse(StringReader stringReader) throws CommandSyntaxException {
		float f = stringReader.readFloat();
		String string = stringReader.readUnquotedString();
		int i = UNITS.getOrDefault(string, 0);
		if (i == 0) {
			throw INVALID_UNIT_EXCEPTION.create();
		} else {
			int j = Math.round(f * (float)i);
			if (j < this.minimum) {
				throw TICK_COUNT_TOO_LOW_EXCEPTION.create(j, this.minimum);
			} else {
				return j;
			}
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		StringReader stringReader = new StringReader(builder.getRemaining());

		try {
			stringReader.readFloat();
		} catch (CommandSyntaxException var5) {
			return builder.buildFuture();
		}

		return CommandSource.suggestMatching(UNITS.keySet(), builder.createOffset(builder.getStart() + stringReader.getCursor()));
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	static {
		UNITS.put("d", 24000);
		UNITS.put("s", 20);
		UNITS.put("t", 1);
		UNITS.put("", 1);
	}

	public static class Serializer implements ArgumentSerializer<TimeArgumentType, TimeArgumentType.Serializer.Properties> {
		public void writePacket(TimeArgumentType.Serializer.Properties properties, PacketByteBuf packetByteBuf) {
			packetByteBuf.writeInt(properties.minimum);
		}

		public TimeArgumentType.Serializer.Properties fromPacket(PacketByteBuf packetByteBuf) {
			int i = packetByteBuf.readInt();
			return new TimeArgumentType.Serializer.Properties(i);
		}

		public void writeJson(TimeArgumentType.Serializer.Properties properties, JsonObject jsonObject) {
			jsonObject.addProperty("min", properties.minimum);
		}

		public TimeArgumentType.Serializer.Properties getArgumentTypeProperties(TimeArgumentType timeArgumentType) {
			return new TimeArgumentType.Serializer.Properties(timeArgumentType.minimum);
		}

		public final class Properties implements ArgumentSerializer.ArgumentTypeProperties<TimeArgumentType> {
			final int minimum;

			Properties(int minimum) {
				this.minimum = minimum;
			}

			public TimeArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
				return TimeArgumentType.time(this.minimum);
			}

			@Override
			public ArgumentSerializer<TimeArgumentType, ?> getSerializer() {
				return Serializer.this;
			}
		}
	}
}
