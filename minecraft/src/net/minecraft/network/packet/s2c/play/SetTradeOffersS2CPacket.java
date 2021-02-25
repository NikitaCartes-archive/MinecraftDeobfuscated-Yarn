package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.village.TradeOfferList;

public class SetTradeOffersS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int syncId;
	private final TradeOfferList recipes;
	private final int levelProgress;
	private final int experience;
	private final boolean leveled;
	private final boolean refreshable;

	public SetTradeOffersS2CPacket(int syncId, TradeOfferList recipes, int levelProgress, int experience, boolean leveled, boolean refreshable) {
		this.syncId = syncId;
		this.recipes = recipes;
		this.levelProgress = levelProgress;
		this.experience = experience;
		this.leveled = leveled;
		this.refreshable = refreshable;
	}

	public SetTradeOffersS2CPacket(PacketByteBuf buf) {
		this.syncId = buf.readVarInt();
		this.recipes = TradeOfferList.fromPacket(buf);
		this.levelProgress = buf.readVarInt();
		this.experience = buf.readVarInt();
		this.leveled = buf.readBoolean();
		this.refreshable = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.syncId);
		this.recipes.toPacket(buf);
		buf.writeVarInt(this.levelProgress);
		buf.writeVarInt(this.experience);
		buf.writeBoolean(this.leveled);
		buf.writeBoolean(this.refreshable);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onSetTradeOffers(this);
	}

	@Environment(EnvType.CLIENT)
	public int getSyncId() {
		return this.syncId;
	}

	@Environment(EnvType.CLIENT)
	public TradeOfferList getOffers() {
		return this.recipes;
	}

	@Environment(EnvType.CLIENT)
	public int getLevelProgress() {
		return this.levelProgress;
	}

	@Environment(EnvType.CLIENT)
	public int getExperience() {
		return this.experience;
	}

	@Environment(EnvType.CLIENT)
	public boolean isLeveled() {
		return this.leveled;
	}

	@Environment(EnvType.CLIENT)
	public boolean isRefreshable() {
		return this.refreshable;
	}
}
