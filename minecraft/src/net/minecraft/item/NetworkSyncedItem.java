package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.world.World;

/**
 * Represents an item which can contain extra data that is synced to the client.
 */
public class NetworkSyncedItem extends Item {
	public NetworkSyncedItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public boolean isNetworkSynced() {
		return true;
	}

	/**
	 * Creates a packet that syncs additional item data to the client.
	 */
	@Nullable
	public Packet<?> createSyncPacket(ItemStack stack, World world, PlayerEntity player) {
		return null;
	}
}
