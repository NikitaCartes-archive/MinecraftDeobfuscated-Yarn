package net.minecraft.client.render.chunk;

import java.util.BitSet;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class ChunkOcclusionData {
	private static final int DIRECTION_COUNT = Direction.values().length;
	private final BitSet visibility = new BitSet(DIRECTION_COUNT * DIRECTION_COUNT);

	public void addOpenEdgeFaces(Set<Direction> faces) {
		for (Direction direction : faces) {
			for (Direction direction2 : faces) {
				this.setVisibleThrough(direction, direction2, true);
			}
		}
	}

	public void setVisibleThrough(Direction from, Direction to, boolean visible) {
		this.visibility.set(from.ordinal() + to.ordinal() * DIRECTION_COUNT, visible);
		this.visibility.set(to.ordinal() + from.ordinal() * DIRECTION_COUNT, visible);
	}

	public void fill(boolean visible) {
		this.visibility.set(0, this.visibility.size(), visible);
	}

	public boolean isVisibleThrough(Direction from, Direction to) {
		return this.visibility.get(from.ordinal() + to.ordinal() * DIRECTION_COUNT);
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
