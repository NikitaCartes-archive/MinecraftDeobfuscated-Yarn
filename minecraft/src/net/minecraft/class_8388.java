package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Comparator;
import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.dynamic.Codecs;

public record class_8388(Text displayName, int voteCount) {
	public static final Comparator<class_8388> field_44025 = Comparator.comparing(class_8388::voteCount);
	public static final Codec<class_8388> field_44026 = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.TEXT.fieldOf("display_name").forGetter(class_8388::displayName), Codec.INT.fieldOf("vote_count").forGetter(class_8388::voteCount)
				)
				.apply(instance, class_8388::new)
	);
	public static final PacketByteBuf.PacketReader<class_8388> field_44027 = packetByteBuf -> {
		Text text = packetByteBuf.readText();
		int i = packetByteBuf.readVarInt();
		return new class_8388(text, i);
	};
	public static final PacketByteBuf.PacketWriter<class_8388> field_44028 = (packetByteBuf, arg) -> {
		packetByteBuf.writeText(arg.displayName);
		packetByteBuf.writeVarInt(arg.voteCount);
	};

	public static class_8388 method_50581(@Nullable class_8388 arg, Text text, int i) {
		int j = arg != null ? arg.voteCount : 0;
		return new class_8388(text, j + i);
	}
}
