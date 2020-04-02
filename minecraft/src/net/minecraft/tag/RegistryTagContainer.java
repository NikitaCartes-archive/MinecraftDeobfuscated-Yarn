package net.minecraft.tag;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableSet.Builder;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RegistryTagContainer<T> extends TagContainer<T> {
	private final Registry<T> registry;

	public RegistryTagContainer(Registry<T> registry, String path, String type) {
		super(registry::getOrEmpty, path, type);
		this.registry = registry;
	}

	public void toPacket(PacketByteBuf buf) {
		Map<Identifier, Tag<T>> map = this.getEntries();
		buf.writeVarInt(map.size());

		for (Entry<Identifier, Tag<T>> entry : map.entrySet()) {
			buf.writeIdentifier((Identifier)entry.getKey());
			buf.writeVarInt(((Tag)entry.getValue()).values().size());

			for (T object : ((Tag)entry.getValue()).values()) {
				buf.writeVarInt(this.registry.getRawId(object));
			}
		}
	}

	public void fromPacket(PacketByteBuf buf) {
		Map<Identifier, Tag<T>> map = Maps.<Identifier, Tag<T>>newHashMap();
		int i = buf.readVarInt();

		for (int j = 0; j < i; j++) {
			Identifier identifier = buf.readIdentifier();
			int k = buf.readVarInt();
			Builder<T> builder = ImmutableSet.builder();

			for (int l = 0; l < k; l++) {
				builder.add(this.registry.get(buf.readVarInt()));
			}

			map.put(identifier, Tag.of(builder.build()));
		}

		this.setEntries(map);
	}
}
