package net.minecraft.server.network.packet;

import java.io.IOException;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class UpdatePlayerAbilitiesC2SPacket implements Packet<ServerPlayPacketListener> {
	private boolean invulnerable;
	private boolean flying;
	private boolean allowFlying;
	private boolean creativeMode;
	private float flySpeed;
	private float walkSpeed;

	public UpdatePlayerAbilitiesC2SPacket() {
	}

	public UpdatePlayerAbilitiesC2SPacket(PlayerAbilities playerAbilities) {
		this.setInvulnerable(playerAbilities.invulnerable);
		this.setFlying(playerAbilities.flying);
		this.setAllowFlying(playerAbilities.allowFlying);
		this.setCreativeMode(playerAbilities.creativeMode);
		this.setFlySpeed(playerAbilities.getFlySpeed());
		this.setWalkSpeed(playerAbilities.getWalkSpeed());
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		byte b = packetByteBuf.readByte();
		this.setInvulnerable((b & 1) > 0);
		this.setFlying((b & 2) > 0);
		this.setAllowFlying((b & 4) > 0);
		this.setCreativeMode((b & 8) > 0);
		this.setFlySpeed(packetByteBuf.readFloat());
		this.setWalkSpeed(packetByteBuf.readFloat());
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		byte b = 0;
		if (this.isInvulnerable()) {
			b = (byte)(b | 1);
		}

		if (this.isFlying()) {
			b = (byte)(b | 2);
		}

		if (this.isFlyingAllowed()) {
			b = (byte)(b | 4);
		}

		if (this.isCreativeMode()) {
			b = (byte)(b | 8);
		}

		packetByteBuf.writeByte(b);
		packetByteBuf.writeFloat(this.flySpeed);
		packetByteBuf.writeFloat(this.walkSpeed);
	}

	public void method_12339(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onPlayerAbilities(this);
	}

	public boolean isInvulnerable() {
		return this.invulnerable;
	}

	public void setInvulnerable(boolean bl) {
		this.invulnerable = bl;
	}

	public boolean isFlying() {
		return this.flying;
	}

	public void setFlying(boolean bl) {
		this.flying = bl;
	}

	public boolean isFlyingAllowed() {
		return this.allowFlying;
	}

	public void setAllowFlying(boolean bl) {
		this.allowFlying = bl;
	}

	public boolean isCreativeMode() {
		return this.creativeMode;
	}

	public void setCreativeMode(boolean bl) {
		this.creativeMode = bl;
	}

	public void setFlySpeed(float f) {
		this.flySpeed = f;
	}

	public void setWalkSpeed(float f) {
		this.walkSpeed = f;
	}
}
