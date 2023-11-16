package net.minecraft.scoreboard.number;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.PacketByteBuf;

public interface NumberFormatType<T extends NumberFormat> {
	MapCodec<T> getCodec();

	void toBuf(PacketByteBuf buf, T format);

	T fromBuf(PacketByteBuf buf);
}
