package net.minecraft.village;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Objects;
import net.minecraft.util.DynamicSerializable;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class PointOfInterest implements DynamicSerializable {
	private final BlockPos pos;
	private final PointOfInterestType type;
	private int freeTickets;
	private final Runnable updateListener;

	private PointOfInterest(BlockPos blockPos, PointOfInterestType pointOfInterestType, int i, Runnable runnable) {
		this.pos = blockPos;
		this.type = pointOfInterestType;
		this.freeTickets = i;
		this.updateListener = runnable;
	}

	public PointOfInterest(BlockPos blockPos, PointOfInterestType pointOfInterestType, Runnable runnable) {
		this(blockPos, pointOfInterestType, pointOfInterestType.getTicketCount(), runnable);
	}

	public <T> PointOfInterest(Dynamic<T> dynamic, Runnable runnable) {
		this(
			(BlockPos)dynamic.get("pos").map(BlockPos::method_19438).orElse(new BlockPos(0, 0, 0)),
			Registry.POINT_OF_INTEREST_TYPE.get(new Identifier(dynamic.get("type").asString(""))),
			dynamic.get("free_tickets").asInt(0),
			runnable
		);
	}

	@Override
	public <T> T serialize(DynamicOps<T> dynamicOps) {
		return dynamicOps.createMap(
			ImmutableMap.of(
				dynamicOps.createString("pos"),
				this.pos.serialize(dynamicOps),
				dynamicOps.createString("type"),
				dynamicOps.createString(Registry.POINT_OF_INTEREST_TYPE.getId(this.type).toString()),
				dynamicOps.createString("free_tickets"),
				dynamicOps.createInt(this.freeTickets)
			)
		);
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

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else {
			return object != null && this.getClass() == object.getClass() ? Objects.equals(this.pos, ((PointOfInterest)object).pos) : false;
		}
	}

	public int hashCode() {
		return this.pos.hashCode();
	}
}
