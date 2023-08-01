package net.minecraft.network.packet.s2c.custom;

import java.lang.runtime.ObjectMethods;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public final class DebugGameTestClearCustomPayload extends Record implements CustomPayload {
	public static final Identifier ID = new Identifier("debug/game_test_clear");

	public DebugGameTestClearCustomPayload(PacketByteBuf buf) {
		this();
	}

	public DebugGameTestClearCustomPayload() {
	}

	@Override
	public void write(PacketByteBuf buf) {
	}

	@Override
	public Identifier id() {
		return ID;
	}

	public final String toString() {
		return ObjectMethods.bootstrap<"toString",DebugGameTestClearCustomPayload,"">(this);
	}

	public final int hashCode() {
		return ObjectMethods.bootstrap<"hashCode",DebugGameTestClearCustomPayload,"">(this);
	}

	public final boolean equals(Object object) {
		return ObjectMethods.bootstrap<"equals",DebugGameTestClearCustomPayload,"">(this, object);
	}
}
