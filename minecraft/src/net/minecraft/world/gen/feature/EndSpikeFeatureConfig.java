package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;

public class EndSpikeFeatureConfig implements FeatureConfig {
	public static final Codec<EndSpikeFeatureConfig> field_24911 = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.BOOL.fieldOf("crystal_invulnerable").withDefault(false).forGetter(endSpikeFeatureConfig -> endSpikeFeatureConfig.crystalInvulnerable),
					EndSpikeFeature.Spike.CODEC.listOf().fieldOf("spikes").forGetter(endSpikeFeatureConfig -> endSpikeFeatureConfig.spikes),
					BlockPos.field_25064
						.optionalFieldOf("crystal_beam_target")
						.forGetter(endSpikeFeatureConfig -> Optional.ofNullable(endSpikeFeatureConfig.crystalBeamTarget))
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

	private EndSpikeFeatureConfig(boolean bl, List<EndSpikeFeature.Spike> list, Optional<BlockPos> optional) {
		this.crystalInvulnerable = bl;
		this.spikes = list;
		this.crystalBeamTarget = (BlockPos)optional.orElse(null);
	}

	public boolean isCrystalInvulerable() {
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
