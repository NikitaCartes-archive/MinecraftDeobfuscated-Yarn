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
import java.util.LinkedHashSet;
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
			this.field_45624.set(new ChunkRenderingDataPreparer.class_8681(builtChunkStorage.chunks.length));
			this.scheduleTerrainUpdate();
		} else {
			this.field_45624.set(null);
		}
	}

	public void scheduleTerrainUpdate() {
		this.field_45621 = true;
	}

	public void method_52828(Frustum frustum, List<ChunkBuilder.BuiltChunk> builtChunks) {
		for (ChunkRenderingDataPreparer.ChunkInfo chunkInfo : ((ChunkRenderingDataPreparer.class_8681)this.field_45624.get()).storage().chunks) {
			if (frustum.isVisible(chunkInfo.chunk.getBoundingBox())) {
				builtChunks.add(chunkInfo.chunk);
			}
		}
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

	public void method_52834(boolean bl, Camera camera, Frustum frustum, List<ChunkBuilder.BuiltChunk> builtChunk) {
		Vec3d vec3d = camera.getPos();
		if (this.field_45621 && (this.field_45622 == null || this.field_45622.isDone())) {
			this.method_52833(bl, camera, vec3d);
		}

		this.method_52835(bl, frustum, builtChunk, vec3d);
	}

	private void method_52833(boolean bl, Camera camera, Vec3d vec3d) {
		this.field_45621 = false;
		this.field_45622 = Util.getMainWorkerExecutor().submit(() -> {
			ChunkRenderingDataPreparer.class_8681 lv = new ChunkRenderingDataPreparer.class_8681(this.builtChunkStorage.chunks.length);
			this.field_45625.set(lv.events);
			Queue<ChunkRenderingDataPreparer.ChunkInfo> queue = Queues.<ChunkRenderingDataPreparer.ChunkInfo>newArrayDeque();
			this.method_52821(camera, queue);
			queue.forEach(chunkInfo -> lv.storage.field_45627.setInfo(chunkInfo.chunk, chunkInfo));
			this.method_52825(lv.storage, vec3d, queue, bl, builtChunk -> {
			});
			this.field_45624.set(lv);
			this.field_45625.set(null);
			this.field_45626.set(true);
		});
	}

	private void method_52835(boolean bl, Frustum frustum, List<ChunkBuilder.BuiltChunk> builtChunks, Vec3d vec3d) {
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
			this.method_52825(lv.storage, vec3d, queue, bl, consumer);
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
		int i = 16;
		Vec3d vec3d = camera.getPos();
		BlockPos blockPos = camera.getBlockPos();
		ChunkBuilder.BuiltChunk builtChunk = this.builtChunkStorage.getRenderedChunk(blockPos);
		if (builtChunk == null) {
			HeightLimitView heightLimitView = this.builtChunkStorage.getWorld();
			boolean bl = blockPos.getY() > heightLimitView.getBottomY();
			int j = bl ? heightLimitView.getTopY() - 8 : heightLimitView.getBottomY() + 8;
			int k = MathHelper.floor(vec3d.x / 16.0) * 16;
			int l = MathHelper.floor(vec3d.z / 16.0) * 16;
			int m = this.builtChunkStorage.getViewDistance();
			List<ChunkRenderingDataPreparer.ChunkInfo> list = Lists.<ChunkRenderingDataPreparer.ChunkInfo>newArrayList();

			for (int n = -m; n <= m; n++) {
				for (int o = -m; o <= m; o++) {
					ChunkBuilder.BuiltChunk builtChunk2 = this.builtChunkStorage
						.getRenderedChunk(new BlockPos(k + ChunkSectionPos.getOffsetPos(n, 8), j, l + ChunkSectionPos.getOffsetPos(o, 8)));
					if (builtChunk2 != null && this.isWithinViewDistance(blockPos, builtChunk2.getOrigin())) {
						Direction direction = bl ? Direction.DOWN : Direction.UP;
						ChunkRenderingDataPreparer.ChunkInfo chunkInfo = new ChunkRenderingDataPreparer.ChunkInfo(builtChunk2, direction, 0);
						chunkInfo.updateCullingState(chunkInfo.cullingState, direction);
						if (n > 0) {
							chunkInfo.updateCullingState(chunkInfo.cullingState, Direction.EAST);
						} else if (n < 0) {
							chunkInfo.updateCullingState(chunkInfo.cullingState, Direction.WEST);
						}

						if (o > 0) {
							chunkInfo.updateCullingState(chunkInfo.cullingState, Direction.SOUTH);
						} else if (o < 0) {
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
		Consumer<ChunkBuilder.BuiltChunk> consumer
	) {
		int i = 16;
		BlockPos blockPos = new BlockPos(MathHelper.floor(pos.x / 16.0) * 16, MathHelper.floor(pos.y / 16.0) * 16, MathHelper.floor(pos.z / 16.0) * 16);
		BlockPos blockPos2 = blockPos.add(8, 8, 8);

		while (!queue.isEmpty()) {
			ChunkRenderingDataPreparer.ChunkInfo chunkInfo = (ChunkRenderingDataPreparer.ChunkInfo)queue.poll();
			ChunkBuilder.BuiltChunk builtChunk = chunkInfo.chunk;
			if (renderableChunks.chunks.add(chunkInfo)) {
				consumer.accept(chunkInfo.chunk);
			}

			boolean bl2 = Math.abs(builtChunk.getOrigin().getX() - blockPos.getX()) > 60
				|| Math.abs(builtChunk.getOrigin().getY() - blockPos.getY()) > 60
				|| Math.abs(builtChunk.getOrigin().getZ() - blockPos.getZ()) > 60;

			for (Direction direction : DIRECTIONS) {
				ChunkBuilder.BuiltChunk builtChunk2 = this.method_52831(blockPos, builtChunk, direction);
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
							if (vec3d.y > (double)heightLimitView.getTopY() || vec3d.y < (double)heightLimitView.getBottomY()) {
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
						} else if (this.isWithinViewDistance(blockPos, builtChunk2.getOrigin())) {
							renderableChunks.field_45627.setInfo(builtChunk2, chunkInfo3);
							renderableChunks.field_45628
								.computeIfAbsent(ChunkPos.toLong(builtChunk2.getOrigin()), (Long2ObjectFunction<? extends List<ChunkBuilder.BuiltChunk>>)(l -> new ArrayList()))
								.add(builtChunk2);
						}
					}
				}
			}
		}
	}

	private boolean isWithinViewDistance(BlockPos from, BlockPos to) {
		int i = ChunkSectionPos.getSectionCoord(from.getX());
		int j = ChunkSectionPos.getSectionCoord(from.getZ());
		int k = ChunkSectionPos.getSectionCoord(to.getX());
		int l = ChunkSectionPos.getSectionCoord(to.getZ());
		return ChunkFilter.isWithinDistanceExcludingEdge(i, j, this.builtChunkStorage.getViewDistance(), k, l);
	}

	@Nullable
	private ChunkBuilder.BuiltChunk method_52831(BlockPos pos, ChunkBuilder.BuiltChunk builtChunk, Direction direction) {
		BlockPos blockPos = builtChunk.getNeighborPosition(direction);
		if (!this.isWithinViewDistance(pos, blockPos)) {
			return null;
		} else {
			return MathHelper.abs(pos.getY() - blockPos.getY()) > this.builtChunkStorage.getViewDistance() * 16
				? null
				: this.builtChunkStorage.getRenderedChunk(blockPos);
		}
	}

	@Nullable
	@Debug
	public ChunkRenderingDataPreparer.ChunkInfo method_52837(ChunkBuilder.BuiltChunk builtChunk) {
		return ((ChunkRenderingDataPreparer.class_8681)this.field_45624.get()).storage.field_45627.getInfo(builtChunk);
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
			return this.chunk.getOrigin().hashCode();
		}

		public boolean equals(Object o) {
			return !(o instanceof ChunkRenderingDataPreparer.ChunkInfo chunkInfo) ? false : this.chunk.getOrigin().equals(chunkInfo.chunk.getOrigin());
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
		public final LinkedHashSet<ChunkRenderingDataPreparer.ChunkInfo> chunks;
		public final Long2ObjectMap<List<ChunkBuilder.BuiltChunk>> field_45628;

		public RenderableChunks(int chunkCount) {
			this.field_45627 = new ChunkRenderingDataPreparer.ChunkInfoList(chunkCount);
			this.chunks = new LinkedHashSet(chunkCount);
			this.field_45628 = new Long2ObjectOpenHashMap<>();
		}
	}

	@Environment(EnvType.CLIENT)
	static record class_8680(LongSet chunksWhichReceivedNeighbors, BlockingQueue<ChunkBuilder.BuiltChunk> sectionsToPropagateFrom) {

		public class_8680() {
			this(new LongOpenHashSet(), new LinkedBlockingQueue());
		}
	}

	@Environment(EnvType.CLIENT)
	static record class_8681(ChunkRenderingDataPreparer.RenderableChunks storage, ChunkRenderingDataPreparer.class_8680 events) {

		public class_8681(int i) {
			this(new ChunkRenderingDataPreparer.RenderableChunks(i), new ChunkRenderingDataPreparer.class_8680());
		}
	}
}
