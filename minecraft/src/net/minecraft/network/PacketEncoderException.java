package net.minecraft.network;

import io.netty.handler.codec.EncoderException;

public class PacketEncoderException extends EncoderException {
	public PacketEncoderException(Throwable cause) {
		super(cause);
	}
}
