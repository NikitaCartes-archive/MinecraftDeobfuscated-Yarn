package net.minecraft.village;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public class TradeOfferList extends ArrayList<TradeOffer> {
	public static final Codec<TradeOfferList> CODEC = TradeOffer.CODEC
		.listOf()
		.fieldOf("Recipes")
		.<TradeOfferList>xmap(TradeOfferList::new, Function.identity())
		.codec();
	public static final PacketCodec<RegistryByteBuf, TradeOfferList> PACKET_CODEC = TradeOffer.PACKET_CODEC
		.collect(PacketCodecs.toCollection(TradeOfferList::new));

	public TradeOfferList() {
	}

	private TradeOfferList(int size) {
		super(size);
	}

	private TradeOfferList(Collection<TradeOffer> tradeOffers) {
		super(tradeOffers);
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

	public TradeOfferList copy() {
		TradeOfferList tradeOfferList = new TradeOfferList(this.size());

		for (TradeOffer tradeOffer : this) {
			tradeOfferList.add(tradeOffer.copy());
		}

		return tradeOfferList;
	}
}
