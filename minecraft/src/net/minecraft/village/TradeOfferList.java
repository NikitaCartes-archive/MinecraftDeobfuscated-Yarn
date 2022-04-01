package net.minecraft.village;

import java.util.ArrayList;
import net.minecraft.class_7317;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;

public class TradeOfferList extends ArrayList<TradeOffer> {
	public TradeOfferList() {
	}

	public TradeOfferList(NbtCompound nbt) {
		NbtList nbtList = nbt.getList("Recipes", NbtElement.COMPOUND_TYPE);

		for (int i = 0; i < nbtList.size(); i++) {
			this.add(new TradeOffer(nbtList.getCompound(i)));
		}
	}

	public void toPacket(PacketByteBuf buf) {
		buf.writeByte((byte)(this.size() & 0xFF));

		for (int i = 0; i < this.size(); i++) {
			TradeOffer tradeOffer = (TradeOffer)this.get(i);
			buf.encode(class_7317.field_38543, tradeOffer.method_42852());
			buf.encode(class_7317.field_38543, tradeOffer.getSellItem());
			buf.writeBoolean(tradeOffer.isDisabled());
			buf.writeInt(tradeOffer.getUses());
			buf.writeInt(tradeOffer.getMaxUses());
			buf.writeInt(tradeOffer.getMerchantExperience());
			buf.writeFloat(tradeOffer.getPriceMultiplier());
			buf.writeInt(tradeOffer.getDemandBonus());
		}
	}

	public static TradeOfferList fromPacket(PacketByteBuf buf) {
		TradeOfferList tradeOfferList = new TradeOfferList();
		int i = buf.readByte() & 255;

		for (int j = 0; j < i; j++) {
			class_7317 lv = buf.decode(class_7317.field_38543);
			class_7317 lv2 = buf.decode(class_7317.field_38543);
			boolean bl = buf.readBoolean();
			int k = buf.readInt();
			int l = buf.readInt();
			int m = buf.readInt();
			float f = buf.readFloat();
			int n = buf.readInt();
			TradeOffer tradeOffer = new TradeOffer(lv, lv2, k, l, m, f, n);
			if (bl) {
				tradeOffer.disable();
			}

			tradeOfferList.add(tradeOffer);
		}

		return tradeOfferList;
	}

	public NbtCompound toNbt() {
		NbtCompound nbtCompound = new NbtCompound();
		NbtList nbtList = new NbtList();

		for (int i = 0; i < this.size(); i++) {
			TradeOffer tradeOffer = (TradeOffer)this.get(i);
			nbtList.add(tradeOffer.toNbt());
		}

		nbtCompound.put("Recipes", nbtList);
		return nbtCompound;
	}
}
