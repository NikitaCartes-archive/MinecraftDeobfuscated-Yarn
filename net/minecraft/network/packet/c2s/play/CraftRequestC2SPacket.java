/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.c2s.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;

public class CraftRequestC2SPacket
implements Packet<ServerPlayPacketListener> {
    private final int syncId;
    private final Identifier recipe;
    private final boolean craftAll;

    @Environment(value=EnvType.CLIENT)
    public CraftRequestC2SPacket(int syncId, Recipe<?> recipe, boolean craftAll) {
        this.syncId = syncId;
        this.recipe = recipe.getId();
        this.craftAll = craftAll;
    }

    public CraftRequestC2SPacket(PacketByteBuf buf) {
        this.syncId = buf.readByte();
        this.recipe = buf.readIdentifier();
        this.craftAll = buf.readBoolean();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeByte(this.syncId);
        buf.writeIdentifier(this.recipe);
        buf.writeBoolean(this.craftAll);
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onCraftRequest(this);
    }

    public int getSyncId() {
        return this.syncId;
    }

    public Identifier getRecipe() {
        return this.recipe;
    }

    public boolean shouldCraftAll() {
        return this.craftAll;
    }
}

