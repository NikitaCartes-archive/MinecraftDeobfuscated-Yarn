package net.minecraft.network.packet.s2c.play;

import java.util.Optional;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;

/**
 * Sent when an explosion occurs in the world.
 * 
 * <p>The client will update {@linkplain
 * net.minecraft.client.MinecraftClient#player the player}'s velocity as
 * well as performing an explosion.
 * 
 * @see net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket
 */
public record ExplosionS2CPacket(Vec3d center, Optional<Vec3d> playerKnockback, ParticleEffect explosionParticle, RegistryEntry<SoundEvent> explosionSound)
	implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<RegistryByteBuf, ExplosionS2CPacket> CODEC = PacketCodec.tuple(
		Vec3d.PACKET_CODEC,
		ExplosionS2CPacket::center,
		Vec3d.PACKET_CODEC.collect(PacketCodecs::optional),
		ExplosionS2CPacket::playerKnockback,
		ParticleTypes.PACKET_CODEC,
		ExplosionS2CPacket::explosionParticle,
		SoundEvent.ENTRY_PACKET_CODEC,
		ExplosionS2CPacket::explosionSound,
		ExplosionS2CPacket::new
	);

	@Override
	public PacketType<ExplosionS2CPacket> getPacketId() {
		return PlayPackets.EXPLODE;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onExplosion(this);
	}
}
