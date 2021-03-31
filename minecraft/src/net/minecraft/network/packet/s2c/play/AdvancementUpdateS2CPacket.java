package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;

public class AdvancementUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private final boolean clearCurrent;
	private final Map<Identifier, Advancement.Task> toEarn;
	private final Set<Identifier> toRemove;
	private final Map<Identifier, AdvancementProgress> toSetProgress;

	public AdvancementUpdateS2CPacket(
		boolean clearCurrent, Collection<Advancement> toEarn, Set<Identifier> toRemove, Map<Identifier, AdvancementProgress> toSetProgress
	) {
		this.clearCurrent = clearCurrent;
		Builder<Identifier, Advancement.Task> builder = ImmutableMap.builder();

		for (Advancement advancement : toEarn) {
			builder.put(advancement.getId(), advancement.createTask());
		}

		this.toEarn = builder.build();
		this.toRemove = ImmutableSet.copyOf(toRemove);
		this.toSetProgress = ImmutableMap.copyOf(toSetProgress);
	}

	public AdvancementUpdateS2CPacket(PacketByteBuf buf) {
		this.clearCurrent = buf.readBoolean();
		this.toEarn = buf.readMap(PacketByteBuf::readIdentifier, Advancement.Task::fromPacket);
		this.toRemove = buf.readCollection(Sets::newLinkedHashSetWithExpectedSize, PacketByteBuf::readIdentifier);
		this.toSetProgress = buf.readMap(PacketByteBuf::readIdentifier, AdvancementProgress::fromPacket);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBoolean(this.clearCurrent);
		buf.writeMap(this.toEarn, PacketByteBuf::writeIdentifier, (bufx, task) -> task.toPacket(bufx));
		buf.writeCollection(this.toRemove, PacketByteBuf::writeIdentifier);
		buf.writeMap(this.toSetProgress, PacketByteBuf::writeIdentifier, (bufx, progress) -> progress.toPacket(bufx));
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onAdvancements(this);
	}

	public Map<Identifier, Advancement.Task> getAdvancementsToEarn() {
		return this.toEarn;
	}

	public Set<Identifier> getAdvancementIdsToRemove() {
		return this.toRemove;
	}

	public Map<Identifier, AdvancementProgress> getAdvancementsToProgress() {
		return this.toSetProgress;
	}

	public boolean shouldClearCurrent() {
		return this.clearCurrent;
	}
}
