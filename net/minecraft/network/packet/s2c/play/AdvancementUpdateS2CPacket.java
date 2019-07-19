/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class AdvancementUpdateS2CPacket
implements Packet<ClientPlayPacketListener> {
    private boolean clearCurrent;
    private Map<Identifier, Advancement.Task> toEarn;
    private Set<Identifier> toRemove;
    private Map<Identifier, AdvancementProgress> toSetProgress;

    public AdvancementUpdateS2CPacket() {
    }

    public AdvancementUpdateS2CPacket(boolean bl, Collection<Advancement> collection, Set<Identifier> set, Map<Identifier, AdvancementProgress> map) {
        this.clearCurrent = bl;
        this.toEarn = Maps.newHashMap();
        for (Advancement advancement : collection) {
            this.toEarn.put(advancement.getId(), advancement.createTask());
        }
        this.toRemove = set;
        this.toSetProgress = Maps.newHashMap(map);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onAdvancements(this);
    }

    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
        Identifier identifier;
        int j;
        this.clearCurrent = packetByteBuf.readBoolean();
        this.toEarn = Maps.newHashMap();
        this.toRemove = Sets.newLinkedHashSet();
        this.toSetProgress = Maps.newHashMap();
        int i = packetByteBuf.readVarInt();
        for (j = 0; j < i; ++j) {
            identifier = packetByteBuf.readIdentifier();
            Advancement.Task task = Advancement.Task.fromPacket(packetByteBuf);
            this.toEarn.put(identifier, task);
        }
        i = packetByteBuf.readVarInt();
        for (j = 0; j < i; ++j) {
            identifier = packetByteBuf.readIdentifier();
            this.toRemove.add(identifier);
        }
        i = packetByteBuf.readVarInt();
        for (j = 0; j < i; ++j) {
            identifier = packetByteBuf.readIdentifier();
            this.toSetProgress.put(identifier, AdvancementProgress.fromPacket(packetByteBuf));
        }
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeBoolean(this.clearCurrent);
        packetByteBuf.writeVarInt(this.toEarn.size());
        for (Map.Entry<Identifier, Advancement.Task> entry : this.toEarn.entrySet()) {
            Identifier identifier = entry.getKey();
            Advancement.Task task = entry.getValue();
            packetByteBuf.writeIdentifier(identifier);
            task.toPacket(packetByteBuf);
        }
        packetByteBuf.writeVarInt(this.toRemove.size());
        for (Identifier identifier : this.toRemove) {
            packetByteBuf.writeIdentifier(identifier);
        }
        packetByteBuf.writeVarInt(this.toSetProgress.size());
        for (Map.Entry entry : this.toSetProgress.entrySet()) {
            packetByteBuf.writeIdentifier((Identifier)entry.getKey());
            ((AdvancementProgress)entry.getValue()).toPacket(packetByteBuf);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public Map<Identifier, Advancement.Task> getAdvancementsToEarn() {
        return this.toEarn;
    }

    @Environment(value=EnvType.CLIENT)
    public Set<Identifier> getAdvancementIdsToRemove() {
        return this.toRemove;
    }

    @Environment(value=EnvType.CLIENT)
    public Map<Identifier, AdvancementProgress> getAdvancementsToProgress() {
        return this.toSetProgress;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldClearCurrent() {
        return this.clearCurrent;
    }
}

