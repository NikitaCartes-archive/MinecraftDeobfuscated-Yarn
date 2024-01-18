package net.minecraft.scoreboard.number;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class BlankNumberFormat implements NumberFormat {
	public static final BlankNumberFormat INSTANCE = new BlankNumberFormat();
	public static final NumberFormatType<BlankNumberFormat> TYPE = new NumberFormatType<BlankNumberFormat>() {
		private static final MapCodec<BlankNumberFormat> CODEC = MapCodec.unit(BlankNumberFormat.INSTANCE);
		private static final PacketCodec<RegistryByteBuf, BlankNumberFormat> PACKET_CODEC = PacketCodec.unit(BlankNumberFormat.INSTANCE);

		@Override
		public MapCodec<BlankNumberFormat> getCodec() {
			return CODEC;
		}

		@Override
		public PacketCodec<RegistryByteBuf, BlankNumberFormat> getPacketCodec() {
			return PACKET_CODEC;
		}
	};

	@Override
	public MutableText format(int number) {
		return Text.empty();
	}

	@Override
	public NumberFormatType<BlankNumberFormat> getType() {
		return TYPE;
	}
}
