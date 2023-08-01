package net.minecraft.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.MinecraftVersion;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.metadata.PackFeatureSetMetadata;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.text.Text;

public class MetadataProvider implements DataProvider {
	private final DataOutput output;
	private final Map<String, Supplier<JsonElement>> metadata = new HashMap();

	public MetadataProvider(DataOutput output) {
		this.output = output;
	}

	public <T> MetadataProvider add(ResourceMetadataSerializer<T> serializer, T metadata) {
		this.metadata.put(serializer.getKey(), (Supplier)() -> serializer.toJson(metadata));
		return this;
	}

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		JsonObject jsonObject = new JsonObject();
		this.metadata.forEach((key, jsonSupplier) -> jsonObject.add(key, (JsonElement)jsonSupplier.get()));
		return DataProvider.writeToPath(writer, jsonObject, this.output.getPath().resolve("pack.mcmeta"));
	}

	@Override
	public final String getName() {
		return "Pack Metadata";
	}

	public static MetadataProvider create(DataOutput output, Text description) {
		return new MetadataProvider(output)
			.add(
				PackResourceMetadata.SERIALIZER,
				new PackResourceMetadata(description, MinecraftVersion.CURRENT.getResourceVersion(ResourceType.SERVER_DATA), Optional.empty())
			);
	}

	public static MetadataProvider create(DataOutput output, Text description, FeatureSet requiredFeatures) {
		return create(output, description).add(PackFeatureSetMetadata.SERIALIZER, new PackFeatureSetMetadata(requiredFeatures));
	}
}
