package net.minecraft.server.network;

import net.minecraft.client.network.packet.CooldownUpdateS2CPacket;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.Item;

public class ServerItemCooldownManager extends ItemCooldownManager {
	private final ServerPlayerEntity player;

	public ServerItemCooldownManager(ServerPlayerEntity serverPlayerEntity) {
		this.player = serverPlayerEntity;
	}

	@Override
	protected void onCooldownUpdate(Item item, int i) {
		super.onCooldownUpdate(item, i);
		this.player.networkHandler.sendPacket(new CooldownUpdateS2CPacket(item, i));
	}

	@Override
	protected void onCooldownUpdate(Item item) {
		super.onCooldownUpdate(item);
		this.player.networkHandler.sendPacket(new CooldownUpdateS2CPacket(item, 0));
	}
}
