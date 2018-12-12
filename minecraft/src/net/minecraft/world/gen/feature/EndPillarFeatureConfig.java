package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;

public class EndPillarFeatureConfig implements FeatureConfig {
	private final boolean crystalInvulnerable;
	private final List<EndSpikeFeature.Spike> spikes;
	@Nullable
	private final BlockPos crystalBeamTarget;

	public EndPillarFeatureConfig(boolean bl, List<EndSpikeFeature.Spike> list, @Nullable BlockPos blockPos) {
		this.crystalInvulnerable = bl;
		this.spikes = list;
		this.crystalBeamTarget = blockPos;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("crystalInvulnerable"),
					dynamicOps.createBoolean(this.crystalInvulnerable),
					dynamicOps.createString("spikes"),
					dynamicOps.createList(this.spikes.stream().map(spike -> spike.serialize(dynamicOps).getValue())),
					dynamicOps.createString("crystalBeamTarget"),
					this.crystalBeamTarget == null
						? dynamicOps.createList(Stream.empty())
						: dynamicOps.createList(
							IntStream.of(new int[]{this.crystalBeamTarget.getX(), this.crystalBeamTarget.getY(), this.crystalBeamTarget.getZ()}).mapToObj(dynamicOps::createInt)
						)
				)
			)
		);
	}

	public static <T> EndPillarFeatureConfig deserialize(Dynamic<T> dynamic) {
		List<EndSpikeFeature.Spike> list = (List<EndSpikeFeature.Spike>)((Stream)dynamic.get("spikes").flatMap(Dynamic::getStream).orElse(Stream.empty()))
			.map(EndSpikeFeature.Spike::deserialize)
			.collect(Collectors.toList());
		List<Number> list2 = (List<Number>)((Stream)dynamic.get("crystalBeamTarget")
				.flatMap(Dynamic::getStream)
				.map(stream -> stream.map(dynamicx -> dynamicx.getNumberValue(0)))
				.orElseGet(Stream::empty))
			.collect(Collectors.toList());
		BlockPos blockPos;
		if (list2.size() == 3) {
			blockPos = new BlockPos(((Number)list2.get(0)).intValue(), ((Number)list2.get(1)).intValue(), ((Number)list2.get(2)).intValue());
		} else {
			blockPos = null;
		}

		return new EndPillarFeatureConfig(dynamic.getBoolean("crystalInvulnerable"), list, blockPos);
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
