package net.minecraft.world.event;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * A position source is a property of a game event listener.
 * 
 * @see net.minecraft.world.event.listener.GameEventListener#getPositionSource()
 */
public interface PositionSource {
	/**
	 * A codec for encoding and decoding any position source whose {@link #getType() type}
	 * is in the {@link net.minecraft.registry.Registries#POSITION_SOURCE_TYPE registry}.
	 */
	Codec<PositionSource> CODEC = Registries.POSITION_SOURCE_TYPE.getCodec().dispatch(PositionSource::getType, PositionSourceType::getCodec);

	Optional<Vec3d> getPos(World world);

	/**
	 * Returns the type of this position source.
	 */
	PositionSourceType<?> getType();
}
