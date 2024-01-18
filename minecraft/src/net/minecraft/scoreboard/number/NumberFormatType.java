package net.minecraft.scoreboard.number;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public interface NumberFormatType<T extends NumberFormat> {
	MapCodec<T> getCodec();

	PacketCodec<RegistryByteBuf, T> getPacketCodec();
}
