package net.minecraft.server.world;

import java.lang.runtime.ObjectMethods;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public final class BlockEvent extends Record {
	private final BlockPos pos;
	private final Block block;
	private final int type;
	private final int data;

	public BlockEvent(BlockPos pos, Block block, int type, int data) {
		this.pos = pos;
		this.block = block;
		this.type = type;
		this.data = data;
	}

	public final String toString() {
		return ObjectMethods.bootstrap<"toString",BlockEvent,"pos;block;paramA;paramB",BlockEvent::pos,BlockEvent::block,BlockEvent::type,BlockEvent::data>(this);
	}

	public final int hashCode() {
		return ObjectMethods.bootstrap<"hashCode",BlockEvent,"pos;block;paramA;paramB",BlockEvent::pos,BlockEvent::block,BlockEvent::type,BlockEvent::data>(this);
	}

	public final boolean equals(Object object) {
		return ObjectMethods.bootstrap<"equals",BlockEvent,"pos;block;paramA;paramB",BlockEvent::pos,BlockEvent::block,BlockEvent::type,BlockEvent::data>(
			this, object
		);
	}

	public BlockPos pos() {
		return this.pos;
	}

	public Block block() {
		return this.block;
	}

	public int type() {
		return this.type;
	}

	public int data() {
		return this.data;
	}
}
