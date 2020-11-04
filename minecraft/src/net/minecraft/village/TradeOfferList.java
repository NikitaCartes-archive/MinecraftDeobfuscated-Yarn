package net.minecraft.village;

import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.PacketByteBuf;

public class TradeOfferList extends ArrayList<TradeOffer> {
	public TradeOfferList() {
	}

	public TradeOfferList(CompoundTag compoundTag) {
		ListTag listTag = compoundTag.getList("Recipes", 10);

		for (int i = 0; i < listTag.size(); i++) {
			this.add(new TradeOffer(listTag.getCompound(i)));
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

	public void toPacket(PacketByteBuf buffer) {
		buffer.writeByte((byte)(this.size() & 0xFF));

		for (int i = 0; i < this.size(); i++) {
			TradeOffer tradeOffer = (TradeOffer)this.get(i);
			buffer.writeItemStack(tradeOffer.getOriginalFirstBuyItem());
			buffer.writeItemStack(tradeOffer.getMutableSellItem());
			ItemStack itemStack = tradeOffer.getSecondBuyItem();
			buffer.writeBoolean(!itemStack.isEmpty());
			if (!itemStack.isEmpty()) {
				buffer.writeItemStack(itemStack);
			}

			buffer.writeBoolean(tradeOffer.isDisabled());
			buffer.writeInt(tradeOffer.getUses());
			buffer.writeInt(tradeOffer.getMaxUses());
			buffer.writeInt(tradeOffer.getMerchantExperience());
			buffer.writeInt(tradeOffer.getSpecialPrice());
			buffer.writeFloat(tradeOffer.getPriceMultiplier());
			buffer.writeInt(tradeOffer.getDemandBonus());
		}
	}

	public static TradeOfferList fromPacket(PacketByteBuf byteBuf) {
		TradeOfferList tradeOfferList = new TradeOfferList();
		int i = byteBuf.readByte() & 255;

		for (int j = 0; j < i; j++) {
			ItemStack itemStack = byteBuf.readItemStack();
			ItemStack itemStack2 = byteBuf.readItemStack();
			ItemStack itemStack3 = ItemStack.EMPTY;
			if (byteBuf.readBoolean()) {
				itemStack3 = byteBuf.readItemStack();
			}

			boolean bl = byteBuf.readBoolean();
			int k = byteBuf.readInt();
			int l = byteBuf.readInt();
			int m = byteBuf.readInt();
			int n = byteBuf.readInt();
			float f = byteBuf.readFloat();
			int o = byteBuf.readInt();
			TradeOffer tradeOffer = new TradeOffer(itemStack, itemStack3, itemStack2, k, l, m, f, o);
			if (bl) {
				tradeOffer.clearUses();
			}

			tradeOffer.setSpecialPrice(n);
			tradeOfferList.add(tradeOffer);
		}

		return tradeOfferList;
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
