/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import java.util.Map;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagPacketSerializer;

public class SynchronizeTagsS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final Map<RegistryKey<? extends Registry<?>>, TagPacketSerializer.Serialized> groups;

    public SynchronizeTagsS2CPacket(Map<RegistryKey<? extends Registry<?>>, TagPacketSerializer.Serialized> groups) {
        this.groups = groups;
    }

    public SynchronizeTagsS2CPacket(PacketByteBuf buf2) {
        this.groups = buf2.readMap(buf -> RegistryKey.ofRegistry(buf.readIdentifier()), TagPacketSerializer.Serialized::fromBuf);
    }

    @Override
    public void write(PacketByteBuf buf2) {
        buf2.writeMap(this.groups, (buf, registryKey) -> buf.writeIdentifier(registryKey.getValue()), (buf, serializedGroup) -> serializedGroup.writeBuf((PacketByteBuf)buf));
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onSynchronizeTags(this);
    }

    public Map<RegistryKey<? extends Registry<?>>, TagPacketSerializer.Serialized> getGroups() {
        return this.groups;
    }
}

