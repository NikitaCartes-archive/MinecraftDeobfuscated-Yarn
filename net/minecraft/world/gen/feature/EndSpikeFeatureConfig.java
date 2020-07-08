/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import org.jetbrains.annotations.Nullable;

public class EndSpikeFeatureConfig
implements FeatureConfig {
    public static final Codec<EndSpikeFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.BOOL.fieldOf("crystal_invulnerable")).orElse(false).forGetter(endSpikeFeatureConfig -> endSpikeFeatureConfig.crystalInvulnerable), ((MapCodec)EndSpikeFeature.Spike.CODEC.listOf().fieldOf("spikes")).forGetter(endSpikeFeatureConfig -> endSpikeFeatureConfig.spikes), BlockPos.field_25064.optionalFieldOf("crystal_beam_target").forGetter(endSpikeFeatureConfig -> Optional.ofNullable(endSpikeFeatureConfig.crystalBeamTarget))).apply((Applicative<EndSpikeFeatureConfig, ?>)instance, EndSpikeFeatureConfig::new));
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
        this.crystalBeamTarget = optional.orElse(null);
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

