package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class class_2252 implements ArgumentType<class_2252.class_2254> {
	private static final Collection<String> field_10672 = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "#stone", "#stone[foo=bar]{baz=nbt}");
	private static final DynamicCommandExceptionType field_10671 = new DynamicCommandExceptionType(object -> new class_2588("arguments.block.tag.unknown", object));

	public static class_2252 method_9645() {
		return new class_2252();
	}

	public class_2252.class_2254 method_9642(StringReader stringReader) throws CommandSyntaxException {
		class_2259 lv = new class_2259(stringReader, true).method_9678(true);
		if (lv.method_9669() != null) {
			class_2252.class_2253 lv2 = new class_2252.class_2253(lv.method_9669(), lv.method_9692().keySet(), lv.method_9694());
			return arg2 -> lv2;
		} else {
			class_2960 lv3 = lv.method_9664();
			return arg3 -> {
				class_3494<class_2248> lvx = arg3.method_15202().method_15193(lv3);
				if (lvx == null) {
					throw field_10671.create(lv3.toString());
				} else {
					return new class_2252.class_2255(lvx, lv.method_9688(), lv.method_9694());
				}
			};
		}
	}

	public static Predicate<class_2694> method_9644(CommandContext<class_2168> commandContext, String string) throws CommandSyntaxException {
		return commandContext.<class_2252.class_2254>getArgument(string, class_2252.class_2254.class).create(commandContext.getSource().method_9211().method_3801());
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		StringReader stringReader = new StringReader(suggestionsBuilder.getInput());
		stringReader.setCursor(suggestionsBuilder.getStart());
		class_2259 lv = new class_2259(stringReader, true);

		try {
			lv.method_9678(true);
		} catch (CommandSyntaxException var6) {
		}

		return lv.method_9666(suggestionsBuilder);
	}

	@Override
	public Collection<String> getExamples() {
		return field_10672;
	}

	static class class_2253 implements Predicate<class_2694> {
		private final class_2680 field_10674;
		private final Set<class_2769<?>> field_10673;
		@Nullable
		private final class_2487 field_10675;

		public class_2253(class_2680 arg, Set<class_2769<?>> set, @Nullable class_2487 arg2) {
			this.field_10674 = arg;
			this.field_10673 = set;
			this.field_10675 = arg2;
		}

		public boolean method_9648(class_2694 arg) {
			class_2680 lv = arg.method_11681();
			if (lv.method_11614() != this.field_10674.method_11614()) {
				return false;
			} else {
				for (class_2769<?> lv2 : this.field_10673) {
					if (lv.method_11654(lv2) != this.field_10674.method_11654(lv2)) {
						return false;
					}
				}

				if (this.field_10675 == null) {
					return true;
				} else {
					class_2586 lv3 = arg.method_11680();
					return lv3 != null && class_2512.method_10687(this.field_10675, lv3.method_11007(new class_2487()), true);
				}
			}
		}
	}

	public interface class_2254 {
		Predicate<class_2694> create(class_3505 arg) throws CommandSyntaxException;
	}

	static class class_2255 implements Predicate<class_2694> {
		private final class_3494<class_2248> field_10676;
		@Nullable
		private final class_2487 field_10677;
		private final Map<String, String> field_10678;

		private class_2255(class_3494<class_2248> arg, Map<String, String> map, @Nullable class_2487 arg2) {
			this.field_10676 = arg;
			this.field_10678 = map;
			this.field_10677 = arg2;
		}

		public boolean method_9649(class_2694 arg) {
			class_2680 lv = arg.method_11681();
			if (!lv.method_11602(this.field_10676)) {
				return false;
			} else {
				for (Entry<String, String> entry : this.field_10678.entrySet()) {
					class_2769<?> lv2 = lv.method_11614().method_9595().method_11663((String)entry.getKey());
					if (lv2 == null) {
						return false;
					}

					Comparable<?> comparable = (Comparable<?>)lv2.method_11900((String)entry.getValue()).orElse(null);
					if (comparable == null) {
						return false;
					}

					if (lv.method_11654(lv2) != comparable) {
						return false;
					}
				}

				if (this.field_10677 == null) {
					return true;
				} else {
					class_2586 lv3 = arg.method_11680();
					return lv3 != null && class_2512.method_10687(this.field_10677, lv3.method_11007(new class_2487()), true);
				}
			}
		}
	}
}
