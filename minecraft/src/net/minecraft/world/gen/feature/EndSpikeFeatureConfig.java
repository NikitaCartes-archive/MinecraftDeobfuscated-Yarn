package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;

public class EndSpikeFeatureConfig implements FeatureConfig {
	public static final Codec<EndSpikeFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.BOOL.fieldOf("crystal_invulnerable").orElse(false).forGetter(endSpikeFeatureConfig -> endSpikeFeatureConfig.crystalInvulnerable),
					EndSpikeFeature.Spike.CODEC.listOf().fieldOf("spikes").forGetter(endSpikeFeatureConfig -> endSpikeFeatureConfig.spikes),
					BlockPos.CODEC.optionalFieldOf("crystal_beam_target").forGetter(endSpikeFeatureConfig -> Optional.ofNullable(endSpikeFeatureConfig.crystalBeamTarget))
				)
				.apply(instance, EndSpikeFeatureConfig::new)
	);
	private final boolean crystalInvulnerable;
	private final List<EndSpikeFeature.Spike> spikes;
	@Nullable
	private final BlockPos crystalBeamTarget;

	public EndSpikeFeatureConfig(boolean crystalInvulnerable, List<EndSpikeFeature.Spike> spikes, @Nullable BlockPos crystalBeamTarget) {
		this(crystalInvulnerable, spikes, Optional.ofNullable(crystalBeamTarget));
	}

	private EndSpikeFeatureConfig(boolean crystalInvulnerable, List<EndSpikeFeature.Spike> spikes, Optional<BlockPos> crystalBeamTarget) {
		this.crystalInvulnerable = crystalInvulnerable;
		this.spikes = spikes;
		this.crystalBeamTarget = (BlockPos)crystalBeamTarget.orElse(null);
	}

	public boolean isCrystalInvulnerable() {
		return this.crystalInvulnerable;
	}

	public List<EndSpikeFeature.Spike> getSpikes() {
		return this.spikes;
	}

	@Nullable
	public BlockPos getPos() {
		return this.crystalBeamTarget;
	}
}
