package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.VillagerProfession;

public class class_4129 {
	public static ImmutableList<Pair<Integer, ? extends class_4097<? super VillagerEntity>>> method_19020(VillagerProfession villagerProfession, float f) {
		float g = f * 1.5F;
		return ImmutableList.of(
			Pair.of(0, new class_4125(0.4F, 0.8F)),
			Pair.of(0, new class_4112()),
			Pair.of(0, new class_4107()),
			Pair.of(0, new class_4110(45, 90)),
			Pair.of(0, new class_4113()),
			Pair.of(2, new class_4108(f)),
			Pair.of(
				5,
				new class_4118<>(
					ImmutableList.of(
						Pair.of(new class_4119(EntityType.CAT, 8.0F), 5),
						Pair.of(new class_4119(EntityType.VILLAGER, 8.0F), 1),
						Pair.of(new class_4119(EntityType.PLAYER, 8.0F), 1),
						Pair.of(new class_4101(30, 60), 1)
					)
				)
			),
			Pair.of(10, new class_4096(villagerProfession.method_19198(), class_4140.field_18439)),
			Pair.of(10, new class_4096(class_4158.field_18517, class_4140.field_18438)),
			Pair.of(10, new class_4096(class_4158.field_18518, class_4140.field_18440)),
			Pair.of(10, new class_4114())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends class_4097<? super VillagerEntity>>> method_19021(VillagerProfession villagerProfession, float f) {
		return ImmutableList.of(
			Pair.of(5, new class_4118<>(ImmutableList.of(Pair.of(new class_4133(), 5), Pair.of(new class_4116(class_4140.field_18439, 16), 2)))),
			Pair.of(10, new class_4130(400, 1600)),
			Pair.of(10, new class_4109(EntityType.PLAYER, 4)),
			Pair.of(2, new class_4122(class_4140.field_18439, f, 3, 100)),
			Pair.of(3, new class_4128(villagerProfession.method_19198(), class_4140.field_18439)),
			Pair.of(99, new class_4127())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends class_4097<? super VillagerEntity>>> method_19022(VillagerProfession villagerProfession, float f) {
		return ImmutableList.of(
			Pair.of(2, new class_4122(class_4140.field_18438, f, 2, 150)),
			Pair.of(3, new class_4128(class_4158.field_18517, class_4140.field_18438)),
			Pair.of(3, new class_4123()),
			Pair.of(99, new class_4127())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends class_4097<? super VillagerEntity>>> method_19023(VillagerProfession villagerProfession, float f) {
		return ImmutableList.of(
			Pair.of(2, new class_4118<>(ImmutableList.of(Pair.of(new class_4116(class_4140.field_18440, 40), 2), Pair.of(new class_4124(), 2)))),
			Pair.of(10, new class_4130(400, 1600)),
			Pair.of(10, new class_4109(EntityType.PLAYER, 4)),
			Pair.of(2, new class_4122(class_4140.field_18440, f, 6, 100)),
			Pair.of(3, new class_4128(class_4158.field_18518, class_4140.field_18440)),
			Pair.of(
				3,
				new class_4103<>(
					ImmutableSet.of(),
					ImmutableSet.of(class_4140.field_18447),
					class_4103.class_4104.field_18348,
					class_4103.class_4105.field_18352,
					ImmutableList.of(Pair.of(new class_4126(), 1))
				)
			),
			Pair.of(99, new class_4127())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends class_4097<? super VillagerEntity>>> method_19024(VillagerProfession villagerProfession, float f) {
		return ImmutableList.of(
			Pair.of(
				2,
				new class_4118<>(
					ImmutableList.of(
						Pair.of(class_4106.method_18941(EntityType.VILLAGER, 8, class_4140.field_18447, f, 2), 2),
						Pair.of(new class_4106<>(EntityType.VILLAGER, 8, VillagerEntity::method_19184, VillagerEntity::method_19184, class_4140.field_18448, f, 2), 1),
						Pair.of(class_4106.method_18941(EntityType.CAT, 8, class_4140.field_18447, f, 2), 1),
						Pair.of(new class_4117(f), 1),
						Pair.of(new class_4120(f, 2.0F), 1),
						Pair.of(new class_4101(30, 60), 1)
					)
				)
			),
			Pair.of(3, new class_4109(EntityType.PLAYER, 4)),
			Pair.of(3, new class_4130(400, 1600)),
			Pair.of(3, new class_4111()),
			Pair.of(
				3,
				new class_4103<>(
					ImmutableSet.of(),
					ImmutableSet.of(class_4140.field_18447),
					class_4103.class_4104.field_18348,
					class_4103.class_4105.field_18352,
					ImmutableList.of(Pair.of(new class_4126(), 1))
				)
			),
			Pair.of(
				3,
				new class_4103<>(
					ImmutableSet.of(),
					ImmutableSet.of(class_4140.field_18448),
					class_4103.class_4104.field_18348,
					class_4103.class_4105.field_18352,
					ImmutableList.of(Pair.of(new class_4111(), 1))
				)
			),
			Pair.of(99, new class_4127())
		);
	}

	public static ImmutableList<Pair<Integer, ? extends class_4097<? super VillagerEntity>>> method_19025(VillagerProfession villagerProfession, float f) {
		float g = f * 1.5F;
		return ImmutableList.of(
			Pair.of(0, new class_4100()),
			Pair.of(1, new class_4121(class_4140.field_18453, g)),
			Pair.of(1, new class_4121(class_4140.field_18452, g)),
			Pair.of(3, new class_4117(g))
		);
	}
}
