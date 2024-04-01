package net.minecraft.predicate.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public record PotatoPredicate(boolean isPotato) implements EntitySubPredicate {
	public static final MapCodec<PotatoPredicate> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(Codec.BOOL.fieldOf("is_potato").forGetter(PotatoPredicate::isPotato)).apply(instance, PotatoPredicate::new)
	);

	@Override
	public MapCodec<PotatoPredicate> getCodec() {
		return CODEC;
	}

	@Override
	public boolean test(Entity entity, ServerWorld world, @Nullable Vec3d pos) {
		return entity.isPotato() == this.isPotato;
	}
}
