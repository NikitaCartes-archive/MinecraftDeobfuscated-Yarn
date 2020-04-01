package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;

public class NetherPortalBlock extends PortalBlock {
	public NetherPortalBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (world.dimension.hasVisibleSky() && world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING) && random.nextInt(2000) < world.getDifficulty().getId()) {
			while (world.getBlockState(pos).getBlock() == this) {
				pos = pos.down();
			}

			if (world.getBlockState(pos).allowsSpawning(world, pos, EntityType.ZOMBIFIED_PIGLIN)) {
				Entity entity = EntityType.ZOMBIFIED_PIGLIN.spawn(world, null, null, null, pos.up(), SpawnType.STRUCTURE, false, false);
				if (entity != null) {
					entity.netherPortalCooldown = entity.getDefaultNetherPortalCooldown();
				}
			}
		}
	}
}
