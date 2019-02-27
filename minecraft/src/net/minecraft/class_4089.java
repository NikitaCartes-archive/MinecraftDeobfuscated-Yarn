package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

@Environment(EnvType.CLIENT)
public class class_4089 {
	@Nullable
	private final List<Identifier> field_18299;

	private class_4089(@Nullable List<Identifier> list) {
		this.field_18299 = list;
	}

	@Nullable
	public List<Identifier> method_18826() {
		return this.field_18299;
	}

	public static class_4089 method_18828(JsonObject jsonObject) {
		JsonArray jsonArray = JsonHelper.getArray(jsonObject, "textures", null);
		List<Identifier> list;
		if (jsonArray != null) {
			list = (List<Identifier>)Streams.stream(jsonArray)
				.map(jsonElement -> JsonHelper.asString(jsonElement, "texture"))
				.map(Identifier::new)
				.collect(ImmutableList.toImmutableList());
		} else {
			list = null;
		}

		return new class_4089(list);
	}
}
