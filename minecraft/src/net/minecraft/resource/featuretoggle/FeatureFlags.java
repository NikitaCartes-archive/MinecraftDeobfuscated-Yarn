package net.minecraft.resource.featuretoggle;

import com.mojang.serialization.Codec;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.util.Identifier;

public class FeatureFlags {
	public static final FeatureFlag VANILLA;
	public static final FeatureFlag WINTER_DROP;
	public static final FeatureFlag TRADE_REBALANCE;
	public static final FeatureFlag REDSTONE_EXPERIMENTS;
	public static final FeatureFlag MINECART_IMPROVEMENTS;
	public static final FeatureManager FEATURE_MANAGER;
	public static final Codec<FeatureSet> CODEC;
	public static final FeatureSet VANILLA_FEATURES;
	public static final FeatureSet DEFAULT_ENABLED_FEATURES;

	public static String printMissingFlags(FeatureSet featuresToCheck, FeatureSet features) {
		return printMissingFlags(FEATURE_MANAGER, featuresToCheck, features);
	}

	public static String printMissingFlags(FeatureManager featureManager, FeatureSet featuresToCheck, FeatureSet features) {
		Set<Identifier> set = featureManager.toId(features);
		Set<Identifier> set2 = featureManager.toId(featuresToCheck);
		return (String)set.stream().filter(id -> !set2.contains(id)).map(Identifier::toString).collect(Collectors.joining(", "));
	}

	public static boolean isNotVanilla(FeatureSet features) {
		return !features.isSubsetOf(VANILLA_FEATURES);
	}

	static {
		FeatureManager.Builder builder = new FeatureManager.Builder("main");
		VANILLA = builder.addVanillaFlag("vanilla");
		WINTER_DROP = builder.addVanillaFlag("winter_drop");
		TRADE_REBALANCE = builder.addVanillaFlag("trade_rebalance");
		REDSTONE_EXPERIMENTS = builder.addVanillaFlag("redstone_experiments");
		MINECART_IMPROVEMENTS = builder.addVanillaFlag("minecart_improvements");
		FEATURE_MANAGER = builder.build();
		CODEC = FEATURE_MANAGER.getCodec();
		VANILLA_FEATURES = FeatureSet.of(VANILLA);
		DEFAULT_ENABLED_FEATURES = VANILLA_FEATURES;
	}
}
