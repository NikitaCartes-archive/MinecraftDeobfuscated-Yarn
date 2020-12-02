package net.minecraft.world.event;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public interface PositionSource {
	Codec<PositionSource> TYPE_CODEC = Registry.POSITION_SOURCE_TYPE.dispatch(PositionSource::getType, PositionSourceType::getCodec);

	Optional<BlockPos> getPos(World world);

	PositionSourceType<?> getType();
}
