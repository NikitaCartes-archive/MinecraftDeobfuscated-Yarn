package net.minecraft;

import java.util.BitSet;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class class_854 {
	private static final int field_4491 = Direction.values().length;
	private final BitSet field_4492 = new BitSet(field_4491 * field_4491);

	public void method_3693(Set<Direction> set) {
		for (Direction direction : set) {
			for (Direction direction2 : set) {
				this.method_3692(direction, direction2, true);
			}
		}
	}

	public void method_3692(Direction direction, Direction direction2, boolean bl) {
		this.field_4492.set(direction.ordinal() + direction2.ordinal() * field_4491, bl);
		this.field_4492.set(direction2.ordinal() + direction.ordinal() * field_4491, bl);
	}

	public void method_3694(boolean bl) {
		this.field_4492.set(0, this.field_4492.size(), bl);
	}

	public boolean method_3695(Direction direction, Direction direction2) {
		return this.field_4492.get(direction.ordinal() + direction2.ordinal() * field_4491);
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
					boolean bl = this.method_3695(direction, direction2);
					stringBuilder.append(' ').append((char)(bl ? 'Y' : 'n'));
				}
			}

			stringBuilder.append('\n');
		}

		return stringBuilder.toString();
	}
}
