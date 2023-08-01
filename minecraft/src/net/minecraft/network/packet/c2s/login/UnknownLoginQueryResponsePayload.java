package net.minecraft.network.packet.c2s.login;

import java.lang.runtime.ObjectMethods;
import net.minecraft.network.PacketByteBuf;

public final class UnknownLoginQueryResponsePayload extends Record implements LoginQueryResponsePayload {
	public static final UnknownLoginQueryResponsePayload INSTANCE = new UnknownLoginQueryResponsePayload();

	@Override
	public void write(PacketByteBuf buf) {
	}

	public final String toString() {
		return ObjectMethods.bootstrap<"toString",UnknownLoginQueryResponsePayload,"">(this);
	}

	public final int hashCode() {
		return ObjectMethods.bootstrap<"hashCode",UnknownLoginQueryResponsePayload,"">(this);
	}

	public final boolean equals(Object object) {
		return ObjectMethods.bootstrap<"equals",UnknownLoginQueryResponsePayload,"">(this, object);
	}
}
