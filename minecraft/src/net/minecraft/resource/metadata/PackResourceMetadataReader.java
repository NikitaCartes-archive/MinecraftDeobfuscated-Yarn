package net.minecraft.resource.metadata;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;

public class PackResourceMetadataReader implements ResourceMetadataReader<PackResourceMetadata> {
	public PackResourceMetadata method_14426(JsonObject jsonObject) {
		Text text = Text.Serializer.fromJson(jsonObject.get("description"));
		if (text == null) {
			throw new JsonParseException("Invalid/missing description!");
		} else {
			int i = JsonHelper.getInt(jsonObject, "pack_format");
			return new PackResourceMetadata(text, i);
		}
	}

	@Override
	public String getKey() {
		return "pack";
	}
}
