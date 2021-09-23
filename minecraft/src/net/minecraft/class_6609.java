package net.minecraft;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.world.ChunkTicket;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.util.collection.SortedArraySet;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkPosDistanceLevelPropagator;

public class class_6609 extends ChunkPosDistanceLevelPropagator {
	private static final int field_34889 = 4;
	protected final Long2ByteMap field_34888 = new Long2ByteOpenHashMap();
	private final Long2ObjectOpenHashMap<SortedArraySet<ChunkTicket<?>>> field_34890 = new Long2ObjectOpenHashMap<>();

	public class_6609() {
		super(34, 16, 256);
		this.field_34888.defaultReturnValue((byte)33);
	}

	private SortedArraySet<ChunkTicket<?>> method_38644(long l) {
		return this.field_34890.computeIfAbsent(l, lx -> SortedArraySet.create(4));
	}

	private int method_38639(SortedArraySet<ChunkTicket<?>> sortedArraySet) {
		return sortedArraySet.isEmpty() ? 34 : sortedArraySet.first().getLevel();
	}

	public void method_38637(long l, ChunkTicket<?> chunkTicket) {
		SortedArraySet<ChunkTicket<?>> sortedArraySet = this.method_38644(l);
		int i = this.method_38639(sortedArraySet);
		sortedArraySet.add(chunkTicket);
		if (chunkTicket.getLevel() < i) {
			this.updateLevel(l, chunkTicket.getLevel(), true);
		}
	}

	public void method_38641(long l, ChunkTicket<?> chunkTicket) {
		SortedArraySet<ChunkTicket<?>> sortedArraySet = this.method_38644(l);
		sortedArraySet.remove(chunkTicket);
		if (sortedArraySet.isEmpty()) {
			this.field_34890.remove(l);
		}

		this.updateLevel(l, this.method_38639(sortedArraySet), false);
	}

	public <T> void method_38638(ChunkTicketType<T> chunkTicketType, ChunkPos chunkPos, int i, T object) {
		this.method_38637(chunkPos.toLong(), new ChunkTicket<>(chunkTicketType, i, object));
	}

	public <T> void method_38642(ChunkTicketType<T> chunkTicketType, ChunkPos chunkPos, int i, T object) {
		ChunkTicket<T> chunkTicket = new ChunkTicket<>(chunkTicketType, i, object);
		this.method_38641(chunkPos.toLong(), chunkTicket);
	}

	public void method_38636(int i) {
		List<Pair<ChunkTicket<ChunkPos>, Long>> list = new ArrayList();

		for (Entry<SortedArraySet<ChunkTicket<?>>> entry : this.field_34890.long2ObjectEntrySet()) {
			for (ChunkTicket<?> chunkTicket : (SortedArraySet)entry.getValue()) {
				if (chunkTicket.getType() == ChunkTicketType.PLAYER) {
					list.add(Pair.of(chunkTicket, entry.getLongKey()));
				}
			}
		}

		for (Pair<ChunkTicket<ChunkPos>, Long> pair : list) {
			Long long_ = pair.getSecond();
			ChunkTicket<ChunkPos> chunkTicketx = pair.getFirst();
			this.method_38641(long_, chunkTicketx);
			ChunkPos chunkPos = new ChunkPos(long_);
			ChunkTicketType<ChunkPos> chunkTicketType = chunkTicketx.getType();
			this.method_38638(chunkTicketType, chunkPos, i, chunkPos);
		}
	}

	@Override
	protected int getInitialLevel(long id) {
		SortedArraySet<ChunkTicket<?>> sortedArraySet = this.field_34890.get(id);
		return sortedArraySet != null && !sortedArraySet.isEmpty() ? sortedArraySet.first().getLevel() : Integer.MAX_VALUE;
	}

	public int method_38640(ChunkPos chunkPos) {
		return this.getLevel(chunkPos.toLong());
	}

	@Override
	public int getLevel(long id) {
		return this.field_34888.get(id);
	}

	@Override
	protected void setLevel(long id, int level) {
		if (level > 33) {
			this.field_34888.remove(id);
		} else {
			this.field_34888.put(id, (byte)level);
		}
	}

	public void method_38635() {
		this.applyPendingUpdates(Integer.MAX_VALUE);
	}

	public String method_38643(long l) {
		SortedArraySet<ChunkTicket<?>> sortedArraySet = this.field_34890.get(l);
		return sortedArraySet != null && !sortedArraySet.isEmpty() ? sortedArraySet.first().toString() : "no_ticket";
	}
}
