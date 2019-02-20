package net.minecraft.resource.metadata;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.text.TextComponent;
import net.minecraft.util.JsonHelper;

public class PackResourceMetadataReader implements ResourceMetadataReader<PackResourceMetadata> {
	public PackResourceMetadata method_14426(JsonObject jsonObject) {
		TextComponent textComponent = TextComponent.Serializer.fromJson(jsonObject.get("description"));
		if (textComponent == null) {
			throw new JsonParseException("Invalid/missing description!");
		} else {
			int i = JsonHelper.getInt(jsonObject, "pack_format");
			return new PackResourceMetadata(textComponent, i);
		}
	}

	@Override
	public String getKey() {
		return "pack";
	}
}
