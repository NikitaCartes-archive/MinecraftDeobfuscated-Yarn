package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.world.World;

public class MapItem extends Item {
	public MapItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public boolean isMap() {
		return true;
	}

	@Nullable
	public Packet<?> method_7757(ItemStack itemStack, World world, PlayerEntity playerEntity) {
		return null;
	}
}
