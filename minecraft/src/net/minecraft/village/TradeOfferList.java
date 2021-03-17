package net.minecraft.village;

import java.util.ArrayList;
import javax.annotation.Nullable;
import net.fabricmc.yarn.constants.NbtTypeIds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;

public class TradeOfferList extends ArrayList<TradeOffer> {
	public TradeOfferList() {
	}

	public TradeOfferList(NbtCompound nbt) {
		NbtList nbtList = nbt.getList("Recipes", NbtTypeIds.COMPOUND);

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
		buf.writeByte((byte)(this.size() & 0xFF));

		for (int i = 0; i < this.size(); i++) {
			TradeOffer tradeOffer = (TradeOffer)this.get(i);
			buf.writeItemStack(tradeOffer.getOriginalFirstBuyItem());
			buf.writeItemStack(tradeOffer.getSellItem());
			ItemStack itemStack = tradeOffer.getSecondBuyItem();
			buf.writeBoolean(!itemStack.isEmpty());
			if (!itemStack.isEmpty()) {
				buf.writeItemStack(itemStack);
			}

			buf.writeBoolean(tradeOffer.isDisabled());
			buf.writeInt(tradeOffer.getUses());
			buf.writeInt(tradeOffer.getMaxUses());
			buf.writeInt(tradeOffer.getMerchantExperience());
			buf.writeInt(tradeOffer.getSpecialPrice());
			buf.writeFloat(tradeOffer.getPriceMultiplier());
			buf.writeInt(tradeOffer.getDemandBonus());
		}
	}

	public static TradeOfferList fromPacket(PacketByteBuf buf) {
		TradeOfferList tradeOfferList = new TradeOfferList();
		int i = buf.readByte() & 255;

		for (int j = 0; j < i; j++) {
			ItemStack itemStack = buf.readItemStack();
			ItemStack itemStack2 = buf.readItemStack();
			ItemStack itemStack3 = ItemStack.EMPTY;
			if (buf.readBoolean()) {
				itemStack3 = buf.readItemStack();
			}

			boolean bl = buf.readBoolean();
			int k = buf.readInt();
			int l = buf.readInt();
			int m = buf.readInt();
			int n = buf.readInt();
			float f = buf.readFloat();
			int o = buf.readInt();
			TradeOffer tradeOffer = new TradeOffer(itemStack, itemStack3, itemStack2, k, l, m, f, o);
			if (bl) {
				tradeOffer.disable();
			}

			tradeOffer.setSpecialPrice(n);
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
