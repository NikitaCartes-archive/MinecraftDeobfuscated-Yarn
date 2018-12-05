package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class class_2828 implements Packet<ServerPlayPacketListener> {
	protected double field_12889;
	protected double field_12886;
	protected double field_12884;
	protected float field_12887;
	protected float field_12885;
	protected boolean field_12891;
	protected boolean field_12890;
	protected boolean field_12888;

	public class_2828() {
	}

	@Environment(EnvType.CLIENT)
	public class_2828(boolean bl) {
		this.field_12891 = bl;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12063(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_12891 = packetByteBuf.readUnsignedByte() != 0;
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeByte(this.field_12891 ? 1 : 0);
	}

	public double method_12269(double d) {
		return this.field_12890 ? this.field_12889 : d;
	}

	public double method_12268(double d) {
		return this.field_12890 ? this.field_12886 : d;
	}

	public double method_12274(double d) {
		return this.field_12890 ? this.field_12884 : d;
	}

	public float method_12271(float f) {
		return this.field_12888 ? this.field_12887 : f;
	}

	public float method_12270(float f) {
		return this.field_12888 ? this.field_12885 : f;
	}

	public boolean method_12273() {
		return this.field_12891;
	}

	public static class class_2829 extends class_2828 {
		public class_2829() {
			this.field_12890 = true;
		}

		@Environment(EnvType.CLIENT)
		public class_2829(double d, double e, double f, boolean bl) {
			this.field_12889 = d;
			this.field_12886 = e;
			this.field_12884 = f;
			this.field_12891 = bl;
			this.field_12890 = true;
		}

		@Override
		public void read(PacketByteBuf packetByteBuf) throws IOException {
			this.field_12889 = packetByteBuf.readDouble();
			this.field_12886 = packetByteBuf.readDouble();
			this.field_12884 = packetByteBuf.readDouble();
			super.read(packetByteBuf);
		}

		@Override
		public void write(PacketByteBuf packetByteBuf) throws IOException {
			packetByteBuf.writeDouble(this.field_12889);
			packetByteBuf.writeDouble(this.field_12886);
			packetByteBuf.writeDouble(this.field_12884);
			super.write(packetByteBuf);
		}
	}

	public static class class_2830 extends class_2828 {
		public class_2830() {
			this.field_12890 = true;
			this.field_12888 = true;
		}

		@Environment(EnvType.CLIENT)
		public class_2830(double d, double e, double f, float g, float h, boolean bl) {
			this.field_12889 = d;
			this.field_12886 = e;
			this.field_12884 = f;
			this.field_12887 = g;
			this.field_12885 = h;
			this.field_12891 = bl;
			this.field_12888 = true;
			this.field_12890 = true;
		}

		@Override
		public void read(PacketByteBuf packetByteBuf) throws IOException {
			this.field_12889 = packetByteBuf.readDouble();
			this.field_12886 = packetByteBuf.readDouble();
			this.field_12884 = packetByteBuf.readDouble();
			this.field_12887 = packetByteBuf.readFloat();
			this.field_12885 = packetByteBuf.readFloat();
			super.read(packetByteBuf);
		}

		@Override
		public void write(PacketByteBuf packetByteBuf) throws IOException {
			packetByteBuf.writeDouble(this.field_12889);
			packetByteBuf.writeDouble(this.field_12886);
			packetByteBuf.writeDouble(this.field_12884);
			packetByteBuf.writeFloat(this.field_12887);
			packetByteBuf.writeFloat(this.field_12885);
			super.write(packetByteBuf);
		}
	}

	public static class class_2831 extends class_2828 {
		public class_2831() {
			this.field_12888 = true;
		}

		@Environment(EnvType.CLIENT)
		public class_2831(float f, float g, boolean bl) {
			this.field_12887 = f;
			this.field_12885 = g;
			this.field_12891 = bl;
			this.field_12888 = true;
		}

		@Override
		public void read(PacketByteBuf packetByteBuf) throws IOException {
			this.field_12887 = packetByteBuf.readFloat();
			this.field_12885 = packetByteBuf.readFloat();
			super.read(packetByteBuf);
		}

		@Override
		public void write(PacketByteBuf packetByteBuf) throws IOException {
			packetByteBuf.writeFloat(this.field_12887);
			packetByteBuf.writeFloat(this.field_12885);
			super.write(packetByteBuf);
		}
	}
}
