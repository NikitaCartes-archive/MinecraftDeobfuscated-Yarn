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

	public AdvancementUpdateS2CPacket(
		boolean clearCurrent, Collection<Advancement> toEarn, Set<Identifier> toRemove, Map<Identifier, AdvancementProgress> toSetProgress
	) {
		this.clearCurrent = clearCurrent;
		this.toEarn = Maps.<Identifier, Advancement.Task>newHashMap();

		for (Advancement advancement : toEarn) {
			this.toEarn.put(advancement.getId(), advancement.createTask());
		}

		this.toRemove = toRemove;
		this.toSetProgress = Maps.<Identifier, AdvancementProgress>newHashMap(toSetProgress);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onAdvancements(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.clearCurrent = buf.readBoolean();
		this.toEarn = Maps.<Identifier, Advancement.Task>newHashMap();
		this.toRemove = Sets.<Identifier>newLinkedHashSet();
		this.toSetProgress = Maps.<Identifier, AdvancementProgress>newHashMap();
		int i = buf.readVarInt();

		for (int j = 0; j < i; j++) {
			Identifier identifier = buf.readIdentifier();
			Advancement.Task task = Advancement.Task.fromPacket(buf);
			this.toEarn.put(identifier, task);
		}

		i = buf.readVarInt();

		for (int j = 0; j < i; j++) {
			Identifier identifier = buf.readIdentifier();
			this.toRemove.add(identifier);
		}

		i = buf.readVarInt();

		for (int j = 0; j < i; j++) {
			Identifier identifier = buf.readIdentifier();
			this.toSetProgress.put(identifier, AdvancementProgress.fromPacket(buf));
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeBoolean(this.clearCurrent);
		buf.writeVarInt(this.toEarn.size());

		for (Entry<Identifier, Advancement.Task> entry : this.toEarn.entrySet()) {
			Identifier identifier = (Identifier)entry.getKey();
			Advancement.Task task = (Advancement.Task)entry.getValue();
			buf.writeIdentifier(identifier);
			task.toPacket(buf);
		}

		buf.writeVarInt(this.toRemove.size());

		for (Identifier identifier2 : this.toRemove) {
			buf.writeIdentifier(identifier2);
		}

		buf.writeVarInt(this.toSetProgress.size());

		for (Entry<Identifier, AdvancementProgress> entry : this.toSetProgress.entrySet()) {
			buf.writeIdentifier((Identifier)entry.getKey());
			((AdvancementProgress)entry.getValue()).toPacket(buf);
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
