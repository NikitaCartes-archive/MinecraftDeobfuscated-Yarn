package net.minecraft.scoreboard.number;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public class FixedNumberFormat implements NumberFormat {
	public static final NumberFormatType<FixedNumberFormat> TYPE = new NumberFormatType<FixedNumberFormat>() {
		private static final MapCodec<FixedNumberFormat> CODEC = TextCodecs.CODEC.fieldOf("value").xmap(FixedNumberFormat::new, format -> format.text);

		@Override
		public MapCodec<FixedNumberFormat> getCodec() {
			return CODEC;
		}

		public void toBuf(PacketByteBuf packetByteBuf, FixedNumberFormat fixedNumberFormat) {
			packetByteBuf.writeText(fixedNumberFormat.text);
		}

		public FixedNumberFormat fromBuf(PacketByteBuf packetByteBuf) {
			Text text = packetByteBuf.readUnlimitedText();
			return new FixedNumberFormat(text);
		}
	};
	final Text text;

	public FixedNumberFormat(Text text) {
		this.text = text;
	}

	@Override
	public MutableText format(int number) {
		return this.text.copy();
	}

	@Override
	public NumberFormatType<FixedNumberFormat> getType() {
		return TYPE;
	}
}
