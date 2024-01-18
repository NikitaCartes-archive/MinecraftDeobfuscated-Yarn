package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.util.Identifier;

public class AdvancementUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<RegistryByteBuf, AdvancementUpdateS2CPacket> CODEC = Packet.createCodec(
		AdvancementUpdateS2CPacket::write, AdvancementUpdateS2CPacket::new
	);
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

	private AdvancementUpdateS2CPacket(RegistryByteBuf buf) {
		this.clearCurrent = buf.readBoolean();
		this.toEarn = AdvancementEntry.LIST_PACKET_CODEC.decode(buf);
		this.toRemove = buf.readCollection(Sets::newLinkedHashSetWithExpectedSize, PacketByteBuf::readIdentifier);
		this.toSetProgress = buf.readMap(PacketByteBuf::readIdentifier, AdvancementProgress::fromPacket);
	}

	private void write(RegistryByteBuf buf) {
		buf.writeBoolean(this.clearCurrent);
		AdvancementEntry.LIST_PACKET_CODEC.encode(buf, this.toEarn);
		buf.writeCollection(this.toRemove, PacketByteBuf::writeIdentifier);
		buf.writeMap(this.toSetProgress, PacketByteBuf::writeIdentifier, (buf2, progress) -> progress.toPacket(buf2));
	}

	@Override
	public PacketType<AdvancementUpdateS2CPacket> getPacketId() {
		return PlayPackets.UPDATE_ADVANCEMENTS;
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
