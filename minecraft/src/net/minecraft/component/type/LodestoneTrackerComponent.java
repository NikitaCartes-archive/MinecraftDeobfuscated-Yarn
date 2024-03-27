package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.poi.PointOfInterestTypes;

public record LodestoneTrackerComponent(Optional<GlobalPos> target, boolean tracked) {
	public static final Codec<LodestoneTrackerComponent> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					GlobalPos.CODEC.optionalFieldOf("target").forGetter(LodestoneTrackerComponent::target),
					Codec.BOOL.optionalFieldOf("tracked", Boolean.valueOf(true)).forGetter(LodestoneTrackerComponent::tracked)
				)
				.apply(instance, LodestoneTrackerComponent::new)
	);
	public static final PacketCodec<ByteBuf, LodestoneTrackerComponent> PACKET_CODEC = PacketCodec.tuple(
		GlobalPos.PACKET_CODEC.collect(PacketCodecs::optional),
		LodestoneTrackerComponent::target,
		PacketCodecs.BOOL,
		LodestoneTrackerComponent::tracked,
		LodestoneTrackerComponent::new
	);

	public LodestoneTrackerComponent forWorld(ServerWorld world) {
		if (this.tracked && !this.target.isEmpty()) {
			if (((GlobalPos)this.target.get()).dimension() != world.getRegistryKey()) {
				return this;
			} else {
				BlockPos blockPos = ((GlobalPos)this.target.get()).pos();
				return world.isInBuildLimit(blockPos) && world.getPointOfInterestStorage().hasTypeAt(PointOfInterestTypes.LODESTONE, blockPos)
					? this
					: new LodestoneTrackerComponent(Optional.empty(), true);
			}
		} else {
			return this;
		}
	}
}
