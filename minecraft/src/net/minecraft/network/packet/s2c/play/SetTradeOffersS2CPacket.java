package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.village.TradeOfferList;

public class SetTradeOffersS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int syncId;
	private final TradeOfferList offers;
	private final int levelProgress;
	private final int experience;
	private final boolean leveled;
	private final boolean refreshable;

	public SetTradeOffersS2CPacket(int syncId, TradeOfferList offers, int levelProgress, int experience, boolean leveled, boolean refreshable) {
		this.syncId = syncId;
		this.offers = offers;
		this.levelProgress = levelProgress;
		this.experience = experience;
		this.leveled = leveled;
		this.refreshable = refreshable;
	}

	public SetTradeOffersS2CPacket(PacketByteBuf buf) {
		this.syncId = buf.readVarInt();
		this.offers = TradeOfferList.fromPacket(buf);
		this.levelProgress = buf.readVarInt();
		this.experience = buf.readVarInt();
		this.leveled = buf.readBoolean();
		this.refreshable = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.syncId);
		this.offers.toPacket(buf);
		buf.writeVarInt(this.levelProgress);
		buf.writeVarInt(this.experience);
		buf.writeBoolean(this.leveled);
		buf.writeBoolean(this.refreshable);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onSetTradeOffers(this);
	}

	public int getSyncId() {
		return this.syncId;
	}

	public TradeOfferList getOffers() {
		return this.offers;
	}

	public int getLevelProgress() {
		return this.levelProgress;
	}

	public int getExperience() {
		return this.experience;
	}

	public boolean isLeveled() {
		return this.leveled;
	}

	public boolean isRefreshable() {
		return this.refreshable;
	}
}
