package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_816 implements class_1100 {
	private final class_2689<class_2248, class_2680> field_4329;
	private final List<class_819> field_4330;

	public class_816(class_2689<class_2248, class_2680> arg, List<class_819> list) {
		this.field_4329 = arg;
		this.field_4330 = list;
	}

	public List<class_819> method_3519() {
		return this.field_4330;
	}

	public Set<class_807> method_3520() {
		Set<class_807> set = Sets.<class_807>newHashSet();

		for (class_819 lv : this.field_4330) {
			set.add(lv.method_3529());
		}

		return set;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof class_816)) {
			return false;
		} else {
			class_816 lv = (class_816)object;
			return Objects.equals(this.field_4329, lv.field_4329) && Objects.equals(this.field_4330, lv.field_4330);
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.field_4329, this.field_4330});
	}

	@Override
	public Collection<class_2960> method_4755() {
		return (Collection<class_2960>)this.method_3519().stream().flatMap(arg -> arg.method_3529().method_4755().stream()).collect(Collectors.toSet());
	}

	@Override
	public Collection<class_2960> method_4754(Function<class_2960, class_1100> function, Set<String> set) {
		return (Collection<class_2960>)this.method_3519().stream().flatMap(arg -> arg.method_3529().method_4754(function, set).stream()).collect(Collectors.toSet());
	}

	@Nullable
	@Override
	public class_1087 method_4753(class_1088 arg, Function<class_2960, class_1058> function, class_3665 arg2) {
		class_1095.class_1096 lv = new class_1095.class_1096();

		for (class_819 lv2 : this.method_3519()) {
			class_1087 lv3 = lv2.method_3529().method_4753(arg, function, arg2);
			if (lv3 != null) {
				lv.method_4749(lv2.method_3530(this.field_4329), lv3);
			}
		}

		return lv.method_4750();
	}

	@Environment(EnvType.CLIENT)
	public static class class_817 implements JsonDeserializer<class_816> {
		private final class_790.class_791 field_4331;

		public class_817(class_790.class_791 arg) {
			this.field_4331 = arg;
		}

		public class_816 method_3523(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			return new class_816(this.field_4331.method_3425(), this.method_3522(jsonDeserializationContext, jsonElement.getAsJsonArray()));
		}

		private List<class_819> method_3522(JsonDeserializationContext jsonDeserializationContext, JsonArray jsonArray) {
			List<class_819> list = Lists.<class_819>newArrayList();

			for (JsonElement jsonElement : jsonArray) {
				list.add(jsonDeserializationContext.deserialize(jsonElement, class_819.class));
			}

			return list;
		}
	}
}
