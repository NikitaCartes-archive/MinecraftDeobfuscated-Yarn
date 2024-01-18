package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.village.TradeOfferList;

public class SetTradeOffersS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<RegistryByteBuf, SetTradeOffersS2CPacket> CODEC = Packet.createCodec(
		SetTradeOffersS2CPacket::write, SetTradeOffersS2CPacket::new
	);
	private final int syncId;
	private final TradeOfferList offers;
	private final int levelProgress;
	private final int experience;
	private final boolean leveled;
	private final boolean refreshable;

	public SetTradeOffersS2CPacket(int syncId, TradeOfferList offers, int levelProgress, int experience, boolean leveled, boolean refreshable) {
		this.syncId = syncId;
		this.offers = offers.copy();
		this.levelProgress = levelProgress;
		this.experience = experience;
		this.leveled = leveled;
		this.refreshable = refreshable;
	}

	private SetTradeOffersS2CPacket(RegistryByteBuf buf) {
		this.syncId = buf.readVarInt();
		this.offers = TradeOfferList.PACKET_CODEC.decode(buf);
		this.levelProgress = buf.readVarInt();
		this.experience = buf.readVarInt();
		this.leveled = buf.readBoolean();
		this.refreshable = buf.readBoolean();
	}

	private void write(RegistryByteBuf buf) {
		buf.writeVarInt(this.syncId);
		TradeOfferList.PACKET_CODEC.encode(buf, this.offers);
		buf.writeVarInt(this.levelProgress);
		buf.writeVarInt(this.experience);
		buf.writeBoolean(this.leveled);
		buf.writeBoolean(this.refreshable);
	}

	@Override
	public PacketType<SetTradeOffersS2CPacket> getPacketId() {
		return PlayPackets.MERCHANT_OFFERS;
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
