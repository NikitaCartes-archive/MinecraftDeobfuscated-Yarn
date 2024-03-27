package net.minecraft.network.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.command.argument.SignedArgumentList;
import net.minecraft.network.PacketByteBuf;

/**
 * A record holding the signatures for all signable arguments of an executed command.
 * 
 * @see SignedCommandArguments
 */
public record ArgumentSignatureDataMap(List<ArgumentSignatureDataMap.Entry> entries) {
	public static final ArgumentSignatureDataMap EMPTY = new ArgumentSignatureDataMap(List.of());
	private static final int MAX_ARGUMENTS = 8;
	private static final int MAX_ARGUMENT_NAME_LENGTH = 16;

	public ArgumentSignatureDataMap(PacketByteBuf buf) {
		this(buf.readCollection(PacketByteBuf.getMaxValidator(ArrayList::new, 8), ArgumentSignatureDataMap.Entry::new));
	}

	public void write(PacketByteBuf buf) {
		buf.writeCollection(this.entries, (buf2, entry) -> entry.write(buf2));
	}

	/**
	 * {@return the signature map with {@code arguments} signed with
	 * {@code signer}}
	 */
	public static ArgumentSignatureDataMap sign(SignedArgumentList<?> arguments, ArgumentSignatureDataMap.ArgumentSigner signer) {
		List<ArgumentSignatureDataMap.Entry> list = arguments.arguments().stream().map(argument -> {
			MessageSignatureData messageSignatureData = signer.sign(argument.value());
			return messageSignatureData != null ? new ArgumentSignatureDataMap.Entry(argument.getNodeName(), messageSignatureData) : null;
		}).filter(Objects::nonNull).toList();
		return new ArgumentSignatureDataMap(list);
	}

	/**
	 * A functional interface that signs an argument of a command.
	 */
	@FunctionalInterface
	public interface ArgumentSigner {
		@Nullable
		MessageSignatureData sign(String value);
	}

	/**
	 * An entry of the signatures map, consisting of the argument's name and signature data.
	 */
	public static record Entry(String name, MessageSignatureData signature) {
		public Entry(PacketByteBuf buf) {
			this(buf.readString(16), MessageSignatureData.fromBuf(buf));
		}

		public void write(PacketByteBuf buf) {
			buf.writeString(this.name, 16);
			MessageSignatureData.write(buf, this.signature);
		}
	}
}
