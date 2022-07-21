package net.minecraft.network.message;

import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_7644;
import net.minecraft.command.argument.SignedArgumentType;
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

	/**
	 * {@return the signature data for {@code argumentName}, or {@code null} if the
	 * argument name is not present in this signatures}
	 */
	public MessageSignatureData get(String argumentName) {
		for (ArgumentSignatureDataMap.Entry entry : this.entries) {
			if (entry.name.equals(argumentName)) {
				return entry.signature;
			}
		}

		return MessageSignatureData.EMPTY;
	}

	public void write(PacketByteBuf buf) {
		buf.writeCollection(this.entries, (buf2, entry) -> entry.write(buf2));
	}

	/**
	 * {@return whether to preview {@code parseResults}}
	 * 
	 * <p>This returns {@code true} if the parsed arguments include {@link
	 * SignedArgumentType}.
	 */
	public static boolean shouldPreview(class_7644<?> arg) {
		return arg.arguments().stream().anyMatch(argx -> argx.previewType() instanceof SignedArgumentType);
	}

	/**
	 * {@return the signature map with arguments from {@code builder} signed with
	 * {@code signer}}
	 */
	public static ArgumentSignatureDataMap sign(class_7644<?> arg, ArgumentSignatureDataMap.ArgumentSigner signer) {
		List<ArgumentSignatureDataMap.Entry> list = method_45020(arg).stream().map(entry -> {
			MessageSignatureData messageSignatureData = signer.sign((String)entry.getFirst(), (String)entry.getSecond());
			return new ArgumentSignatureDataMap.Entry((String)entry.getFirst(), messageSignatureData);
		}).toList();
		return new ArgumentSignatureDataMap(list);
	}

	public static List<Pair<String, String>> method_45020(class_7644<?> arg) {
		List<Pair<String, String>> list = new ArrayList();

		for (class_7644.class_7645<?> lv : arg.arguments()) {
			if (lv.previewType() instanceof SignedArgumentType<?> signedArgumentType) {
				String string = resultToString(signedArgumentType, lv.parsedValue());
				list.add(Pair.of(lv.method_45046(), string));
			}
		}

		return list;
	}

	private static <T> String resultToString(SignedArgumentType<T> type, ParsedArgument<?, ?> argument) {
		return type.toSignedString((T)argument.getResult());
	}

	/**
	 * A functional interface that signs an argument of a command.
	 */
	@FunctionalInterface
	public interface ArgumentSigner {
		MessageSignatureData sign(String argumentName, String value);
	}

	/**
	 * An entry of the signatures map, consisting of the argument's name and signature data.
	 */
	public static record Entry(String name, MessageSignatureData signature) {

		public Entry(PacketByteBuf buf) {
			this(buf.readString(16), new MessageSignatureData(buf));
		}

		public void write(PacketByteBuf buf) {
			buf.writeString(this.name, 16);
			this.signature.write(buf);
		}
	}
}
