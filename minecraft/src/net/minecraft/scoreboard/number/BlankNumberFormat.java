package net.minecraft.scoreboard.number;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class BlankNumberFormat implements NumberFormat {
	public static final BlankNumberFormat INSTANCE = new BlankNumberFormat();
	public static final NumberFormatType<BlankNumberFormat> TYPE = new NumberFormatType<BlankNumberFormat>() {
		private static final MapCodec<BlankNumberFormat> CODEC = MapCodec.unit(BlankNumberFormat.INSTANCE);

		@Override
		public MapCodec<BlankNumberFormat> getCodec() {
			return CODEC;
		}

		public void toBuf(PacketByteBuf packetByteBuf, BlankNumberFormat blankNumberFormat) {
		}

		public BlankNumberFormat fromBuf(PacketByteBuf packetByteBuf) {
			return BlankNumberFormat.INSTANCE;
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
