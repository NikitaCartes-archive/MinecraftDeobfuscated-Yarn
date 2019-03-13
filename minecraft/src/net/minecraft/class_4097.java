package net.minecraft;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.world.ServerWorld;

public abstract class class_4097<E extends LivingEntity> {
	private class_4097.class_4098 field_18333 = class_4097.class_4098.field_18337;
	private long field_18334;
	private final int field_18335;
	private final int field_18336;

	public class_4097() {
		this(60, 60);
	}

	public class_4097(int i, int j) {
		this.field_18335 = i;
		this.field_18336 = j;
	}

	public class_4097.class_4098 method_18921() {
		return this.field_18333;
	}

	public final boolean method_18922(ServerWorld serverWorld, E livingEntity, long l) {
		if (this.method_18914().stream().allMatch(pair -> livingEntity.method_18868().method_18876((class_4140<?>)pair.getFirst(), (class_4141)pair.getSecond()))
			&& this.method_18919(serverWorld, livingEntity)) {
			this.field_18333 = class_4097.class_4098.field_18338;
			this.field_18334 = l + (long)this.field_18335 + (long)serverWorld.getRandom().nextInt(this.field_18336 + 1 - this.field_18335);
			this.method_18920(serverWorld, livingEntity, l);
			return true;
		} else {
			return false;
		}
	}

	protected void method_18920(ServerWorld serverWorld, E livingEntity, long l) {
	}

	public final boolean method_18923(ServerWorld serverWorld, E livingEntity, long l) {
		if (!this.method_18915(l) && this.method_18927(serverWorld, livingEntity, l)) {
			this.method_18924(serverWorld, livingEntity, l);
			return true;
		} else {
			this.method_18925(serverWorld, livingEntity, l);
			return false;
		}
	}

	protected void method_18924(ServerWorld serverWorld, E livingEntity, long l) {
	}

	public final void method_18925(ServerWorld serverWorld, E livingEntity, long l) {
		this.field_18333 = class_4097.class_4098.field_18337;
		this.method_18926(serverWorld, livingEntity, l);
	}

	protected void method_18926(ServerWorld serverWorld, E livingEntity, long l) {
	}

	protected boolean method_18927(ServerWorld serverWorld, E livingEntity, long l) {
		return false;
	}

	protected boolean method_18915(long l) {
		return l > this.field_18334;
	}

	protected void method_18916(LivingEntity livingEntity, LivingEntity livingEntity2) {
		class_4095<?> lv = livingEntity.method_18868();
		class_4095<?> lv2 = livingEntity2.method_18868();
		lv.method_18878(class_4140.field_18446, new class_4102(livingEntity2));
		lv2.method_18878(class_4140.field_18446, new class_4102(livingEntity));
		lv.method_18878(
			class_4140.field_18445, new class_4142(new class_4102(livingEntity2), (float)livingEntity.method_5996(EntityAttributes.MOVEMENT_SPEED).getValue(), 2)
		);
		lv2.method_18878(
			class_4140.field_18445, new class_4142(new class_4102(livingEntity), (float)livingEntity2.method_5996(EntityAttributes.MOVEMENT_SPEED).getValue(), 2)
		);
	}

	protected boolean method_18918(class_4095<?> arg, class_4140<? extends LivingEntity> arg2, EntityType<?> entityType) {
		if (arg.method_18904(arg2).isPresent() && arg.method_18904(class_4140.field_18442).isPresent()) {
			LivingEntity livingEntity = (LivingEntity)arg.method_18904(arg2).get();
			return livingEntity.method_5864() == entityType && livingEntity.isValid() && ((List)arg.method_18904(class_4140.field_18442).get()).contains(livingEntity);
		} else {
			return false;
		}
	}

	protected boolean method_18919(ServerWorld serverWorld, E livingEntity) {
		return true;
	}

	protected abstract Set<Pair<class_4140<?>, class_4141>> method_18914();

	public static enum class_4098 {
		field_18337,
		field_18338;
	}
}
