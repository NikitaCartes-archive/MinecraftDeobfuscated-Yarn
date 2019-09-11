/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import com.google.common.collect.Maps;
import java.util.HashMap;
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
        Map map = this.getEntries();
        packetByteBuf.writeVarInt(map.size());
        for (Map.Entry entry : map.entrySet()) {
            packetByteBuf.writeIdentifier(entry.getKey());
            packetByteBuf.writeVarInt(entry.getValue().values().size());
            for (Object object : entry.getValue().values()) {
                packetByteBuf.writeVarInt(this.registry.getRawId(object));
            }
        }
    }

    public void fromPacket(PacketByteBuf packetByteBuf) {
        HashMap map = Maps.newHashMap();
        int i = packetByteBuf.readVarInt();
        for (int j = 0; j < i; ++j) {
            Identifier identifier = packetByteBuf.readIdentifier();
            int k = packetByteBuf.readVarInt();
            Tag.Builder builder = Tag.Builder.create();
            for (int l = 0; l < k; ++l) {
                builder.add(this.registry.get(packetByteBuf.readVarInt()));
            }
            map.put(identifier, builder.build(identifier));
        }
        this.setEntries(map);
    }
}

