package net.minecraft.network.handler;

import io.netty.handler.codec.EncoderException;

public class PacketEncoderException extends EncoderException {
	public PacketEncoderException(Throwable cause) {
		super(cause);
	}
}
