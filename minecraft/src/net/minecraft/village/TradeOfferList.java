package net.minecraft.village;

import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public class TradeOfferList extends ArrayList<TradeOffer> {
	public static final PacketCodec<RegistryByteBuf, TradeOfferList> PACKET_CODEC = TradeOffer.PACKET_CODEC
		.collect(PacketCodecs.toCollection(TradeOfferList::new));

	public TradeOfferList() {
	}

	private TradeOfferList(int size) {
		super(size);
	}

	public TradeOfferList(NbtCompound nbt) {
		NbtList nbtList = nbt.getList("Recipes", NbtElement.COMPOUND_TYPE);

		for (int i = 0; i < nbtList.size(); i++) {
			this.add(new TradeOffer(nbtList.getCompound(i)));
		}
	}

	@Nullable
	public TradeOffer getValidOffer(ItemStack firstBuyItem, ItemStack secondBuyItem, int index) {
		if (index > 0 && index < this.size()) {
			TradeOffer tradeOffer = (TradeOffer)this.get(index);
			return tradeOffer.matchesBuyItems(firstBuyItem, secondBuyItem) ? tradeOffer : null;
		} else {
			for (int i = 0; i < this.size(); i++) {
				TradeOffer tradeOffer2 = (TradeOffer)this.get(i);
				if (tradeOffer2.matchesBuyItems(firstBuyItem, secondBuyItem)) {
					return tradeOffer2;
				}
			}

			return null;
		}
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

	public TradeOfferList copy() {
		TradeOfferList tradeOfferList = new TradeOfferList(this.size());

		for (TradeOffer tradeOffer : this) {
			tradeOfferList.add(tradeOffer.copy());
		}

		return tradeOfferList;
	}
}
