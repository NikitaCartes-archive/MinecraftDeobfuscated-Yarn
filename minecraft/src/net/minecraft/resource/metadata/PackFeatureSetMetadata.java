package net.minecraft.resource.metadata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;

public record PackFeatureSetMetadata(FeatureSet flags) {
	private static final Codec<PackFeatureSetMetadata> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(FeatureFlags.CODEC.fieldOf("enabled").forGetter(PackFeatureSetMetadata::flags)).apply(instance, PackFeatureSetMetadata::new)
	);
	public static final ResourceMetadataSerializer<PackFeatureSetMetadata> SERIALIZER = ResourceMetadataSerializer.fromCodec("features", CODEC);
}
