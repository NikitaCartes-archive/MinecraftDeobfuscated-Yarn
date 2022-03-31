package net.minecraft.entity.ai;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

public class WardenAngerManager {
	private static final int maxAnger = 150;
	private static final int angerDecreasePerTick = 1;
	public static final Codec<WardenAngerManager> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(Codec.unboundedMap(Codecs.UUID, Codecs.NONNEGATIVE_INT).fieldOf("suspects").forGetter(angerManager -> angerManager.suspects))
				.apply(instance, WardenAngerManager::new)
	);
	private final Object2IntMap<UUID> suspects;

	public WardenAngerManager(Map<UUID, Integer> suspects) {
		this.suspects = new Object2IntOpenHashMap<>(suspects);
	}

	public void tick() {
		ObjectIterator<Entry<UUID>> objectIterator = this.suspects.object2IntEntrySet().iterator();

		while (objectIterator.hasNext()) {
			Entry<UUID> entry = (Entry<UUID>)objectIterator.next();
			int i = entry.getIntValue();
			if (i <= 1) {
				objectIterator.remove();
			} else {
				entry.setValue(Math.max(0, i - 1));
			}
		}
	}

	public int increaseAngerAt(Entity entity, int amount) {
		return this.suspects.computeInt(entity.getUuid(), (uuid, anger) -> Math.min(150, (anger == null ? 0 : anger) + amount));
	}

	public void removeSuspect(Entity entity) {
		this.suspects.removeInt(entity.getUuid());
	}

	private Optional<Entry<UUID>> getPrimeSuspect() {
		return this.suspects.object2IntEntrySet().stream().max(java.util.Map.Entry.comparingByValue());
	}

	public int getPrimeSuspectAnger() {
		return (Integer)this.getPrimeSuspect().map(java.util.Map.Entry::getValue).orElse(0);
	}

	public Optional<LivingEntity> getPrimeSuspect(World world) {
		return world instanceof ServerWorld serverWorld
			? this.getPrimeSuspect()
				.map(java.util.Map.Entry::getKey)
				.map(serverWorld::getEntity)
				.filter(suspect -> suspect instanceof LivingEntity)
				.map(suspect -> (LivingEntity)suspect)
			: Optional.empty();
	}
}
