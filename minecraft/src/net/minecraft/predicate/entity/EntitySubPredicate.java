package net.minecraft.predicate.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public interface EntitySubPredicate {
	Codec<EntitySubPredicate> CODEC = Registries.ENTITY_SUB_PREDICATE_TYPE.getCodec().dispatch(EntitySubPredicate::getCodec, Function.identity());

	MapCodec<? extends EntitySubPredicate> getCodec();

	boolean test(Entity entity, ServerWorld world, @Nullable Vec3d pos);
}
