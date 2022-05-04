package net.minecraft.entity.ai;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Streams;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Angriness;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.AbstractRandom;

public class WardenAngerManager {
	@VisibleForTesting
	protected static final int field_38733 = 2;
	@VisibleForTesting
	protected static final int maxAnger = 150;
	private static final int angerDecreasePerTick = 1;
	private int updateTimer = MathHelper.nextBetween(AbstractRandom.createAtomic(), 0, 2);
	private static final Codec<Pair<UUID, Integer>> SUSPECT_CODEC = RecordCodecBuilder.create(
		instance -> instance.group(Codecs.UUID.fieldOf("uuid").forGetter(Pair::getFirst), Codecs.NONNEGATIVE_INT.fieldOf("anger").forGetter(Pair::getSecond))
				.apply(instance, Pair::of)
	);
	private final Predicate<Entity> suspectPredicate;
	@VisibleForTesting
	protected final ArrayList<Entity> suspects;
	private final WardenAngerManager.SuspectComparator suspectComparator;
	@VisibleForTesting
	protected final Object2IntMap<Entity> suspectsToAngerLevel;
	@VisibleForTesting
	protected final Object2IntMap<UUID> suspectUuidsToAngerLevel;

	public static Codec<WardenAngerManager> method_43692(Predicate<Entity> predicate) {
		return RecordCodecBuilder.create(
			instance -> instance.group(SUSPECT_CODEC.listOf().fieldOf("suspects").orElse(Collections.emptyList()).forGetter(WardenAngerManager::getSuspects))
					.apply(instance, list -> new WardenAngerManager(predicate, list))
		);
	}

	public WardenAngerManager(Predicate<Entity> suspectPredicate, List<Pair<UUID, Integer>> suspectUuidsToAngerLevel) {
		this.suspectPredicate = suspectPredicate;
		this.suspects = new ArrayList();
		this.suspectComparator = new WardenAngerManager.SuspectComparator(this);
		this.suspectsToAngerLevel = new Object2IntOpenHashMap<>();
		this.suspectUuidsToAngerLevel = new Object2IntOpenHashMap<>(suspectUuidsToAngerLevel.size());
		suspectUuidsToAngerLevel.forEach(pair -> this.suspectUuidsToAngerLevel.put((UUID)pair.getFirst(), (Integer)pair.getSecond()));
	}

	private List<Pair<UUID, Integer>> getSuspects() {
		return (List<Pair<UUID, Integer>>)Streams.concat(
				this.suspects.stream().map(suspect -> Pair.of(suspect.getUuid(), this.suspectsToAngerLevel.getInt(suspect))),
				this.suspectUuidsToAngerLevel.object2IntEntrySet().stream().map(entry -> Pair.of((UUID)entry.getKey(), entry.getIntValue()))
			)
			.collect(Collectors.toList());
	}

	public void tick(ServerWorld world, Predicate<Entity> suspectPredicate) {
		this.updateTimer--;
		if (this.updateTimer <= 0) {
			this.updateSuspectsMap(world);
			this.updateTimer = 2;
		}

		ObjectIterator<Entry<UUID>> objectIterator = this.suspectUuidsToAngerLevel.object2IntEntrySet().iterator();

		while (objectIterator.hasNext()) {
			Entry<UUID> entry = (Entry<UUID>)objectIterator.next();
			int i = entry.getIntValue();
			if (i <= 1) {
				objectIterator.remove();
			} else {
				entry.setValue(i - 1);
			}
		}

		ObjectIterator<Entry<Entity>> objectIterator2 = this.suspectsToAngerLevel.object2IntEntrySet().iterator();

		while (objectIterator2.hasNext()) {
			Entry<Entity> entry2 = (Entry<Entity>)objectIterator2.next();
			int j = entry2.getIntValue();
			Entity entity = (Entity)entry2.getKey();
			Entity.RemovalReason removalReason = entity.getRemovalReason();
			if (j > 1 && suspectPredicate.test(entity) && removalReason == null) {
				entry2.setValue(j - 1);
			} else {
				this.suspects.remove(entity);
				objectIterator2.remove();
				if (j > 1 && removalReason != null) {
					switch (removalReason) {
						case CHANGED_DIMENSION:
						case UNLOADED_TO_CHUNK:
						case UNLOADED_WITH_PLAYER:
							this.suspectUuidsToAngerLevel.put(entity.getUuid(), j - 1);
					}
				}
			}
		}

		this.suspects.sort(this.suspectComparator);
	}

	private void updateSuspectsMap(ServerWorld world) {
		ObjectIterator<Entry<UUID>> objectIterator = this.suspectUuidsToAngerLevel.object2IntEntrySet().iterator();

		while (objectIterator.hasNext()) {
			Entry<UUID> entry = (Entry<UUID>)objectIterator.next();
			int i = entry.getIntValue();
			Entity entity = world.getEntity((UUID)entry.getKey());
			if (entity != null) {
				this.suspectsToAngerLevel.put(entity, i);
				this.suspects.add(entity);
				objectIterator.remove();
			}
		}

		this.suspects.sort(this.suspectComparator);
	}

	public int increaseAngerAt(Entity entity, int amount) {
		boolean bl = !this.suspectsToAngerLevel.containsKey(entity);
		int i = this.suspectsToAngerLevel.computeInt(entity, (suspect, anger) -> Math.min(150, (anger == null ? 0 : anger) + amount));
		if (bl) {
			int j = this.suspectUuidsToAngerLevel.removeInt(entity.getUuid());
			i += j;
			this.suspectsToAngerLevel.put(entity, i);
			this.suspects.add(entity);
		}

		this.suspects.sort(this.suspectComparator);
		return i;
	}

	public void removeSuspect(Entity entity) {
		this.suspectsToAngerLevel.removeInt(entity);
		this.suspects.remove(entity);
	}

	@Nullable
	private Entity getPrimeSuspect() {
		return (Entity)this.suspects.stream().filter(this.suspectPredicate).findFirst().orElse(null);
	}

	public int getPrimeSuspectAnger() {
		return this.suspectsToAngerLevel.getInt(this.getPrimeSuspect());
	}

	public Optional<LivingEntity> getPrimeSuspect() {
		return Optional.ofNullable(this.getPrimeSuspect()).filter(suspect -> suspect instanceof LivingEntity).map(suspect -> (LivingEntity)suspect);
	}

	@VisibleForTesting
	protected static record SuspectComparator(WardenAngerManager angerManagement) implements Comparator<Entity> {
		public int compare(Entity entity, Entity entity2) {
			if (entity.equals(entity2)) {
				return 0;
			} else {
				int i = this.angerManagement.suspectsToAngerLevel.getOrDefault(entity, 0);
				int j = this.angerManagement.suspectsToAngerLevel.getOrDefault(entity2, 0);
				boolean bl = Angriness.getForAnger(i).isAngry();
				boolean bl2 = Angriness.getForAnger(j).isAngry();
				if (bl != bl2) {
					return bl ? -1 : 1;
				} else {
					if (bl) {
						boolean bl3 = entity instanceof PlayerEntity;
						boolean bl4 = entity2 instanceof PlayerEntity;
						if (bl3 != bl4) {
							return bl3 ? -1 : 1;
						}
					}

					return i > j ? -1 : 1;
				}
			}
		}
	}
}
