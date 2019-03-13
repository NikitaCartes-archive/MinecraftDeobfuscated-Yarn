package net.minecraft;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

public class class_4103<E extends LivingEntity> extends class_4097<E> {
	private final Set<Pair<class_4140<?>, class_4141>> field_18343;
	private final Set<class_4140<?>> field_18344;
	private final class_4103.class_4104 field_18345;
	private final class_4103.class_4105 field_18346;
	private final class_4131<class_4097<? super E>> field_18347 = new class_4131<>();

	public class_4103(
		Set<Pair<class_4140<?>, class_4141>> set,
		Set<class_4140<?>> set2,
		class_4103.class_4104 arg,
		class_4103.class_4105 arg2,
		List<Pair<class_4097<? super E>, Integer>> list
	) {
		this.field_18343 = set;
		this.field_18344 = set2;
		this.field_18345 = arg;
		this.field_18346 = arg2;
		list.forEach(pair -> this.field_18347.method_19031((class_4097<? super E>)pair.getFirst(), (Integer)pair.getSecond()));
	}

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return this.field_18343;
	}

	@Override
	protected boolean method_18927(ServerWorld serverWorld, E livingEntity, long l) {
		return this.field_18347
			.method_19032()
			.filter(arg -> arg.method_18921() == class_4097.class_4098.field_18338)
			.anyMatch(arg -> arg.method_18927(serverWorld, livingEntity, l));
	}

	@Override
	protected void method_18920(ServerWorld serverWorld, E livingEntity, long l) {
		this.field_18345.method_18939(this.field_18347);
		if (this.field_18346 == class_4103.class_4105.field_18352) {
			this.field_18347
				.method_19032()
				.filter(arg -> arg.method_18921() == class_4097.class_4098.field_18337)
				.filter(arg -> arg.method_18922(serverWorld, livingEntity, l))
				.findFirst();
		} else if (this.field_18346 == class_4103.class_4105.field_18353) {
			this.field_18347
				.method_19032()
				.filter(arg -> arg.method_18921() == class_4097.class_4098.field_18337)
				.forEach(arg -> arg.method_18922(serverWorld, livingEntity, l));
		}
	}

	@Override
	protected void method_18924(ServerWorld serverWorld, E livingEntity, long l) {
		this.field_18347
			.method_19032()
			.filter(arg -> arg.method_18921() == class_4097.class_4098.field_18338)
			.forEach(arg -> arg.method_18923(serverWorld, livingEntity, l));
	}

	@Override
	protected void method_18926(ServerWorld serverWorld, E livingEntity, long l) {
		this.field_18347
			.method_19032()
			.filter(arg -> arg.method_18921() == class_4097.class_4098.field_18338)
			.forEach(arg -> arg.method_18925(serverWorld, livingEntity, l));
		class_4095<?> lv = livingEntity.method_18868();
		this.field_18344.forEach(lv::method_18875);
	}

	static enum class_4104 {
		field_18348(arg -> {
		}),
		field_18349(class_4131::method_19029);

		private final Consumer<class_4131<?>> field_18350;

		private class_4104(Consumer<class_4131<?>> consumer) {
			this.field_18350 = consumer;
		}

		public void method_18939(class_4131<?> arg) {
			this.field_18350.accept(arg);
		}
	}

	static enum class_4105 {
		field_18352,
		field_18353;
	}
}
