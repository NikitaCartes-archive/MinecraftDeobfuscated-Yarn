package net.minecraft.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.bridge.game.PackType;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.MinecraftVersion;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.metadata.PackFeatureSetMetadata;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.text.Text;

public class MetadataProvider implements DataProvider {
	private final String name;
	private final DataOutput output;
	private final Map<String, Supplier<JsonElement>> metadata = new HashMap();

	public MetadataProvider(DataOutput output, String name) {
		this.output = output;
		this.name = name;
	}

	public <T> MetadataProvider add(ResourceMetadataSerializer<T> serializer, T metadata) {
		this.metadata.put(serializer.getKey(), (Supplier)() -> serializer.toJson(metadata));
		return this;
	}

	@Override
	public void run(DataWriter writer) throws IOException {
		JsonObject jsonObject = new JsonObject();
		this.metadata.forEach((key, jsonSupplier) -> jsonObject.add(key, (JsonElement)jsonSupplier.get()));
		DataProvider.writeToPath(writer, jsonObject, this.output.getPath().resolve("pack.mcmeta"));
	}

	@Override
	public String getName() {
		return this.name;
	}

	public static MetadataProvider create(DataOutput output, String packName, Text description, FeatureSet requiredFeatures) {
		return new MetadataProvider(output, "Pack metadata for " + packName)
			.add(PackResourceMetadata.SERIALIZER, new PackResourceMetadata(description, MinecraftVersion.CURRENT.getPackVersion(PackType.DATA)))
			.add(PackFeatureSetMetadata.SERIALIZER, new PackFeatureSetMetadata(requiredFeatures));
	}
}
