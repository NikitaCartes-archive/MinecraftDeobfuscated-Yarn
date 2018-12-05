package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.particle.Particle;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class ParticleClientPacket implements Packet<ClientPlayPacketListener> {
	private float x;
	private float y;
	private float z;
	private float offsetX;
	private float offsetY;
	private float offsetZ;
	private float field_12260;
	private int particleCount;
	private boolean longDistance;
	private Particle field_12259;

	public ParticleClientPacket() {
	}

	public <T extends Particle> ParticleClientPacket(T particle, boolean bl, float f, float g, float h, float i, float j, float k, float l, int m) {
		this.field_12259 = particle;
		this.longDistance = bl;
		this.x = f;
		this.y = g;
		this.z = h;
		this.offsetX = i;
		this.offsetY = j;
		this.offsetZ = k;
		this.field_12260 = l;
		this.particleCount = m;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		ParticleType<?> particleType = Registry.PARTICLE_TYPE.getInt(packetByteBuf.readInt());
		if (particleType == null) {
			particleType = ParticleTypes.field_11235;
		}

		this.longDistance = packetByteBuf.readBoolean();
		this.x = packetByteBuf.readFloat();
		this.y = packetByteBuf.readFloat();
		this.z = packetByteBuf.readFloat();
		this.offsetX = packetByteBuf.readFloat();
		this.offsetY = packetByteBuf.readFloat();
		this.offsetZ = packetByteBuf.readFloat();
		this.field_12260 = packetByteBuf.readFloat();
		this.particleCount = packetByteBuf.readInt();
		this.field_12259 = this.method_11542(packetByteBuf, (ParticleType<Particle>)particleType);
	}

	private <T extends Particle> T method_11542(PacketByteBuf packetByteBuf, ParticleType<T> particleType) {
		return particleType.method_10298().method_10297(particleType, packetByteBuf);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeInt(Registry.PARTICLE_TYPE.getRawId((ParticleType<? extends Particle>)this.field_12259.getParticleType()));
		packetByteBuf.writeBoolean(this.longDistance);
		packetByteBuf.writeFloat(this.x);
		packetByteBuf.writeFloat(this.y);
		packetByteBuf.writeFloat(this.z);
		packetByteBuf.writeFloat(this.offsetX);
		packetByteBuf.writeFloat(this.offsetY);
		packetByteBuf.writeFloat(this.offsetZ);
		packetByteBuf.writeFloat(this.field_12260);
		packetByteBuf.writeInt(this.particleCount);
		this.field_12259.method_10294(packetByteBuf);
	}

	@Environment(EnvType.CLIENT)
	public boolean isLongDistance() {
		return this.longDistance;
	}

	@Environment(EnvType.CLIENT)
	public double getX() {
		return (double)this.x;
	}

	@Environment(EnvType.CLIENT)
	public double getY() {
		return (double)this.y;
	}

	@Environment(EnvType.CLIENT)
	public double getZ() {
		return (double)this.z;
	}

	@Environment(EnvType.CLIENT)
	public float getOffsetX() {
		return this.offsetX;
	}

	@Environment(EnvType.CLIENT)
	public float getOffsetY() {
		return this.offsetY;
	}

	@Environment(EnvType.CLIENT)
	public float getOffsetZ() {
		return this.offsetZ;
	}

	@Environment(EnvType.CLIENT)
	public float method_11543() {
		return this.field_12260;
	}

	@Environment(EnvType.CLIENT)
	public int getParticleCount() {
		return this.particleCount;
	}

	@Environment(EnvType.CLIENT)
	public Particle method_11551() {
		return this.field_12259;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onParticle(this);
	}
}
