package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4089 {
	@Nullable
	private final List<class_2960> field_18299;

	private class_4089(@Nullable List<class_2960> list) {
		this.field_18299 = list;
	}

	@Nullable
	public List<class_2960> method_18826() {
		return this.field_18299;
	}

	public static class_4089 method_18828(JsonObject jsonObject) {
		JsonArray jsonArray = class_3518.method_15292(jsonObject, "textures", null);
		List<class_2960> list;
		if (jsonArray != null) {
			list = (List<class_2960>)Streams.stream(jsonArray)
				.map(jsonElement -> class_3518.method_15287(jsonElement, "texture"))
				.map(class_2960::new)
				.collect(ImmutableList.toImmutableList());
		} else {
			list = null;
		}

		return new class_4089(list);
	}
}
