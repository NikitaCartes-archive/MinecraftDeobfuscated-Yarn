package net.minecraft.tag;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class RegistryTagContainer<T> extends TagContainer<T> {
	private final Registry<T> registry;

	public RegistryTagContainer(Registry<T> registry, String string, String string2) {
		super(registry::contains, registry::get, string, false, string2);
		this.registry = registry;
	}

	public void toPacket(PacketByteBuf packetByteBuf) {
		packetByteBuf.writeVarInt(this.getEntries().size());

		for (Entry<Identifier, Tag<T>> entry : this.getEntries().entrySet()) {
			packetByteBuf.writeIdentifier((Identifier)entry.getKey());
			packetByteBuf.writeVarInt(((Tag)entry.getValue()).values().size());

			for (T object : ((Tag)entry.getValue()).values()) {
				packetByteBuf.writeVarInt(this.registry.getRawId(object));
			}
		}
	}

	public void fromPacket(PacketByteBuf packetByteBuf) {
		int i = packetByteBuf.readVarInt();

		for (int j = 0; j < i; j++) {
			Identifier identifier = packetByteBuf.readIdentifier();
			int k = packetByteBuf.readVarInt();
			List<T> list = Lists.<T>newArrayList();

			for (int l = 0; l < k; l++) {
				list.add(this.registry.getInt(packetByteBuf.readVarInt()));
			}

			this.getEntries().put(identifier, Tag.Builder.create().add(list).build(identifier));
		}
	}
}
