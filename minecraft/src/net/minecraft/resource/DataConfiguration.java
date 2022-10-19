package net.minecraft.resource;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;

public record DataConfiguration(DataPackSettings dataPacks, FeatureSet enabledFeatures) {
	public static final String ENABLED_FEATURES_KEY = "enabled_features";
	public static final Codec<DataConfiguration> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					DataPackSettings.CODEC.optionalFieldOf("DataPacks", DataPackSettings.SAFE_MODE).forGetter(DataConfiguration::dataPacks),
					FeatureFlags.CODEC.optionalFieldOf("enabled_features", FeatureFlags.DEFAULT_ENABLED_FEATURES).forGetter(DataConfiguration::enabledFeatures)
				)
				.apply(instance, DataConfiguration::new)
	);
	public static final DataConfiguration SAFE_MODE = new DataConfiguration(DataPackSettings.SAFE_MODE, FeatureFlags.DEFAULT_ENABLED_FEATURES);

	public DataConfiguration withFeaturesAdded(FeatureSet features) {
		return new DataConfiguration(this.dataPacks, this.enabledFeatures.combine(features));
	}
}
