package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class ParticleS2CPacket implements Packet<ClientPlayPacketListener> {
	private float x;
	private float y;
	private float z;
	private float offsetX;
	private float offsetY;
	private float offsetZ;
	private float speed;
	private int count;
	private boolean longDistance;
	private ParticleParameters parameters;

	public ParticleS2CPacket() {
	}

	public <T extends ParticleParameters> ParticleS2CPacket(T particleParameters, boolean bl, float f, float g, float h, float i, float j, float k, float l, int m) {
		this.parameters = particleParameters;
		this.longDistance = bl;
		this.x = f;
		this.y = g;
		this.z = h;
		this.offsetX = i;
		this.offsetY = j;
		this.offsetZ = k;
		this.speed = l;
		this.count = m;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		ParticleType<?> particleType = Registry.PARTICLE_TYPE.get(packetByteBuf.readInt());
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
		this.speed = packetByteBuf.readFloat();
		this.count = packetByteBuf.readInt();
		this.parameters = this.readParticleParameters(packetByteBuf, (ParticleType<ParticleParameters>)particleType);
	}

	private <T extends ParticleParameters> T readParticleParameters(PacketByteBuf packetByteBuf, ParticleType<T> particleType) {
		return particleType.getParametersFactory().read(particleType, packetByteBuf);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeInt(Registry.PARTICLE_TYPE.getRawId((ParticleType<? extends ParticleParameters>)this.parameters.getType()));
		packetByteBuf.writeBoolean(this.longDistance);
		packetByteBuf.writeFloat(this.x);
		packetByteBuf.writeFloat(this.y);
		packetByteBuf.writeFloat(this.z);
		packetByteBuf.writeFloat(this.offsetX);
		packetByteBuf.writeFloat(this.offsetY);
		packetByteBuf.writeFloat(this.offsetZ);
		packetByteBuf.writeFloat(this.speed);
		packetByteBuf.writeInt(this.count);
		this.parameters.write(packetByteBuf);
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
	public float getSpeed() {
		return this.speed;
	}

	@Environment(EnvType.CLIENT)
	public int getCount() {
		return this.count;
	}

	@Environment(EnvType.CLIENT)
	public ParticleParameters getParameters() {
		return this.parameters;
	}

	public void method_11553(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onParticle(this);
	}
}
