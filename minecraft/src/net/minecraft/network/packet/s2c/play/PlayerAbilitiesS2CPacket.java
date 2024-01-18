package net.minecraft.network.packet.s2c.play;

import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class PlayerAbilitiesS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, PlayerAbilitiesS2CPacket> CODEC = Packet.createCodec(
		PlayerAbilitiesS2CPacket::write, PlayerAbilitiesS2CPacket::new
	);
	private static final int INVULNERABLE_MASK = 1;
	private static final int FLYING_MASK = 2;
	private static final int ALLOW_FLYING_MASK = 4;
	private static final int CREATIVE_MODE_MASK = 8;
	private final boolean invulnerable;
	private final boolean flying;
	private final boolean allowFlying;
	private final boolean creativeMode;
	private final float flySpeed;
	private final float walkSpeed;

	public PlayerAbilitiesS2CPacket(PlayerAbilities abilities) {
		this.invulnerable = abilities.invulnerable;
		this.flying = abilities.flying;
		this.allowFlying = abilities.allowFlying;
		this.creativeMode = abilities.creativeMode;
		this.flySpeed = abilities.getFlySpeed();
		this.walkSpeed = abilities.getWalkSpeed();
	}

	private PlayerAbilitiesS2CPacket(PacketByteBuf buf) {
		byte b = buf.readByte();
		this.invulnerable = (b & 1) != 0;
		this.flying = (b & 2) != 0;
		this.allowFlying = (b & 4) != 0;
		this.creativeMode = (b & 8) != 0;
		this.flySpeed = buf.readFloat();
		this.walkSpeed = buf.readFloat();
	}

	private void write(PacketByteBuf buf) {
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

	@Override
	public PacketType<PlayerAbilitiesS2CPacket> getPacketId() {
		return PlayPackets.PLAYER_ABILITIES_S2C;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerAbilities(this);
	}

	public boolean isInvulnerable() {
		return this.invulnerable;
	}

	public boolean isFlying() {
		return this.flying;
	}

	public boolean allowFlying() {
		return this.allowFlying;
	}

	public boolean isCreativeMode() {
		return this.creativeMode;
	}

	public float getFlySpeed() {
		return this.flySpeed;
	}

	public float getWalkSpeed() {
		return this.walkSpeed;
	}
}
