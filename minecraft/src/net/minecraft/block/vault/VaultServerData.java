package net.minecraft.block.vault;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.MathHelper;

public class VaultServerData {
	static final String SERVER_DATA_KEY = "server_data";
	public static Codec<VaultServerData> codec = RecordCodecBuilder.create(
		instance -> instance.group(
					Uuids.LINKED_SET_CODEC.lenientOptionalFieldOf("rewarded_players", Set.of()).forGetter(data -> data.rewardedPlayers),
					Codec.LONG.lenientOptionalFieldOf("state_updating_resumes_at", Long.valueOf(0L)).forGetter(data -> data.stateUpdatingResumesAt),
					ItemStack.CODEC.listOf().lenientOptionalFieldOf("items_to_eject", List.of()).forGetter(data -> data.itemsToEject),
					Codec.INT.lenientOptionalFieldOf("total_ejections_needed", Integer.valueOf(0)).forGetter(data -> data.totalEjectionsNeeded)
				)
				.apply(instance, VaultServerData::new)
	);
	private static final int MAX_STORED_REWARDED_PLAYERS = 128;
	private final Set<UUID> rewardedPlayers = new ObjectLinkedOpenHashSet<>();
	private long stateUpdatingResumesAt;
	private final List<ItemStack> itemsToEject = new ObjectArrayList<>();
	private long lastFailedUnlockTime;
	private int totalEjectionsNeeded;
	public boolean dirty;

	VaultServerData(Set<UUID> rewardedPlayers, long stateUpdatingResumesAt, List<ItemStack> itemsToEject, int totalEjectionsNeeded) {
		this.rewardedPlayers.addAll(rewardedPlayers);
		this.stateUpdatingResumesAt = stateUpdatingResumesAt;
		this.itemsToEject.addAll(itemsToEject);
		this.totalEjectionsNeeded = totalEjectionsNeeded;
	}

	public VaultServerData() {
	}

	public void setLastFailedUnlockTime(long lastFailedUnlockTime) {
		this.lastFailedUnlockTime = lastFailedUnlockTime;
	}

	public long getLastFailedUnlockTime() {
		return this.lastFailedUnlockTime;
	}

	Set<UUID> getRewardedPlayers() {
		return this.rewardedPlayers;
	}

	public boolean hasRewardedPlayer(PlayerEntity player) {
		return this.rewardedPlayers.contains(player.getUuid());
	}

	@VisibleForTesting
	public void markPlayerAsRewarded(PlayerEntity player) {
		this.rewardedPlayers.add(player.getUuid());
		if (this.rewardedPlayers.size() > 128) {
			Iterator<UUID> iterator = this.rewardedPlayers.iterator();
			if (iterator.hasNext()) {
				iterator.next();
				iterator.remove();
			}
		}

		this.markDirty();
	}

	public long getStateUpdatingResumeTime() {
		return this.stateUpdatingResumesAt;
	}

	public void setStateUpdatingResumeTime(long stateUpdatingResumesAt) {
		this.stateUpdatingResumesAt = stateUpdatingResumesAt;
		this.markDirty();
	}

	public List<ItemStack> getItemsToEject() {
		return this.itemsToEject;
	}

	public void finishEjecting() {
		this.totalEjectionsNeeded = 0;
		this.markDirty();
	}

	public void setItemsToEject(List<ItemStack> itemsToEject) {
		this.itemsToEject.clear();
		this.itemsToEject.addAll(itemsToEject);
		this.totalEjectionsNeeded = this.itemsToEject.size();
		this.markDirty();
	}

	public ItemStack getItemToDisplay() {
		return this.itemsToEject.isEmpty()
			? ItemStack.EMPTY
			: (ItemStack)Objects.requireNonNullElse((ItemStack)this.itemsToEject.get(this.itemsToEject.size() - 1), ItemStack.EMPTY);
	}

	public ItemStack getItemToEject() {
		if (this.itemsToEject.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			this.markDirty();
			return (ItemStack)Objects.requireNonNullElse((ItemStack)this.itemsToEject.remove(this.itemsToEject.size() - 1), ItemStack.EMPTY);
		}
	}

	void copyFrom(VaultServerData data) {
		this.stateUpdatingResumesAt = data.getStateUpdatingResumeTime();
		this.itemsToEject.clear();
		this.itemsToEject.addAll(data.itemsToEject);
		this.rewardedPlayers.clear();
		this.rewardedPlayers.addAll(data.rewardedPlayers);
	}

	private void markDirty() {
		this.dirty = true;
	}

	public float getEjectSoundPitchModifier() {
		return this.totalEjectionsNeeded == 1
			? 1.0F
			: 1.0F - MathHelper.getLerpProgress((float)this.getItemsToEject().size(), 1.0F, (float)this.totalEjectionsNeeded);
	}
}
