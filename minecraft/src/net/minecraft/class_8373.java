package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Comparator;
import java.util.UUID;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Uuids;

public record class_8373(UUID voteId, int index) {
	public static final Comparator<class_8373> field_43985 = Comparator.comparing(class_8373::voteId).thenComparingInt(class_8373::index);
	public static final Codec<class_8373> field_43986 = RecordCodecBuilder.create(
		instance -> instance.group(Uuids.STRING_CODEC.fieldOf("uuid").forGetter(class_8373::voteId), Codec.INT.fieldOf("index").forGetter(class_8373::index))
				.apply(instance, class_8373::new)
	);
	public static final Codec<class_8373> field_43987 = Codec.STRING.comapFlatMap(string -> {
		int i = string.indexOf(58);
		if (i == -1) {
			return DataResult.error(() -> "No separator in " + string);
		} else {
			String string2 = string.substring(0, i);

			UUID uUID;
			try {
				uUID = UUID.fromString(string2);
			} catch (Exception var8) {
				return DataResult.error(() -> "Invalid UUID " + string2 + ": " + var8.getMessage());
			}

			String string3 = string.substring(i + 1);

			int j;
			try {
				j = Integer.parseInt(string3);
			} catch (Exception var7) {
				return DataResult.error(() -> "Invalid index " + string3 + ": " + var7.getMessage());
			}

			return DataResult.success(new class_8373(uUID, j));
		}
	}, arg -> arg.voteId + ":" + arg.index);
	public static final PacketByteBuf.PacketReader<class_8373> field_43988 = packetByteBuf -> {
		UUID uUID = packetByteBuf.readUuid();
		int i = packetByteBuf.readVarInt();
		return new class_8373(uUID, i);
	};
	public static final PacketByteBuf.PacketWriter<class_8373> field_43989 = (packetByteBuf, arg) -> {
		packetByteBuf.writeUuid(arg.voteId);
		packetByteBuf.writeVarInt(arg.index);
	};

	public String toString() {
		return this.voteId + ":" + this.index;
	}
}
