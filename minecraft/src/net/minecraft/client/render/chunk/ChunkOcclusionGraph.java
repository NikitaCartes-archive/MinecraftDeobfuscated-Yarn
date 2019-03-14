package net.minecraft.client.render.chunk;

import java.util.BitSet;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class ChunkOcclusionGraph {
	private static final int DIRECTION_COUNT = Direction.values().length;
	private final BitSet visibility = new BitSet(DIRECTION_COUNT * DIRECTION_COUNT);

	public void addOpenEdgeFaces(Set<Direction> set) {
		for (Direction direction : set) {
			for (Direction direction2 : set) {
				this.setVisibleThrough(direction, direction2, true);
			}
		}
	}

	public void setVisibleThrough(Direction direction, Direction direction2, boolean bl) {
		this.visibility.set(direction.ordinal() + direction2.ordinal() * DIRECTION_COUNT, bl);
		this.visibility.set(direction2.ordinal() + direction.ordinal() * DIRECTION_COUNT, bl);
	}

	public void fill(boolean bl) {
		this.visibility.set(0, this.visibility.size(), bl);
	}

	public boolean isVisibleThrough(Direction direction, Direction direction2) {
		return this.visibility.get(direction.ordinal() + direction2.ordinal() * DIRECTION_COUNT);
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(' ');

		for (Direction direction : Direction.values()) {
			stringBuilder.append(' ').append(direction.toString().toUpperCase().charAt(0));
		}

		stringBuilder.append('\n');

		for (Direction direction : Direction.values()) {
			stringBuilder.append(direction.toString().toUpperCase().charAt(0));

			for (Direction direction2 : Direction.values()) {
				if (direction == direction2) {
					stringBuilder.append("  ");
				} else {
					boolean bl = this.isVisibleThrough(direction, direction2);
					stringBuilder.append(' ').append((char)(bl ? 'Y' : 'n'));
				}
			}

			stringBuilder.append('\n');
		}

		return stringBuilder.toString();
	}
}
