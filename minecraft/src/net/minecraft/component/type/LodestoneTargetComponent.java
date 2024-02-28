package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.poi.PointOfInterestTypes;

public record LodestoneTargetComponent(GlobalPos pos, boolean tracked) {
	public static final Codec<LodestoneTargetComponent> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					GlobalPos.MAP_CODEC.forGetter(LodestoneTargetComponent::pos),
					Codecs.createStrictOptionalFieldCodec(Codec.BOOL, "tracked", true).forGetter(LodestoneTargetComponent::tracked)
				)
				.apply(instance, LodestoneTargetComponent::new)
	);
	public static final PacketCodec<ByteBuf, LodestoneTargetComponent> PACKET_CODEC = PacketCodec.tuple(
		GlobalPos.PACKET_CODEC, LodestoneTargetComponent::pos, PacketCodecs.BOOL, LodestoneTargetComponent::tracked, LodestoneTargetComponent::new
	);

	public boolean isInvalid(ServerWorld world) {
		if (!this.tracked) {
			return false;
		} else if (this.pos.dimension() != world.getRegistryKey()) {
			return false;
		} else {
			BlockPos blockPos = this.pos.pos();
			return !world.isInBuildLimit(blockPos) || !world.getPointOfInterestStorage().hasTypeAt(PointOfInterestTypes.LODESTONE, blockPos);
		}
	}
}
