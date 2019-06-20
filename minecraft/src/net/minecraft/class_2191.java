package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class class_2191 implements ArgumentType<class_2191.class_2192> {
	private static final Collection<String> field_9868 = Arrays.asList("Player", "0123", "dd12be42-52a9-4a91-a8a1-11c01849e498", "@e");
	public static final SimpleCommandExceptionType field_9869 = new SimpleCommandExceptionType(new class_2588("argument.player.unknown"));

	public static Collection<GameProfile> method_9330(CommandContext<class_2168> commandContext, String string) throws CommandSyntaxException {
		return commandContext.<class_2191.class_2192>getArgument(string, class_2191.class_2192.class).getNames(commandContext.getSource());
	}

	public static class_2191 method_9329() {
		return new class_2191();
	}

	public class_2191.class_2192 method_9331(StringReader stringReader) throws CommandSyntaxException {
		if (stringReader.canRead() && stringReader.peek() == '@') {
			class_2303 lv = new class_2303(stringReader);
			class_2300 lv2 = lv.method_9882();
			if (lv2.method_9819()) {
				throw class_2186.field_9861.create();
			} else {
				return new class_2191.class_2193(lv2);
			}
		} else {
			int i = stringReader.getCursor();

			while (stringReader.canRead() && stringReader.peek() != ' ') {
				stringReader.skip();
			}

			String string = stringReader.getString().substring(i, stringReader.getCursor());
			return arg -> {
				GameProfile gameProfile = arg.method_9211().method_3793().method_14515(string);
				if (gameProfile == null) {
					throw field_9869.create();
				} else {
					return Collections.singleton(gameProfile);
				}
			};
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		if (commandContext.getSource() instanceof class_2172) {
			StringReader stringReader = new StringReader(suggestionsBuilder.getInput());
			stringReader.setCursor(suggestionsBuilder.getStart());
			class_2303 lv = new class_2303(stringReader);

			try {
				lv.method_9882();
			} catch (CommandSyntaxException var6) {
			}

			return lv.method_9908(
				suggestionsBuilder, suggestionsBuilderx -> class_2172.method_9265(((class_2172)commandContext.getSource()).method_9262(), suggestionsBuilderx)
			);
		} else {
			return Suggestions.empty();
		}
	}

	@Override
	public Collection<String> getExamples() {
		return field_9868;
	}

	@FunctionalInterface
	public interface class_2192 {
		Collection<GameProfile> getNames(class_2168 arg) throws CommandSyntaxException;
	}

	public static class class_2193 implements class_2191.class_2192 {
		private final class_2300 field_9870;

		public class_2193(class_2300 arg) {
			this.field_9870 = arg;
		}

		@Override
		public Collection<GameProfile> getNames(class_2168 arg) throws CommandSyntaxException {
			List<class_3222> list = this.field_9870.method_9813(arg);
			if (list.isEmpty()) {
				throw class_2186.field_9856.create();
			} else {
				List<GameProfile> list2 = Lists.<GameProfile>newArrayList();

				for (class_3222 lv : list) {
					list2.add(lv.method_7334());
				}

				return list2;
			}
		}
	}
}
