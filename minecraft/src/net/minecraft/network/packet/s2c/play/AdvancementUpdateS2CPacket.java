package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Identifier;

public class AdvancementUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private final boolean clearCurrent;
	private final List<AdvancementEntry> toEarn;
	private final Set<Identifier> toRemove;
	private final Map<Identifier, AdvancementProgress> toSetProgress;

	public AdvancementUpdateS2CPacket(
		boolean clearCurrent, Collection<AdvancementEntry> toEarn, Set<Identifier> toRemove, Map<Identifier, AdvancementProgress> toSetProgress
	) {
		this.clearCurrent = clearCurrent;
		this.toEarn = List.copyOf(toEarn);
		this.toRemove = Set.copyOf(toRemove);
		this.toSetProgress = Map.copyOf(toSetProgress);
	}

	public AdvancementUpdateS2CPacket(PacketByteBuf buf) {
		this.clearCurrent = buf.readBoolean();
		this.toEarn = buf.readList(AdvancementEntry::read);
		this.toRemove = buf.readCollection(Sets::newLinkedHashSetWithExpectedSize, PacketByteBuf::readIdentifier);
		this.toSetProgress = buf.readMap(PacketByteBuf::readIdentifier, AdvancementProgress::fromPacket);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBoolean(this.clearCurrent);
		buf.writeCollection(this.toEarn, (buf2, task) -> task.write(buf2));
		buf.writeCollection(this.toRemove, PacketByteBuf::writeIdentifier);
		buf.writeMap(this.toSetProgress, PacketByteBuf::writeIdentifier, (buf2, progress) -> progress.toPacket(buf2));
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onAdvancements(this);
	}

	public List<AdvancementEntry> getAdvancementsToEarn() {
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
