package net.minecraft.client.report.log;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.message.MessageHeader;
import net.minecraft.network.message.MessageSignatureData;

/**
 * An entry of {@link ChatLog} containing only the message header.
 */
@Environment(EnvType.CLIENT)
public interface HeaderEntry extends ChatLogEntry {
	static HeaderEntry.Impl of(MessageHeader header, MessageSignatureData headerSignature, byte[] bodyDigest) {
		return new HeaderEntry.Impl(header, headerSignature, bodyDigest);
	}

	MessageHeader header();

	MessageSignatureData headerSignature();

	byte[] bodyDigest();

	@Environment(EnvType.CLIENT)
	public static record Impl(MessageHeader header, MessageSignatureData headerSignature, byte[] bodyDigest) implements HeaderEntry {
	}
}
