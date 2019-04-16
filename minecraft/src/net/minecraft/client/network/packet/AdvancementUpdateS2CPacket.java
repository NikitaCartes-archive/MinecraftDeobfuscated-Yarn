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
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class AdvancementUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private boolean clearCurrent;
	private Map<Identifier, Advancement.Task> toEarn;
	private Set<Identifier> toRemove;
	private Map<Identifier, AdvancementProgress> toSetProgress;

	public AdvancementUpdateS2CPacket() {
	}

	public AdvancementUpdateS2CPacket(boolean bl, Collection<Advancement> collection, Set<Identifier> set, Map<Identifier, AdvancementProgress> map) {
		this.clearCurrent = bl;
		this.toEarn = Maps.<Identifier, Advancement.Task>newHashMap();

		for (Advancement advancement : collection) {
			this.toEarn.put(advancement.getId(), advancement.createTask());
		}

		this.toRemove = set;
		this.toSetProgress = Maps.<Identifier, AdvancementProgress>newHashMap(map);
	}

	public void method_11925(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onAdvancements(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.clearCurrent = packetByteBuf.readBoolean();
		this.toEarn = Maps.<Identifier, Advancement.Task>newHashMap();
		this.toRemove = Sets.<Identifier>newLinkedHashSet();
		this.toSetProgress = Maps.<Identifier, AdvancementProgress>newHashMap();
		int i = packetByteBuf.readVarInt();

		for (int j = 0; j < i; j++) {
			Identifier identifier = packetByteBuf.readIdentifier();
			Advancement.Task task = Advancement.Task.fromPacket(packetByteBuf);
			this.toEarn.put(identifier, task);
		}

		i = packetByteBuf.readVarInt();

		for (int j = 0; j < i; j++) {
			Identifier identifier = packetByteBuf.readIdentifier();
			this.toRemove.add(identifier);
		}

		i = packetByteBuf.readVarInt();

		for (int j = 0; j < i; j++) {
			Identifier identifier = packetByteBuf.readIdentifier();
			this.toSetProgress.put(identifier, AdvancementProgress.fromPacket(packetByteBuf));
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeBoolean(this.clearCurrent);
		packetByteBuf.writeVarInt(this.toEarn.size());

		for (Entry<Identifier, Advancement.Task> entry : this.toEarn.entrySet()) {
			Identifier identifier = (Identifier)entry.getKey();
			Advancement.Task task = (Advancement.Task)entry.getValue();
			packetByteBuf.writeIdentifier(identifier);
			task.toPacket(packetByteBuf);
		}

		packetByteBuf.writeVarInt(this.toRemove.size());

		for (Identifier identifier2 : this.toRemove) {
			packetByteBuf.writeIdentifier(identifier2);
		}

		packetByteBuf.writeVarInt(this.toSetProgress.size());

		for (Entry<Identifier, AdvancementProgress> entry : this.toSetProgress.entrySet()) {
			packetByteBuf.writeIdentifier((Identifier)entry.getKey());
			((AdvancementProgress)entry.getValue()).toPacket(packetByteBuf);
		}
	}

	@Environment(EnvType.CLIENT)
	public Map<Identifier, Advancement.Task> getAdvancementsToEarn() {
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
