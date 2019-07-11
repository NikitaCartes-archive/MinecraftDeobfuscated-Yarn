package net.minecraft.village;

import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.PacketByteBuf;

public class TraderOfferList extends ArrayList<TradeOffer> {
	public TraderOfferList() {
	}

	public TraderOfferList(CompoundTag compoundTag) {
		ListTag listTag = compoundTag.getList("Recipes", 10);

		for (int i = 0; i < listTag.size(); i++) {
			this.add(new TradeOffer(listTag.getCompoundTag(i)));
		}
	}

	@Nullable
	public TradeOffer getValidRecipe(ItemStack itemStack, ItemStack itemStack2, int i) {
		if (i > 0 && i < this.size()) {
			TradeOffer tradeOffer = (TradeOffer)this.get(i);
			return tradeOffer.matchesBuyItems(itemStack, itemStack2) ? tradeOffer : null;
		} else {
			for (int j = 0; j < this.size(); j++) {
				TradeOffer tradeOffer2 = (TradeOffer)this.get(j);
				if (tradeOffer2.matchesBuyItems(itemStack, itemStack2)) {
					return tradeOffer2;
				}
			}

			return null;
		}
	}

	public void toPacket(PacketByteBuf packetByteBuf) {
		packetByteBuf.writeByte((byte)(this.size() & 0xFF));

		for (int i = 0; i < this.size(); i++) {
			TradeOffer tradeOffer = (TradeOffer)this.get(i);
			packetByteBuf.writeItemStack(tradeOffer.getOriginalFirstBuyItem());
			packetByteBuf.writeItemStack(tradeOffer.getMutableSellItem());
			ItemStack itemStack = tradeOffer.getSecondBuyItem();
			packetByteBuf.writeBoolean(!itemStack.isEmpty());
			if (!itemStack.isEmpty()) {
				packetByteBuf.writeItemStack(itemStack);
			}

			packetByteBuf.writeBoolean(tradeOffer.isDisabled());
			packetByteBuf.writeInt(tradeOffer.getUses());
			packetByteBuf.writeInt(tradeOffer.getMaxUses());
			packetByteBuf.writeInt(tradeOffer.getTraderExperience());
			packetByteBuf.writeInt(tradeOffer.getSpecialPrice());
			packetByteBuf.writeFloat(tradeOffer.getPriceMultiplier());
			packetByteBuf.writeInt(tradeOffer.method_21725());
		}
	}

	public static TraderOfferList fromPacket(PacketByteBuf packetByteBuf) {
		TraderOfferList traderOfferList = new TraderOfferList();
		int i = packetByteBuf.readByte() & 255;

		for (int j = 0; j < i; j++) {
			ItemStack itemStack = packetByteBuf.readItemStack();
			ItemStack itemStack2 = packetByteBuf.readItemStack();
			ItemStack itemStack3 = ItemStack.EMPTY;
			if (packetByteBuf.readBoolean()) {
				itemStack3 = packetByteBuf.readItemStack();
			}

			boolean bl = packetByteBuf.readBoolean();
			int k = packetByteBuf.readInt();
			int l = packetByteBuf.readInt();
			int m = packetByteBuf.readInt();
			int n = packetByteBuf.readInt();
			float f = packetByteBuf.readFloat();
			int o = packetByteBuf.readInt();
			TradeOffer tradeOffer = new TradeOffer(itemStack, itemStack3, itemStack2, k, l, m, f, o);
			if (bl) {
				tradeOffer.clearUses();
			}

			tradeOffer.setSpecialPrice(n);
			traderOfferList.add(tradeOffer);
		}

		return traderOfferList;
	}

	public CompoundTag toTag() {
		CompoundTag compoundTag = new CompoundTag();
		ListTag listTag = new ListTag();

		for (int i = 0; i < this.size(); i++) {
			TradeOffer tradeOffer = (TradeOffer)this.get(i);
			listTag.add(tradeOffer.toTag());
		}

		compoundTag.put("Recipes", listTag);
		return compoundTag;
	}
}
