package net.minecraft.client.network.packet;

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

public class AdvancementUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private boolean clearCurrent;
	private Map<Identifier, SimpleAdvancement.Builder> toEarn;
	private Set<Identifier> toRemove;
	private Map<Identifier, AdvancementProgress> toSetProgress;

	public AdvancementUpdateS2CPacket() {
	}

	public AdvancementUpdateS2CPacket(boolean bl, Collection<SimpleAdvancement> collection, Set<Identifier> set, Map<Identifier, AdvancementProgress> map) {
		this.clearCurrent = bl;
		this.toEarn = Maps.<Identifier, SimpleAdvancement.Builder>newHashMap();

		for (SimpleAdvancement simpleAdvancement : collection) {
			this.toEarn.put(simpleAdvancement.method_688(), simpleAdvancement.createTask());
		}

		this.toRemove = set;
		this.toSetProgress = Maps.<Identifier, AdvancementProgress>newHashMap(map);
	}

	public void method_11925(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11130(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.clearCurrent = packetByteBuf.readBoolean();
		this.toEarn = Maps.<Identifier, SimpleAdvancement.Builder>newHashMap();
		this.toRemove = Sets.<Identifier>newLinkedHashSet();
		this.toSetProgress = Maps.<Identifier, AdvancementProgress>newHashMap();
		int i = packetByteBuf.readVarInt();

		for (int j = 0; j < i; j++) {
			Identifier identifier = packetByteBuf.method_10810();
			SimpleAdvancement.Builder builder = SimpleAdvancement.Builder.deserialize(packetByteBuf);
			this.toEarn.put(identifier, builder);
		}

		i = packetByteBuf.readVarInt();

		for (int j = 0; j < i; j++) {
			Identifier identifier = packetByteBuf.method_10810();
			this.toRemove.add(identifier);
		}

		i = packetByteBuf.readVarInt();

		for (int j = 0; j < i; j++) {
			Identifier identifier = packetByteBuf.method_10810();
			this.toSetProgress.put(identifier, AdvancementProgress.fromPacket(packetByteBuf));
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeBoolean(this.clearCurrent);
		packetByteBuf.writeVarInt(this.toEarn.size());

		for (Entry<Identifier, SimpleAdvancement.Builder> entry : this.toEarn.entrySet()) {
			Identifier identifier = (Identifier)entry.getKey();
			SimpleAdvancement.Builder builder = (SimpleAdvancement.Builder)entry.getValue();
			packetByteBuf.method_10812(identifier);
			builder.serialize(packetByteBuf);
		}

		packetByteBuf.writeVarInt(this.toRemove.size());

		for (Identifier identifier2 : this.toRemove) {
			packetByteBuf.method_10812(identifier2);
		}

		packetByteBuf.writeVarInt(this.toSetProgress.size());

		for (Entry<Identifier, AdvancementProgress> entry : this.toSetProgress.entrySet()) {
			packetByteBuf.method_10812((Identifier)entry.getKey());
			((AdvancementProgress)entry.getValue()).toPacket(packetByteBuf);
		}
	}

	@Environment(EnvType.CLIENT)
	public Map<Identifier, SimpleAdvancement.Builder> getAdvancementsToEarn() {
		return this.toEarn;
	}

	@Environment(EnvType.CLIENT)
	public Set<Identifier> getAdvancementIdsToRemove() {
		return this.toRemove;
	}

	@Environment(EnvType.CLIENT)
	public Map<Identifier, AdvancementProgress> getAdvancementsToProgress() {
		return this.toSetProgress;
	}

	@Environment(EnvType.CLIENT)
	public boolean shouldClearCurrent() {
		return this.clearCurrent;
	}
}
