package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.UUID;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Uuids;

public record class_8375(Map<UUID, class_8388> voters) {
	public static final class_8375 field_43991 = new class_8375(Map.of());
	public static final Codec<class_8375> field_43992 = RecordCodecBuilder.create(
		instance -> instance.group(Codec.unboundedMap(Uuids.STRING_CODEC, class_8388.field_44026).fieldOf("voters").forGetter(class_8375::voters))
				.apply(instance, class_8375::new)
	);
	public static final PacketByteBuf.PacketReader<class_8375> field_43993 = packetByteBuf -> new class_8375(
			packetByteBuf.readMap(PacketByteBuf::readUuid, class_8388.field_44027)
		);
	public static final PacketByteBuf.PacketWriter<class_8375> field_43994 = (packetByteBuf, arg) -> packetByteBuf.writeMap(
			arg.voters, PacketByteBuf::writeUuid, class_8388.field_44028
		);

	public int method_50507() {
		return this.voters.size();
	}

	public int method_50512() {
		return this.voters.values().stream().mapToInt(class_8388::voteCount).sum();
	}

	public static class_8375 method_50509(UUID uUID, class_8388 arg) {
		return new class_8375(Map.of(uUID, arg));
	}
}
