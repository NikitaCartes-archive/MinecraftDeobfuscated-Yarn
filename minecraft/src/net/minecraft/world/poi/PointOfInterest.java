package net.minecraft.world.poi;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class PointOfInterest {
	private final BlockPos pos;
	private final PointOfInterestType type;
	private int freeTickets;
	private final Runnable updateListener;

	public static Codec<PointOfInterest> createCodec(Runnable updateListener) {
		return RecordCodecBuilder.create(
			instance -> instance.group(
						BlockPos.CODEC.fieldOf("pos").forGetter(pointOfInterest -> pointOfInterest.pos),
						Registry.POINT_OF_INTEREST_TYPE.fieldOf("type").forGetter(pointOfInterest -> pointOfInterest.type),
						Codec.INT.fieldOf("free_tickets").orElse(0).forGetter(pointOfInterest -> pointOfInterest.freeTickets),
						RecordCodecBuilder.point(updateListener)
					)
					.apply(instance, PointOfInterest::new)
		);
	}

	private PointOfInterest(BlockPos pos, PointOfInterestType type, int freeTickets, Runnable updateListener) {
		this.pos = pos.toImmutable();
		this.type = type;
		this.freeTickets = freeTickets;
		this.updateListener = updateListener;
	}

	public PointOfInterest(BlockPos pos, PointOfInterestType type, Runnable updateListener) {
		this(pos, type, type.getTicketCount(), updateListener);
	}

	protected boolean reserveTicket() {
		if (this.freeTickets <= 0) {
			return false;
		} else {
			this.freeTickets--;
			this.updateListener.run();
			return true;
		}
	}

	protected boolean releaseTicket() {
		if (this.freeTickets >= this.type.getTicketCount()) {
			return false;
		} else {
			this.freeTickets++;
			this.updateListener.run();
			return true;
		}
	}

	public boolean hasSpace() {
		return this.freeTickets > 0;
	}

	public boolean isOccupied() {
		return this.freeTickets != this.type.getTicketCount();
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public PointOfInterestType getType() {
		return this.type;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else {
			return obj != null && this.getClass() == obj.getClass() ? Objects.equals(this.pos, ((PointOfInterest)obj).pos) : false;
		}
	}

	public int hashCode() {
		return this.pos.hashCode();
	}
}
