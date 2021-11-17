package net.minecraft.world.event;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

/**
 * A position source is a property of a game event listener.
 * 
 * @see net.minecraft.world.event.listener.GameEventListener#getPositionSource()
 */
public interface PositionSource {
	/**
	 * A codec for encoding and decoding any position source whose {@link #getType() type}
	 * is in the {@link net.minecraft.util.registry.Registry#POSITION_SOURCE_TYPE registry}.
	 */
	Codec<PositionSource> CODEC = Registry.POSITION_SOURCE_TYPE.getCodec().dispatch(PositionSource::getType, PositionSourceType::getCodec);

	Optional<BlockPos> getPos(World world);

	/**
	 * Returns the type of this position source.
	 */
	PositionSourceType<?> getType();
}
