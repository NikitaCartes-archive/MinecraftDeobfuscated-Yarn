package net.minecraft.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.PositionSourceType;

public class Vibration {
	public static final Codec<Vibration> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					BlockPos.CODEC.fieldOf("origin").forGetter(vibration -> vibration.origin),
					PositionSource.CODEC.fieldOf("destination").forGetter(vibration -> vibration.destination),
					Codec.INT.fieldOf("arrival_in_ticks").forGetter(vibration -> vibration.arrivalInTicks)
				)
				.apply(instance, Vibration::new)
	);
	private final BlockPos origin;
	private final PositionSource destination;
	private final int arrivalInTicks;

	public Vibration(BlockPos origin, PositionSource destination, int arrivalInTicks) {
		this.origin = origin;
		this.destination = destination;
		this.arrivalInTicks = arrivalInTicks;
	}

	public int getArrivalInTicks() {
		return this.arrivalInTicks;
	}

	public BlockPos getOrigin() {
		return this.origin;
	}

	public PositionSource getDestination() {
		return this.destination;
	}

	public static Vibration readFromBuf(PacketByteBuf buf) {
		BlockPos blockPos = buf.readBlockPos();
		PositionSource positionSource = PositionSourceType.read(buf);
		int i = buf.readVarInt();
		return new Vibration(blockPos, positionSource, i);
	}

	public static void writeToBuf(PacketByteBuf buf, Vibration vibration) {
		buf.writeBlockPos(vibration.origin);
		PositionSourceType.write(vibration.destination, buf);
		buf.writeVarInt(vibration.arrivalInTicks);
	}
}
