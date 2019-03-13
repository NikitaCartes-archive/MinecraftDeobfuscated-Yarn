package net.minecraft;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;

public class class_4147 extends class_4148<LivingEntity> {
	@Override
	public void method_19101(ServerWorld serverWorld, LivingEntity livingEntity) {
		this.field_18463 = serverWorld.getTime();
		List<PlayerEntity> list = (List<PlayerEntity>)serverWorld.getPlayers()
			.stream()
			.filter(EntityPredicates.EXCEPT_SPECTATOR)
			.filter(serverPlayerEntity -> livingEntity.squaredDistanceTo(serverPlayerEntity) < 256.0)
			.sorted(Comparator.comparingDouble(livingEntity::squaredDistanceTo))
			.collect(Collectors.toList());
		class_4095<?> lv = livingEntity.method_18868();
		lv.method_18878(class_4140.field_18443, list);
		lv.method_18879(class_4140.field_18444, list.stream().filter(livingEntity::canSee).findFirst());
	}

	@Override
	protected Set<class_4140<?>> method_19099() {
		return ImmutableSet.of(class_4140.field_18443, class_4140.field_18444);
	}
}
