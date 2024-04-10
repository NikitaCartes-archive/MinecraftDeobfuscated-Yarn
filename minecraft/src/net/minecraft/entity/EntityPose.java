package net.minecraft.entity;

import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.function.ValueLists;

public enum EntityPose {
	STANDING(0),
	FALL_FLYING(1),
	SLEEPING(2),
	SWIMMING(3),
	SPIN_ATTACK(4),
	CROUCHING(5),
	LONG_JUMPING(6),
	DYING(7),
	CROAKING(8),
	USING_TONGUE(9),
	SITTING(10),
	ROARING(11),
	SNIFFING(12),
	EMERGING(13),
	DIGGING(14),
	SLIDING(15),
	SHOOTING(16),
	INHALING(17);

	public static final IntFunction<EntityPose> INDEX_TO_VALUE = ValueLists.createIdToValueFunction(
		EntityPose::getIndex, values(), ValueLists.OutOfBoundsHandling.ZERO
	);
	public static final PacketCodec<ByteBuf, EntityPose> PACKET_CODEC = PacketCodecs.indexed(INDEX_TO_VALUE, EntityPose::getIndex);
	private final int index;

	private EntityPose(final int index) {
		this.index = index;
	}

	public int getIndex() {
		return this.index;
	}
}
