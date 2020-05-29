package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.SpawnHelper;

public class PhantomSpawner implements Spawner {
	private int ticksUntilNextSpawn;

	@Override
	public int spawn(ServerWorld serverWorld, boolean bl, boolean bl2) {
		if (!bl) {
			return 0;
		} else if (!serverWorld.getGameRules().getBoolean(GameRules.DO_INSOMNIA)) {
			return 0;
		} else {
			Random random = serverWorld.random;
			this.ticksUntilNextSpawn--;
			if (this.ticksUntilNextSpawn > 0) {
				return 0;
			} else {
				this.ticksUntilNextSpawn = this.ticksUntilNextSpawn + (60 + random.nextInt(60)) * 20;
				if (serverWorld.getAmbientDarkness() < 5 && serverWorld.getDimension().hasSkyLight()) {
					return 0;
				} else {
					int i = 0;

					for (PlayerEntity playerEntity : serverWorld.getPlayers()) {
						if (!playerEntity.isSpectator()) {
							BlockPos blockPos = playerEntity.getBlockPos();
							if (!serverWorld.getDimension().hasSkyLight() || blockPos.getY() >= serverWorld.getSeaLevel() && serverWorld.isSkyVisible(blockPos)) {
								LocalDifficulty localDifficulty = serverWorld.getLocalDifficulty(blockPos);
								if (localDifficulty.isHarderThan(random.nextFloat() * 3.0F)) {
									ServerStatHandler serverStatHandler = ((ServerPlayerEntity)playerEntity).getStatHandler();
									int j = MathHelper.clamp(serverStatHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST)), 1, Integer.MAX_VALUE);
									int k = 24000;
									if (random.nextInt(j) >= 72000) {
										BlockPos blockPos2 = blockPos.up(20 + random.nextInt(15)).east(-10 + random.nextInt(21)).south(-10 + random.nextInt(21));
										BlockState blockState = serverWorld.getBlockState(blockPos2);
										FluidState fluidState = serverWorld.getFluidState(blockPos2);
										if (SpawnHelper.isClearForSpawn(serverWorld, blockPos2, blockState, fluidState, EntityType.PHANTOM)) {
											EntityData entityData = null;
											int l = 1 + random.nextInt(localDifficulty.getGlobalDifficulty().getId() + 1);

											for (int m = 0; m < l; m++) {
												PhantomEntity phantomEntity = EntityType.PHANTOM.create(serverWorld);
												phantomEntity.refreshPositionAndAngles(blockPos2, 0.0F, 0.0F);
												entityData = phantomEntity.initialize(serverWorld, localDifficulty, SpawnReason.NATURAL, entityData, null);
												serverWorld.spawnEntity(phantomEntity);
											}

											i += l;
										}
									}
								}
							}
						}
					}

					return i;
				}
			}
		}
	}
}
