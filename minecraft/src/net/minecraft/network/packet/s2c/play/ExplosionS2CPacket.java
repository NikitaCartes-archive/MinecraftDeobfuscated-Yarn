package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
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
	public static final PacketCodec<RegistryByteBuf, ExplosionS2CPacket> CODEC = Packet.createCodec(ExplosionS2CPacket::write, ExplosionS2CPacket::new);
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
	private final RegistryEntry<SoundEvent> soundEvent;

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
		RegistryEntry<SoundEvent> registryEntry
	) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.radius = radius;
		this.affectedBlocks = Lists.<BlockPos>newArrayList(affectedBlocks);
		this.soundEvent = registryEntry;
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

	private ExplosionS2CPacket(RegistryByteBuf buf) {
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
		this.particle = ParticleTypes.PACKET_CODEC.decode(buf);
		this.emitterParticle = ParticleTypes.PACKET_CODEC.decode(buf);
		this.soundEvent = SoundEvent.ENTRY_PACKET_CODEC.decode(buf);
	}

	private void write(RegistryByteBuf buf) {
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
		ParticleTypes.PACKET_CODEC.encode(buf, this.particle);
		ParticleTypes.PACKET_CODEC.encode(buf, this.emitterParticle);
		SoundEvent.ENTRY_PACKET_CODEC.encode(buf, this.soundEvent);
	}

	@Override
	public PacketType<ExplosionS2CPacket> getPacketId() {
		return PlayPackets.EXPLODE;
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

	public RegistryEntry<SoundEvent> getSoundEvent() {
		return this.soundEvent;
	}
}
