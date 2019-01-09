package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class class_2306 {
	private static final Map<String, class_2306.class_2308> field_10891 = Maps.<String, class_2306.class_2308>newHashMap();
	public static final DynamicCommandExceptionType field_10885 = new DynamicCommandExceptionType(
		object -> new class_2588("argument.entity.options.unknown", object)
	);
	public static final DynamicCommandExceptionType field_10887 = new DynamicCommandExceptionType(
		object -> new class_2588("argument.entity.options.inapplicable", object)
	);
	public static final SimpleCommandExceptionType field_10890 = new SimpleCommandExceptionType(new class_2588("argument.entity.options.distance.negative"));
	public static final SimpleCommandExceptionType field_10893 = new SimpleCommandExceptionType(new class_2588("argument.entity.options.level.negative"));
	public static final SimpleCommandExceptionType field_10886 = new SimpleCommandExceptionType(new class_2588("argument.entity.options.limit.toosmall"));
	public static final DynamicCommandExceptionType field_10888 = new DynamicCommandExceptionType(
		object -> new class_2588("argument.entity.options.sort.irreversible", object)
	);
	public static final DynamicCommandExceptionType field_10889 = new DynamicCommandExceptionType(
		object -> new class_2588("argument.entity.options.mode.invalid", object)
	);
	public static final DynamicCommandExceptionType field_10892 = new DynamicCommandExceptionType(
		object -> new class_2588("argument.entity.options.type.invalid", object)
	);

	private static void method_9961(String string, class_2306.class_2307 arg, Predicate<class_2303> predicate, class_2561 arg2) {
		field_10891.put(string, new class_2306.class_2308(arg, predicate, arg2));
	}

	public static void method_9960() {
		if (field_10891.isEmpty()) {
			method_9961("name", arg -> {
				int i = arg.method_9835().getCursor();
				boolean bl = arg.method_9892();
				String string = arg.method_9835().readString();
				if (arg.method_9844() && !bl) {
					arg.method_9835().setCursor(i);
					throw field_10887.createWithContext(arg.method_9835(), "name");
				} else {
					if (bl) {
						arg.method_9913(true);
					} else {
						arg.method_9899(true);
					}

					arg.method_9916(argx -> argx.method_5477().method_10851().equals(string) != bl);
				}
			}, arg -> !arg.method_9912(), new class_2588("argument.entity.options.name.description"));
			method_9961("distance", arg -> {
				int i = arg.method_9835().getCursor();
				class_2096.class_2099 lv = class_2096.class_2099.method_9049(arg.method_9835());
				if ((lv.method_9038() == null || !((Float)lv.method_9038() < 0.0F)) && (lv.method_9042() == null || !((Float)lv.method_9042() < 0.0F))) {
					arg.method_9870(lv);
					arg.method_9852();
				} else {
					arg.method_9835().setCursor(i);
					throw field_10890.createWithContext(arg.method_9835());
				}
			}, arg -> arg.method_9873().method_9041(), new class_2588("argument.entity.options.distance.description"));
			method_9961("level", arg -> {
				int i = arg.method_9835().getCursor();
				class_2096.class_2100 lv = class_2096.class_2100.method_9060(arg.method_9835());
				if ((lv.method_9038() == null || (Integer)lv.method_9038() >= 0) && (lv.method_9042() == null || (Integer)lv.method_9042() >= 0)) {
					arg.method_9846(lv);
					arg.method_9841(false);
				} else {
					arg.method_9835().setCursor(i);
					throw field_10893.createWithContext(arg.method_9835());
				}
			}, arg -> arg.method_9895().method_9041(), new class_2588("argument.entity.options.level.description"));
			method_9961("x", arg -> {
				arg.method_9852();
				arg.method_9850(arg.method_9835().readDouble());
			}, arg -> arg.method_9902() == null, new class_2588("argument.entity.options.x.description"));
			method_9961("y", arg -> {
				arg.method_9852();
				arg.method_9864(arg.method_9835().readDouble());
			}, arg -> arg.method_9884() == null, new class_2588("argument.entity.options.y.description"));
			method_9961("z", arg -> {
				arg.method_9852();
				arg.method_9879(arg.method_9835().readDouble());
			}, arg -> arg.method_9868() == null, new class_2588("argument.entity.options.z.description"));
			method_9961("dx", arg -> {
				arg.method_9852();
				arg.method_9891(arg.method_9835().readDouble());
			}, arg -> arg.method_9851() == null, new class_2588("argument.entity.options.dx.description"));
			method_9961("dy", arg -> {
				arg.method_9852();
				arg.method_9905(arg.method_9835().readDouble());
			}, arg -> arg.method_9840() == null, new class_2588("argument.entity.options.dy.description"));
			method_9961("dz", arg -> {
				arg.method_9852();
				arg.method_9918(arg.method_9835().readDouble());
			}, arg -> arg.method_9907() == null, new class_2588("argument.entity.options.dz.description"));
			method_9961(
				"x_rotation",
				arg -> arg.method_9898(class_2152.method_9172(arg.method_9835(), true, class_3532::method_15393)),
				arg -> arg.method_9883() == class_2152.field_9780,
				new class_2588("argument.entity.options.x_rotation.description")
			);
			method_9961(
				"y_rotation",
				arg -> arg.method_9855(class_2152.method_9172(arg.method_9835(), true, class_3532::method_15393)),
				arg -> arg.method_9853() == class_2152.field_9780,
				new class_2588("argument.entity.options.y_rotation.description")
			);
			method_9961("limit", arg -> {
				int i = arg.method_9835().getCursor();
				int j = arg.method_9835().readInt();
				if (j < 1) {
					arg.method_9835().setCursor(i);
					throw field_10886.createWithContext(arg.method_9835());
				} else {
					arg.method_9900(j);
					arg.method_9877(true);
				}
			}, arg -> !arg.method_9885() && !arg.method_9866(), new class_2588("argument.entity.options.limit.description"));
			method_9961("sort", arg -> {
				int i = arg.method_9835().getCursor();
				String string = arg.method_9835().readUnquotedString();
				arg.method_9875((suggestionsBuilder, consumer) -> class_2172.method_9265(Arrays.asList("nearest", "furthest", "random", "arbitrary"), suggestionsBuilder));
				BiConsumer<class_243, List<? extends class_1297>> biConsumer;
				switch (string) {
					case "nearest":
						biConsumer = class_2303.field_10869;
						break;
					case "furthest":
						biConsumer = class_2303.field_10882;
						break;
					case "random":
						biConsumer = class_2303.field_10850;
						break;
					case "arbitrary":
						biConsumer = class_2303.field_10856;
						break;
					default:
						arg.method_9835().setCursor(i);
						throw field_10888.createWithContext(arg.method_9835(), string);
				}

				arg.method_9845(biConsumer);
				arg.method_9887(true);
			}, arg -> !arg.method_9885() && !arg.method_9889(), new class_2588("argument.entity.options.sort.description"));
			method_9961("gamemode", arg -> {
				arg.method_9875((suggestionsBuilder, consumer) -> {
					String stringx = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
					boolean blx = !arg.method_9837();
					boolean bl2 = true;
					if (!stringx.isEmpty()) {
						if (stringx.charAt(0) == '!') {
							blx = false;
							stringx = stringx.substring(1);
						} else {
							bl2 = false;
						}
					}

					for (class_1934 lvx : class_1934.values()) {
						if (lvx != class_1934.field_9218 && lvx.method_8381().toLowerCase(Locale.ROOT).startsWith(stringx)) {
							if (bl2) {
								suggestionsBuilder.suggest('!' + lvx.method_8381());
							}

							if (blx) {
								suggestionsBuilder.suggest(lvx.method_8381());
							}
						}
					}

					return suggestionsBuilder.buildFuture();
				});
				int i = arg.method_9835().getCursor();
				boolean bl = arg.method_9892();
				if (arg.method_9837() && !bl) {
					arg.method_9835().setCursor(i);
					throw field_10887.createWithContext(arg.method_9835(), "gamemode");
				} else {
					String string = arg.method_9835().readUnquotedString();
					class_1934 lv = class_1934.method_8378(string, class_1934.field_9218);
					if (lv == class_1934.field_9218) {
						arg.method_9835().setCursor(i);
						throw field_10889.createWithContext(arg.method_9835(), string);
					} else {
						arg.method_9841(false);
						arg.method_9916(arg2 -> {
							if (!(arg2 instanceof class_3222)) {
								return false;
							} else {
								class_1934 lvx = ((class_3222)arg2).field_13974.method_14257();
								return bl ? lvx != lv : lvx == lv;
							}
						});
						if (bl) {
							arg.method_9857(true);
						} else {
							arg.method_9890(true);
						}
					}
				}
			}, arg -> !arg.method_9839(), new class_2588("argument.entity.options.gamemode.description"));
			method_9961("team", arg -> {
				boolean bl = arg.method_9892();
				String string = arg.method_9835().readUnquotedString();
				arg.method_9916(argx -> {
					if (!(argx instanceof class_1309)) {
						return false;
					} else {
						class_270 lv = argx.method_5781();
						String string2 = lv == null ? "" : lv.method_1197();
						return string2.equals(string) != bl;
					}
				});
				if (bl) {
					arg.method_9833(true);
				} else {
					arg.method_9865(true);
				}
			}, arg -> !arg.method_9904(), new class_2588("argument.entity.options.team.description"));
			method_9961("type", arg -> {
				arg.method_9875((suggestionsBuilder, consumer) -> {
					class_2172.method_9258(class_2378.field_11145.method_10235(), suggestionsBuilder, String.valueOf('!'));
					class_2172.method_9258(class_3483.method_15082().method_15189(), suggestionsBuilder, "!#");
					if (!arg.method_9910()) {
						class_2172.method_9270(class_2378.field_11145.method_10235(), suggestionsBuilder);
						class_2172.method_9258(class_3483.method_15082().method_15189(), suggestionsBuilder, String.valueOf('#'));
					}

					return suggestionsBuilder.buildFuture();
				});
				int i = arg.method_9835().getCursor();
				boolean bl = arg.method_9892();
				if (arg.method_9910() && !bl) {
					arg.method_9835().setCursor(i);
					throw field_10887.createWithContext(arg.method_9835(), "type");
				} else {
					if (bl) {
						arg.method_9860();
					}

					if (arg.method_9915()) {
						class_2960 lv = class_2960.method_12835(arg.method_9835());
						class_3494<class_1299<?>> lv2 = class_3483.method_15082().method_15193(lv);
						if (lv2 == null) {
							arg.method_9835().setCursor(i);
							throw field_10892.createWithContext(arg.method_9835(), lv.toString());
						}

						arg.method_9916(arg2 -> lv2.method_15141(arg2.method_5864()) != bl);
					} else {
						class_2960 lv = class_2960.method_12835(arg.method_9835());
						class_1299<? extends class_1297> lv3 = (class_1299<? extends class_1297>)class_2378.field_11145.method_10223(lv);
						if (lv3 == null) {
							arg.method_9835().setCursor(i);
							throw field_10892.createWithContext(arg.method_9835(), lv.toString());
						}

						if (Objects.equals(class_1299.field_6097, lv3) && !bl) {
							arg.method_9841(false);
						}

						arg.method_9916(arg2 -> Objects.equals(lv3, arg2.method_5864()) != bl);
						if (!bl) {
							arg.method_9842(lv3.method_5891());
						}
					}
				}
			}, arg -> !arg.method_9886(), new class_2588("argument.entity.options.type.description"));
			method_9961("tag", arg -> {
				boolean bl = arg.method_9892();
				String string = arg.method_9835().readUnquotedString();
				arg.method_9916(argx -> "".equals(string) ? argx.method_5752().isEmpty() != bl : argx.method_5752().contains(string) != bl);
			}, arg -> true, new class_2588("argument.entity.options.tag.description"));
			method_9961("nbt", arg -> {
				boolean bl = arg.method_9892();
				class_2487 lv = new class_2522(arg.method_9835()).method_10727();
				arg.method_9916(arg2 -> {
					class_2487 lvx = arg2.method_5647(new class_2487());
					if (arg2 instanceof class_3222) {
						class_1799 lv2 = ((class_3222)arg2).field_7514.method_7391();
						if (!lv2.method_7960()) {
							lvx.method_10566("SelectedItem", lv2.method_7953(new class_2487()));
						}
					}

					return class_2512.method_10687(lv, lvx, true) != bl;
				});
			}, arg -> true, new class_2588("argument.entity.options.nbt.description"));
			method_9961("scores", arg -> {
				StringReader stringReader = arg.method_9835();
				Map<String, class_2096.class_2100> map = Maps.<String, class_2096.class_2100>newHashMap();
				stringReader.expect('{');
				stringReader.skipWhitespace();

				while (stringReader.canRead() && stringReader.peek() != '}') {
					stringReader.skipWhitespace();
					String string = stringReader.readUnquotedString();
					stringReader.skipWhitespace();
					stringReader.expect('=');
					stringReader.skipWhitespace();
					class_2096.class_2100 lv = class_2096.class_2100.method_9060(stringReader);
					map.put(string, lv);
					stringReader.skipWhitespace();
					if (stringReader.canRead() && stringReader.peek() == ',') {
						stringReader.skip();
					}
				}

				stringReader.expect('}');
				if (!map.isEmpty()) {
					arg.method_9916(argx -> {
						class_269 lvx = argx.method_5682().method_3845();
						String stringx = argx.method_5820();

						for (Entry<String, class_2096.class_2100> entry : map.entrySet()) {
							class_266 lv2 = lvx.method_1170((String)entry.getKey());
							if (lv2 == null) {
								return false;
							}

							if (!lvx.method_1183(stringx, lv2)) {
								return false;
							}

							class_267 lv3 = lvx.method_1180(stringx, lv2);
							int i = lv3.method_1126();
							if (!((class_2096.class_2100)entry.getValue()).method_9054(i)) {
								return false;
							}
						}

						return true;
					});
				}

				arg.method_9848(true);
			}, arg -> !arg.method_9843(), new class_2588("argument.entity.options.scores.description"));
			method_9961("advancements", arg -> {
				StringReader stringReader = arg.method_9835();
				Map<class_2960, Predicate<class_167>> map = Maps.<class_2960, Predicate<class_167>>newHashMap();
				stringReader.expect('{');
				stringReader.skipWhitespace();

				while (stringReader.canRead() && stringReader.peek() != '}') {
					stringReader.skipWhitespace();
					class_2960 lv = class_2960.method_12835(stringReader);
					stringReader.skipWhitespace();
					stringReader.expect('=');
					stringReader.skipWhitespace();
					if (stringReader.canRead() && stringReader.peek() == '{') {
						Map<String, Predicate<class_178>> map2 = Maps.<String, Predicate<class_178>>newHashMap();
						stringReader.skipWhitespace();
						stringReader.expect('{');
						stringReader.skipWhitespace();

						while (stringReader.canRead() && stringReader.peek() != '}') {
							stringReader.skipWhitespace();
							String string = stringReader.readUnquotedString();
							stringReader.skipWhitespace();
							stringReader.expect('=');
							stringReader.skipWhitespace();
							boolean bl = stringReader.readBoolean();
							map2.put(string, (Predicate)argx -> argx.method_784() == bl);
							stringReader.skipWhitespace();
							if (stringReader.canRead() && stringReader.peek() == ',') {
								stringReader.skip();
							}
						}

						stringReader.skipWhitespace();
						stringReader.expect('}');
						stringReader.skipWhitespace();
						map.put(lv, (Predicate)argx -> {
							for (Entry<String, Predicate<class_178>> entry : map2.entrySet()) {
								class_178 lvx = argx.method_737((String)entry.getKey());
								if (lvx == null || !((Predicate)entry.getValue()).test(lvx)) {
									return false;
								}
							}

							return true;
						});
					} else {
						boolean bl2 = stringReader.readBoolean();
						map.put(lv, (Predicate)argx -> argx.method_740() == bl2);
					}

					stringReader.skipWhitespace();
					if (stringReader.canRead() && stringReader.peek() == ',') {
						stringReader.skip();
					}
				}

				stringReader.expect('}');
				if (!map.isEmpty()) {
					arg.method_9916(argx -> {
						if (!(argx instanceof class_3222)) {
							return false;
						} else {
							class_3222 lvx = (class_3222)argx;
							class_2985 lv2 = lvx.method_14236();
							class_2989 lv3 = lvx.method_5682().method_3851();

							for (Entry<class_2960, Predicate<class_167>> entry : map.entrySet()) {
								class_161 lv4 = lv3.method_12896((class_2960)entry.getKey());
								if (lv4 == null || !((Predicate)entry.getValue()).test(lv2.method_12882(lv4))) {
									return false;
								}
							}

							return true;
						}
					});
					arg.method_9841(false);
				}

				arg.method_9906(true);
			}, arg -> !arg.method_9861(), new class_2588("argument.entity.options.advancements.description"));
		}
	}

	public static class_2306.class_2307 method_9976(class_2303 arg, String string, int i) throws CommandSyntaxException {
		class_2306.class_2308 lv = (class_2306.class_2308)field_10891.get(string);
		if (lv != null) {
			if (lv.field_10896.test(arg)) {
				return lv.field_10895;
			} else {
				throw field_10887.createWithContext(arg.method_9835(), string);
			}
		} else {
			arg.method_9835().setCursor(i);
			throw field_10885.createWithContext(arg.method_9835(), string);
		}
	}

	public static void method_9930(class_2303 arg, SuggestionsBuilder suggestionsBuilder) {
		String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);

		for (Entry<String, class_2306.class_2308> entry : field_10891.entrySet()) {
			if (((class_2306.class_2308)entry.getValue()).field_10896.test(arg) && ((String)entry.getKey()).toLowerCase(Locale.ROOT).startsWith(string)) {
				suggestionsBuilder.suggest((String)entry.getKey() + '=', ((class_2306.class_2308)entry.getValue()).field_10894);
			}
		}
	}

	public interface class_2307 {
		void handle(class_2303 arg) throws CommandSyntaxException;
	}

	static class class_2308 {
		public final class_2306.class_2307 field_10895;
		public final Predicate<class_2303> field_10896;
		public final class_2561 field_10894;

		private class_2308(class_2306.class_2307 arg, Predicate<class_2303> predicate, class_2561 arg2) {
			this.field_10895 = arg;
			this.field_10896 = predicate;
			this.field_10894 = arg2;
		}
	}
}
