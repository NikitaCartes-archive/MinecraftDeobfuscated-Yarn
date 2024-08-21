package net.minecraft.server.network;

import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.network.packet.s2c.play.CooldownUpdateS2CPacket;
import net.minecraft.util.Identifier;

public class ServerItemCooldownManager extends ItemCooldownManager {
	private final ServerPlayerEntity player;

	public ServerItemCooldownManager(ServerPlayerEntity player) {
		this.player = player;
	}

	@Override
	protected void onCooldownUpdate(Identifier groupId, int duration) {
		super.onCooldownUpdate(groupId, duration);
		this.player.networkHandler.sendPacket(new CooldownUpdateS2CPacket(groupId, duration));
	}

	@Override
	protected void onCooldownUpdate(Identifier groupId) {
		super.onCooldownUpdate(groupId);
		this.player.networkHandler.sendPacket(new CooldownUpdateS2CPacket(groupId, 0));
	}
}
