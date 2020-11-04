package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;

@Environment(EnvType.CLIENT)
public class class_5610 {
	private final List<class_5604> field_27728;
	private final class_5603 field_27729;
	private final Map<String, class_5610> field_27730 = Maps.<String, class_5610>newHashMap();

	class_5610(List<class_5604> list, class_5603 arg) {
		this.field_27728 = list;
		this.field_27729 = arg;
	}

	public class_5610 method_32117(String string, class_5606 arg, class_5603 arg2) {
		class_5610 lv = new class_5610(arg.method_32107(), arg2);
		class_5610 lv2 = (class_5610)this.field_27730.put(string, lv);
		if (lv2 != null) {
			lv.field_27730.putAll(lv2.field_27730);
		}

		return lv;
	}

	public ModelPart method_32112(int i, int j) {
		Object2ObjectArrayMap<String, ModelPart> object2ObjectArrayMap = (Object2ObjectArrayMap<String, ModelPart>)this.field_27730
			.entrySet()
			.stream()
			.collect(
				Collectors.toMap(
					Entry::getKey, entry -> ((class_5610)entry.getValue()).method_32112(i, j), (modelPartx, modelPart2) -> modelPartx, Object2ObjectArrayMap::new
				)
			);
		List<ModelPart.Cuboid> list = (List<ModelPart.Cuboid>)this.field_27728.stream().map(arg -> arg.method_32093(i, j)).collect(ImmutableList.toImmutableList());
		ModelPart modelPart = new ModelPart(list, object2ObjectArrayMap);
		modelPart.method_32085(this.field_27729);
		return modelPart;
	}

	public class_5610 method_32116(String string) {
		return (class_5610)this.field_27730.get(string);
	}
}
