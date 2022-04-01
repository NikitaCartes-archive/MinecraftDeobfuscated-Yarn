package net.minecraft;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.Registry;

public interface class_7317 {
	Codec<class_7317> field_38543 = new Codec<class_7317>() {
		@Override
		public <T> DataResult<Pair<class_7317, T>> decode(DynamicOps<T> dynamicOps, T object) {
			DataResult<Pair<class_7317, T>> dataResult = class_7317.class_7318.field_38544.decode(dynamicOps, object).map(pair -> pair.mapFirst(Function.identity()));
			DataResult<Pair<class_7317, T>> dataResult2 = class_7317.class_7319.field_38545.decode(dynamicOps, object).map(pair -> pair.mapFirst(Function.identity()));
			return dataResult.result().isPresent() ? dataResult : dataResult2;
		}

		public <T> DataResult<T> encode(class_7317 arg, DynamicOps<T> dynamicOps, T object) {
			return class_7317.method_42844(arg, dynamicOps);
		}
	};

	static class_7317.class_7318 method_42845(Block block) {
		return new class_7317.class_7318(block);
	}

	static class_7317.class_7319 method_42842(EntityType<?> entityType) {
		return new class_7317.class_7319(entityType);
	}

	void method_42841(ServerPlayerEntity serverPlayerEntity);

	boolean method_42843(class_7317 arg);

	default ItemStack method_42840() {
		return ItemStack.EMPTY;
	}

	Codec<? extends class_7317> method_42846();

	static <T, C extends class_7317> DataResult<T> method_42844(C arg, DynamicOps<T> dynamicOps) {
		Codec<C> codec = (Codec<C>)arg.method_42846();
		return codec.encodeStart(dynamicOps, arg);
	}

	public static record class_7318(Block block) implements class_7317 {
		public static final Codec<class_7317.class_7318> field_38544 = RecordCodecBuilder.create(
			instance -> instance.group(Registry.BLOCK.getCodec().fieldOf("block").forGetter(class_7317.class_7318::block)).apply(instance, class_7317.class_7318::new)
		);

		@Override
		public void method_42841(ServerPlayerEntity serverPlayerEntity) {
			serverPlayerEntity.method_42838(this.block.getDefaultState());
		}

		@Override
		public boolean method_42843(class_7317 arg) {
			return arg instanceof class_7317.class_7318 lv ? lv.block() == this.block : false;
		}

		@Override
		public ItemStack method_42840() {
			return new ItemStack(this.block);
		}

		@Override
		public Codec<? extends class_7317> method_42846() {
			return field_38544;
		}
	}

	public static record class_7319(EntityType<?> entity) implements class_7317 {
		public static final Codec<class_7317.class_7319> field_38545 = RecordCodecBuilder.create(
			instance -> instance.group(Registry.ENTITY_TYPE.getCodec().fieldOf("entity").forGetter(class_7317.class_7319::entity))
					.apply(instance, class_7317.class_7319::new)
		);

		@Override
		public void method_42841(ServerPlayerEntity serverPlayerEntity) {
			ServerWorld serverWorld = serverPlayerEntity.getWorld();
			Entity entity = this.entity.spawnFromItemStack(serverWorld, null, serverPlayerEntity, serverPlayerEntity.getBlockPos(), SpawnReason.SPAWNER, false, false);
			if (entity != null) {
				serverPlayerEntity.method_42834(entity);
			}
		}

		@Override
		public boolean method_42843(class_7317 arg) {
			return arg instanceof class_7317.class_7319 lv ? lv.entity() == this.entity : false;
		}

		@Override
		public Codec<? extends class_7317> method_42846() {
			return field_38545;
		}
	}
}
