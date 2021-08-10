package net.minecraft;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.util.HashMap;
import java.util.List;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.entity.EntityLike;

public class class_6480 {
	final Long2ObjectMap<List<EntityLike>> field_34290 = new Long2ObjectOpenHashMap<>();
	final HashMap<EntityLike, class_6480.class_6481> field_34291 = Maps.newHashMap();
	private final ThreadedAnvilChunkStorage field_34292;
	private final ServerWorld field_34293;

	public class_6480(ThreadedAnvilChunkStorage threadedAnvilChunkStorage, ServerWorld serverWorld) {
		this.field_34292 = threadedAnvilChunkStorage;
		this.field_34293 = serverWorld;
	}

	private List<EntityLike> method_37834(long l) {
		return this.field_34290.computeIfAbsent(l, lx -> this.field_34292.method_37832(new ChunkPos(lx)));
	}

	public void method_37835(long l, SpawnGroup spawnGroup) {
		List<EntityLike> list = this.method_37834(l);
		float f = 1.0F / (float)spawnGroup.getCapacity();

		for (EntityLike entityLike : list) {
			class_6480.class_6481 lv = (class_6480.class_6481)this.field_34291.computeIfAbsent(entityLike, entityLikex -> new class_6480.class_6481());
			lv.method_37842(spawnGroup, f);
		}
	}

	public boolean method_37836(SpawnGroup spawnGroup, ChunkPos chunkPos) {
		return this.method_37834(chunkPos.toLong()).stream().anyMatch(entityLike -> this.method_37839(entityLike, spawnGroup));
	}

	private boolean method_37839(EntityLike entityLike, SpawnGroup spawnGroup) {
		class_6480.class_6481 lv = (class_6480.class_6481)this.field_34291.get(entityLike);
		return lv == null || lv.method_37841(spawnGroup);
	}

	class class_6481 {
		private final Object2FloatMap<SpawnGroup> field_34295 = new Object2FloatOpenHashMap<>(SpawnGroup.values().length);

		public void method_37842(SpawnGroup spawnGroup, float f) {
			float g = this.field_34295.getOrDefault(spawnGroup, 0.0F);
			this.field_34295.put(spawnGroup, g + f);
		}

		public boolean method_37841(SpawnGroup spawnGroup) {
			return this.field_34295.getOrDefault(spawnGroup, 0.0F) < 1.0F;
		}
	}
}
