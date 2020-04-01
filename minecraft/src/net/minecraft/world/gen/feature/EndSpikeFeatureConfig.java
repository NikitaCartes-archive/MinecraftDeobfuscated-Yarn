package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;

public class EndSpikeFeatureConfig implements FeatureConfig {
	private final boolean crystalInvulnerable;
	private final List<EndSpikeFeature.Spike> spikes;
	@Nullable
	private final BlockPos crystalBeamTarget;

	public EndSpikeFeatureConfig(boolean crystalInvulnerable, List<EndSpikeFeature.Spike> spikes, @Nullable BlockPos crystalBeamTarget) {
		this.crystalInvulnerable = crystalInvulnerable;
		this.spikes = spikes;
		this.crystalBeamTarget = crystalBeamTarget;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
			ops,
			ops.createMap(
				ImmutableMap.of(
					ops.createString("crystalInvulnerable"),
					ops.createBoolean(this.crystalInvulnerable),
					ops.createString("spikes"),
					ops.createList(this.spikes.stream().map(spike -> spike.serialize(ops).getValue())),
					ops.createString("crystalBeamTarget"),
					this.crystalBeamTarget == null
						? ops.createList(Stream.empty())
						: ops.createList(
							IntStream.of(new int[]{this.crystalBeamTarget.getX(), this.crystalBeamTarget.getY(), this.crystalBeamTarget.getZ()}).mapToObj(ops::createInt)
						)
				)
			)
		);
	}

	public static <T> EndSpikeFeatureConfig deserialize(Dynamic<T> dynamic) {
		List<EndSpikeFeature.Spike> list = dynamic.get("spikes").asList(EndSpikeFeature.Spike::deserialize);
		List<Integer> list2 = dynamic.get("crystalBeamTarget").asList(dynamicx -> dynamicx.asInt(0));
		BlockPos blockPos;
		if (list2.size() == 3) {
			blockPos = new BlockPos((Integer)list2.get(0), (Integer)list2.get(1), (Integer)list2.get(2));
		} else {
			blockPos = null;
		}

		return new EndSpikeFeatureConfig(dynamic.get("crystalInvulnerable").asBoolean(false), list, blockPos);
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

	public static EndSpikeFeatureConfig method_26647(Random random) {
		return new EndSpikeFeatureConfig(false, ImmutableList.of(), null);
	}
}
