package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class class_4111 extends class_4097<VillagerEntity> {
	private final int field_18367 = 5;
	private long field_18368 = 0L;

	public class_4111() {
		super(350, 350);
	}

	protected boolean method_18972(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		class_4095<?> lv = villagerEntity.method_18868();
		VillagerEntity villagerEntity2 = (VillagerEntity)lv.method_18904(class_4140.field_18448).get();
		return villagerEntity.method_5864() == EntityType.VILLAGER
			&& this.method_18918(lv, class_4140.field_18448, EntityType.VILLAGER)
			&& villagerEntity.method_19184()
			&& villagerEntity2.method_19184();
	}

	protected boolean method_18973(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		class_4095<?> lv = villagerEntity.method_18868();
		return l <= this.field_18368
			&& this.method_18918(lv, class_4140.field_18448, EntityType.VILLAGER)
			&& villagerEntity.method_19184()
			&& ((VillagerEntity)lv.method_18904(class_4140.field_18448).get()).method_19184();
	}

	protected void method_18974(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		VillagerEntity villagerEntity2 = (VillagerEntity)villagerEntity.method_18868().method_18904(class_4140.field_18448).get();
		this.method_18916(villagerEntity, villagerEntity2);
		serverWorld.summonParticle(villagerEntity2, (byte)18);
		serverWorld.summonParticle(villagerEntity, (byte)18);
		this.field_18368 = l + 275L + (long)villagerEntity.getRand().nextInt(50);
	}

	protected void method_18975(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		VillagerEntity villagerEntity2 = (VillagerEntity)villagerEntity.method_18868().method_18904(class_4140.field_18448).get();
		if (!(villagerEntity.squaredDistanceTo(villagerEntity2) > 5.0)) {
			this.method_18916(villagerEntity, villagerEntity2);
			if (l >= this.field_18368) {
				Optional<BlockPos> optional = serverWorld.method_19494()
					.method_19126(class_4158.field_18517.method_19164(), blockPos -> true, new BlockPos(villagerEntity), 48);
				if (!optional.isPresent()) {
					serverWorld.summonParticle(villagerEntity2, (byte)13);
					serverWorld.summonParticle(villagerEntity, (byte)13);
					return;
				}

				villagerEntity.method_19185();
				villagerEntity2.method_19185();
				Optional<VillagerEntity> optional2 = this.method_18970(villagerEntity, villagerEntity2);
				if (optional2.isPresent()) {
					villagerEntity.method_19193(12);
					villagerEntity2.method_19193(12);
					((VillagerEntity)optional2.get())
						.method_18868()
						.method_18878(class_4140.field_18438, class_4208.method_19443(serverWorld.method_8597().method_12460(), (BlockPos)optional.get()));
				} else {
					serverWorld.method_19494().method_19129((BlockPos)optional.get());
				}
			}

			if (villagerEntity.getRand().nextInt(35) == 0) {
				serverWorld.summonParticle(villagerEntity2, (byte)12);
				serverWorld.summonParticle(villagerEntity, (byte)12);
			}
		}
	}

	protected void method_18976(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		villagerEntity.method_18868().method_18875(class_4140.field_18448);
	}

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(Pair.of(class_4140.field_18448, class_4141.field_18456), Pair.of(class_4140.field_18442, class_4141.field_18456));
	}

	private Optional<VillagerEntity> method_18970(VillagerEntity villagerEntity, VillagerEntity villagerEntity2) {
		VillagerEntity villagerEntity3 = villagerEntity.method_7225(villagerEntity2);
		if (villagerEntity3 == null) {
			return Optional.empty();
		} else {
			villagerEntity.setBreedingAge(6000);
			villagerEntity2.setBreedingAge(6000);
			villagerEntity3.setBreedingAge(-24000);
			villagerEntity3.setPositionAndAngles(villagerEntity.x, villagerEntity.y, villagerEntity.z, 0.0F, 0.0F);
			villagerEntity.field_6002.spawnEntity(villagerEntity3);
			villagerEntity.field_6002.summonParticle(villagerEntity3, (byte)12);
			return Optional.of(villagerEntity3);
		}
	}
}
