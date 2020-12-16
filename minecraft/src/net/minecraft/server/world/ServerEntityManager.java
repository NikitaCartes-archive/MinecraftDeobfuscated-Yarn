package net.minecraft.server.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.class_5566;
import net.minecraft.class_5569;
import net.minecraft.class_5570;
import net.minecraft.class_5571;
import net.minecraft.class_5572;
import net.minecraft.class_5573;
import net.minecraft.class_5577;
import net.minecraft.class_5578;
import net.minecraft.class_5584;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLike;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.profiler.CsvWriter;
import net.minecraft.world.EntityLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerEntityManager<T extends EntityLike> implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Set<UUID> entityUuids = Sets.<UUID>newHashSet();
	private final EntityLoader<T> entityLoader;
	private final class_5571<T> field_27263;
	private final class_5570<T> trackedEntities;
	private final class_5573<T> field_27265;
	private final class_5577<T> field_27266;
	private final Long2ObjectMap<class_5584> field_27267 = new Long2ObjectOpenHashMap<>();
	private final Long2ObjectMap<ServerEntityManager.class_5581> field_27268 = new Long2ObjectOpenHashMap<>();
	private final LongSet field_27269 = new LongOpenHashSet();
	private final Queue<class_5566<T>> loadingQueue = Queues.<class_5566<T>>newConcurrentLinkedQueue();

	public ServerEntityManager(Class<T> class_, EntityLoader<T> entityLoader, class_5571<T> arg) {
		this.trackedEntities = new class_5570<>();
		this.field_27265 = new class_5573<>(class_, this.field_27267);
		this.field_27267.defaultReturnValue(class_5584.HIDDEN);
		this.field_27268.defaultReturnValue(ServerEntityManager.class_5581.field_27275);
		this.entityLoader = entityLoader;
		this.field_27263 = arg;
		this.field_27266 = new class_5578<>(this.trackedEntities, this.field_27265);
	}

	private void method_31811(long l, class_5572<T> arg) {
		if (arg.method_31761()) {
			this.field_27265.method_31786(l);
		}
	}

	private boolean canAddEntity(T entity) {
		if (!this.entityUuids.add(entity.getUuid())) {
			LOGGER.warn("UUID of added entity already exists: {}", entity);
			return false;
		} else {
			return true;
		}
	}

	public boolean addEntity(T entity) {
		return this.addEntity(entity, false);
	}

	private boolean addEntity(T entity, boolean bl) {
		if (!this.canAddEntity(entity)) {
			return false;
		} else {
			long l = class_5573.method_31779(entity.getBlockPos());
			class_5572<T> lv = this.field_27265.method_31784(l);
			lv.method_31764(entity);
			entity.method_31744(new ServerEntityManager.class_5580(entity, l, lv));
			if (!bl) {
				this.entityLoader.method_31802(entity);
			}

			class_5584 lv2 = method_31832(entity, lv.method_31768());
			if (lv2.shouldTrack()) {
				this.method_31847(entity);
			}

			if (lv2.shouldTick()) {
				this.method_31838(entity);
			}

			return true;
		}
	}

	private static <T extends EntityLike> class_5584 method_31832(T entityLike, class_5584 arg) {
		return entityLike.isPlayer() ? class_5584.TICKING : arg;
	}

	public void method_31828(Stream<T> stream) {
		stream.forEach(entity -> this.addEntity((T)entity, true));
	}

	public void method_31835(Stream<T> stream) {
		stream.forEach(entity -> this.addEntity((T)entity, false));
	}

	private void method_31838(T entity) {
		this.entityLoader.addEntity(entity);
	}

	private void removeEntity(T entity) {
		this.entityLoader.removeEntity(entity);
	}

	private void method_31847(T entity) {
		this.trackedEntities.addEntity(entity);
		this.entityLoader.onLoadEntity(entity);
	}

	private void method_31850(T entity) {
		this.entityLoader.onUnloadEntity(entity);
		this.trackedEntities.removeEntity(entity);
	}

	public void method_31815(ChunkPos chunkPos, ChunkHolder.LevelType levelType) {
		class_5584 lv = class_5584.method_31884(levelType);
		this.method_31816(chunkPos, lv);
	}

	public void method_31816(ChunkPos chunkPos, class_5584 arg) {
		long l = chunkPos.toLong();
		if (arg == class_5584.HIDDEN) {
			this.field_27267.remove(l);
			this.field_27269.add(l);
		} else {
			this.field_27267.put(l, arg);
			this.field_27269.remove(l);
			this.method_31810(l);
		}

		this.field_27265.method_31782(l).forEach(arg2 -> {
			class_5584 lv = arg2.method_31763(arg);
			boolean bl = lv.shouldTrack();
			boolean bl2 = arg.shouldTrack();
			boolean bl3 = lv.shouldTick();
			boolean bl4 = arg.shouldTick();
			if (bl3 && !bl4) {
				arg2.method_31766().filter(entityLike -> !entityLike.isPlayer()).forEach(this::removeEntity);
			}

			if (bl && !bl2) {
				arg2.method_31766().filter(entityLike -> !entityLike.isPlayer()).forEach(this::method_31850);
			} else if (!bl && bl2) {
				arg2.method_31766().filter(entityLike -> !entityLike.isPlayer()).forEach(this::method_31847);
			}

			if (!bl3 && bl4) {
				arg2.method_31766().filter(entityLike -> !entityLike.isPlayer()).forEach(this::method_31838);
			}
		});
	}

	private void method_31810(long l) {
		ServerEntityManager.class_5581 lv = this.field_27268.get(l);
		if (lv == ServerEntityManager.class_5581.field_27275) {
			this.method_31830(l);
		}
	}

	private boolean method_31812(long l, Consumer<T> consumer) {
		ServerEntityManager.class_5581 lv = this.field_27268.get(l);
		if (lv == ServerEntityManager.class_5581.field_27276) {
			return false;
		} else {
			List<T> list = (List<T>)this.field_27265.method_31782(l).flatMap(arg -> arg.method_31766().filter(EntityLike::shouldSave)).collect(Collectors.toList());
			if (list.isEmpty()) {
				if (lv == ServerEntityManager.class_5581.field_27277) {
					this.field_27263.method_31760(new class_5566<>(new ChunkPos(l), ImmutableList.of()));
				}

				return true;
			} else if (lv == ServerEntityManager.class_5581.field_27275) {
				this.method_31830(l);
				return false;
			} else {
				this.field_27263.method_31760(new class_5566<>(new ChunkPos(l), list));
				list.forEach(consumer);
				return true;
			}
		}
	}

	private void method_31830(long l) {
		this.field_27268.put(l, ServerEntityManager.class_5581.field_27276);
		ChunkPos chunkPos = new ChunkPos(l);
		this.field_27263.method_31759(chunkPos).thenAccept(this.loadingQueue::add).exceptionally(throwable -> {
			LOGGER.error("Failed to read chunk {}", chunkPos, throwable);
			return null;
		});
	}

	private boolean method_31837(long l) {
		boolean bl = this.method_31812(l, entityLike -> entityLike.streamPassengers().forEach(this::method_31852));
		if (!bl) {
			return false;
		} else {
			this.field_27268.remove(l);
			return true;
		}
	}

	private void method_31852(EntityLike entity) {
		entity.setRemoved(Entity.RemovalReason.UNLOADED_TO_CHUNK);
		entity.method_31744(class_5569.field_27243);
	}

	private void method_31851() {
		this.field_27269.removeIf(l -> this.field_27267.get(l) != class_5584.HIDDEN ? true : this.method_31837(l));
	}

	private void method_31853() {
		class_5566<T> lv;
		while ((lv = (class_5566<T>)this.loadingQueue.poll()) != null) {
			lv.method_31742().forEach(entityLike -> this.addEntity((T)entityLike, true));
			this.field_27268.put(lv.method_31741().toLong(), ServerEntityManager.class_5581.field_27277);
		}
	}

	public void method_31809() {
		this.method_31853();
		this.method_31851();
	}

	private LongSet method_31855() {
		LongSet longSet = this.field_27265.method_31770();

		for (Entry<ServerEntityManager.class_5581> entry : Long2ObjectMaps.fastIterable(this.field_27268)) {
			if (entry.getValue() == ServerEntityManager.class_5581.field_27277) {
				longSet.add(entry.getLongKey());
			}
		}

		return longSet;
	}

	public void method_31829() {
		this.method_31855().forEach(l -> {
			boolean bl = this.field_27267.get(l) == class_5584.HIDDEN;
			if (bl) {
				this.method_31837(l);
			} else {
				this.method_31812(l, entityLike -> {
				});
			}
		});
	}

	public void method_31836() {
		LongSet longSet = this.method_31855();

		while (!longSet.isEmpty()) {
			this.field_27263.method_31758();
			this.method_31853();
			longSet.removeIf(l -> {
				boolean bl = this.field_27267.get(l) == class_5584.HIDDEN;
				return bl ? this.method_31837(l) : this.method_31812(l, entityLike -> {
				});
			});
		}
	}

	public void close() throws IOException {
		this.method_31836();
		this.field_27263.close();
	}

	public boolean method_31827(UUID uUID) {
		return this.entityUuids.contains(uUID);
	}

	public class_5577<T> method_31841() {
		return this.field_27266;
	}

	public void method_31826(Writer writer) throws IOException {
		CsvWriter csvWriter = CsvWriter.makeHeader()
			.addColumn("x")
			.addColumn("y")
			.addColumn("z")
			.addColumn("visibility")
			.addColumn("load_status")
			.addColumn("entity_count")
			.startBody(writer);
		this.field_27265.method_31770().forEach(l -> {
			ServerEntityManager.class_5581 lv = this.field_27268.get(l);
			this.field_27265.method_31772(l).forEach(lx -> {
				class_5572<T> lvx = this.field_27265.method_31785(lx);
				if (lvx != null) {
					try {
						csvWriter.printRow(ChunkSectionPos.unpackX(lx), ChunkSectionPos.unpackY(lx), ChunkSectionPos.unpackZ(lx), lvx.method_31768(), lv, lvx.method_31769());
					} catch (IOException var7) {
						throw new UncheckedIOException(var7);
					}
				}
			});
		});
	}

	public String getDebugString() {
		return this.entityUuids.size()
			+ ","
			+ this.trackedEntities.getEntityCount()
			+ ","
			+ this.field_27265.method_31781()
			+ ","
			+ this.field_27268.size()
			+ ","
			+ this.field_27267.size()
			+ ","
			+ this.loadingQueue.size()
			+ ","
			+ this.field_27269.size();
	}

	class class_5580 implements class_5569 {
		private final T entity;
		private long field_27273;
		private class_5572<T> field_27274;

		private class_5580(T entity, long l, class_5572<T> arg) {
			this.entity = entity;
			this.field_27273 = l;
			this.field_27274 = arg;
		}

		@Override
		public void updateEntityPosition() {
			BlockPos blockPos = this.entity.getBlockPos();
			long l = class_5573.method_31779(blockPos);
			if (l != this.field_27273) {
				class_5584 lv = this.field_27274.method_31768();
				if (!this.field_27274.method_31767(this.entity)) {
					ServerEntityManager.LOGGER.warn("Entity {} wasn't found in section {} (moving to {})", this.entity, ChunkSectionPos.from(this.field_27273), l);
				}

				ServerEntityManager.this.method_31811(this.field_27273, this.field_27274);
				class_5572<T> lv2 = ServerEntityManager.this.field_27265.method_31784(l);
				lv2.method_31764(this.entity);
				this.field_27274 = lv2;
				this.field_27273 = l;
				this.method_31865(lv, lv2.method_31768());
			}
		}

		private void method_31865(class_5584 arg, class_5584 arg2) {
			class_5584 lv = ServerEntityManager.method_31832(this.entity, arg);
			class_5584 lv2 = ServerEntityManager.method_31832(this.entity, arg2);
			if (lv != lv2) {
				boolean bl = lv.shouldTrack();
				boolean bl2 = lv2.shouldTrack();
				if (bl && !bl2) {
					ServerEntityManager.this.method_31850(this.entity);
				} else if (!bl && bl2) {
					ServerEntityManager.this.method_31847(this.entity);
				}

				boolean bl3 = lv.shouldTick();
				boolean bl4 = lv2.shouldTick();
				if (bl3 && !bl4) {
					ServerEntityManager.this.removeEntity(this.entity);
				} else if (!bl3 && bl4) {
					ServerEntityManager.this.method_31838(this.entity);
				}
			}
		}

		@Override
		public void remove(Entity.RemovalReason reason) {
			if (!this.field_27274.method_31767(this.entity)) {
				ServerEntityManager.LOGGER.warn("Entity {} wasn't found in section {} (destroying due to {})", this.entity, ChunkSectionPos.from(this.field_27273), reason);
			}

			class_5584 lv = ServerEntityManager.method_31832(this.entity, this.field_27274.method_31768());
			if (lv.shouldTick()) {
				ServerEntityManager.this.removeEntity(this.entity);
			}

			if (lv.shouldTrack()) {
				ServerEntityManager.this.method_31850(this.entity);
			}

			if (reason.shouldDestroy()) {
				ServerEntityManager.this.entityLoader.destroyEntity(this.entity);
			}

			ServerEntityManager.this.entityUuids.remove(this.entity.getUuid());
			this.entity.method_31744(field_27243);
			ServerEntityManager.this.method_31811(this.field_27273, this.field_27274);
		}
	}

	static enum class_5581 {
		field_27275,
		field_27276,
		field_27277;
	}
}
