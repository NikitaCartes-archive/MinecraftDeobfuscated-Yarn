package net.minecraft.world.event.listener;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;

public interface GameEventListener {
	PositionSource getPositionSource();

	int getRange();

	boolean listen(World world, GameEvent event, @Nullable Entity entity, BlockPos pos);
}
