package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_269 {
	private final Map<String, class_266> field_1428 = Maps.<String, class_266>newHashMap();
	private final Map<class_274, List<class_266>> field_1429 = Maps.<class_274, List<class_266>>newHashMap();
	private final Map<String, Map<class_266, class_267>> field_1431 = Maps.<String, Map<class_266, class_267>>newHashMap();
	private final class_266[] field_1432 = new class_266[19];
	private final Map<String, class_268> field_1426 = Maps.<String, class_268>newHashMap();
	private final Map<String, class_268> field_1427 = Maps.<String, class_268>newHashMap();
	private static String[] field_1430;

	@Environment(EnvType.CLIENT)
	public boolean method_1181(String string) {
		return this.field_1428.containsKey(string);
	}

	public class_266 method_1165(String string) {
		return (class_266)this.field_1428.get(string);
	}

	@Nullable
	public class_266 method_1170(@Nullable String string) {
		return (class_266)this.field_1428.get(string);
	}

	public class_266 method_1168(String string, class_274 arg, class_2561 arg2, class_274.class_275 arg3) {
		if (string.length() > 16) {
			throw new IllegalArgumentException("The objective name '" + string + "' is too long!");
		} else if (this.field_1428.containsKey(string)) {
			throw new IllegalArgumentException("An objective with the name '" + string + "' already exists!");
		} else {
			class_266 lv = new class_266(this, string, arg, arg2, arg3);
			((List)this.field_1429.computeIfAbsent(arg, argx -> Lists.newArrayList())).add(lv);
			this.field_1428.put(string, lv);
			this.method_1185(lv);
			return lv;
		}
	}

	public final void method_1162(class_274 arg, String string, Consumer<class_267> consumer) {
		((List)this.field_1429.getOrDefault(arg, Collections.emptyList())).forEach(argx -> consumer.accept(this.method_1180(string, argx)));
	}

	public boolean method_1183(String string, class_266 arg) {
		Map<class_266, class_267> map = (Map<class_266, class_267>)this.field_1431.get(string);
		if (map == null) {
			return false;
		} else {
			class_267 lv = (class_267)map.get(arg);
			return lv != null;
		}
	}

	public class_267 method_1180(String string, class_266 arg) {
		if (string.length() > 40) {
			throw new IllegalArgumentException("The player name '" + string + "' is too long!");
		} else {
			Map<class_266, class_267> map = (Map<class_266, class_267>)this.field_1431.computeIfAbsent(string, stringx -> Maps.newHashMap());
			return (class_267)map.computeIfAbsent(arg, argx -> {
				class_267 lv = new class_267(this, argx, string);
				lv.method_1128(0);
				return lv;
			});
		}
	}

	public Collection<class_267> method_1184(class_266 arg) {
		List<class_267> list = Lists.<class_267>newArrayList();

		for (Map<class_266, class_267> map : this.field_1431.values()) {
			class_267 lv = (class_267)map.get(arg);
			if (lv != null) {
				list.add(lv);
			}
		}

		Collections.sort(list, class_267.field_1413);
		return list;
	}

	public Collection<class_266> method_1151() {
		return this.field_1428.values();
	}

	public Collection<String> method_1163() {
		return this.field_1428.keySet();
	}

	public Collection<String> method_1178() {
		return Lists.<String>newArrayList(this.field_1431.keySet());
	}

	public void method_1155(String string, @Nullable class_266 arg) {
		if (arg == null) {
			Map<class_266, class_267> map = (Map<class_266, class_267>)this.field_1431.remove(string);
			if (map != null) {
				this.method_1152(string);
			}
		} else {
			Map<class_266, class_267> map = (Map<class_266, class_267>)this.field_1431.get(string);
			if (map != null) {
				class_267 lv = (class_267)map.remove(arg);
				if (map.size() < 1) {
					Map<class_266, class_267> map2 = (Map<class_266, class_267>)this.field_1431.remove(string);
					if (map2 != null) {
						this.method_1152(string);
					}
				} else if (lv != null) {
					this.method_1190(string, arg);
				}
			}
		}
	}

	public Map<class_266, class_267> method_1166(String string) {
		Map<class_266, class_267> map = (Map<class_266, class_267>)this.field_1431.get(string);
		if (map == null) {
			map = Maps.<class_266, class_267>newHashMap();
		}

		return map;
	}

	public void method_1194(class_266 arg) {
		this.field_1428.remove(arg.method_1113());

		for (int i = 0; i < 19; i++) {
			if (this.method_1189(i) == arg) {
				this.method_1158(i, null);
			}
		}

		List<class_266> list = (List<class_266>)this.field_1429.get(arg.method_1116());
		if (list != null) {
			list.remove(arg);
		}

		for (Map<class_266, class_267> map : this.field_1431.values()) {
			map.remove(arg);
		}

		this.method_1173(arg);
	}

	public void method_1158(int i, @Nullable class_266 arg) {
		this.field_1432[i] = arg;
	}

	@Nullable
	public class_266 method_1189(int i) {
		return this.field_1432[i];
	}

	public class_268 method_1153(String string) {
		return (class_268)this.field_1426.get(string);
	}

	public class_268 method_1171(String string) {
		if (string.length() > 16) {
			throw new IllegalArgumentException("The team name '" + string + "' is too long!");
		} else {
			class_268 lv = this.method_1153(string);
			if (lv != null) {
				throw new IllegalArgumentException("A team with the name '" + string + "' already exists!");
			} else {
				lv = new class_268(this, string);
				this.field_1426.put(string, lv);
				this.method_1160(lv);
				return lv;
			}
		}
	}

	public void method_1191(class_268 arg) {
		this.field_1426.remove(arg.method_1197());

		for (String string : arg.method_1204()) {
			this.field_1427.remove(string);
		}

		this.method_1193(arg);
	}

	public boolean method_1172(String string, class_268 arg) {
		if (string.length() > 40) {
			throw new IllegalArgumentException("The player name '" + string + "' is too long!");
		} else {
			if (this.method_1164(string) != null) {
				this.method_1195(string);
			}

			this.field_1427.put(string, arg);
			return arg.method_1204().add(string);
		}
	}

	public boolean method_1195(String string) {
		class_268 lv = this.method_1164(string);
		if (lv != null) {
			this.method_1157(string, lv);
			return true;
		} else {
			return false;
		}
	}

	public void method_1157(String string, class_268 arg) {
		if (this.method_1164(string) != arg) {
			throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team '" + arg.method_1197() + "'.");
		} else {
			this.field_1427.remove(string);
			arg.method_1204().remove(string);
		}
	}

	public Collection<String> method_1196() {
		return this.field_1426.keySet();
	}

	public Collection<class_268> method_1159() {
		return this.field_1426.values();
	}

	@Nullable
	public class_268 method_1164(String string) {
		return (class_268)this.field_1427.get(string);
	}

	public void method_1185(class_266 arg) {
	}

	public void method_1175(class_266 arg) {
	}

	public void method_1173(class_266 arg) {
	}

	public void method_1176(class_267 arg) {
	}

	public void method_1152(String string) {
	}

	public void method_1190(String string, class_266 arg) {
	}

	public void method_1160(class_268 arg) {
	}

	public void method_1154(class_268 arg) {
	}

	public void method_1193(class_268 arg) {
	}

	public static String method_1167(int i) {
		switch (i) {
			case 0:
				return "list";
			case 1:
				return "sidebar";
			case 2:
				return "belowName";
			default:
				if (i >= 3 && i <= 18) {
					class_124 lv = class_124.method_534(i - 3);
					if (lv != null && lv != class_124.field_1070) {
						return "sidebar.team." + lv.method_537();
					}
				}

				return null;
		}
	}

	public static int method_1192(String string) {
		if ("list".equalsIgnoreCase(string)) {
			return 0;
		} else if ("sidebar".equalsIgnoreCase(string)) {
			return 1;
		} else if ("belowName".equalsIgnoreCase(string)) {
			return 2;
		} else {
			if (string.startsWith("sidebar.team.")) {
				String string2 = string.substring("sidebar.team.".length());
				class_124 lv = class_124.method_533(string2);
				if (lv != null && lv.method_536() >= 0) {
					return lv.method_536() + 3;
				}
			}

			return -1;
		}
	}

	public static String[] method_1186() {
		if (field_1430 == null) {
			field_1430 = new String[19];

			for (int i = 0; i < 19; i++) {
				field_1430[i] = method_1167(i);
			}
		}

		return field_1430;
	}

	public void method_1150(class_1297 arg) {
		if (arg != null && !(arg instanceof class_1657) && !arg.method_5805()) {
			String string = arg.method_5845();
			this.method_1155(string, null);
			this.method_1195(string);
		}
	}

	protected class_2499 method_1169() {
		class_2499 lv = new class_2499();
		this.field_1431.values().stream().map(Map::values).forEach(collection -> collection.stream().filter(argx -> argx.method_1127() != null).forEach(arg2 -> {
				class_2487 lvx = new class_2487();
				lvx.method_10582("Name", arg2.method_1129());
				lvx.method_10582("Objective", arg2.method_1127().method_1113());
				lvx.method_10569("Score", arg2.method_1126());
				lvx.method_10556("Locked", arg2.method_1131());
				lv.method_10606(lvx);
			}));
		return lv;
	}

	protected void method_1188(class_2499 arg) {
		for (int i = 0; i < arg.size(); i++) {
			class_2487 lv = arg.method_10602(i);
			class_266 lv2 = this.method_1165(lv.method_10558("Objective"));
			String string = lv.method_10558("Name");
			if (string.length() > 40) {
				string = string.substring(0, 40);
			}

			class_267 lv3 = this.method_1180(string, lv2);
			lv3.method_1128(lv.method_10550("Score"));
			if (lv.method_10545("Locked")) {
				lv3.method_1125(lv.method_10577("Locked"));
			}
		}
	}
}
