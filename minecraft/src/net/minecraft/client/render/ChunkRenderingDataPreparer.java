package net.minecraft.client.render;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.Octree;
import net.minecraft.server.network.ChunkFilter;
import net.minecraft.util.Util;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.HeightLimitView;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ChunkRenderingDataPreparer {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Direction[] DIRECTIONS = Direction.values();
	private static final int field_45619 = 60;
	private static final double field_45620 = Math.ceil(Math.sqrt(3.0) * 16.0);
	private boolean field_45621 = true;
	@Nullable
	private Future<?> field_45622;
	@Nullable
	private BuiltChunkStorage builtChunkStorage;
	private final AtomicReference<ChunkRenderingDataPreparer.class_8681> field_45624 = new AtomicReference();
	private final AtomicReference<ChunkRenderingDataPreparer.class_8680> field_45625 = new AtomicReference();
	private final AtomicBoolean field_45626 = new AtomicBoolean(false);

	public void method_52826(@Nullable BuiltChunkStorage builtChunkStorage) {
		if (this.field_45622 != null) {
			try {
				this.field_45622.get();
				this.field_45622 = null;
			} catch (Exception var3) {
				LOGGER.warn("Full update failed", (Throwable)var3);
			}
		}

		this.builtChunkStorage = builtChunkStorage;
		if (builtChunkStorage != null) {
			this.field_45624.set(new ChunkRenderingDataPreparer.class_8681(builtChunkStorage));
			this.scheduleTerrainUpdate();
		} else {
			this.field_45624.set(null);
		}
	}

	public void scheduleTerrainUpdate() {
		this.field_45621 = true;
	}

	public void method_52828(Frustum frustum, List<ChunkBuilder.BuiltChunk> builtChunks) {
		((ChunkRenderingDataPreparer.class_8681)this.field_45624.get()).storage().field_53929.visit((node, bl, i) -> {
			ChunkBuilder.BuiltChunk builtChunk = node.getBuiltChunk();
			if (builtChunk != null) {
				builtChunks.add(builtChunk);
			}
		}, frustum);
	}

	public boolean method_52836() {
		return this.field_45626.compareAndSet(true, false);
	}

	public void method_52819(ChunkPos chunkPos) {
		ChunkRenderingDataPreparer.class_8680 lv = (ChunkRenderingDataPreparer.class_8680)this.field_45625.get();
		if (lv != null) {
			this.addNeighbors(lv, chunkPos);
		}

		ChunkRenderingDataPreparer.class_8680 lv2 = ((ChunkRenderingDataPreparer.class_8681)this.field_45624.get()).events;
		if (lv2 != lv) {
			this.addNeighbors(lv2, chunkPos);
		}
	}

	public void method_52827(ChunkBuilder.BuiltChunk builtChunk) {
		ChunkRenderingDataPreparer.class_8680 lv = (ChunkRenderingDataPreparer.class_8680)this.field_45625.get();
		if (lv != null) {
			lv.sectionsToPropagateFrom.add(builtChunk);
		}

		ChunkRenderingDataPreparer.class_8680 lv2 = ((ChunkRenderingDataPreparer.class_8681)this.field_45624.get()).events;
		if (lv2 != lv) {
			lv2.sectionsToPropagateFrom.add(builtChunk);
		}
	}

	public void method_52834(boolean bl, Camera camera, Frustum frustum, List<ChunkBuilder.BuiltChunk> builtChunk, LongOpenHashSet longOpenHashSet) {
		Vec3d vec3d = camera.getPos();
		if (this.field_45621 && (this.field_45622 == null || this.field_45622.isDone())) {
			this.method_52833(bl, camera, vec3d, longOpenHashSet);
		}

		this.method_52835(bl, frustum, builtChunk, vec3d, longOpenHashSet);
	}

	private void method_52833(boolean bl, Camera camera, Vec3d vec3d, LongOpenHashSet longOpenHashSet) {
		this.field_45621 = false;
		LongOpenHashSet longOpenHashSet2 = longOpenHashSet.clone();
		this.field_45622 = Util.getMainWorkerExecutor().submit(() -> {
			ChunkRenderingDataPreparer.class_8681 lv = new ChunkRenderingDataPreparer.class_8681(this.builtChunkStorage);
			this.field_45625.set(lv.events);
			Queue<ChunkRenderingDataPreparer.ChunkInfo> queue = Queues.<ChunkRenderingDataPreparer.ChunkInfo>newArrayDeque();
			this.method_52821(camera, queue);
			queue.forEach(chunkInfo -> lv.storage.field_45627.setInfo(chunkInfo.chunk, chunkInfo));
			this.method_52825(lv.storage, vec3d, queue, bl, builtChunk -> {
			}, longOpenHashSet2);
			this.field_45624.set(lv);
			this.field_45625.set(null);
			this.field_45626.set(true);
		});
	}

	private void method_52835(boolean bl, Frustum frustum, List<ChunkBuilder.BuiltChunk> builtChunks, Vec3d vec3d, LongOpenHashSet longOpenHashSet) {
		ChunkRenderingDataPreparer.class_8681 lv = (ChunkRenderingDataPreparer.class_8681)this.field_45624.get();
		this.method_52823(lv);
		if (!lv.events.sectionsToPropagateFrom.isEmpty()) {
			Queue<ChunkRenderingDataPreparer.ChunkInfo> queue = Queues.<ChunkRenderingDataPreparer.ChunkInfo>newArrayDeque();

			while (!lv.events.sectionsToPropagateFrom.isEmpty()) {
				ChunkBuilder.BuiltChunk builtChunk = (ChunkBuilder.BuiltChunk)lv.events.sectionsToPropagateFrom.poll();
				ChunkRenderingDataPreparer.ChunkInfo chunkInfo = lv.storage.field_45627.getInfo(builtChunk);
				if (chunkInfo != null && chunkInfo.chunk == builtChunk) {
					queue.add(chunkInfo);
				}
			}

			Frustum frustum2 = WorldRenderer.method_52816(frustum);
			Consumer<ChunkBuilder.BuiltChunk> consumer = builtChunk -> {
				if (frustum2.isVisible(builtChunk.getBoundingBox())) {
					builtChunks.add(builtChunk);
				}
			};
			this.method_52825(lv.storage, vec3d, queue, bl, consumer, longOpenHashSet);
		}
	}

	private void method_52823(ChunkRenderingDataPreparer.class_8681 arg) {
		LongIterator longIterator = arg.events.chunksWhichReceivedNeighbors.iterator();

		while (longIterator.hasNext()) {
			long l = longIterator.nextLong();
			List<ChunkBuilder.BuiltChunk> list = arg.storage.field_45628.get(l);
			if (list != null && ((ChunkBuilder.BuiltChunk)list.get(0)).shouldBuild()) {
				arg.events.sectionsToPropagateFrom.addAll(list);
				arg.storage.field_45628.remove(l);
			}
		}

		arg.events.chunksWhichReceivedNeighbors.clear();
	}

	private void addNeighbors(ChunkRenderingDataPreparer.class_8680 arg, ChunkPos chunkPos) {
		arg.chunksWhichReceivedNeighbors.add(ChunkPos.toLong(chunkPos.x - 1, chunkPos.z));
		arg.chunksWhichReceivedNeighbors.add(ChunkPos.toLong(chunkPos.x, chunkPos.z - 1));
		arg.chunksWhichReceivedNeighbors.add(ChunkPos.toLong(chunkPos.x + 1, chunkPos.z));
		arg.chunksWhichReceivedNeighbors.add(ChunkPos.toLong(chunkPos.x, chunkPos.z + 1));
	}

	private void method_52821(Camera camera, Queue<ChunkRenderingDataPreparer.ChunkInfo> queue) {
		BlockPos blockPos = camera.getBlockPos();
		long l = ChunkSectionPos.toLong(blockPos);
		int i = ChunkSectionPos.unpackY(l);
		ChunkBuilder.BuiltChunk builtChunk = this.builtChunkStorage.method_62963(l);
		if (builtChunk == null) {
			HeightLimitView heightLimitView = this.builtChunkStorage.getWorld();
			boolean bl = i < heightLimitView.getBottomSectionCoord();
			int j = bl ? heightLimitView.getBottomSectionCoord() : heightLimitView.getTopSectionCoord();
			int k = this.builtChunkStorage.getViewDistance();
			List<ChunkRenderingDataPreparer.ChunkInfo> list = Lists.<ChunkRenderingDataPreparer.ChunkInfo>newArrayList();
			int m = ChunkSectionPos.unpackX(l);
			int n = ChunkSectionPos.unpackZ(l);

			for (int o = -k; o <= k; o++) {
				for (int p = -k; p <= k; p++) {
					ChunkBuilder.BuiltChunk builtChunk2 = this.builtChunkStorage.method_62963(ChunkSectionPos.asLong(o + m, j, p + n));
					if (builtChunk2 != null && this.isWithinViewDistance(l, builtChunk2.method_62975())) {
						Direction direction = bl ? Direction.UP : Direction.DOWN;
						ChunkRenderingDataPreparer.ChunkInfo chunkInfo = new ChunkRenderingDataPreparer.ChunkInfo(builtChunk2, direction, 0);
						chunkInfo.updateCullingState(chunkInfo.cullingState, direction);
						if (o > 0) {
							chunkInfo.updateCullingState(chunkInfo.cullingState, Direction.EAST);
						} else if (o < 0) {
							chunkInfo.updateCullingState(chunkInfo.cullingState, Direction.WEST);
						}

						if (p > 0) {
							chunkInfo.updateCullingState(chunkInfo.cullingState, Direction.SOUTH);
						} else if (p < 0) {
							chunkInfo.updateCullingState(chunkInfo.cullingState, Direction.NORTH);
						}

						list.add(chunkInfo);
					}
				}
			}

			list.sort(Comparator.comparingDouble(chunkInfox -> blockPos.getSquaredDistance(chunkInfox.chunk.getOrigin().add(8, 8, 8))));
			queue.addAll(list);
		} else {
			queue.add(new ChunkRenderingDataPreparer.ChunkInfo(builtChunk, null, 0));
		}
	}

	private void method_52825(
		ChunkRenderingDataPreparer.RenderableChunks renderableChunks,
		Vec3d pos,
		Queue<ChunkRenderingDataPreparer.ChunkInfo> queue,
		boolean bl,
		Consumer<ChunkBuilder.BuiltChunk> consumer,
		LongOpenHashSet longOpenHashSet
	) {
		int i = 16;
		BlockPos blockPos = new BlockPos(MathHelper.floor(pos.x / 16.0) * 16, MathHelper.floor(pos.y / 16.0) * 16, MathHelper.floor(pos.z / 16.0) * 16);
		long l = ChunkSectionPos.toLong(blockPos);
		BlockPos blockPos2 = blockPos.add(8, 8, 8);

		while (!queue.isEmpty()) {
			ChunkRenderingDataPreparer.ChunkInfo chunkInfo = (ChunkRenderingDataPreparer.ChunkInfo)queue.poll();
			ChunkBuilder.BuiltChunk builtChunk = chunkInfo.chunk;
			if (!longOpenHashSet.contains(chunkInfo.chunk.method_62975())) {
				if (renderableChunks.field_53929.add(chunkInfo.chunk)) {
					consumer.accept(chunkInfo.chunk);
				}
			} else {
				chunkInfo.chunk.data.compareAndSet(ChunkBuilder.ChunkData.EMPTY, ChunkBuilder.ChunkData.field_52172);
			}

			boolean bl2 = Math.abs(builtChunk.getOrigin().getX() - blockPos.getX()) > 60
				|| Math.abs(builtChunk.getOrigin().getY() - blockPos.getY()) > 60
				|| Math.abs(builtChunk.getOrigin().getZ() - blockPos.getZ()) > 60;

			for (Direction direction : DIRECTIONS) {
				ChunkBuilder.BuiltChunk builtChunk2 = this.method_52831(l, builtChunk, direction);
				if (builtChunk2 != null && (!bl || !chunkInfo.canCull(direction.getOpposite()))) {
					if (bl && chunkInfo.hasAnyDirection()) {
						ChunkBuilder.ChunkData chunkData = builtChunk.getData();
						boolean bl3 = false;

						for (int j = 0; j < DIRECTIONS.length; j++) {
							if (chunkInfo.hasDirection(j) && chunkData.isVisibleThrough(DIRECTIONS[j].getOpposite(), direction)) {
								bl3 = true;
								break;
							}
						}

						if (!bl3) {
							continue;
						}
					}

					if (bl && bl2) {
						BlockPos blockPos3 = builtChunk2.getOrigin();
						BlockPos blockPos4 = blockPos3.add(
							(direction.getAxis() == Direction.Axis.X ? blockPos2.getX() <= blockPos3.getX() : blockPos2.getX() >= blockPos3.getX()) ? 0 : 16,
							(direction.getAxis() == Direction.Axis.Y ? blockPos2.getY() <= blockPos3.getY() : blockPos2.getY() >= blockPos3.getY()) ? 0 : 16,
							(direction.getAxis() == Direction.Axis.Z ? blockPos2.getZ() <= blockPos3.getZ() : blockPos2.getZ() >= blockPos3.getZ()) ? 0 : 16
						);
						Vec3d vec3d = new Vec3d((double)blockPos4.getX(), (double)blockPos4.getY(), (double)blockPos4.getZ());
						Vec3d vec3d2 = pos.subtract(vec3d).normalize().multiply(field_45620);
						boolean bl4 = true;

						while (pos.subtract(vec3d).lengthSquared() > 3600.0) {
							vec3d = vec3d.add(vec3d2);
							HeightLimitView heightLimitView = this.builtChunkStorage.getWorld();
							if (vec3d.y > (double)heightLimitView.getTopYInclusive() || vec3d.y < (double)heightLimitView.getBottomY()) {
								break;
							}

							ChunkBuilder.BuiltChunk builtChunk3 = this.builtChunkStorage.getRenderedChunk(BlockPos.ofFloored(vec3d.x, vec3d.y, vec3d.z));
							if (builtChunk3 == null || renderableChunks.field_45627.getInfo(builtChunk3) == null) {
								bl4 = false;
								break;
							}
						}

						if (!bl4) {
							continue;
						}
					}

					ChunkRenderingDataPreparer.ChunkInfo chunkInfo2 = renderableChunks.field_45627.getInfo(builtChunk2);
					if (chunkInfo2 != null) {
						chunkInfo2.addDirection(direction);
					} else {
						ChunkRenderingDataPreparer.ChunkInfo chunkInfo3 = new ChunkRenderingDataPreparer.ChunkInfo(builtChunk2, direction, chunkInfo.propagationLevel + 1);
						chunkInfo3.updateCullingState(chunkInfo.cullingState, direction);
						if (builtChunk2.shouldBuild()) {
							queue.add(chunkInfo3);
							renderableChunks.field_45627.setInfo(builtChunk2, chunkInfo3);
						} else if (this.isWithinViewDistance(l, builtChunk2.method_62975())) {
							renderableChunks.field_45627.setInfo(builtChunk2, chunkInfo3);
							renderableChunks.field_45628
								.computeIfAbsent(ChunkPos.toLong(builtChunk2.getOrigin()), (Long2ObjectFunction<? extends List<ChunkBuilder.BuiltChunk>>)(lx -> new ArrayList()))
								.add(builtChunk2);
						}
					}
				}
			}
		}
	}

	private boolean isWithinViewDistance(long l, long m) {
		return ChunkFilter.isWithinDistanceExcludingEdge(
			ChunkSectionPos.unpackX(l), ChunkSectionPos.unpackZ(l), this.builtChunkStorage.getViewDistance(), ChunkSectionPos.unpackX(m), ChunkSectionPos.unpackZ(m)
		);
	}

	@Nullable
	private ChunkBuilder.BuiltChunk method_52831(long l, ChunkBuilder.BuiltChunk builtChunk, Direction direction) {
		long m = builtChunk.method_62974(direction);
		if (!this.isWithinViewDistance(l, m)) {
			return null;
		} else {
			return MathHelper.abs(ChunkSectionPos.unpackY(l) - ChunkSectionPos.unpackY(m)) > this.builtChunkStorage.getViewDistance()
				? null
				: this.builtChunkStorage.method_62963(m);
		}
	}

	@Nullable
	@Debug
	public ChunkRenderingDataPreparer.ChunkInfo method_52837(ChunkBuilder.BuiltChunk builtChunk) {
		return ((ChunkRenderingDataPreparer.class_8681)this.field_45624.get()).storage.field_45627.getInfo(builtChunk);
	}

	public Octree method_62925() {
		return ((ChunkRenderingDataPreparer.class_8681)this.field_45624.get()).storage.field_53929;
	}

	@Environment(EnvType.CLIENT)
	@Debug
	public static class ChunkInfo {
		@Debug
		protected final ChunkBuilder.BuiltChunk chunk;
		private byte direction;
		byte cullingState;
		@Debug
		public final int propagationLevel;

		ChunkInfo(ChunkBuilder.BuiltChunk chunk, @Nullable Direction direction, int propagationLevel) {
			this.chunk = chunk;
			if (direction != null) {
				this.addDirection(direction);
			}

			this.propagationLevel = propagationLevel;
		}

		void updateCullingState(byte parentCullingState, Direction from) {
			this.cullingState = (byte)(this.cullingState | parentCullingState | 1 << from.ordinal());
		}

		boolean canCull(Direction from) {
			return (this.cullingState & 1 << from.ordinal()) > 0;
		}

		void addDirection(Direction direction) {
			this.direction = (byte)(this.direction | this.direction | 1 << direction.ordinal());
		}

		@Debug
		public boolean hasDirection(int ordinal) {
			return (this.direction & 1 << ordinal) > 0;
		}

		boolean hasAnyDirection() {
			return this.direction != 0;
		}

		public int hashCode() {
			return Long.hashCode(this.chunk.method_62975());
		}

		public boolean equals(Object o) {
			return !(o instanceof ChunkRenderingDataPreparer.ChunkInfo chunkInfo) ? false : this.chunk.method_62975() == chunkInfo.chunk.method_62975();
		}
	}

	@Environment(EnvType.CLIENT)
	static class ChunkInfoList {
		private final ChunkRenderingDataPreparer.ChunkInfo[] current;

		ChunkInfoList(int size) {
			this.current = new ChunkRenderingDataPreparer.ChunkInfo[size];
		}

		public void setInfo(ChunkBuilder.BuiltChunk chunk, ChunkRenderingDataPreparer.ChunkInfo info) {
			this.current[chunk.index] = info;
		}

		@Nullable
		public ChunkRenderingDataPreparer.ChunkInfo getInfo(ChunkBuilder.BuiltChunk chunk) {
			int i = chunk.index;
			return i >= 0 && i < this.current.length ? this.current[i] : null;
		}
	}

	@Environment(EnvType.CLIENT)
	static class RenderableChunks {
		public final ChunkRenderingDataPreparer.ChunkInfoList field_45627;
		public final Octree field_53929;
		public final Long2ObjectMap<List<ChunkBuilder.BuiltChunk>> field_45628;

		public RenderableChunks(BuiltChunkStorage builtChunkStorage) {
			this.field_45627 = new ChunkRenderingDataPreparer.ChunkInfoList(builtChunkStorage.chunks.length);
			this.field_53929 = new Octree(
				builtChunkStorage.method_62966(), builtChunkStorage.getViewDistance(), builtChunkStorage.sizeY, builtChunkStorage.world.getBottomY()
			);
			this.field_45628 = new Long2ObjectOpenHashMap<>();
		}
	}

	@Environment(EnvType.CLIENT)
	static record class_8680(LongSet chunksWhichReceivedNeighbors, BlockingQueue<ChunkBuilder.BuiltChunk> sectionsToPropagateFrom) {

		class_8680() {
			this(new LongOpenHashSet(), new LinkedBlockingQueue());
		}
	}

	@Environment(EnvType.CLIENT)
	static record class_8681(ChunkRenderingDataPreparer.RenderableChunks storage, ChunkRenderingDataPreparer.class_8680 events) {

		class_8681(BuiltChunkStorage builtChunkStorage) {
			this(new ChunkRenderingDataPreparer.RenderableChunks(builtChunkStorage), new ChunkRenderingDataPreparer.class_8680());
		}
	}
}
