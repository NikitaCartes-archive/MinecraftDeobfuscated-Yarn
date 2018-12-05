package net.minecraft;

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
import net.minecraft.entity.player.ChunkTicket;
import net.minecraft.entity.player.ChunkTicketType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerChunkManagerEntry;
import net.minecraft.world.chunk.ChunkPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class class_3204 extends class_3196 {
	private static final Logger field_16211 = LogManager.getLogger();
	private final Long2ObjectOpenHashMap<ObjectSortedSet<ChunkTicket<?>>> field_13895 = new Long2ObjectOpenHashMap<>();
	private final class_3204.class_3205 field_13893 = new class_3204.class_3205() {
		@Override
		protected boolean method_14056(long l) {
			return class_3204.this.method_14050(l).stream().anyMatch(chunkTicket -> chunkTicket.method_14281() == ChunkTicketType.PLAYER);
		}
	};
	private final Set<ServerChunkManagerEntry> field_16210 = Sets.<ServerChunkManagerEntry>newHashSet();
	private long field_13894;

	protected class_3204() {
		super(ServerChunkManager.field_13922 + 2, 16, 256);
	}

	public void method_14045() {
		this.field_13894++;
		ObjectIterator<Entry<ObjectSortedSet<ChunkTicket<?>>>> objectIterator = this.field_13895.long2ObjectEntrySet().fastIterator();

		while (objectIterator.hasNext()) {
			Entry<ObjectSortedSet<ChunkTicket<?>>> entry = (Entry<ObjectSortedSet<ChunkTicket<?>>>)objectIterator.next();
			if (((ObjectSortedSet)entry.getValue())
				.removeIf(chunkTicket -> chunkTicket.method_14281() == ChunkTicketType.UNKNOWN && chunkTicket.getLocation() != this.field_13894)) {
				this.method_14027(entry.getLongKey(), this.method_14046((ObjectSortedSet<ChunkTicket<?>>)entry.getValue()), false);
			}

			if (((ObjectSortedSet)entry.getValue()).isEmpty()) {
				objectIterator.remove();
			}
		}
	}

	private int method_14046(ObjectSortedSet<ChunkTicket<?>> objectSortedSet) {
		ObjectBidirectionalIterator<ChunkTicket<?>> objectBidirectionalIterator = objectSortedSet.iterator();
		return objectBidirectionalIterator.hasNext() ? ((ChunkTicket)objectBidirectionalIterator.next()).method_14283() : ServerChunkManager.field_13922 + 1;
	}

	@Override
	protected int method_15480(long l) {
		if (!this.method_14035(l)) {
			ServerChunkManagerEntry serverChunkManagerEntry = this.method_14038(l);
			if (serverChunkManagerEntry != null) {
				return serverChunkManagerEntry.method_14005();
			}
		}

		return ServerChunkManager.field_13922 + 1;
	}

	protected abstract boolean method_14035(long l);

	@Nullable
	protected abstract ServerChunkManagerEntry method_14038(long l);

	@Override
	protected void method_15485(long l, int i) {
		ServerChunkManagerEntry serverChunkManagerEntry = this.method_14038(l);
		int j = serverChunkManagerEntry == null ? ServerChunkManager.field_13922 + 1 : serverChunkManagerEntry.method_14005();
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
		ObjectSortedSet<ChunkTicket<?>> objectSortedSet = this.field_13895.get(l);
		if (objectSortedSet == null) {
			return Integer.MAX_VALUE;
		} else {
			ObjectBidirectionalIterator<ChunkTicket<?>> objectBidirectionalIterator = objectSortedSet.iterator();
			return !objectBidirectionalIterator.hasNext() ? Integer.MAX_VALUE : ((ChunkTicket)objectBidirectionalIterator.next()).method_14283();
		}
	}

	public boolean method_14055() {
		return this.method_15489();
	}

	public boolean method_15892(ServerChunkManager serverChunkManager) {
		boolean bl = this.method_15492(Integer.MAX_VALUE) != Integer.MAX_VALUE;
		if (!this.field_16210.isEmpty()) {
			this.field_16210.forEach(serverChunkManagerEntry -> serverChunkManagerEntry.method_14007(serverChunkManager));
			this.field_16210.clear();
			return true;
		} else {
			return bl;
		}
	}

	private void method_14042(long l, ChunkTicket<?> chunkTicket) {
		ObjectSortedSet<ChunkTicket<?>> objectSortedSet = this.method_14050(l);
		ObjectBidirectionalIterator<ChunkTicket<?>> objectBidirectionalIterator = objectSortedSet.iterator();
		int i;
		if (objectBidirectionalIterator.hasNext()) {
			i = ((ChunkTicket)objectBidirectionalIterator.next()).method_14283();
		} else {
			i = ServerChunkManager.field_13922 + 1;
		}

		objectSortedSet.add(chunkTicket);
		if (chunkTicket.method_14283() < i) {
			this.method_14027(l, chunkTicket.method_14283(), true);
		}
	}

	public void method_14054(int i, ChunkPos chunkPos) {
		this.method_14042(chunkPos.toLong(), new ChunkTicket<>(ChunkTicketType.UNKNOWN, i, chunkPos, this.field_13894));
	}

	private ObjectSortedSet<ChunkTicket<?>> method_14050(long l) {
		return this.field_13895.computeIfAbsent(l, lx -> new ObjectAVLTreeSet());
	}

	public void method_14043(ChunkPos chunkPos, int i) {
		this.method_14042(chunkPos.toLong(), new ChunkTicket<>(ChunkTicketType.START, 33 - i, null, this.field_13894));
	}

	public void method_14036(ChunkPos chunkPos, boolean bl) {
		ChunkTicket<ChunkPos> chunkTicket = new ChunkTicket<>(ChunkTicketType.FORCED, 32, chunkPos, this.field_13894);
		if (bl) {
			this.method_14042(chunkPos.toLong(), chunkTicket);
		} else {
			ObjectSortedSet<ChunkTicket<?>> objectSortedSet = this.method_14050(chunkPos.toLong());
			objectSortedSet.remove(chunkTicket);
			this.method_14027(chunkPos.toLong(), this.method_14046(objectSortedSet), false);
		}
	}

	public void method_14048(long l, int i, ServerPlayerEntity serverPlayerEntity) {
		this.method_14042(l, serverPlayerEntity.method_14215(l, i, this.field_13894));
		this.field_13893.method_14027(l, 0, true);
	}

	public void method_14051(long l, ServerPlayerEntity serverPlayerEntity) {
		ObjectSortedSet<ChunkTicket<?>> objectSortedSet = this.method_14050(l);
		objectSortedSet.remove(serverPlayerEntity.getChunkTicket());
		this.method_14027(l, this.method_14046(objectSortedSet), false);
		this.field_13893
			.method_14027(l, objectSortedSet.stream().anyMatch(chunkTicket -> chunkTicket.method_14281() == ChunkTicketType.PLAYER) ? 0 : Integer.MAX_VALUE, false);
	}

	public void method_14049(int i) {
		for (Entry<ObjectSortedSet<ChunkTicket<?>>> entry : this.field_13895.long2ObjectEntrySet()) {
			ObjectSortedSet<ChunkTicket<?>> objectSortedSet = (ObjectSortedSet<ChunkTicket<?>>)entry.getValue();
			if (!objectSortedSet.isEmpty()) {
				int j = ((ChunkTicket)objectSortedSet.first()).method_14283();

				for (ChunkTicket<?> chunkTicket : (List)objectSortedSet.stream()
					.filter(chunkTicketx -> chunkTicketx.method_14281() == ChunkTicketType.PLAYER)
					.collect(Collectors.toList())) {
					objectSortedSet.remove(chunkTicket);
					objectSortedSet.add(chunkTicket.method_14282(i));
				}

				int k = ((ChunkTicket)objectSortedSet.first()).method_14283();
				if (j != k) {
					this.method_14027(entry.getLongKey(), k, k < j);
				}
			}
		}
	}

	public int method_14052() {
		this.field_13893.method_14057();
		return this.field_13893.field_13896.size();
	}

	abstract static class class_3205 extends class_3196 {
		private final Long2ByteMap field_13896 = new Long2ByteOpenHashMap();

		protected class_3205() {
			super(10, 16, 256);
			this.field_13896.defaultReturnValue((byte)10);
		}

		@Override
		protected int method_15480(long l) {
			return this.field_13896.get(l);
		}

		@Override
		protected void method_15485(long l, int i) {
			if (i > 8) {
				this.field_13896.remove(l);
			} else {
				this.field_13896.put(l, (byte)i);
			}
		}

		@Override
		protected int method_14028(long l) {
			return this.method_14056(l) ? 0 : Integer.MAX_VALUE;
		}

		protected abstract boolean method_14056(long l);

		public void method_14057() {
			this.method_15492(Integer.MAX_VALUE);
		}
	}
}
