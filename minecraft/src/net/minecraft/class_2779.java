package net.minecraft;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class class_2779 implements Packet<ClientPlayPacketListener> {
	private boolean field_12718;
	private Map<Identifier, SimpleAdvancement.Builder> field_12717;
	private Set<Identifier> field_12715;
	private Map<Identifier, AdvancementProgress> field_12716;

	public class_2779() {
	}

	public class_2779(boolean bl, Collection<SimpleAdvancement> collection, Set<Identifier> set, Map<Identifier, AdvancementProgress> map) {
		this.field_12718 = bl;
		this.field_12717 = Maps.<Identifier, SimpleAdvancement.Builder>newHashMap();

		for (SimpleAdvancement simpleAdvancement : collection) {
			this.field_12717.put(simpleAdvancement.getId(), simpleAdvancement.createTask());
		}

		this.field_12715 = set;
		this.field_12716 = Maps.<Identifier, AdvancementProgress>newHashMap(map);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onAdvancements(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_12718 = packetByteBuf.readBoolean();
		this.field_12717 = Maps.<Identifier, SimpleAdvancement.Builder>newHashMap();
		this.field_12715 = Sets.<Identifier>newLinkedHashSet();
		this.field_12716 = Maps.<Identifier, AdvancementProgress>newHashMap();
		int i = packetByteBuf.readVarInt();

		for (int j = 0; j < i; j++) {
			Identifier identifier = packetByteBuf.readIdentifier();
			SimpleAdvancement.Builder builder = SimpleAdvancement.Builder.deserialize(packetByteBuf);
			this.field_12717.put(identifier, builder);
		}

		i = packetByteBuf.readVarInt();

		for (int j = 0; j < i; j++) {
			Identifier identifier = packetByteBuf.readIdentifier();
			this.field_12715.add(identifier);
		}

		i = packetByteBuf.readVarInt();

		for (int j = 0; j < i; j++) {
			Identifier identifier = packetByteBuf.readIdentifier();
			this.field_12716.put(identifier, AdvancementProgress.deserialize(packetByteBuf));
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeBoolean(this.field_12718);
		packetByteBuf.writeVarInt(this.field_12717.size());

		for (Entry<Identifier, SimpleAdvancement.Builder> entry : this.field_12717.entrySet()) {
			Identifier identifier = (Identifier)entry.getKey();
			SimpleAdvancement.Builder builder = (SimpleAdvancement.Builder)entry.getValue();
			packetByteBuf.writeIdentifier(identifier);
			builder.serialize(packetByteBuf);
		}

		packetByteBuf.writeVarInt(this.field_12715.size());

		for (Identifier identifier2 : this.field_12715) {
			packetByteBuf.writeIdentifier(identifier2);
		}

		packetByteBuf.writeVarInt(this.field_12716.size());

		for (Entry<Identifier, AdvancementProgress> entry : this.field_12716.entrySet()) {
			packetByteBuf.writeIdentifier((Identifier)entry.getKey());
			((AdvancementProgress)entry.getValue()).serialize(packetByteBuf);
		}
	}

	@Environment(EnvType.CLIENT)
	public Map<Identifier, SimpleAdvancement.Builder> method_11928() {
		return this.field_12717;
	}

	@Environment(EnvType.CLIENT)
	public Set<Identifier> method_11926() {
		return this.field_12715;
	}

	@Environment(EnvType.CLIENT)
	public Map<Identifier, AdvancementProgress> method_11927() {
		return this.field_12716;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11924() {
		return this.field_12718;
	}
}
