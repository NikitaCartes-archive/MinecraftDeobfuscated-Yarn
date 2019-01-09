package net.minecraft;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3232 extends class_2888 {
	private static final Logger field_14064 = LogManager.getLogger();
	private static final class_2975<?> field_14075 = class_1959.method_8699(
		class_3031.field_13547, new class_3101(0.004, class_3098.class_3100.field_13692), class_3284.field_14250, class_2998.field_13436
	);
	private static final class_2975<?> field_14079 = class_1959.method_8699(
		class_3031.field_13587, new class_3812("village/plains/town_centers", 6), class_3284.field_14250, class_2998.field_13436
	);
	private static final class_2975<?> field_14063 = class_1959.method_8699(
		class_3031.field_13565, class_3037.field_13603, class_3284.field_14250, class_2998.field_13436
	);
	private static final class_2975<?> field_14067 = class_1959.method_8699(
		class_3031.field_13520, class_3037.field_13603, class_3284.field_14250, class_2998.field_13436
	);
	private static final class_2975<?> field_14061 = class_1959.method_8699(
		class_3031.field_13515, class_3037.field_13603, class_3284.field_14250, class_2998.field_13436
	);
	private static final class_2975<?> field_14078 = class_1959.method_8699(
		class_3031.field_13586, class_3037.field_13603, class_3284.field_14250, class_2998.field_13436
	);
	private static final class_2975<?> field_14070 = class_1959.method_8699(
		class_3031.field_13527, class_3037.field_13603, class_3284.field_14250, class_2998.field_13436
	);
	private static final class_2975<?> field_14062 = class_1959.method_8699(
		class_3031.field_13589, new class_3172(false), class_3284.field_14250, class_2998.field_13436
	);
	private static final class_2975<?> field_14076 = class_1959.method_8699(
		class_3031.field_13588, class_3037.field_13603, class_3284.field_14250, class_2998.field_13436
	);
	private static final class_2975<?> field_14071 = class_1959.method_8699(
		class_3031.field_13573, new class_3087(class_2246.field_10382.method_9564()), class_3284.field_14242, new class_3297(4)
	);
	private static final class_2975<?> field_14066 = class_1959.method_8699(
		class_3031.field_13573, new class_3087(class_2246.field_10164.method_9564()), class_3284.field_14237, new class_3297(80)
	);
	private static final class_2975<?> field_14084 = class_1959.method_8699(
		class_3031.field_13553, class_3037.field_13603, class_3284.field_14250, class_2998.field_13436
	);
	private static final class_2975<?> field_14068 = class_1959.method_8699(
		class_3031.field_13528, class_3037.field_13603, class_3284.field_14250, class_2998.field_13436
	);
	private static final class_2975<?> field_14065 = class_1959.method_8699(
		class_3031.field_13569, class_3037.field_13603, class_3284.field_14250, class_2998.field_13436
	);
	private static final class_2975<?> field_14085 = class_1959.method_8699(
		class_3031.field_13536, new class_3114(class_3411.class_3413.field_14528, 0.3F, 0.1F), class_3284.field_14250, class_2998.field_13436
	);
	public static final Map<class_2975<?>, class_2893.class_2895> field_14069 = class_156.method_654(
		Maps.<class_2975<?>, class_2893.class_2895>newHashMap(), hashMap -> {
			hashMap.put(field_14075, class_2893.class_2895.field_13172);
			hashMap.put(field_14079, class_2893.class_2895.field_13173);
			hashMap.put(field_14063, class_2893.class_2895.field_13172);
			hashMap.put(field_14067, class_2893.class_2895.field_13173);
			hashMap.put(field_14061, class_2893.class_2895.field_13173);
			hashMap.put(field_14078, class_2893.class_2895.field_13173);
			hashMap.put(field_14070, class_2893.class_2895.field_13173);
			hashMap.put(field_14062, class_2893.class_2895.field_13173);
			hashMap.put(field_14085, class_2893.class_2895.field_13173);
			hashMap.put(field_14071, class_2893.class_2895.field_13171);
			hashMap.put(field_14066, class_2893.class_2895.field_13171);
			hashMap.put(field_14084, class_2893.class_2895.field_13173);
			hashMap.put(field_14068, class_2893.class_2895.field_13173);
			hashMap.put(field_14065, class_2893.class_2895.field_13172);
			hashMap.put(field_14076, class_2893.class_2895.field_13173);
		}
	);
	public static final Map<String, class_2975<?>[]> field_14073 = class_156.method_654(Maps.<String, class_2975<?>[]>newHashMap(), hashMap -> {
		hashMap.put("mineshaft", new class_2975[]{field_14075});
		hashMap.put("village", new class_2975[]{field_14079});
		hashMap.put("stronghold", new class_2975[]{field_14063});
		hashMap.put("biome_1", new class_2975[]{field_14067, field_14061, field_14078, field_14070, field_14085, field_14062});
		hashMap.put("oceanmonument", new class_2975[]{field_14076});
		hashMap.put("lake", new class_2975[]{field_14071});
		hashMap.put("lava_lake", new class_2975[]{field_14066});
		hashMap.put("endcity", new class_2975[]{field_14084});
		hashMap.put("mansion", new class_2975[]{field_14068});
		hashMap.put("fortress", new class_2975[]{field_14065});
	});
	public static final Map<class_2975<?>, class_3037> field_14080 = class_156.method_654(Maps.<class_2975<?>, class_3037>newHashMap(), hashMap -> {
		hashMap.put(field_14075, new class_3101(0.004, class_3098.class_3100.field_13692));
		hashMap.put(field_14079, new class_3812("village/plains/town_centers", 6));
		hashMap.put(field_14063, class_3037.field_13603);
		hashMap.put(field_14067, class_3037.field_13603);
		hashMap.put(field_14061, class_3037.field_13603);
		hashMap.put(field_14078, class_3037.field_13603);
		hashMap.put(field_14070, class_3037.field_13603);
		hashMap.put(field_14085, new class_3114(class_3411.class_3413.field_14528, 0.3F, 0.9F));
		hashMap.put(field_14062, new class_3172(false));
		hashMap.put(field_14076, class_3037.field_13603);
		hashMap.put(field_14084, class_3037.field_13603);
		hashMap.put(field_14068, class_3037.field_13603);
		hashMap.put(field_14065, class_3037.field_13603);
	});
	private final List<class_3229> field_14072 = Lists.<class_3229>newArrayList();
	private final Map<String, Map<String, String>> field_14074 = Maps.<String, Map<String, String>>newHashMap();
	private class_1959 field_14081;
	private final class_2680[] field_14082 = new class_2680[256];
	private boolean field_14077;
	private int field_14083;

	@Nullable
	public static class_2248 method_14334(String string) {
		try {
			class_2960 lv = new class_2960(string);
			if (class_2378.field_11146.method_10250(lv)) {
				return class_2378.field_11146.method_10223(lv);
			}
		} catch (IllegalArgumentException var2) {
			field_14064.warn("Invalid blockstate: {}", string, var2);
		}

		return null;
	}

	public class_1959 method_14326() {
		return this.field_14081;
	}

	public void method_14325(class_1959 arg) {
		this.field_14081 = arg;
	}

	public Map<String, Map<String, String>> method_14333() {
		return this.field_14074;
	}

	public List<class_3229> method_14327() {
		return this.field_14072;
	}

	public void method_14330() {
		int i = 0;

		for (class_3229 lv : this.field_14072) {
			lv.method_14287(i);
			i += lv.method_14289();
		}

		this.field_14083 = 0;
		this.field_14077 = true;
		i = 0;

		for (class_3229 lv : this.field_14072) {
			for (int j = lv.method_14288(); j < lv.method_14288() + lv.method_14289(); j++) {
				class_2680 lv2 = lv.method_14286();
				if (lv2.method_11614() != class_2246.field_10124) {
					this.field_14077 = false;
					this.field_14082[j] = lv2;
				}
			}

			if (lv.method_14286().method_11614() == class_2246.field_10124) {
				i += lv.method_14289();
			} else {
				this.field_14083 = this.field_14083 + lv.method_14289() + i;
				i = 0;
			}
		}
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < this.field_14072.size(); i++) {
			if (i > 0) {
				stringBuilder.append(",");
			}

			stringBuilder.append(this.field_14072.get(i));
		}

		stringBuilder.append(";");
		stringBuilder.append(class_2378.field_11153.method_10221(this.field_14081));
		stringBuilder.append(";");
		if (!this.field_14074.isEmpty()) {
			int i = 0;

			for (Entry<String, Map<String, String>> entry : this.field_14074.entrySet()) {
				if (i++ > 0) {
					stringBuilder.append(",");
				}

				stringBuilder.append(((String)entry.getKey()).toLowerCase(Locale.ROOT));
				Map<String, String> map = (Map<String, String>)entry.getValue();
				if (!map.isEmpty()) {
					stringBuilder.append("(");
					int j = 0;

					for (Entry<String, String> entry2 : map.entrySet()) {
						if (j++ > 0) {
							stringBuilder.append(" ");
						}

						stringBuilder.append((String)entry2.getKey());
						stringBuilder.append("=");
						stringBuilder.append((String)entry2.getValue());
					}

					stringBuilder.append(")");
				}
			}
		}

		return stringBuilder.toString();
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	private static class_3229 method_14315(String string, int i) {
		String[] strings = string.split("\\*", 2);
		int j;
		if (strings.length == 2) {
			try {
				j = class_3532.method_15340(Integer.parseInt(strings[0]), 0, 256 - i);
			} catch (NumberFormatException var7) {
				field_14064.error("Error while parsing flat world string => {}", var7.getMessage());
				return null;
			}
		} else {
			j = 1;
		}

		class_2248 lv;
		try {
			lv = method_14334(strings[strings.length - 1]);
		} catch (Exception var6) {
			field_14064.error("Error while parsing flat world string => {}", var6.getMessage());
			return null;
		}

		if (lv == null) {
			field_14064.error("Error while parsing flat world string => Unknown block, {}", strings[strings.length - 1]);
			return null;
		} else {
			class_3229 lv2 = new class_3229(j, lv);
			lv2.method_14287(i);
			return lv2;
		}
	}

	@Environment(EnvType.CLIENT)
	private static List<class_3229> method_14328(String string) {
		List<class_3229> list = Lists.<class_3229>newArrayList();
		String[] strings = string.split(",");
		int i = 0;

		for (String string2 : strings) {
			class_3229 lv = method_14315(string2, i);
			if (lv == null) {
				return Collections.emptyList();
			}

			list.add(lv);
			i += lv.method_14289();
		}

		return list;
	}

	@Environment(EnvType.CLIENT)
	public <T> Dynamic<T> method_14313(DynamicOps<T> dynamicOps) {
		T object = dynamicOps.createList(
			this.field_14072
				.stream()
				.map(
					arg -> dynamicOps.createMap(
							ImmutableMap.of(
								dynamicOps.createString("height"),
								dynamicOps.createInt(arg.method_14289()),
								dynamicOps.createString("block"),
								dynamicOps.createString(class_2378.field_11146.method_10221(arg.method_14286().method_11614()).toString())
							)
						)
				)
		);
		T object2 = dynamicOps.createMap(
			(Map<T, T>)this.field_14074
				.entrySet()
				.stream()
				.map(
					entry -> Pair.of(
							dynamicOps.createString(((String)entry.getKey()).toLowerCase(Locale.ROOT)),
							dynamicOps.createMap(
								(Map<T, T>)((Map)entry.getValue())
									.entrySet()
									.stream()
									.map(entryx -> Pair.of(dynamicOps.createString((String)entryx.getKey()), dynamicOps.createString((String)entryx.getValue())))
									.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))
							)
						)
				)
				.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))
		);
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("layers"),
					object,
					dynamicOps.createString("biome"),
					dynamicOps.createString(class_2378.field_11153.method_10221(this.field_14081).toString()),
					dynamicOps.createString("structures"),
					object2
				)
			)
		);
	}

	public static class_3232 method_14323(Dynamic<?> dynamic) {
		class_3232 lv = class_2798.field_12766.method_12117();
		List<Pair<Integer, class_2248>> list = dynamic.get("layers")
			.asList(dynamicx -> Pair.of(dynamicx.get("height").asInt(1), method_14334(dynamicx.get("block").asString(""))));
		if (list.stream().anyMatch(pair -> pair.getSecond() == null)) {
			return method_14309();
		} else {
			List<class_3229> list2 = (List<class_3229>)list.stream()
				.map(pair -> new class_3229((Integer)pair.getFirst(), (class_2248)pair.getSecond()))
				.collect(Collectors.toList());
			if (list2.isEmpty()) {
				return method_14309();
			} else {
				lv.method_14327().addAll(list2);
				lv.method_14330();
				lv.method_14325(class_2378.field_11153.method_10223(new class_2960(dynamic.get("biome").asString(""))));
				dynamic.get("structures")
					.flatMap(Dynamic::getMapValues)
					.ifPresent(map -> map.keySet().forEach(dynamicx -> dynamicx.asString().map(string -> (Map)lv.method_14333().put(string, Maps.newHashMap()))));
				return lv;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class_3232 method_14319(String string) {
		Iterator<String> iterator = Splitter.on(';').split(string).iterator();
		if (!iterator.hasNext()) {
			return method_14309();
		} else {
			class_3232 lv = class_2798.field_12766.method_12117();
			List<class_3229> list = method_14328((String)iterator.next());
			if (list.isEmpty()) {
				return method_14309();
			} else {
				lv.method_14327().addAll(list);
				lv.method_14330();
				class_1959 lv2 = iterator.hasNext() ? class_2378.field_11153.method_10223(new class_2960((String)iterator.next())) : null;
				lv.method_14325(lv2 == null ? class_1972.field_9451 : lv2);
				if (iterator.hasNext()) {
					String[] strings = ((String)iterator.next()).toLowerCase(Locale.ROOT).split(",");

					for (String string2 : strings) {
						String[] strings2 = string2.split("\\(", 2);
						if (!strings2[0].isEmpty()) {
							lv.method_14314(strings2[0]);
							if (strings2.length > 1 && strings2[1].endsWith(")") && strings2[1].length() > 1) {
								String[] strings3 = strings2[1].substring(0, strings2[1].length() - 1).split(" ");

								for (String string3 : strings3) {
									String[] strings4 = string3.split("=", 2);
									if (strings4.length == 2) {
										lv.method_14324(strings2[0], strings4[0], strings4[1]);
									}
								}
							}
						}
					}
				} else {
					lv.method_14333().put("village", Maps.newHashMap());
				}

				return lv;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	private void method_14314(String string) {
		Map<String, String> map = Maps.<String, String>newHashMap();
		this.field_14074.put(string, map);
	}

	@Environment(EnvType.CLIENT)
	private void method_14324(String string, String string2, String string3) {
		((Map)this.field_14074.get(string)).put(string2, string3);
		if ("village".equals(string) && "distance".equals(string2)) {
			this.field_13146 = class_3532.method_15364(string3, this.field_13146, 9);
		}

		if ("biome_1".equals(string) && "distance".equals(string2)) {
			this.field_13139 = class_3532.method_15364(string3, this.field_13139, 9);
		}

		if ("stronghold".equals(string)) {
			if ("distance".equals(string2)) {
				this.field_13142 = class_3532.method_15364(string3, this.field_13142, 1);
			} else if ("count".equals(string2)) {
				this.field_13141 = class_3532.method_15364(string3, this.field_13141, 1);
			} else if ("spread".equals(string2)) {
				this.field_13140 = class_3532.method_15364(string3, this.field_13140, 1);
			}
		}

		if ("oceanmonument".equals(string)) {
			if ("separation".equals(string2)) {
				this.field_13143 = class_3532.method_15364(string3, this.field_13143, 1);
			} else if ("spacing".equals(string2)) {
				this.field_13144 = class_3532.method_15364(string3, this.field_13144, 1);
			}
		}

		if ("endcity".equals(string) && "distance".equals(string2)) {
			this.field_13152 = class_3532.method_15364(string3, this.field_13152, 1);
		}

		if ("mansion".equals(string) && "distance".equals(string2)) {
			this.field_13148 = class_3532.method_15364(string3, this.field_13148, 1);
		}
	}

	public static class_3232 method_14309() {
		class_3232 lv = class_2798.field_12766.method_12117();
		lv.method_14325(class_1972.field_9451);
		lv.method_14327().add(new class_3229(1, class_2246.field_9987));
		lv.method_14327().add(new class_3229(2, class_2246.field_10566));
		lv.method_14327().add(new class_3229(1, class_2246.field_10219));
		lv.method_14330();
		lv.method_14333().put("village", Maps.newHashMap());
		return lv;
	}

	public boolean method_14320() {
		return this.field_14077;
	}

	public class_2680[] method_14312() {
		return this.field_14082;
	}
}
