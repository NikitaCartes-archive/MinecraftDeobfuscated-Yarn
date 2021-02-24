/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.village.TradeOfferList;

public class SetTradeOffersS2CPacket
implements Packet<ClientPlayPacketListener> {
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

    public SetTradeOffersS2CPacket(PacketByteBuf packetByteBuf) {
        this.syncId = packetByteBuf.readVarInt();
        this.recipes = TradeOfferList.fromPacket(packetByteBuf);
        this.levelProgress = packetByteBuf.readVarInt();
        this.experience = packetByteBuf.readVarInt();
        this.leveled = packetByteBuf.readBoolean();
        this.refreshable = packetByteBuf.readBoolean();
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

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onSetTradeOffers(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getSyncId() {
        return this.syncId;
    }

    @Environment(value=EnvType.CLIENT)
    public TradeOfferList getOffers() {
        return this.recipes;
    }

    @Environment(value=EnvType.CLIENT)
    public int getLevelProgress() {
        return this.levelProgress;
    }

    @Environment(value=EnvType.CLIENT)
    public int getExperience() {
        return this.experience;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isLeveled() {
        return this.leveled;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isRefreshable() {
        return this.refreshable;
    }
}

