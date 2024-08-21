package net.minecraft.item.consume;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public enum UseAction implements StringIdentifiable {
	NONE(0, "none"),
	EAT(1, "eat"),
	DRINK(2, "drink"),
	BLOCK(3, "block"),
	BOW(4, "bow"),
	SPEAR(5, "spear"),
	CROSSBOW(6, "crossbow"),
	SPYGLASS(7, "spyglass"),
	TOOT_HORN(8, "toot_horn"),
	BRUSH(9, "brush");

	private static final IntFunction<UseAction> BY_ID = ValueLists.createIdToValueFunction(UseAction::getId, values(), ValueLists.OutOfBoundsHandling.ZERO);
	public static final Codec<UseAction> CODEC = StringIdentifiable.createCodec(UseAction::values);
	public static final PacketCodec<ByteBuf, UseAction> PACKET_CODEC = PacketCodecs.indexed(BY_ID, UseAction::getId);
	private final int id;
	private final String name;

	private UseAction(final int id, final String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return this.id;
	}

	@Override
	public String asString() {
		return this.name;
	}
}
