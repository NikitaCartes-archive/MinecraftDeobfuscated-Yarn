package net.minecraft.client.network.packet;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class PlayerPositionLookClientPacket implements Packet<ClientPlayPacketListener> {
	private double x;
	private double y;
	private double z;
	private float yaw;
	private float pitch;
	private Set<PlayerPositionLookClientPacket.Flag> flags;
	private int teleportId;

	public PlayerPositionLookClientPacket() {
	}

	public PlayerPositionLookClientPacket(double d, double e, double f, float g, float h, Set<PlayerPositionLookClientPacket.Flag> set, int i) {
		this.x = d;
		this.y = e;
		this.z = f;
		this.yaw = g;
		this.pitch = h;
		this.flags = set;
		this.teleportId = i;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.x = packetByteBuf.readDouble();
		this.y = packetByteBuf.readDouble();
		this.z = packetByteBuf.readDouble();
		this.yaw = packetByteBuf.readFloat();
		this.pitch = packetByteBuf.readFloat();
		this.flags = PlayerPositionLookClientPacket.Flag.getFlags(packetByteBuf.readUnsignedByte());
		this.teleportId = packetByteBuf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeDouble(this.x);
		packetByteBuf.writeDouble(this.y);
		packetByteBuf.writeDouble(this.z);
		packetByteBuf.writeFloat(this.yaw);
		packetByteBuf.writeFloat(this.pitch);
		packetByteBuf.writeByte(PlayerPositionLookClientPacket.Flag.getBitfield(this.flags));
		packetByteBuf.writeVarInt(this.teleportId);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerPositionLook(this);
	}

	@Environment(EnvType.CLIENT)
	public double getX() {
		return this.x;
	}

	@Environment(EnvType.CLIENT)
	public double getY() {
		return this.y;
	}

	@Environment(EnvType.CLIENT)
	public double getZ() {
		return this.z;
	}

	@Environment(EnvType.CLIENT)
	public float getYaw() {
		return this.yaw;
	}

	@Environment(EnvType.CLIENT)
	public float getPitch() {
		return this.pitch;
	}

	@Environment(EnvType.CLIENT)
	public int getTeleportId() {
		return this.teleportId;
	}

	@Environment(EnvType.CLIENT)
	public Set<PlayerPositionLookClientPacket.Flag> getFlags() {
		return this.flags;
	}

	public static enum Flag {
		X(0),
		Y(1),
		Z(2),
		Y_ROT(3),
		X_ROT(4);

		private final int shift;

		private Flag(int j) {
			this.shift = j;
		}

		private int getMask() {
			return 1 << this.shift;
		}

		private boolean isSet(int i) {
			return (i & this.getMask()) == this.getMask();
		}

		public static Set<PlayerPositionLookClientPacket.Flag> getFlags(int i) {
			Set<PlayerPositionLookClientPacket.Flag> set = EnumSet.noneOf(PlayerPositionLookClientPacket.Flag.class);

			for (PlayerPositionLookClientPacket.Flag flag : values()) {
				if (flag.isSet(i)) {
					set.add(flag);
				}
			}

			return set;
		}

		public static int getBitfield(Set<PlayerPositionLookClientPacket.Flag> set) {
			int i = 0;

			for (PlayerPositionLookClientPacket.Flag flag : set) {
				i |= flag.getMask();
			}

			return i;
		}
	}
}
