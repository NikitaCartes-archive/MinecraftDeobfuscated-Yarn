package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class PlayerAbilitiesS2CPacket implements Packet<ClientPlayPacketListener> {
	private final boolean invulnerable;
	private final boolean flying;
	private final boolean allowFlying;
	private final boolean creativeMode;
	private final float flySpeed;
	private final float walkSpeed;

	public PlayerAbilitiesS2CPacket(PlayerAbilities playerAbilities) {
		this.invulnerable = playerAbilities.invulnerable;
		this.flying = playerAbilities.flying;
		this.allowFlying = playerAbilities.allowFlying;
		this.creativeMode = playerAbilities.creativeMode;
		this.flySpeed = playerAbilities.getFlySpeed();
		this.walkSpeed = playerAbilities.getWalkSpeed();
	}

	public PlayerAbilitiesS2CPacket(PacketByteBuf packetByteBuf) {
		byte b = packetByteBuf.readByte();
		this.invulnerable = (b & 1) != 0;
		this.flying = (b & 2) != 0;
		this.allowFlying = (b & 4) != 0;
		this.creativeMode = (b & 8) != 0;
		this.flySpeed = packetByteBuf.readFloat();
		this.walkSpeed = packetByteBuf.readFloat();
	}

	@Override
	public void write(PacketByteBuf buf) {
		byte b = 0;
		if (this.invulnerable) {
			b = (byte)(b | 1);
		}

		if (this.flying) {
			b = (byte)(b | 2);
		}

		if (this.allowFlying) {
			b = (byte)(b | 4);
		}

		if (this.creativeMode) {
			b = (byte)(b | 8);
		}

		buf.writeByte(b);
		buf.writeFloat(this.flySpeed);
		buf.writeFloat(this.walkSpeed);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerAbilities(this);
	}

	@Environment(EnvType.CLIENT)
	public boolean isInvulnerable() {
		return this.invulnerable;
	}

	@Environment(EnvType.CLIENT)
	public boolean isFlying() {
		return this.flying;
	}

	@Environment(EnvType.CLIENT)
	public boolean allowFlying() {
		return this.allowFlying;
	}

	@Environment(EnvType.CLIENT)
	public boolean isCreativeMode() {
		return this.creativeMode;
	}

	@Environment(EnvType.CLIENT)
	public float getFlySpeed() {
		return this.flySpeed;
	}

	@Environment(EnvType.CLIENT)
	public float getWalkSpeed() {
		return this.walkSpeed;
	}
}
