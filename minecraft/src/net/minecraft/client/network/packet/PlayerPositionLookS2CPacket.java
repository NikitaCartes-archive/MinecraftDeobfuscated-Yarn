package net.minecraft.client.network.packet;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class PlayerPositionLookS2CPacket implements Packet<ClientPlayPacketListener> {
	private double x;
	private double y;
	private double z;
	private float yaw;
	private float pitch;
	private Set<PlayerPositionLookS2CPacket.Flag> flags;
	private int teleportId;

	public PlayerPositionLookS2CPacket() {
	}

	public PlayerPositionLookS2CPacket(double d, double e, double f, float g, float h, Set<PlayerPositionLookS2CPacket.Flag> set, int i) {
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
		this.flags = PlayerPositionLookS2CPacket.Flag.getFlags(packetByteBuf.readUnsignedByte());
		this.teleportId = packetByteBuf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeDouble(this.x);
		packetByteBuf.writeDouble(this.y);
		packetByteBuf.writeDouble(this.z);
		packetByteBuf.writeFloat(this.yaw);
		packetByteBuf.writeFloat(this.pitch);
		packetByteBuf.writeByte(PlayerPositionLookS2CPacket.Flag.getBitfield(this.flags));
		packetByteBuf.writeVarInt(this.teleportId);
	}

	public void method_11740(ClientPlayPacketListener clientPlayPacketListener) {
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
	public Set<PlayerPositionLookS2CPacket.Flag> getFlags() {
		return this.flags;
	}

	public static enum Flag {
		field_12400(0),
		field_12398(1),
		field_12403(2),
		field_12401(3),
		field_12397(4);

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

		public static Set<PlayerPositionLookS2CPacket.Flag> getFlags(int i) {
			Set<PlayerPositionLookS2CPacket.Flag> set = EnumSet.noneOf(PlayerPositionLookS2CPacket.Flag.class);

			for (PlayerPositionLookS2CPacket.Flag flag : values()) {
				if (flag.isSet(i)) {
					set.add(flag);
				}
			}

			return set;
		}

		public static int getBitfield(Set<PlayerPositionLookS2CPacket.Flag> set) {
			int i = 0;

			for (PlayerPositionLookS2CPacket.Flag flag : set) {
				i |= flag.getMask();
			}

			return i;
		}
	}
}
