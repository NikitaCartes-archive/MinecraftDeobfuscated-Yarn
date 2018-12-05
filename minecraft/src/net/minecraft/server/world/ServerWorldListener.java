package net.minecraft.server.world;

import javax.annotation.Nullable;
import net.minecraft.class_2765;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.packet.BlockBreakingProgressClientPacket;
import net.minecraft.client.network.packet.PlaySoundClientPacket;
import net.minecraft.client.network.packet.WorldEventClientPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.Particle;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldListener;

public class ServerWorldListener implements WorldListener {
	private final MinecraftServer server;
	private final ServerWorld world;

	public ServerWorldListener(MinecraftServer minecraftServer, ServerWorld serverWorld) {
		this.server = minecraftServer;
		this.world = serverWorld;
	}

	@Override
	public void addParticle(Particle particle, boolean bl, double d, double e, double f, double g, double h, double i) {
	}

	@Override
	public void addParticle(Particle particle, boolean bl, boolean bl2, double d, double e, double f, double g, double h, double i) {
	}

	@Override
	public void onEntityAdded(Entity entity) {
		this.world.getEntityTracker().add(entity);
		if (entity instanceof ServerPlayerEntity) {
			this.world.dimension.method_12457((ServerPlayerEntity)entity);
		}
	}

	@Override
	public void onEntityRemoved(Entity entity) {
		this.world.getEntityTracker().remove(entity);
		this.world.method_14170().resetEntityScore(entity);
		if (entity instanceof ServerPlayerEntity) {
			this.world.dimension.method_12458((ServerPlayerEntity)entity);
		}
	}

	@Override
	public void onSound(@Nullable PlayerEntity playerEntity, SoundEvent soundEvent, SoundCategory soundCategory, double d, double e, double f, float g, float h) {
		this.server
			.getConfigurationManager()
			.sendToAround(
				playerEntity,
				d,
				e,
				f,
				g > 1.0F ? (double)(16.0F * g) : 16.0,
				this.world.dimension.getType(),
				new PlaySoundClientPacket(soundEvent, soundCategory, d, e, f, g, h)
			);
	}

	@Override
	public void method_8565(@Nullable PlayerEntity playerEntity, SoundEvent soundEvent, SoundCategory soundCategory, Entity entity, float f, float g) {
		this.server
			.getConfigurationManager()
			.sendToAround(
				playerEntity,
				entity.x,
				entity.y,
				entity.z,
				f > 1.0F ? (double)(16.0F * f) : 16.0,
				this.world.dimension.getType(),
				new class_2765(soundEvent, soundCategory, entity, f, g)
			);
	}

	@Override
	public void onBlockUpdate(BlockView blockView, BlockPos blockPos, BlockState blockState, BlockState blockState2, int i) {
		this.world.getChunkManager().markForUpdate(blockPos);
	}

	@Override
	public void playRecord(SoundEvent soundEvent, BlockPos blockPos) {
	}

	@Override
	public void onWorldEvent(PlayerEntity playerEntity, int i, BlockPos blockPos, int j) {
		this.server
			.getConfigurationManager()
			.sendToAround(
				playerEntity,
				(double)blockPos.getX(),
				(double)blockPos.getY(),
				(double)blockPos.getZ(),
				64.0,
				this.world.dimension.getType(),
				new WorldEventClientPacket(i, blockPos, j, false)
			);
	}

	@Override
	public void onGlobalWorldEvent(int i, BlockPos blockPos, int j) {
		this.server.getConfigurationManager().sendToAll(new WorldEventClientPacket(i, blockPos, j, true));
	}

	@Override
	public void onBlockBreakingStage(int i, BlockPos blockPos, int j) {
		for (ServerPlayerEntity serverPlayerEntity : this.server.getConfigurationManager().getPlayerList()) {
			if (serverPlayerEntity != null && serverPlayerEntity.world == this.world && serverPlayerEntity.getEntityId() != i) {
				double d = (double)blockPos.getX() - serverPlayerEntity.x;
				double e = (double)blockPos.getY() - serverPlayerEntity.y;
				double f = (double)blockPos.getZ() - serverPlayerEntity.z;
				if (d * d + e * e + f * f < 1024.0) {
					serverPlayerEntity.networkHandler.sendPacket(new BlockBreakingProgressClientPacket(i, blockPos, j));
				}
			}
		}
	}
}
