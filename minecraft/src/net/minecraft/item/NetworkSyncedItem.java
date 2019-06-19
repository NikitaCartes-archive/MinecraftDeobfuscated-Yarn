package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.world.World;

public class NetworkSyncedItem extends Item {
	public NetworkSyncedItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public boolean isNetworkSynced() {
		return true;
	}

	@Nullable
	public Packet<?> createSyncPacket(ItemStack itemStack, World world, PlayerEntity playerEntity) {
		return null;
	}
}
