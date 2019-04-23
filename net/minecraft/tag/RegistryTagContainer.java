/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Map;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class RegistryTagContainer<T>
extends TagContainer<T> {
    private final Registry<T> registry;

    public RegistryTagContainer(Registry<T> registry, String string, String string2) {
        super(registry::getOrEmpty, string, false, string2);
        this.registry = registry;
    }

    public void toPacket(PacketByteBuf packetByteBuf) {
        packetByteBuf.writeVarInt(this.getEntries().size());
        for (Map.Entry entry : this.getEntries().entrySet()) {
            packetByteBuf.writeIdentifier(entry.getKey());
            packetByteBuf.writeVarInt(entry.getValue().values().size());
            for (Object object : entry.getValue().values()) {
                packetByteBuf.writeVarInt(this.registry.getRawId(object));
            }
        }
    }

    public void fromPacket(PacketByteBuf packetByteBuf) {
        int i = packetByteBuf.readVarInt();
        for (int j = 0; j < i; ++j) {
            Identifier identifier = packetByteBuf.readIdentifier();
            int k = packetByteBuf.readVarInt();
            ArrayList list = Lists.newArrayList();
            for (int l = 0; l < k; ++l) {
                list.add(this.registry.get(packetByteBuf.readVarInt()));
            }
            this.getEntries().put(identifier, Tag.Builder.create().add(list).build(identifier));
        }
    }
}

