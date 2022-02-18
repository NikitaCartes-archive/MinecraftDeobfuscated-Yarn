package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.util.math.BlockPos;

public class EndGatewayFeatureConfig implements FeatureConfig {
	public static final Codec<EndGatewayFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					BlockPos.CODEC.optionalFieldOf("exit").forGetter(config -> config.exitPos), Codec.BOOL.fieldOf("exact").forGetter(config -> config.exact)
				)
				.apply(instance, EndGatewayFeatureConfig::new)
	);
	private final Optional<BlockPos> exitPos;
	private final boolean exact;

	private EndGatewayFeatureConfig(Optional<BlockPos> exitPos, boolean exact) {
		this.exitPos = exitPos;
		this.exact = exact;
	}

	public static EndGatewayFeatureConfig createConfig(BlockPos exitPortalPosition, boolean exitsAtSpawn) {
		return new EndGatewayFeatureConfig(Optional.of(exitPortalPosition), exitsAtSpawn);
	}

	public static EndGatewayFeatureConfig createConfig() {
		return new EndGatewayFeatureConfig(Optional.empty(), false);
	}

	public Optional<BlockPos> getExitPos() {
		return this.exitPos;
	}

	public boolean isExact() {
		return this.exact;
	}
}
