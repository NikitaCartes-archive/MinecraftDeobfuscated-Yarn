package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;

public class class_4805<E extends MobEntityWithAi> extends RandomTask<E> {
	public class_4805(float f) {
		super(ImmutableList.of(Pair.of(new class_4818(f, 1, 0), 1), Pair.of(new WaitTask(10, 20), 1)));
	}

	protected boolean shouldRun(ServerWorld serverWorld, E mobEntityWithAi) {
		return !mobEntityWithAi.getOffHandStack().isEmpty();
	}
}
