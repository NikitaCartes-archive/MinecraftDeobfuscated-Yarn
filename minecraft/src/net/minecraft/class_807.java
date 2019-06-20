package net.minecraft;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_807 implements class_1100 {
	private final List<class_813> field_4294;

	public class_807(List<class_813> list) {
		this.field_4294 = list;
	}

	public List<class_813> method_3497() {
		return this.field_4294;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof class_807) {
			class_807 lv = (class_807)object;
			return this.field_4294.equals(lv.field_4294);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return this.field_4294.hashCode();
	}

	@Override
	public Collection<class_2960> method_4755() {
		return (Collection<class_2960>)this.method_3497().stream().map(class_813::method_3510).collect(Collectors.toSet());
	}

	@Override
	public Collection<class_2960> method_4754(Function<class_2960, class_1100> function, Set<String> set) {
		return (Collection<class_2960>)this.method_3497()
			.stream()
			.map(class_813::method_3510)
			.distinct()
			.flatMap(arg -> ((class_1100)function.apply(arg)).method_4754(function, set).stream())
			.collect(Collectors.toSet());
	}

	@Nullable
	@Override
	public class_1087 method_4753(class_1088 arg, Function<class_2960, class_1058> function, class_3665 arg2) {
		if (this.method_3497().isEmpty()) {
			return null;
		} else {
			class_1097.class_1098 lv = new class_1097.class_1098();

			for (class_813 lv2 : this.method_3497()) {
				class_1087 lv3 = arg.method_15878(lv2.method_3510(), lv2);
				lv.method_4752(lv3, lv2.method_3511());
			}

			return lv.method_4751();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_808 implements JsonDeserializer<class_807> {
		public class_807 method_3499(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			List<class_813> list = Lists.<class_813>newArrayList();
			if (jsonElement.isJsonArray()) {
				JsonArray jsonArray = jsonElement.getAsJsonArray();
				if (jsonArray.size() == 0) {
					throw new JsonParseException("Empty variant array");
				}

				for (JsonElement jsonElement2 : jsonArray) {
					list.add(jsonDeserializationContext.deserialize(jsonElement2, class_813.class));
				}
			} else {
				list.add(jsonDeserializationContext.deserialize(jsonElement, class_813.class));
			}

			return new class_807(list);
		}
	}
}
