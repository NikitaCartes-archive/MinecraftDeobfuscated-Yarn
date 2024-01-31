package net.minecraft.block.vault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;

public class VaultSharedData {
	static final String SHARED_DATA_KEY = "shared_data";
	public static Codec<VaultSharedData> codec = RecordCodecBuilder.create(
		instance -> instance.group(
					ItemStack.createOptionalCodec("display_item").forGetter(data -> data.displayItem),
					Uuids.LINKED_SET_CODEC.optionalFieldOf("connected_players", Set.of()).forGetter(data -> data.connectedPlayers),
					Codec.DOUBLE
						.optionalFieldOf("connected_particles_range", Double.valueOf(VaultConfig.DEFAULT.deactivationRange()))
						.forGetter(data -> data.connectedParticlesRange)
				)
				.apply(instance, VaultSharedData::new)
	);
	private ItemStack displayItem = ItemStack.EMPTY;
	private Set<UUID> connectedPlayers = new ObjectLinkedOpenHashSet<>();
	private double connectedParticlesRange = VaultConfig.DEFAULT.deactivationRange();
	public boolean dirty;

	VaultSharedData(ItemStack displayItem, Set<UUID> connectedPlayers, double connectedParticlesRange) {
		this.displayItem = displayItem;
		this.connectedPlayers.addAll(connectedPlayers);
		this.connectedParticlesRange = connectedParticlesRange;
	}

	public VaultSharedData() {
	}

	public ItemStack getDisplayItem() {
		return this.displayItem;
	}

	public boolean hasDisplayItem() {
		return !this.displayItem.isEmpty();
	}

	public void setDisplayItem(ItemStack stack) {
		if (!ItemStack.areEqual(this.displayItem, stack)) {
			this.displayItem = stack.copy();
			this.markDirty();
		}
	}

	public boolean hasConnectedPlayers() {
		return !this.connectedPlayers.isEmpty();
	}

	public Set<UUID> getConnectedPlayers() {
		return this.connectedPlayers;
	}

	public double getConnectedParticlesRange() {
		return this.connectedParticlesRange;
	}

	public void updateConnectedPlayers(ServerWorld world, BlockPos pos, VaultServerData serverData, VaultConfig config, double radius) {
		Set<UUID> set = (Set<UUID>)config.playerDetector()
			.detect(world, config.entitySelector(), pos, radius)
			.stream()
			.filter(uuid -> !serverData.getRewardedPlayers().contains(uuid))
			.collect(Collectors.toSet());
		if (!this.connectedPlayers.equals(set)) {
			this.connectedPlayers = set;
			this.markDirty();
		}
	}

	private void markDirty() {
		this.dirty = true;
	}

	void copyFrom(VaultSharedData data) {
		this.displayItem = data.displayItem;
		this.connectedPlayers = data.connectedPlayers;
		this.connectedParticlesRange = data.connectedParticlesRange;
	}
}
