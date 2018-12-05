package net.minecraft;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class class_2910 {
	private int field_13244;

	public int spawn(World world, boolean bl, boolean bl2) {
		if (!bl) {
			return 0;
		} else {
			Random random = world.random;
			this.field_13244--;
			if (this.field_13244 > 0) {
				return 0;
			} else {
				this.field_13244 = this.field_13244 + (60 + random.nextInt(60)) * 20;
				if (world.getAmbientDarkness() < 5 && world.dimension.method_12451()) {
					return 0;
				} else {
					int i = 0;

					for (PlayerEntity playerEntity : world.players) {
						if (!playerEntity.isSpectator()) {
							BlockPos blockPos = new BlockPos(playerEntity);
							if (!world.dimension.method_12451() || blockPos.getY() >= world.getSeaLevel() && world.getSkyLightLevel(blockPos)) {
								LocalDifficulty localDifficulty = world.getLocalDifficulty(blockPos);
								if (localDifficulty.method_5455(random.nextFloat() * 3.0F)) {
									class_3442 lv = ((ServerPlayerEntity)playerEntity).method_14248();
									int j = MathHelper.clamp(lv.method_15025(Stats.field_15419.method_14956(Stats.field_15429)), 1, Integer.MAX_VALUE);
									int k = 24000;
									if (random.nextInt(j) >= 72000) {
										BlockPos blockPos2 = blockPos.up(20 + random.nextInt(15)).east(-10 + random.nextInt(21)).south(-10 + random.nextInt(21));
										BlockState blockState = world.getBlockState(blockPos2);
										FluidState fluidState = world.getFluidState(blockPos2);
										if (class_1948.method_8662(world, blockPos2, blockState, fluidState)) {
											EntityData entityData = null;
											int l = 1 + random.nextInt(localDifficulty.method_5454().getId() + 1);

											for (int m = 0; m < l; m++) {
												PhantomEntity phantomEntity = new PhantomEntity(world);
												phantomEntity.setPositionAndAngles(blockPos2, 0.0F, 0.0F);
												entityData = phantomEntity.method_5943(world, localDifficulty, class_3730.field_16459, entityData, null);
												world.spawnEntity(phantomEntity);
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
