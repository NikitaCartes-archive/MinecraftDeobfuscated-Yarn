package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class class_4122 extends class_4097<LivingEntity> {
	private final class_4140<class_4208> field_18382;
	private final float field_18383;
	private final int field_18384;
	private final int field_18385;

	public class_4122(class_4140<class_4208> arg, float f, int i, int j) {
		this.field_18382 = arg;
		this.field_18383 = f;
		this.field_18384 = i;
		this.field_18385 = j;
	}

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(Pair.of(class_4140.field_18445, class_4141.field_18457), Pair.of(this.field_18382, class_4141.field_18456));
	}

	@Override
	protected void method_18920(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		MinecraftServer minecraftServer = serverWorld.getServer();
		class_4095<?> lv = livingEntity.method_18868();
		lv.method_18904(this.field_18382).ifPresent(arg2 -> {
			boolean bl;
			if (!Objects.equals(arg2.method_19442(), serverWorld.method_8597().method_12460())) {
				bl = true;
			} else {
				int i = arg2.method_19446().method_19455(new BlockPos(livingEntity));
				bl = i >= this.field_18385;
			}

			if (bl) {
				minecraftServer.method_3847(arg2.method_19442()).method_19494().method_19129(arg2.method_19446());
				lv.method_18875(this.field_18382);
			} else {
				lv.method_18878(class_4140.field_18445, new class_4142(arg2.method_19446(), this.field_18383, this.field_18384));
			}
		});
	}
}
