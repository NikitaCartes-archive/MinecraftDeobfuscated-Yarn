/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;

public class CraftFailedResponseS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final int syncId;
    private final Identifier recipeId;

    public CraftFailedResponseS2CPacket(int syncId, Recipe<?> recipe) {
        this.syncId = syncId;
        this.recipeId = recipe.getId();
    }

    public CraftFailedResponseS2CPacket(PacketByteBuf buf) {
        this.syncId = buf.readByte();
        this.recipeId = buf.readIdentifier();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeByte(this.syncId);
        buf.writeIdentifier(this.recipeId);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onCraftFailedResponse(this);
    }

    @Environment(value=EnvType.CLIENT)
    public Identifier getRecipeId() {
        return this.recipeId;
    }

    @Environment(value=EnvType.CLIENT)
    public int getSyncId() {
        return this.syncId;
    }
}

