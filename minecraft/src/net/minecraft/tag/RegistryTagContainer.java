package net.minecraft.tag;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class RegistryTagContainer<T> extends TagContainer<T> {
	private final Registry<T> registry;

	public RegistryTagContainer(Registry<T> registry, String string, String string2) {
		super(registry::getOrEmpty, string, false, string2);
		this.registry = registry;
	}

	public void toPacket(PacketByteBuf packetByteBuf) {
		Map<Identifier, Tag<T>> map = this.getEntries();
		packetByteBuf.writeVarInt(map.size());

		for (Entry<Identifier, Tag<T>> entry : map.entrySet()) {
			packetByteBuf.writeIdentifier((Identifier)entry.getKey());
			packetByteBuf.writeVarInt(((Tag)entry.getValue()).values().size());

			for (T object : ((Tag)entry.getValue()).values()) {
				packetByteBuf.writeVarInt(this.registry.getRawId(object));
			}
		}
	}

	public void fromPacket(PacketByteBuf packetByteBuf) {
		Map<Identifier, Tag<T>> map = Maps.<Identifier, Tag<T>>newHashMap();
		int i = packetByteBuf.readVarInt();

		for (int j = 0; j < i; j++) {
			Identifier identifier = packetByteBuf.readIdentifier();
			int k = packetByteBuf.readVarInt();
			Tag.Builder<T> builder = Tag.Builder.create();

			for (int l = 0; l < k; l++) {
				builder.add(this.registry.get(packetByteBuf.readVarInt()));
			}

			map.put(identifier, builder.build(identifier));
		}

		this.setEntries(map);
	}
}
