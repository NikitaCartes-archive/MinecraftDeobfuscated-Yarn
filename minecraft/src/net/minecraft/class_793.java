package net.minecraft;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_793 implements class_1100 {
	private static final Logger field_4248 = LogManager.getLogger();
	private static final class_796 field_4249 = new class_796();
	@VisibleForTesting
	static final Gson field_4254 = new GsonBuilder()
		.registerTypeAdapter(class_793.class, new class_793.class_795())
		.registerTypeAdapter(class_785.class, new class_785.class_786())
		.registerTypeAdapter(class_783.class, new class_783.class_784())
		.registerTypeAdapter(class_787.class, new class_787.class_788())
		.registerTypeAdapter(class_804.class, new class_804.class_805())
		.registerTypeAdapter(class_809.class, new class_809.class_810())
		.registerTypeAdapter(class_799.class, new class_799.class_800())
		.create();
	private final List<class_785> field_4245;
	private final boolean field_4246;
	private final boolean field_4244;
	private final class_809 field_4250;
	private final List<class_799> field_4255;
	public String field_4252 = "";
	@VisibleForTesting
	protected final Map<String, String> field_4251;
	@Nullable
	protected class_793 field_4253;
	@Nullable
	protected class_2960 field_4247;

	public static class_793 method_3437(Reader reader) {
		return class_3518.method_15276(field_4254, reader, class_793.class);
	}

	public static class_793 method_3430(String string) {
		return method_3437(new StringReader(string));
	}

	public class_793(@Nullable class_2960 arg, List<class_785> list, Map<String, String> map, boolean bl, boolean bl2, class_809 arg2, List<class_799> list2) {
		this.field_4245 = list;
		this.field_4244 = bl;
		this.field_4246 = bl2;
		this.field_4251 = map;
		this.field_4247 = arg;
		this.field_4250 = arg2;
		this.field_4255 = list2;
	}

	public List<class_785> method_3433() {
		return this.field_4245.isEmpty() && this.field_4253 != null ? this.field_4253.method_3433() : this.field_4245;
	}

	public boolean method_3444() {
		return this.field_4253 != null ? this.field_4253.method_3444() : this.field_4244;
	}

	public boolean method_3445() {
		return this.field_4246;
	}

	public List<class_799> method_3434() {
		return this.field_4255;
	}

	private class_806 method_3440(class_1088 arg, class_793 arg2) {
		return this.field_4255.isEmpty() ? class_806.field_4292 : new class_806(arg, arg2, arg::method_4726, this.field_4255);
	}

	@Override
	public Collection<class_2960> method_4755() {
		Set<class_2960> set = Sets.<class_2960>newHashSet();

		for (class_799 lv : this.field_4255) {
			set.add(lv.method_3472());
		}

		if (this.field_4247 != null) {
			set.add(this.field_4247);
		}

		return set;
	}

	@Override
	public Collection<class_2960> method_4754(Function<class_2960, class_1100> function, Set<String> set) {
		Set<class_1100> set2 = Sets.<class_1100>newLinkedHashSet();

		for (class_793 lv = this; lv.field_4247 != null && lv.field_4253 == null; lv = lv.field_4253) {
			set2.add(lv);
			class_1100 lv2 = (class_1100)function.apply(lv.field_4247);
			if (lv2 == null) {
				field_4248.warn("No parent '{}' while loading model '{}'", this.field_4247, lv);
			}

			if (set2.contains(lv2)) {
				field_4248.warn(
					"Found 'parent' loop while loading model '{}' in chain: {} -> {}",
					lv,
					set2.stream().map(Object::toString).collect(Collectors.joining(" -> ")),
					this.field_4247
				);
				lv2 = null;
			}

			if (lv2 == null) {
				lv.field_4247 = class_1088.field_5374;
				lv2 = (class_1100)function.apply(lv.field_4247);
			}

			if (!(lv2 instanceof class_793)) {
				throw new IllegalStateException("BlockModel parent has to be a block model.");
			}

			lv.field_4253 = (class_793)lv2;
		}

		Set<class_2960> set3 = Sets.<class_2960>newHashSet(new class_2960(this.method_3436("particle")));

		for (class_785 lv3 : this.method_3433()) {
			for (class_783 lv4 : lv3.field_4230.values()) {
				String string = this.method_3436(lv4.field_4224);
				if (Objects.equals(string, class_1047.method_4541().method_4598().toString())) {
					set.add(String.format("%s in %s", lv4.field_4224, this.field_4252));
				}

				set3.add(new class_2960(string));
			}
		}

		this.field_4255.forEach(arg -> {
			class_1100 lvx = (class_1100)function.apply(arg.method_3472());
			if (!Objects.equals(lvx, this)) {
				set3.addAll(lvx.method_4754(function, set));
			}
		});
		if (this.method_3431() == class_1088.field_5400) {
			class_801.field_4270.forEach(stringx -> set3.add(new class_2960(this.method_3436(stringx))));
		}

		return set3;
	}

	@Override
	public class_1087 method_4753(class_1088 arg, Function<class_2960, class_1058> function, class_3665 arg2) {
		return this.method_3446(arg, this, function, arg2);
	}

	public class_1087 method_3446(class_1088 arg, class_793 arg2, Function<class_2960, class_1058> function, class_3665 arg3) {
		class_1058 lv = (class_1058)function.apply(new class_2960(this.method_3436("particle")));
		if (this.method_3431() == class_1088.field_5389) {
			return new class_1090(this.method_3443(), this.method_3440(arg, arg2), lv);
		} else {
			class_1093.class_1094 lv2 = new class_1093.class_1094(this, this.method_3440(arg, arg2)).method_4747(lv);

			for (class_785 lv3 : this.method_3433()) {
				for (class_2350 lv4 : lv3.field_4230.keySet()) {
					class_783 lv5 = (class_783)lv3.field_4230.get(lv4);
					class_1058 lv6 = (class_1058)function.apply(new class_2960(this.method_3436(lv5.field_4224)));
					if (lv5.field_4225 == null) {
						lv2.method_4748(method_3447(lv3, lv5, lv6, lv4, arg3));
					} else {
						lv2.method_4745(arg3.method_3509().method_4705(lv5.field_4225), method_3447(lv3, lv5, lv6, lv4, arg3));
					}
				}
			}

			return lv2.method_4746();
		}
	}

	private static class_777 method_3447(class_785 arg, class_783 arg2, class_1058 arg3, class_2350 arg4, class_3665 arg5) {
		return field_4249.method_3468(arg.field_4228, arg.field_4231, arg2, arg3, arg4, arg5, arg.field_4232, arg.field_4229);
	}

	public boolean method_3432(String string) {
		return !class_1047.method_4541().method_4598().toString().equals(this.method_3436(string));
	}

	public String method_3436(String string) {
		if (!this.method_3439(string)) {
			string = '#' + string;
		}

		return this.method_3442(string, new class_793.class_794(this));
	}

	private String method_3442(String string, class_793.class_794 arg) {
		if (this.method_3439(string)) {
			if (this == arg.field_4256) {
				field_4248.warn("Unable to resolve texture due to upward reference: {} in {}", string, this.field_4252);
				return class_1047.method_4541().method_4598().toString();
			} else {
				String string2 = (String)this.field_4251.get(string.substring(1));
				if (string2 == null && this.field_4253 != null) {
					string2 = this.field_4253.method_3442(string, arg);
				}

				arg.field_4256 = this;
				if (string2 != null && this.method_3439(string2)) {
					string2 = arg.field_4257.method_3442(string2, arg);
				}

				return string2 != null && !this.method_3439(string2) ? string2 : class_1047.method_4541().method_4598().toString();
			}
		} else {
			return string;
		}
	}

	private boolean method_3439(String string) {
		return string.charAt(0) == '#';
	}

	public class_793 method_3431() {
		return this.field_4253 == null ? this : this.field_4253.method_3431();
	}

	public class_809 method_3443() {
		class_804 lv = this.method_3438(class_809.class_811.field_4323);
		class_804 lv2 = this.method_3438(class_809.class_811.field_4320);
		class_804 lv3 = this.method_3438(class_809.class_811.field_4321);
		class_804 lv4 = this.method_3438(class_809.class_811.field_4322);
		class_804 lv5 = this.method_3438(class_809.class_811.field_4316);
		class_804 lv6 = this.method_3438(class_809.class_811.field_4317);
		class_804 lv7 = this.method_3438(class_809.class_811.field_4318);
		class_804 lv8 = this.method_3438(class_809.class_811.field_4319);
		return new class_809(lv, lv2, lv3, lv4, lv5, lv6, lv7, lv8);
	}

	private class_804 method_3438(class_809.class_811 arg) {
		return this.field_4253 != null && !this.field_4250.method_3501(arg) ? this.field_4253.method_3438(arg) : this.field_4250.method_3503(arg);
	}

	public String toString() {
		return this.field_4252;
	}

	@Environment(EnvType.CLIENT)
	static final class class_794 {
		public final class_793 field_4257;
		public class_793 field_4256;

		private class_794(class_793 arg) {
			this.field_4257 = arg;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_795 implements JsonDeserializer<class_793> {
		public class_793 method_3451(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			List<class_785> list = this.method_3449(jsonDeserializationContext, jsonObject);
			String string = this.method_3450(jsonObject);
			Map<String, String> map = this.method_3448(jsonObject);
			boolean bl = this.method_3453(jsonObject);
			class_809 lv = class_809.field_4301;
			if (jsonObject.has("display")) {
				JsonObject jsonObject2 = class_3518.method_15296(jsonObject, "display");
				lv = jsonDeserializationContext.deserialize(jsonObject2, class_809.class);
			}

			List<class_799> list2 = this.method_3452(jsonDeserializationContext, jsonObject);
			class_2960 lv2 = string.isEmpty() ? null : new class_2960(string);
			return new class_793(lv2, list, map, bl, true, lv, list2);
		}

		protected List<class_799> method_3452(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
			List<class_799> list = Lists.<class_799>newArrayList();
			if (jsonObject.has("overrides")) {
				for (JsonElement jsonElement : class_3518.method_15261(jsonObject, "overrides")) {
					list.add(jsonDeserializationContext.deserialize(jsonElement, class_799.class));
				}
			}

			return list;
		}

		private Map<String, String> method_3448(JsonObject jsonObject) {
			Map<String, String> map = Maps.<String, String>newHashMap();
			if (jsonObject.has("textures")) {
				JsonObject jsonObject2 = jsonObject.getAsJsonObject("textures");

				for (Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
					map.put(entry.getKey(), ((JsonElement)entry.getValue()).getAsString());
				}
			}

			return map;
		}

		private String method_3450(JsonObject jsonObject) {
			return class_3518.method_15253(jsonObject, "parent", "");
		}

		protected boolean method_3453(JsonObject jsonObject) {
			return class_3518.method_15258(jsonObject, "ambientocclusion", true);
		}

		protected List<class_785> method_3449(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
			List<class_785> list = Lists.<class_785>newArrayList();
			if (jsonObject.has("elements")) {
				for (JsonElement jsonElement : class_3518.method_15261(jsonObject, "elements")) {
					list.add(jsonDeserializationContext.deserialize(jsonElement, class_785.class));
				}
			}

			return list;
		}
	}
}
