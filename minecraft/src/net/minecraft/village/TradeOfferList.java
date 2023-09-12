package net.minecraft.village;

import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;

public class TradeOfferList extends ArrayList<TradeOffer> {
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

	public void toPacket(PacketByteBuf buf) {
		buf.writeCollection(this, (buf2, offer) -> {
			buf2.writeItemStack(offer.getOriginalFirstBuyItem());
			buf2.writeItemStack(offer.getSellItem());
			buf2.writeItemStack(offer.getSecondBuyItem());
			buf2.writeBoolean(offer.isDisabled());
			buf2.writeInt(offer.getUses());
			buf2.writeInt(offer.getMaxUses());
			buf2.writeInt(offer.getMerchantExperience());
			buf2.writeInt(offer.getSpecialPrice());
			buf2.writeFloat(offer.getPriceMultiplier());
			buf2.writeInt(offer.getDemandBonus());
		});
	}

	public static TradeOfferList fromPacket(PacketByteBuf buf) {
		return buf.readCollection(TradeOfferList::new, buf2 -> {
			ItemStack itemStack = buf2.readItemStack();
			ItemStack itemStack2 = buf2.readItemStack();
			ItemStack itemStack3 = buf2.readItemStack();
			boolean bl = buf2.readBoolean();
			int i = buf2.readInt();
			int j = buf2.readInt();
			int k = buf2.readInt();
			int l = buf2.readInt();
			float f = buf2.readFloat();
			int m = buf2.readInt();
			TradeOffer tradeOffer = new TradeOffer(itemStack, itemStack3, itemStack2, i, j, k, f, m);
			if (bl) {
				tradeOffer.disable();
			}

			tradeOffer.setSpecialPrice(l);
			return tradeOffer;
		});
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
