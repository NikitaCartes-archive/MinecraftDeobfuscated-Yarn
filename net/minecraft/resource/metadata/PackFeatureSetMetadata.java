/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource.metadata;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;

public record PackFeatureSetMetadata(FeatureSet flags) {
    private static final Codec<PackFeatureSetMetadata> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)FeatureFlags.CODEC.fieldOf("enabled")).forGetter(PackFeatureSetMetadata::flags)).apply((Applicative<PackFeatureSetMetadata, ?>)instance, PackFeatureSetMetadata::new));
    public static final ResourceMetadataSerializer<PackFeatureSetMetadata> SERIALIZER = ResourceMetadataSerializer.fromCodec("features", CODEC);
}

