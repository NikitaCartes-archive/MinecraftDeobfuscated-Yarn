package net.minecraft.scoreboard.number;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.RegistryByteBuf;

public interface NumberFormatType<T extends NumberFormat> {
	MapCodec<T> getCodec();

	PacketCodec<RegistryByteBuf, T> getPacketCodec();
}
