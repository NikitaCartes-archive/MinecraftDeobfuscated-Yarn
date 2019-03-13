package net.minecraft.server.network;

import net.minecraft.client.network.packet.CooldownUpdateS2CPacket;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.Item;

public class ServerItemCooldownManager extends ItemCooldownManager {
	private final ServerPlayerEntity field_8910;

	public ServerItemCooldownManager(ServerPlayerEntity serverPlayerEntity) {
		this.field_8910 = serverPlayerEntity;
	}

	@Override
	protected void onCooldownUpdate(Item item, int i) {
		super.onCooldownUpdate(item, i);
		this.field_8910.field_13987.sendPacket(new CooldownUpdateS2CPacket(item, i));
	}

	@Override
	protected void onCooldownUpdate(Item item) {
		super.onCooldownUpdate(item);
		this.field_8910.field_13987.sendPacket(new CooldownUpdateS2CPacket(item, 0));
	}
}
