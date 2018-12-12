package net.minecraft.server.world;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.class_3196;
import net.minecraft.class_3898;
import net.minecraft.entity.player.ChunkTicketType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.chunk.ChunkPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ChunkTicketManager extends class_3196 {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Long2ObjectOpenHashMap<ObjectSortedSet<ChunkTicket<?>>> positionToTicketSet = new Long2ObjectOpenHashMap<>();
	private final ChunkTicketManager.class_3205 field_13893 = new ChunkTicketManager.class_3205() {
		@Override
		protected boolean method_14056(long l) {
			return ChunkTicketManager.this.getTicketSet(l).stream().anyMatch(chunkTicket -> chunkTicket.method_14281() == ChunkTicketType.PLAYER);
		}
	};
	private final Set<ServerChunkManagerEntry> field_16210 = Sets.<ServerChunkManagerEntry>newHashSet();
	private long location;

	protected ChunkTicketManager() {
		super(ServerChunkManager.FULL_CHUNK_LEVEL + 2, 16, 256);
	}

	protected void method_14045() {
		this.location++;
		ObjectIterator<Entry<ObjectSortedSet<ChunkTicket<?>>>> objectIterator = this.positionToTicketSet.long2ObjectEntrySet().fastIterator();

		while (objectIterator.hasNext()) {
			Entry<ObjectSortedSet<ChunkTicket<?>>> entry = (Entry<ObjectSortedSet<ChunkTicket<?>>>)objectIterator.next();
			if (((ObjectSortedSet)entry.getValue())
				.removeIf(chunkTicket -> chunkTicket.method_14281() == ChunkTicketType.UNKNOWN && chunkTicket.getLocation() != this.location)) {
				this.scheduleNewLevelUpdate(entry.getLongKey(), this.getLevel((ObjectSortedSet<ChunkTicket<?>>)entry.getValue()), false);
			}

			if (((ObjectSortedSet)entry.getValue()).isEmpty()) {
				objectIterator.remove();
			}
		}
	}

	private int getLevel(ObjectSortedSet<ChunkTicket<?>> objectSortedSet) {
		ObjectBidirectionalIterator<ChunkTicket<?>> objectBidirectionalIterator = objectSortedSet.iterator();
		return objectBidirectionalIterator.hasNext() ? ((ChunkTicket)objectBidirectionalIterator.next()).getLevel() : ServerChunkManager.FULL_CHUNK_LEVEL + 1;
	}

	@Override
	protected int getCurrentLevelFor(long l) {
		if (!this.method_14035(l)) {
			ServerChunkManagerEntry serverChunkManagerEntry = this.method_14038(l);
			if (serverChunkManagerEntry != null) {
				return serverChunkManagerEntry.getLevel();
			}
		}

		return ServerChunkManager.FULL_CHUNK_LEVEL + 1;
	}

	protected abstract boolean method_14035(long l);

	@Nullable
	protected abstract ServerChunkManagerEntry method_14038(long l);

	@Override
	protected void setLevelFor(long l, int i) {
		ServerChunkManagerEntry serverChunkManagerEntry = this.method_14038(l);
		int j = serverChunkManagerEntry == null ? ServerChunkManager.FULL_CHUNK_LEVEL + 1 : serverChunkManagerEntry.getLevel();
		if (j != i) {
			serverChunkManagerEntry = this.method_14053(l, i, serverChunkManagerEntry, j);
			if (serverChunkManagerEntry != null) {
				this.field_16210.add(serverChunkManagerEntry);
			}
		}
	}

	@Nullable
	protected abstract ServerChunkManagerEntry method_14053(long l, int i, @Nullable ServerChunkManagerEntry serverChunkManagerEntry, int j);

	@Override
	protected int method_14028(long l) {
		ObjectSortedSet<ChunkTicket<?>> objectSortedSet = this.positionToTicketSet.get(l);
		if (objectSortedSet == null) {
			return Integer.MAX_VALUE;
		} else {
			ObjectBidirectionalIterator<ChunkTicket<?>> objectBidirectionalIterator = objectSortedSet.iterator();
			return !objectBidirectionalIterator.hasNext() ? Integer.MAX_VALUE : ((ChunkTicket)objectBidirectionalIterator.next()).getLevel();
		}
	}

	public boolean update(class_3898 arg) {
		int i = Integer.MAX_VALUE - this.updateLevels(Integer.MAX_VALUE);
		boolean bl = i != 0;
		if (bl) {
		}

		if (!this.field_16210.isEmpty()) {
			this.field_16210.forEach(serverChunkManagerEntry -> serverChunkManagerEntry.method_14007(arg));
			this.field_16210.clear();
			return true;
		} else {
			return bl;
		}
	}

	private void method_14042(long l, ChunkTicket<?> chunkTicket) {
		ObjectSortedSet<ChunkTicket<?>> objectSortedSet = this.getTicketSet(l);
		ObjectBidirectionalIterator<ChunkTicket<?>> objectBidirectionalIterator = objectSortedSet.iterator();
		int i;
		if (objectBidirectionalIterator.hasNext()) {
			i = ((ChunkTicket)objectBidirectionalIterator.next()).getLevel();
		} else {
			i = ServerChunkManager.FULL_CHUNK_LEVEL + 1;
		}

		objectSortedSet.add(chunkTicket);
		if (chunkTicket.getLevel() < i) {
			this.scheduleNewLevelUpdate(l, chunkTicket.getLevel(), true);
		}
	}

	public <T> void addTicketAtLevel(ChunkTicketType<T> chunkTicketType, ChunkPos chunkPos, int i, T object) {
		this.method_14042(chunkPos.toLong(), new ChunkTicket<>(chunkTicketType, i, object, this.location));
	}

	public <T> void addTicket(ChunkTicketType<T> chunkTicketType, ChunkPos chunkPos, int i, T object) {
		this.method_14042(chunkPos.toLong(), new ChunkTicket<>(chunkTicketType, 33 - i, object, this.location));
	}

	public <T> void removeTicket(ChunkTicketType<T> chunkTicketType, ChunkPos chunkPos, int i, T object) {
		ObjectSortedSet<ChunkTicket<?>> objectSortedSet = this.getTicketSet(chunkPos.toLong());
		ChunkTicket<T> chunkTicket = new ChunkTicket<>(chunkTicketType, 33 - i, object, this.location);
		objectSortedSet.remove(chunkTicket);
		this.scheduleNewLevelUpdate(chunkPos.toLong(), this.getLevel(objectSortedSet), false);
	}

	private ObjectSortedSet<ChunkTicket<?>> getTicketSet(long l) {
		return this.positionToTicketSet.computeIfAbsent(l, lx -> new ObjectAVLTreeSet());
	}

	protected void setChunkForced(ChunkPos chunkPos, boolean bl) {
		ChunkTicket<ChunkPos> chunkTicket = new ChunkTicket<>(ChunkTicketType.FORCED, 32, chunkPos, this.location);
		if (bl) {
			this.method_14042(chunkPos.toLong(), chunkTicket);
		} else {
			ObjectSortedSet<ChunkTicket<?>> objectSortedSet = this.getTicketSet(chunkPos.toLong());
			objectSortedSet.remove(chunkTicket);
			this.scheduleNewLevelUpdate(chunkPos.toLong(), this.getLevel(objectSortedSet), false);
		}
	}

	public void method_14048(long l, int i, ServerPlayerEntity serverPlayerEntity) {
		this.method_14042(l, serverPlayerEntity.method_14215(l, i, this.location));
		this.field_13893.scheduleNewLevelUpdate(l, 0, true);
	}

	public void method_14051(long l, ServerPlayerEntity serverPlayerEntity) {
		ObjectSortedSet<ChunkTicket<?>> objectSortedSet = this.getTicketSet(l);
		objectSortedSet.remove(serverPlayerEntity.method_14214());
		this.scheduleNewLevelUpdate(l, this.getLevel(objectSortedSet), false);
		this.field_13893
			.scheduleNewLevelUpdate(
				l, objectSortedSet.stream().anyMatch(chunkTicket -> chunkTicket.method_14281() == ChunkTicketType.PLAYER) ? 0 : Integer.MAX_VALUE, false
			);
	}

	protected void method_14049(int i) {
		for (Entry<ObjectSortedSet<ChunkTicket<?>>> entry : this.positionToTicketSet.long2ObjectEntrySet()) {
			ObjectSortedSet<ChunkTicket<?>> objectSortedSet = (ObjectSortedSet<ChunkTicket<?>>)entry.getValue();
			if (!objectSortedSet.isEmpty()) {
				int j = ((ChunkTicket)objectSortedSet.first()).getLevel();

				for (ChunkTicket<?> chunkTicket : (List)objectSortedSet.stream()
					.filter(chunkTicketx -> chunkTicketx.method_14281() == ChunkTicketType.PLAYER)
					.collect(Collectors.toList())) {
					objectSortedSet.remove(chunkTicket);
					objectSortedSet.add(chunkTicket.method_14282(i));
				}

				int k = ((ChunkTicket)objectSortedSet.first()).getLevel();
				if (j != k) {
					this.scheduleNewLevelUpdate(entry.getLongKey(), k, k < j);
				}
			}
		}
	}

	public int method_14052() {
		this.field_13893.updateLevels();
		return this.field_13893.currentLevels.size();
	}

	abstract static class class_3205 extends class_3196 {
		private final Long2ByteMap currentLevels = new Long2ByteOpenHashMap();

		protected class_3205() {
			super(10, 16, 256);
			this.currentLevels.defaultReturnValue((byte)10);
		}

		@Override
		protected int getCurrentLevelFor(long l) {
			return this.currentLevels.get(l);
		}

		@Override
		protected void setLevelFor(long l, int i) {
			if (i > 8) {
				this.currentLevels.remove(l);
			} else {
				this.currentLevels.put(l, (byte)i);
			}
		}

		@Override
		protected int method_14028(long l) {
			return this.method_14056(l) ? 0 : Integer.MAX_VALUE;
		}

		protected abstract boolean method_14056(long l);

		public void updateLevels() {
			this.updateLevels(Integer.MAX_VALUE);
		}
	}
}
