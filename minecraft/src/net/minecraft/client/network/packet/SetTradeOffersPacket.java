package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.village.TraderOfferList;

public class SetTradeOffersPacket implements Packet<ClientPlayPacketListener> {
	private int syncId;
	private TraderOfferList recipes;
	private int levelProgress;
	private int experience;
	private boolean leveled;
	private boolean refreshable;

	public SetTradeOffersPacket() {
	}

	public SetTradeOffersPacket(int syncId, TraderOfferList recipes, int levelProgress, int experience, boolean leveled, boolean refreshable) {
		this.syncId = syncId;
		this.recipes = recipes;
		this.levelProgress = levelProgress;
		this.experience = experience;
		this.leveled = leveled;
		this.refreshable = refreshable;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.syncId = buf.readVarInt();
		this.recipes = TraderOfferList.fromPacket(buf);
		this.levelProgress = buf.readVarInt();
		this.experience = buf.readVarInt();
		this.leveled = buf.readBoolean();
		this.refreshable = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
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
	public TraderOfferList getOffers() {
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
	public boolean method_20722() {
		return this.refreshable;
	}
}
