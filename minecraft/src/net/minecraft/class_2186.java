package net.minecraft;

import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class class_2186 implements ArgumentType<class_2300> {
	private static final Collection<String> field_9859 = Arrays.asList("Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498");
	public static final SimpleCommandExceptionType field_9860 = new SimpleCommandExceptionType(new class_2588("argument.entity.toomany"));
	public static final SimpleCommandExceptionType field_9864 = new SimpleCommandExceptionType(new class_2588("argument.player.toomany"));
	public static final SimpleCommandExceptionType field_9861 = new SimpleCommandExceptionType(new class_2588("argument.player.entities"));
	public static final SimpleCommandExceptionType field_9863 = new SimpleCommandExceptionType(new class_2588("argument.entity.notfound.entity"));
	public static final SimpleCommandExceptionType field_9856 = new SimpleCommandExceptionType(new class_2588("argument.entity.notfound.player"));
	public static final SimpleCommandExceptionType field_9862 = new SimpleCommandExceptionType(new class_2588("argument.entity.selector.not_allowed"));
	private final boolean field_9858;
	private final boolean field_9857;

	protected class_2186(boolean bl, boolean bl2) {
		this.field_9858 = bl;
		this.field_9857 = bl2;
	}

	public static class_2186 method_9309() {
		return new class_2186(true, false);
	}

	public static class_1297 method_9313(CommandContext<class_2168> commandContext, String string) throws CommandSyntaxException {
		return commandContext.<class_2300>getArgument(string, class_2300.class).method_9809(commandContext.getSource());
	}

	public static class_2186 method_9306() {
		return new class_2186(false, false);
	}

	public static Collection<? extends class_1297> method_9317(CommandContext<class_2168> commandContext, String string) throws CommandSyntaxException {
		Collection<? extends class_1297> collection = method_9307(commandContext, string);
		if (collection.isEmpty()) {
			throw field_9863.create();
		} else {
			return collection;
		}
	}

	public static Collection<? extends class_1297> method_9307(CommandContext<class_2168> commandContext, String string) throws CommandSyntaxException {
		return commandContext.<class_2300>getArgument(string, class_2300.class).method_9816(commandContext.getSource());
	}

	public static Collection<class_3222> method_9310(CommandContext<class_2168> commandContext, String string) throws CommandSyntaxException {
		return commandContext.<class_2300>getArgument(string, class_2300.class).method_9813(commandContext.getSource());
	}

	public static class_2186 method_9305() {
		return new class_2186(true, true);
	}

	public static class_3222 method_9315(CommandContext<class_2168> commandContext, String string) throws CommandSyntaxException {
		return commandContext.<class_2300>getArgument(string, class_2300.class).method_9811(commandContext.getSource());
	}

	public static class_2186 method_9308() {
		return new class_2186(false, true);
	}

	public static Collection<class_3222> method_9312(CommandContext<class_2168> commandContext, String string) throws CommandSyntaxException {
		List<class_3222> list = commandContext.<class_2300>getArgument(string, class_2300.class).method_9813(commandContext.getSource());
		if (list.isEmpty()) {
			throw field_9856.create();
		} else {
			return list;
		}
	}

	public class_2300 method_9318(StringReader stringReader) throws CommandSyntaxException {
		int i = 0;
		class_2303 lv = new class_2303(stringReader);
		class_2300 lv2 = lv.method_9882();
		if (lv2.method_9815() > 1 && this.field_9858) {
			if (this.field_9857) {
				stringReader.setCursor(0);
				throw field_9864.createWithContext(stringReader);
			} else {
				stringReader.setCursor(0);
				throw field_9860.createWithContext(stringReader);
			}
		} else if (lv2.method_9819() && this.field_9857 && !lv2.method_9820()) {
			stringReader.setCursor(0);
			throw field_9861.createWithContext(stringReader);
		} else {
			return lv2;
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		if (commandContext.getSource() instanceof class_2172) {
			StringReader stringReader = new StringReader(suggestionsBuilder.getInput());
			stringReader.setCursor(suggestionsBuilder.getStart());
			class_2172 lv = (class_2172)commandContext.getSource();
			class_2303 lv2 = new class_2303(stringReader, lv.method_9259(2));

			try {
				lv2.method_9882();
			} catch (CommandSyntaxException var7) {
			}

			return lv2.method_9908(suggestionsBuilder, suggestionsBuilderx -> {
				Collection<String> collection = lv.method_9262();
				Iterable<String> iterable = (Iterable<String>)(this.field_9857 ? collection : Iterables.concat(collection, lv.method_9269()));
				class_2172.method_9265(iterable, suggestionsBuilderx);
			});
		} else {
			return Suggestions.empty();
		}
	}

	@Override
	public Collection<String> getExamples() {
		return field_9859;
	}

	public static class class_2187 implements class_2314<class_2186> {
		public void method_9320(class_2186 arg, class_2540 arg2) {
			byte b = 0;
			if (arg.field_9858) {
				b = (byte)(b | 1);
			}

			if (arg.field_9857) {
				b = (byte)(b | 2);
			}

			arg2.writeByte(b);
		}

		public class_2186 method_9321(class_2540 arg) {
			byte b = arg.readByte();
			return new class_2186((b & 1) != 0, (b & 2) != 0);
		}

		public void method_9319(class_2186 arg, JsonObject jsonObject) {
			jsonObject.addProperty("amount", arg.field_9858 ? "single" : "multiple");
			jsonObject.addProperty("type", arg.field_9857 ? "players" : "entities");
		}
	}
}
