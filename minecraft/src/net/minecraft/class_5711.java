package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;

public class class_5711 implements class_5713 {
	private final List<GameEventListener> listeners = Lists.<GameEventListener>newArrayList();
	private final World world;

	public class_5711(World world) {
		this.world = world;
	}

	@Override
	public boolean isEmpty() {
		return this.listeners.isEmpty();
	}

	@Override
	public void addListener(GameEventListener listener) {
		this.listeners.add(listener);
		DebugInfoSender.method_33140(this.world, listener);
	}

	@Override
	public void removeListener(GameEventListener listener) {
		this.listeners.remove(listener);
	}

	@Override
	public void method_32943(GameEvent gameEvent, @Nullable Entity entity, BlockPos blockPos) {
		boolean bl = this.listeners.stream().filter(gameEventListener -> this.method_32936(this.world, gameEvent, entity, blockPos, gameEventListener)).count() > 0L;
		if (bl) {
			DebugInfoSender.method_33139(this.world, gameEvent, blockPos);
		}
	}

	private boolean method_32936(World world, GameEvent gameEvent, @Nullable Entity entity, BlockPos blockPos, GameEventListener gameEventListener) {
		Optional<BlockPos> optional = gameEventListener.getPositionSource().getPos(world);
		if (!optional.isPresent()) {
			return false;
		} else {
			double d = ((BlockPos)optional.get()).getSquaredDistance(blockPos, false);
			int i = gameEventListener.getRange() * gameEventListener.getRange();
			return d <= (double)i && gameEventListener.method_32947(world, gameEvent, entity, blockPos);
		}
	}
}
