package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.ArrayUtils;

public class class_161 {
	private final class_161 field_1143;
	private final class_185 field_1146;
	private final class_170 field_1145;
	private final class_2960 field_1144;
	private final Map<String, class_175> field_1139;
	private final String[][] field_1142;
	private final Set<class_161> field_1140 = Sets.<class_161>newLinkedHashSet();
	private final class_2561 field_1141;

	public class_161(class_2960 arg, @Nullable class_161 arg2, @Nullable class_185 arg3, class_170 arg4, Map<String, class_175> map, String[][] strings) {
		this.field_1144 = arg;
		this.field_1146 = arg3;
		this.field_1139 = ImmutableMap.copyOf(map);
		this.field_1143 = arg2;
		this.field_1145 = arg4;
		this.field_1142 = strings;
		if (arg2 != null) {
			arg2.method_690(this);
		}

		if (arg3 == null) {
			this.field_1141 = new class_2585(arg.toString());
		} else {
			class_2561 lv = arg3.method_811();
			class_124 lv2 = arg3.method_815().method_830();
			class_2561 lv3 = lv.method_10853().method_10854(lv2).method_10864("\n").method_10852(arg3.method_817());
			class_2561 lv4 = lv.method_10853().method_10859(arg2x -> arg2x.method_10949(new class_2568(class_2568.class_2569.field_11762, lv3)));
			this.field_1141 = new class_2585("[").method_10852(lv4).method_10864("]").method_10854(lv2);
		}
	}

	public class_161.class_162 method_689() {
		return new class_161.class_162(
			this.field_1143 == null ? null : this.field_1143.method_688(), this.field_1146, this.field_1145, this.field_1139, this.field_1142
		);
	}

	@Nullable
	public class_161 method_687() {
		return this.field_1143;
	}

	@Nullable
	public class_185 method_686() {
		return this.field_1146;
	}

	public class_170 method_691() {
		return this.field_1145;
	}

	public String toString() {
		return "SimpleAdvancement{id="
			+ this.method_688()
			+ ", parent="
			+ (this.field_1143 == null ? "null" : this.field_1143.method_688())
			+ ", display="
			+ this.field_1146
			+ ", rewards="
			+ this.field_1145
			+ ", criteria="
			+ this.field_1139
			+ ", requirements="
			+ Arrays.deepToString(this.field_1142)
			+ '}';
	}

	public Iterable<class_161> method_681() {
		return this.field_1140;
	}

	public Map<String, class_175> method_682() {
		return this.field_1139;
	}

	@Environment(EnvType.CLIENT)
	public int method_683() {
		return this.field_1142.length;
	}

	public void method_690(class_161 arg) {
		this.field_1140.add(arg);
	}

	public class_2960 method_688() {
		return this.field_1144;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof class_161)) {
			return false;
		} else {
			class_161 lv = (class_161)object;
			return this.field_1144.equals(lv.field_1144);
		}
	}

	public int hashCode() {
		return this.field_1144.hashCode();
	}

	public String[][] method_680() {
		return this.field_1142;
	}

	public class_2561 method_684() {
		return this.field_1141;
	}

	public static class class_162 {
		private class_2960 field_1152;
		private class_161 field_1149;
		private class_185 field_1147;
		private class_170 field_1153 = class_170.field_1167;
		private Map<String, class_175> field_1148 = Maps.<String, class_175>newLinkedHashMap();
		private String[][] field_1150;
		private class_193 field_1151 = class_193.field_16882;

		private class_162(@Nullable class_2960 arg, @Nullable class_185 arg2, class_170 arg3, Map<String, class_175> map, String[][] strings) {
			this.field_1152 = arg;
			this.field_1147 = arg2;
			this.field_1153 = arg3;
			this.field_1148 = map;
			this.field_1150 = strings;
		}

		private class_162() {
		}

		public static class_161.class_162 method_707() {
			return new class_161.class_162();
		}

		public class_161.class_162 method_701(class_161 arg) {
			this.field_1149 = arg;
			return this;
		}

		public class_161.class_162 method_708(class_2960 arg) {
			this.field_1152 = arg;
			return this;
		}

		public class_161.class_162 method_697(
			class_1935 arg, class_2561 arg2, class_2561 arg3, @Nullable class_2960 arg4, class_189 arg5, boolean bl, boolean bl2, boolean bl3
		) {
			return this.method_693(new class_185(new class_1799(arg.method_8389()), arg2, arg3, arg4, arg5, bl, bl2, bl3));
		}

		public class_161.class_162 method_693(class_185 arg) {
			this.field_1147 = arg;
			return this;
		}

		public class_161.class_162 method_703(class_170.class_171 arg) {
			return this.method_706(arg.method_751());
		}

		public class_161.class_162 method_706(class_170 arg) {
			this.field_1153 = arg;
			return this;
		}

		public class_161.class_162 method_709(String string, class_184 arg) {
			return this.method_705(string, new class_175(arg));
		}

		public class_161.class_162 method_705(String string, class_175 arg) {
			if (this.field_1148.containsKey(string)) {
				throw new IllegalArgumentException("Duplicate criterion " + string);
			} else {
				this.field_1148.put(string, arg);
				return this;
			}
		}

		public class_161.class_162 method_704(class_193 arg) {
			this.field_1151 = arg;
			return this;
		}

		public boolean method_700(Function<class_2960, class_161> function) {
			if (this.field_1152 == null) {
				return true;
			} else {
				if (this.field_1149 == null) {
					this.field_1149 = (class_161)function.apply(this.field_1152);
				}

				return this.field_1149 != null;
			}
		}

		public class_161 method_695(class_2960 arg) {
			if (!this.method_700(argx -> null)) {
				throw new IllegalStateException("Tried to build incomplete advancement!");
			} else {
				if (this.field_1150 == null) {
					this.field_1150 = this.field_1151.createRequirements(this.field_1148.keySet());
				}

				return new class_161(arg, this.field_1149, this.field_1147, this.field_1153, this.field_1148, this.field_1150);
			}
		}

		public class_161 method_694(Consumer<class_161> consumer, String string) {
			class_161 lv = this.method_695(new class_2960(string));
			consumer.accept(lv);
			return lv;
		}

		public JsonObject method_698() {
			if (this.field_1150 == null) {
				this.field_1150 = this.field_1151.createRequirements(this.field_1148.keySet());
			}

			JsonObject jsonObject = new JsonObject();
			if (this.field_1149 != null) {
				jsonObject.addProperty("parent", this.field_1149.method_688().toString());
			} else if (this.field_1152 != null) {
				jsonObject.addProperty("parent", this.field_1152.toString());
			}

			if (this.field_1147 != null) {
				jsonObject.add("display", this.field_1147.method_814());
			}

			jsonObject.add("rewards", this.field_1153.method_747());
			JsonObject jsonObject2 = new JsonObject();

			for (Entry<String, class_175> entry : this.field_1148.entrySet()) {
				jsonObject2.add((String)entry.getKey(), ((class_175)entry.getValue()).method_773());
			}

			jsonObject.add("criteria", jsonObject2);
			JsonArray jsonArray = new JsonArray();

			for (String[] strings : this.field_1150) {
				JsonArray jsonArray2 = new JsonArray();

				for (String string : strings) {
					jsonArray2.add(string);
				}

				jsonArray.add(jsonArray2);
			}

			jsonObject.add("requirements", jsonArray);
			return jsonObject;
		}

		public void method_699(class_2540 arg) {
			if (this.field_1152 == null) {
				arg.writeBoolean(false);
			} else {
				arg.writeBoolean(true);
				arg.method_10812(this.field_1152);
			}

			if (this.field_1147 == null) {
				arg.writeBoolean(false);
			} else {
				arg.writeBoolean(true);
				this.field_1147.method_813(arg);
			}

			class_175.method_775(this.field_1148, arg);
			arg.method_10804(this.field_1150.length);

			for (String[] strings : this.field_1150) {
				arg.method_10804(strings.length);

				for (String string : strings) {
					arg.method_10814(string);
				}
			}
		}

		public String toString() {
			return "Task Advancement{parentId="
				+ this.field_1152
				+ ", display="
				+ this.field_1147
				+ ", rewards="
				+ this.field_1153
				+ ", criteria="
				+ this.field_1148
				+ ", requirements="
				+ Arrays.deepToString(this.field_1150)
				+ '}';
		}

		public static class_161.class_162 method_692(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			class_2960 lv = jsonObject.has("parent") ? new class_2960(class_3518.method_15265(jsonObject, "parent")) : null;
			class_185 lv2 = jsonObject.has("display") ? class_185.method_809(class_3518.method_15296(jsonObject, "display"), jsonDeserializationContext) : null;
			class_170 lv3 = class_3518.method_15283(jsonObject, "rewards", class_170.field_1167, jsonDeserializationContext, class_170.class);
			Map<String, class_175> map = class_175.method_772(class_3518.method_15296(jsonObject, "criteria"), jsonDeserializationContext);
			if (map.isEmpty()) {
				throw new JsonSyntaxException("Advancement criteria cannot be empty");
			} else {
				JsonArray jsonArray = class_3518.method_15292(jsonObject, "requirements", new JsonArray());
				String[][] strings = new String[jsonArray.size()][];

				for (int i = 0; i < jsonArray.size(); i++) {
					JsonArray jsonArray2 = class_3518.method_15252(jsonArray.get(i), "requirements[" + i + "]");
					strings[i] = new String[jsonArray2.size()];

					for (int j = 0; j < jsonArray2.size(); j++) {
						strings[i][j] = class_3518.method_15287(jsonArray2.get(j), "requirements[" + i + "][" + j + "]");
					}
				}

				if (strings.length == 0) {
					strings = new String[map.size()][];
					int i = 0;

					for (String string : map.keySet()) {
						strings[i++] = new String[]{string};
					}
				}

				for (String[] strings2 : strings) {
					if (strings2.length == 0 && map.isEmpty()) {
						throw new JsonSyntaxException("Requirement entry cannot be empty");
					}

					for (String string2 : strings2) {
						if (!map.containsKey(string2)) {
							throw new JsonSyntaxException("Unknown required criterion '" + string2 + "'");
						}
					}
				}

				for (String string3 : map.keySet()) {
					boolean bl = false;

					for (String[] strings3 : strings) {
						if (ArrayUtils.contains(strings3, string3)) {
							bl = true;
							break;
						}
					}

					if (!bl) {
						throw new JsonSyntaxException(
							"Criterion '" + string3 + "' isn't a requirement for completion. This isn't supported behaviour, all criteria must be required."
						);
					}
				}

				return new class_161.class_162(lv, lv2, lv3, map, strings);
			}
		}

		public static class_161.class_162 method_696(class_2540 arg) {
			class_2960 lv = arg.readBoolean() ? arg.method_10810() : null;
			class_185 lv2 = arg.readBoolean() ? class_185.method_820(arg) : null;
			Map<String, class_175> map = class_175.method_768(arg);
			String[][] strings = new String[arg.method_10816()][];

			for (int i = 0; i < strings.length; i++) {
				strings[i] = new String[arg.method_10816()];

				for (int j = 0; j < strings[i].length; j++) {
					strings[i][j] = arg.method_10800(32767);
				}
			}

			return new class_161.class_162(lv, lv2, class_170.field_1167, map, strings);
		}

		public Map<String, class_175> method_710() {
			return this.field_1148;
		}
	}
}
