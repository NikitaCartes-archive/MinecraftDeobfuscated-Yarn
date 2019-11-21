/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.village.TraderOfferList;

public class SetTradeOffersPacket
implements Packet<ClientPlayPacketListener> {
    private int syncId;
    private TraderOfferList recipes;
    private int levelProgress;
    private int experience;
    private boolean leveled;
    private boolean refreshable;

    public SetTradeOffersPacket() {
    }

    public SetTradeOffersPacket(int i, TraderOfferList traderOfferList, int j, int k, boolean bl, boolean bl2) {
        this.syncId = i;
        this.recipes = traderOfferList;
        this.levelProgress = j;
        this.experience = k;
        this.leveled = bl;
        this.refreshable = bl2;
    }

    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
        this.syncId = packetByteBuf.readVarInt();
        this.recipes = TraderOfferList.fromPacket(packetByteBuf);
        this.levelProgress = packetByteBuf.readVarInt();
        this.experience = packetByteBuf.readVarInt();
        this.leveled = packetByteBuf.readBoolean();
        this.refreshable = packetByteBuf.readBoolean();
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeVarInt(this.syncId);
        this.recipes.toPacket(packetByteBuf);
        packetByteBuf.writeVarInt(this.levelProgress);
        packetByteBuf.writeVarInt(this.experience);
        packetByteBuf.writeBoolean(this.leveled);
        packetByteBuf.writeBoolean(this.refreshable);
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
    public TraderOfferList getOffers() {
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
    public boolean method_20722() {
        return this.refreshable;
    }
}

