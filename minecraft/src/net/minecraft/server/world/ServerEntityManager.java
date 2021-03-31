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
import net.minecraft.entity.Entity;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.profiler.CsvWriter;
import net.minecraft.world.entity.EntityChangeListener;
import net.minecraft.world.entity.EntityHandler;
import net.minecraft.world.entity.EntityIndex;
import net.minecraft.world.entity.EntityLike;
import net.minecraft.world.entity.EntityLookup;
import net.minecraft.world.entity.EntityTrackingSection;
import net.minecraft.world.entity.EntityTrackingStatus;
import net.minecraft.world.entity.SectionedEntityCache;
import net.minecraft.world.entity.SimpleEntityLookup;
import net.minecraft.world.storage.ChunkDataAccess;
import net.minecraft.world.storage.ChunkDataList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * An entity manager for a server environment.
 */
public class ServerEntityManager<T extends EntityLike> implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Set<UUID> entityUuids = Sets.<UUID>newHashSet();
	private final EntityHandler<T> handler;
	private final ChunkDataAccess<T> dataAccess;
	private final EntityIndex<T> index;
	private final SectionedEntityCache<T> cache;
	private final EntityLookup<T> lookup;
	private final Long2ObjectMap<EntityTrackingStatus> trackingStatuses = new Long2ObjectOpenHashMap<>();
	private final Long2ObjectMap<ServerEntityManager.Status> managedStatuses = new Long2ObjectOpenHashMap<>();
	private final LongSet pendingUnloads = new LongOpenHashSet();
	private final Queue<ChunkDataList<T>> loadingQueue = Queues.<ChunkDataList<T>>newConcurrentLinkedQueue();

	public ServerEntityManager(Class<T> entityClass, EntityHandler<T> handler, ChunkDataAccess<T> dataAccess) {
		this.index = new EntityIndex<>();
		this.cache = new SectionedEntityCache<>(entityClass, this.trackingStatuses);
		this.trackingStatuses.defaultReturnValue(EntityTrackingStatus.HIDDEN);
		this.managedStatuses.defaultReturnValue(ServerEntityManager.Status.FRESH);
		this.handler = handler;
		this.dataAccess = dataAccess;
		this.lookup = new SimpleEntityLookup<>(this.index, this.cache);
	}

	private void entityLeftSection(long sectionPos, EntityTrackingSection<T> section) {
		if (section.isEmpty()) {
			this.cache.removeSection(sectionPos);
		}
	}

	private boolean addEntityUuid(T entity) {
		if (!this.entityUuids.add(entity.getUuid())) {
			LOGGER.warn("UUID of added entity already exists: {}", entity);
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Adds a newly created entity to this manager.
	 * 
	 * @return if the entity was added
	 * 
	 * @param entity the newly created entity
	 */
	public boolean addEntity(T entity) {
		return this.addEntity(entity, false);
	}

	/**
	 * Loads or adds an entity to this manager.
	 * 
	 * @return if the entity was loaded or added
	 * 
	 * @param entity the entity
	 * @param existing whether this entity is loaded from the map than created anew
	 */
	private boolean addEntity(T entity, boolean existing) {
		if (!this.addEntityUuid(entity)) {
			return false;
		} else {
			long l = ChunkSectionPos.toLong(entity.getBlockPos());
			EntityTrackingSection<T> entityTrackingSection = this.cache.getTrackingSection(l);
			entityTrackingSection.add(entity);
			entity.setListener(new ServerEntityManager.Listener(entity, l, entityTrackingSection));
			if (!existing) {
				this.handler.create(entity);
			}

			EntityTrackingStatus entityTrackingStatus = getNeededLoadStatus(entity, entityTrackingSection.getStatus());
			if (entityTrackingStatus.shouldTrack()) {
				this.startTracking(entity);
			}

			if (entityTrackingStatus.shouldTick()) {
				this.startTicking(entity);
			}

			return true;
		}
	}

	private static <T extends EntityLike> EntityTrackingStatus getNeededLoadStatus(T entity, EntityTrackingStatus current) {
		return entity.isPlayer() ? EntityTrackingStatus.TICKING : current;
	}

	/**
	 * Loads a few entities from disk to this manager.
	 */
	public void loadEntities(Stream<T> entities) {
		entities.forEach(entity -> this.addEntity((T)entity, true));
	}

	/**
	 * Adds a few newly created entities to this manager.
	 */
	public void addEntities(Stream<T> entities) {
		entities.forEach(entity -> this.addEntity((T)entity, false));
	}

	private void startTicking(T entity) {
		this.handler.startTicking(entity);
	}

	private void stopTicking(T entity) {
		this.handler.stopTicking(entity);
	}

	private void startTracking(T entity) {
		this.index.add(entity);
		this.handler.startTracking(entity);
	}

	private void stopTracking(T entity) {
		this.handler.stopTracking(entity);
		this.index.remove(entity);
	}

	/**
	 * Updates the tracking status of tracking sections in a chunk at {@code
	 * chunkPos} given the {@code levelType}.
	 * 
	 * @see updateTrackingStatus(ChunkPos, EntityTrackingStatus)
	 * 
	 * @param chunkPos the chunk to update
	 * @param levelType the updated level type of the chunk
	 */
	public void updateTrackingStatus(ChunkPos chunkPos, ChunkHolder.LevelType levelType) {
		EntityTrackingStatus entityTrackingStatus = EntityTrackingStatus.fromLevelType(levelType);
		this.updateTrackingStatus(chunkPos, entityTrackingStatus);
	}

	/**
	 * Updates the {@code trackingStatus} of tracking sections in a chunk
	 * at {@code chunkPos}.
	 * 
	 * @param chunkPos the chunk to update
	 * @param trackingStatus the updated section tracking status
	 */
	public void updateTrackingStatus(ChunkPos chunkPos, EntityTrackingStatus trackingStatus) {
		long l = chunkPos.toLong();
		if (trackingStatus == EntityTrackingStatus.HIDDEN) {
			this.trackingStatuses.remove(l);
			this.pendingUnloads.add(l);
		} else {
			this.trackingStatuses.put(l, trackingStatus);
			this.pendingUnloads.remove(l);
			this.readIfFresh(l);
		}

		this.cache.getTrackingSections(l).forEach(group -> {
			EntityTrackingStatus entityTrackingStatus2 = group.swapStatus(trackingStatus);
			boolean bl = entityTrackingStatus2.shouldTrack();
			boolean bl2 = trackingStatus.shouldTrack();
			boolean bl3 = entityTrackingStatus2.shouldTick();
			boolean bl4 = trackingStatus.shouldTick();
			if (bl3 && !bl4) {
				group.stream().filter(entityLike -> !entityLike.isPlayer()).forEach(this::stopTicking);
			}

			if (bl && !bl2) {
				group.stream().filter(entityLike -> !entityLike.isPlayer()).forEach(this::stopTracking);
			} else if (!bl && bl2) {
				group.stream().filter(entityLike -> !entityLike.isPlayer()).forEach(this::startTracking);
			}

			if (!bl3 && bl4) {
				group.stream().filter(entityLike -> !entityLike.isPlayer()).forEach(this::startTicking);
			}
		});
	}

	private void readIfFresh(long chunkPos) {
		ServerEntityManager.Status status = this.managedStatuses.get(chunkPos);
		if (status == ServerEntityManager.Status.FRESH) {
			this.scheduleRead(chunkPos);
		}
	}

	/**
	 * Tries to save entities in a chunk and performs an {@code action} on each
	 * saved entity if successful.
	 * 
	 * <p>If a chunk is {@link Status#FRESH} or {@link Status#PENDING}, it
	 * cannot be saved.
	 * 
	 * @return whether the saving is successful
	 * 
	 * @param action action performed on each saved entity if saving is successful
	 */
	private boolean trySave(long chunkPos, Consumer<T> action) {
		ServerEntityManager.Status status = this.managedStatuses.get(chunkPos);
		if (status == ServerEntityManager.Status.PENDING) {
			return false;
		} else {
			List<T> list = (List<T>)this.cache
				.getTrackingSections(chunkPos)
				.flatMap(entityTrackingSection -> entityTrackingSection.stream().filter(EntityLike::shouldSave))
				.collect(Collectors.toList());
			if (list.isEmpty()) {
				if (status == ServerEntityManager.Status.LOADED) {
					this.dataAccess.writeChunkData(new ChunkDataList<>(new ChunkPos(chunkPos), ImmutableList.of()));
				}

				return true;
			} else if (status == ServerEntityManager.Status.FRESH) {
				this.scheduleRead(chunkPos);
				return false;
			} else {
				this.dataAccess.writeChunkData(new ChunkDataList<>(new ChunkPos(chunkPos), list));
				list.forEach(action);
				return true;
			}
		}
	}

	private void scheduleRead(long chunkPos) {
		this.managedStatuses.put(chunkPos, ServerEntityManager.Status.PENDING);
		ChunkPos chunkPos2 = new ChunkPos(chunkPos);
		this.dataAccess.readChunkData(chunkPos2).thenAccept(this.loadingQueue::add).exceptionally(throwable -> {
			LOGGER.error("Failed to read chunk {}", chunkPos2, throwable);
			return null;
		});
	}

	private boolean unload(long chunkPos) {
		boolean bl = this.trySave(chunkPos, entityLike -> entityLike.streamPassengersAndSelf().forEach(this::unload));
		if (!bl) {
			return false;
		} else {
			this.managedStatuses.remove(chunkPos);
			return true;
		}
	}

	private void unload(EntityLike entity) {
		entity.setRemoved(Entity.RemovalReason.UNLOADED_TO_CHUNK);
		entity.setListener(EntityChangeListener.NONE);
	}

	private void unloadChunks() {
		this.pendingUnloads.removeIf(pos -> this.trackingStatuses.get(pos) != EntityTrackingStatus.HIDDEN ? true : this.unload(pos));
	}

	private void loadChunks() {
		ChunkDataList<T> chunkDataList;
		while ((chunkDataList = (ChunkDataList<T>)this.loadingQueue.poll()) != null) {
			chunkDataList.stream().forEach(entity -> this.addEntity((T)entity, true));
			this.managedStatuses.put(chunkDataList.getChunkPos().toLong(), ServerEntityManager.Status.LOADED);
		}
	}

	public void tick() {
		this.loadChunks();
		this.unloadChunks();
	}

	private LongSet getLoadedChunks() {
		LongSet longSet = this.cache.getChunkPositions();

		for (Entry<ServerEntityManager.Status> entry : Long2ObjectMaps.fastIterable(this.managedStatuses)) {
			if (entry.getValue() == ServerEntityManager.Status.LOADED) {
				longSet.add(entry.getLongKey());
			}
		}

		return longSet;
	}

	public void save() {
		this.getLoadedChunks().forEach(pos -> {
			boolean bl = this.trackingStatuses.get(pos) == EntityTrackingStatus.HIDDEN;
			if (bl) {
				this.unload(pos);
			} else {
				this.trySave(pos, entityLike -> {
				});
			}
		});
	}

	public void flush() {
		LongSet longSet = this.getLoadedChunks();

		while (!longSet.isEmpty()) {
			this.dataAccess.awaitAll();
			this.loadChunks();
			longSet.removeIf(pos -> {
				boolean bl = this.trackingStatuses.get(pos) == EntityTrackingStatus.HIDDEN;
				return bl ? this.unload(pos) : this.trySave(pos, entityLike -> {
				});
			});
		}
	}

	public void close() throws IOException {
		this.flush();
		this.dataAccess.close();
	}

	public boolean has(UUID uuid) {
		return this.entityUuids.contains(uuid);
	}

	public EntityLookup<T> getLookup() {
		return this.lookup;
	}

	public void dump(Writer writer) throws IOException {
		CsvWriter csvWriter = CsvWriter.makeHeader()
			.addColumn("x")
			.addColumn("y")
			.addColumn("z")
			.addColumn("visibility")
			.addColumn("load_status")
			.addColumn("entity_count")
			.startBody(writer);
		this.cache
			.getChunkPositions()
			.forEach(
				chunkPos -> {
					ServerEntityManager.Status status = this.managedStatuses.get(chunkPos);
					this.cache
						.getSections(chunkPos)
						.forEach(
							sectionPos -> {
								EntityTrackingSection<T> entityTrackingSection = this.cache.findTrackingSection(sectionPos);
								if (entityTrackingSection != null) {
									try {
										csvWriter.printRow(
											ChunkSectionPos.unpackX(sectionPos),
											ChunkSectionPos.unpackY(sectionPos),
											ChunkSectionPos.unpackZ(sectionPos),
											entityTrackingSection.getStatus(),
											status,
											entityTrackingSection.size()
										);
									} catch (IOException var7) {
										throw new UncheckedIOException(var7);
									}
								}
							}
						);
				}
			);
	}

	@Debug
	public String getDebugString() {
		return this.entityUuids.size()
			+ ","
			+ this.index.size()
			+ ","
			+ this.cache.sectionCount()
			+ ","
			+ this.managedStatuses.size()
			+ ","
			+ this.trackingStatuses.size()
			+ ","
			+ this.loadingQueue.size()
			+ ","
			+ this.pendingUnloads.size();
	}

	class Listener implements EntityChangeListener {
		private final T entity;
		private long sectionPos;
		private EntityTrackingSection<T> section;

		private Listener(T entity, long sectionPos, EntityTrackingSection<T> section) {
			this.entity = entity;
			this.sectionPos = sectionPos;
			this.section = section;
		}

		@Override
		public void updateEntityPosition() {
			BlockPos blockPos = this.entity.getBlockPos();
			long l = ChunkSectionPos.toLong(blockPos);
			if (l != this.sectionPos) {
				EntityTrackingStatus entityTrackingStatus = this.section.getStatus();
				if (!this.section.remove(this.entity)) {
					ServerEntityManager.LOGGER.warn("Entity {} wasn't found in section {} (moving to {})", this.entity, ChunkSectionPos.from(this.sectionPos), l);
				}

				ServerEntityManager.this.entityLeftSection(this.sectionPos, this.section);
				EntityTrackingSection<T> entityTrackingSection = ServerEntityManager.this.cache.getTrackingSection(l);
				entityTrackingSection.add(this.entity);
				this.section = entityTrackingSection;
				this.sectionPos = l;
				this.updateLoadStatus(entityTrackingStatus, entityTrackingSection.getStatus());
			}
		}

		private void updateLoadStatus(EntityTrackingStatus oldStatus, EntityTrackingStatus newStatus) {
			EntityTrackingStatus entityTrackingStatus = ServerEntityManager.getNeededLoadStatus(this.entity, oldStatus);
			EntityTrackingStatus entityTrackingStatus2 = ServerEntityManager.getNeededLoadStatus(this.entity, newStatus);
			if (entityTrackingStatus != entityTrackingStatus2) {
				boolean bl = entityTrackingStatus.shouldTrack();
				boolean bl2 = entityTrackingStatus2.shouldTrack();
				if (bl && !bl2) {
					ServerEntityManager.this.stopTracking(this.entity);
				} else if (!bl && bl2) {
					ServerEntityManager.this.startTracking(this.entity);
				}

				boolean bl3 = entityTrackingStatus.shouldTick();
				boolean bl4 = entityTrackingStatus2.shouldTick();
				if (bl3 && !bl4) {
					ServerEntityManager.this.stopTicking(this.entity);
				} else if (!bl3 && bl4) {
					ServerEntityManager.this.startTicking(this.entity);
				}
			}
		}

		@Override
		public void remove(Entity.RemovalReason reason) {
			if (!this.section.remove(this.entity)) {
				ServerEntityManager.LOGGER.warn("Entity {} wasn't found in section {} (destroying due to {})", this.entity, ChunkSectionPos.from(this.sectionPos), reason);
			}

			EntityTrackingStatus entityTrackingStatus = ServerEntityManager.getNeededLoadStatus(this.entity, this.section.getStatus());
			if (entityTrackingStatus.shouldTick()) {
				ServerEntityManager.this.stopTicking(this.entity);
			}

			if (entityTrackingStatus.shouldTrack()) {
				ServerEntityManager.this.stopTracking(this.entity);
			}

			if (reason.shouldDestroy()) {
				ServerEntityManager.this.handler.destroy(this.entity);
			}

			ServerEntityManager.this.entityUuids.remove(this.entity.getUuid());
			this.entity.setListener(NONE);
			ServerEntityManager.this.entityLeftSection(this.sectionPos, this.section);
		}
	}

	/**
	 * The status of chunks within a server entity manager.
	 */
	static enum Status {
		FRESH,
		PENDING,
		LOADED;
	}
}
