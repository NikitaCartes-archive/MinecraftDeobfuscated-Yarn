package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;

/**
 * Sent when an explosion occurs in the world.
 * 
 * <p>The client will update {@linkplain
 * net.minecraft.client.MinecraftClient#player the player}'s velocity as
 * well as performing an explosion.
 * 
 * @see net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket
 */
public class ExplosionS2CPacket implements Packet<ClientPlayPacketListener> {
	private final double x;
	private final double y;
	private final double z;
	private final float radius;
	private final List<BlockPos> affectedBlocks;
	private final float playerVelocityX;
	private final float playerVelocityY;
	private final float playerVelocityZ;
	private final ParticleEffect particle;
	private final ParticleEffect emitterParticle;
	private final Explosion.DestructionType destructionType;
	private final SoundEvent soundEvent;

	public ExplosionS2CPacket(
		double x,
		double y,
		double z,
		float radius,
		List<BlockPos> affectedBlocks,
		@Nullable Vec3d playerVelocity,
		Explosion.DestructionType destructionType,
		ParticleEffect particle,
		ParticleEffect emitterParticle,
		SoundEvent soundEvent
	) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.radius = radius;
		this.affectedBlocks = Lists.<BlockPos>newArrayList(affectedBlocks);
		this.soundEvent = soundEvent;
		if (playerVelocity != null) {
			this.playerVelocityX = (float)playerVelocity.x;
			this.playerVelocityY = (float)playerVelocity.y;
			this.playerVelocityZ = (float)playerVelocity.z;
		} else {
			this.playerVelocityX = 0.0F;
			this.playerVelocityY = 0.0F;
			this.playerVelocityZ = 0.0F;
		}

		this.destructionType = destructionType;
		this.particle = particle;
		this.emitterParticle = emitterParticle;
	}

	public ExplosionS2CPacket(PacketByteBuf buf) {
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
		this.radius = buf.readFloat();
		int i = MathHelper.floor(this.x);
		int j = MathHelper.floor(this.y);
		int k = MathHelper.floor(this.z);
		this.affectedBlocks = buf.readList(buf2 -> {
			int l = buf2.readByte() + i;
			int m = buf2.readByte() + j;
			int n = buf2.readByte() + k;
			return new BlockPos(l, m, n);
		});
		this.playerVelocityX = buf.readFloat();
		this.playerVelocityY = buf.readFloat();
		this.playerVelocityZ = buf.readFloat();
		this.destructionType = buf.readEnumConstant(Explosion.DestructionType.class);
		this.particle = readParticleEffect(buf, buf.readRegistryValue(Registries.PARTICLE_TYPE));
		this.emitterParticle = readParticleEffect(buf, buf.readRegistryValue(Registries.PARTICLE_TYPE));
		this.soundEvent = SoundEvent.fromBuf(buf);
	}

	private static <T extends ParticleEffect> T readParticleEffect(PacketByteBuf buf, ParticleType<T> particleType) {
		return particleType.getParametersFactory().read(particleType, buf);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
		buf.writeFloat(this.radius);
		int i = MathHelper.floor(this.x);
		int j = MathHelper.floor(this.y);
		int k = MathHelper.floor(this.z);
		buf.writeCollection(this.affectedBlocks, (buf2, pos) -> {
			int l = pos.getX() - i;
			int m = pos.getY() - j;
			int n = pos.getZ() - k;
			buf2.writeByte(l);
			buf2.writeByte(m);
			buf2.writeByte(n);
		});
		buf.writeFloat(this.playerVelocityX);
		buf.writeFloat(this.playerVelocityY);
		buf.writeFloat(this.playerVelocityZ);
		buf.writeEnumConstant(this.destructionType);
		buf.writeRegistryValue(Registries.PARTICLE_TYPE, this.particle.getType());
		buf.writeRegistryValue(Registries.PARTICLE_TYPE, this.emitterParticle.getType());
		this.soundEvent.writeBuf(buf);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onExplosion(this);
	}

	public float getPlayerVelocityX() {
		return this.playerVelocityX;
	}

	public float getPlayerVelocityY() {
		return this.playerVelocityY;
	}

	public float getPlayerVelocityZ() {
		return this.playerVelocityZ;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public double getZ() {
		return this.z;
	}

	public float getRadius() {
		return this.radius;
	}

	public List<BlockPos> getAffectedBlocks() {
		return this.affectedBlocks;
	}

	public Explosion.DestructionType getDestructionType() {
		return this.destructionType;
	}

	public ParticleEffect getParticle() {
		return this.particle;
	}

	public ParticleEffect getEmitterParticle() {
		return this.emitterParticle;
	}

	public SoundEvent getSoundEvent() {
		return this.soundEvent;
	}
}
