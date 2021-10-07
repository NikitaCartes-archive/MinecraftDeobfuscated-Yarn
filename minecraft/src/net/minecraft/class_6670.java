package net.minecraft;

import com.google.common.collect.Iterables;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.sensor.Sensor;

public class class_6670 {
	private static final class_6670 field_35104 = new class_6670();
	private final List<LivingEntity> field_35105;
	private final Predicate<LivingEntity> field_35106;

	private class_6670() {
		this.field_35105 = List.of();
		this.field_35106 = livingEntity -> false;
	}

	public class_6670(LivingEntity livingEntity, List<LivingEntity> list) {
		this.field_35105 = list;
		Object2BooleanOpenHashMap<LivingEntity> object2BooleanOpenHashMap = new Object2BooleanOpenHashMap<>(list.size());
		Predicate<LivingEntity> predicate = livingEntity2 -> Sensor.testTargetPredicate(livingEntity, livingEntity2);
		this.field_35106 = livingEntityx -> object2BooleanOpenHashMap.computeBooleanIfAbsent(livingEntityx, predicate);
	}

	public static class_6670 method_38971() {
		return field_35104;
	}

	public Optional<LivingEntity> method_38975(Predicate<LivingEntity> predicate) {
		for (LivingEntity livingEntity : this.field_35105) {
			if (predicate.test(livingEntity) && this.field_35106.test(livingEntity)) {
				return Optional.of(livingEntity);
			}
		}

		return Optional.empty();
	}

	public Iterable<LivingEntity> method_38978(Predicate<LivingEntity> predicate) {
		return Iterables.filter(this.field_35105, livingEntity -> predicate.test(livingEntity) && this.field_35106.test(livingEntity));
	}

	public Stream<LivingEntity> method_38980(Predicate<LivingEntity> predicate) {
		return this.field_35105.stream().filter(livingEntity -> predicate.test(livingEntity) && this.field_35106.test(livingEntity));
	}

	public boolean method_38972(LivingEntity livingEntity) {
		return this.field_35105.contains(livingEntity) && this.field_35106.test(livingEntity);
	}

	public boolean method_38981(Predicate<LivingEntity> predicate) {
		for (LivingEntity livingEntity : this.field_35105) {
			if (predicate.test(livingEntity) && this.field_35106.test(livingEntity)) {
				return true;
			}
		}

		return false;
	}
}
