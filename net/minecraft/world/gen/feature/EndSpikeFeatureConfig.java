/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import org.jetbrains.annotations.Nullable;

public class EndSpikeFeatureConfig
implements FeatureConfig {
    private final boolean crystalInvulnerable;
    private final List<EndSpikeFeature.Spike> spikes;
    @Nullable
    private final BlockPos crystalBeamTarget;

    public EndSpikeFeatureConfig(boolean bl, List<EndSpikeFeature.Spike> list, @Nullable BlockPos blockPos) {
        this.crystalInvulnerable = bl;
        this.spikes = list;
        this.crystalBeamTarget = blockPos;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic<Object>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("crystalInvulnerable"), dynamicOps.createBoolean(this.crystalInvulnerable), dynamicOps.createString("spikes"), dynamicOps.createList(this.spikes.stream().map(spike -> spike.serialize(dynamicOps).getValue())), dynamicOps.createString("crystalBeamTarget"), this.crystalBeamTarget == null ? dynamicOps.createList(Stream.empty()) : dynamicOps.createList(IntStream.of(this.crystalBeamTarget.getX(), this.crystalBeamTarget.getY(), this.crystalBeamTarget.getZ()).mapToObj(dynamicOps::createInt)))));
    }

    public static <T> EndSpikeFeatureConfig deserialize(Dynamic<T> dynamic2) {
        List<EndSpikeFeature.Spike> list = dynamic2.get("spikes").asList(EndSpikeFeature.Spike::deserialize);
        List<Integer> list2 = dynamic2.get("crystalBeamTarget").asList(dynamic -> dynamic.asInt(0));
        BlockPos blockPos = list2.size() == 3 ? new BlockPos(list2.get(0), list2.get(1), list2.get(2)) : null;
        return new EndSpikeFeatureConfig(dynamic2.get("crystalInvulnerable").asBoolean(false), list, blockPos);
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

