package net.minecraft.util.math;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;

public record GlobalPos(RegistryKey<World> dimension, BlockPos pos) {
	public static final MapCodec<GlobalPos> MAP_CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(World.CODEC.fieldOf("dimension").forGetter(GlobalPos::dimension), BlockPos.CODEC.fieldOf("pos").forGetter(GlobalPos::pos))
				.apply(instance, GlobalPos::create)
	);
	public static final Codec<GlobalPos> CODEC = MAP_CODEC.codec();
	public static final PacketCodec<ByteBuf, GlobalPos> PACKET_CODEC = PacketCodec.tuple(
		RegistryKey.createPacketCodec(RegistryKeys.WORLD), GlobalPos::dimension, BlockPos.PACKET_CODEC, GlobalPos::pos, GlobalPos::create
	);

	public static GlobalPos create(RegistryKey<World> dimension, BlockPos pos) {
		return new GlobalPos(dimension, pos);
	}

	public String toString() {
		return this.dimension + " " + this.pos;
	}
}
