package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;

public enum class_4990 implements StringIdentifiable {
	field_23292("identity", class_4998.field_23362, false, false, false),
	field_23299("rot_180_face_xy", class_4998.field_23362, true, true, false),
	field_23300("rot_180_face_xz", class_4998.field_23362, true, false, true),
	field_23301("rot_180_face_yz", class_4998.field_23362, false, true, true),
	field_23302("rot_120_nnn", class_4998.field_23365, false, false, false),
	field_23303("rot_120_nnp", class_4998.field_23366, true, false, true),
	field_23304("rot_120_npn", class_4998.field_23366, false, true, true),
	field_23305("rot_120_npp", class_4998.field_23365, true, false, true),
	field_23306("rot_120_pnn", class_4998.field_23366, true, true, false),
	field_23307("rot_120_pnp", class_4998.field_23365, true, true, false),
	field_23308("rot_120_ppn", class_4998.field_23365, false, true, true),
	field_23309("rot_120_ppp", class_4998.field_23366, false, false, false),
	field_23310("rot_180_edge_xy_neg", class_4998.field_23363, true, true, true),
	field_23311("rot_180_edge_xy_pos", class_4998.field_23363, false, false, true),
	field_23312("rot_180_edge_xz_neg", class_4998.field_23367, true, true, true),
	field_23313("rot_180_edge_xz_pos", class_4998.field_23367, false, true, false),
	field_23314("rot_180_edge_yz_neg", class_4998.field_23364, true, true, true),
	field_23315("rot_180_edge_yz_pos", class_4998.field_23364, true, false, false),
	field_23316("rot_90_x_neg", class_4998.field_23364, false, false, true),
	field_23317("rot_90_x_pos", class_4998.field_23364, false, true, false),
	field_23318("rot_90_y_neg", class_4998.field_23367, true, false, false),
	field_23319("rot_90_y_pos", class_4998.field_23367, false, false, true),
	field_23320("rot_90_z_neg", class_4998.field_23363, false, true, false),
	field_23321("rot_90_z_pos", class_4998.field_23363, true, false, false),
	field_23322("inversion", class_4998.field_23362, true, true, true),
	field_23323("invert_x", class_4998.field_23362, true, false, false),
	field_23266("invert_y", class_4998.field_23362, false, true, false),
	field_23267("invert_z", class_4998.field_23362, false, false, true),
	field_23268("rot_60_ref_nnn", class_4998.field_23366, true, true, true),
	field_23269("rot_60_ref_nnp", class_4998.field_23365, true, false, false),
	field_23270("rot_60_ref_npn", class_4998.field_23365, false, false, true),
	field_23271("rot_60_ref_npp", class_4998.field_23366, false, false, true),
	field_23272("rot_60_ref_pnn", class_4998.field_23365, false, true, false),
	field_23273("rot_60_ref_pnp", class_4998.field_23366, true, false, false),
	field_23274("rot_60_ref_ppn", class_4998.field_23366, false, true, false),
	field_23275("rot_60_ref_ppp", class_4998.field_23365, true, true, true),
	field_23276("swap_xy", class_4998.field_23363, false, false, false),
	field_23277("swap_yz", class_4998.field_23364, false, false, false),
	field_23278("swap_xz", class_4998.field_23367, false, false, false),
	field_23279("swap_neg_xy", class_4998.field_23363, true, true, false),
	field_23280("swap_neg_yz", class_4998.field_23364, false, true, true),
	field_23281("swap_neg_xz", class_4998.field_23367, true, false, true),
	field_23282("rot_90_ref_x_neg", class_4998.field_23364, true, false, true),
	field_23283("rot_90_ref_x_pos", class_4998.field_23364, true, true, false),
	field_23284("rot_90_ref_y_neg", class_4998.field_23367, true, true, false),
	field_23285("rot_90_ref_y_pos", class_4998.field_23367, false, true, true),
	field_23286("rot_90_ref_z_neg", class_4998.field_23363, false, true, true),
	field_23287("rot_90_ref_z_pos", class_4998.field_23363, true, false, true);

	private final Matrix3f field_23288;
	private final String field_23289;
	@Nullable
	private Map<Direction, Direction> field_23290;
	private final boolean field_23291;
	private final boolean field_23293;
	private final boolean field_23294;
	private final class_4998 field_23295;
	private static final class_4990[][] field_23296 = Util.make(
		new class_4990[values().length][values().length],
		args -> {
			Map<Pair<class_4998, BooleanList>, class_4990> map = (Map<Pair<class_4998, BooleanList>, class_4990>)Arrays.stream(values())
				.collect(Collectors.toMap(arg -> Pair.of(arg.field_23295, arg.method_26391()), arg -> arg));

			for (class_4990 lv : values()) {
				for (class_4990 lv2 : values()) {
					BooleanList booleanList = lv.method_26391();
					BooleanList booleanList2 = lv2.method_26391();
					class_4998 lv3 = lv2.field_23295.method_26418(lv.field_23295);
					BooleanArrayList booleanArrayList = new BooleanArrayList(3);

					for (int i = 0; i < 3; i++) {
						booleanArrayList.add(booleanList.getBoolean(i) ^ booleanList2.getBoolean(lv.field_23295.method_26417(i)));
					}

					args[lv.ordinal()][lv2.ordinal()] = (class_4990)map.get(Pair.of(lv3, booleanArrayList));
				}
			}
		}
	);
	private static final class_4990[] field_23297 = (class_4990[])Arrays.stream(values())
		.map(arg -> (class_4990)Arrays.stream(values()).filter(arg2 -> arg.method_26385(arg2) == field_23292).findAny().get())
		.toArray(class_4990[]::new);

	private class_4990(String string2, class_4998 arg, boolean bl, boolean bl2, boolean bl3) {
		this.field_23289 = string2;
		this.field_23291 = bl;
		this.field_23293 = bl2;
		this.field_23294 = bl3;
		this.field_23295 = arg;
		this.field_23288 = new Matrix3f();
		this.field_23288.a00 = bl ? -1.0F : 1.0F;
		this.field_23288.a11 = bl2 ? -1.0F : 1.0F;
		this.field_23288.a22 = bl3 ? -1.0F : 1.0F;
		this.field_23288.multiply(arg.method_26416());
	}

	private BooleanList method_26391() {
		return new BooleanArrayList(new boolean[]{this.field_23291, this.field_23293, this.field_23294});
	}

	public class_4990 method_26385(class_4990 arg) {
		return field_23296[this.ordinal()][arg.ordinal()];
	}

	public String toString() {
		return this.field_23289;
	}

	@Override
	public String asString() {
		return this.field_23289;
	}

	public Direction method_26388(Direction direction) {
		if (this.field_23290 == null) {
			this.field_23290 = Maps.newEnumMap(Direction.class);

			for (Direction direction2 : Direction.values()) {
				Direction.Axis axis = direction2.getAxis();
				Direction.AxisDirection axisDirection = direction2.getDirection();
				Direction.Axis axis2 = Direction.Axis.values()[this.field_23295.method_26417(axis.ordinal())];
				Direction.AxisDirection axisDirection2 = this.method_26387(axis2) ? axisDirection.method_26424() : axisDirection;
				Direction direction3 = Direction.from(axis2, axisDirection2);
				this.field_23290.put(direction2, direction3);
			}
		}

		return (Direction)this.field_23290.get(direction);
	}

	public boolean method_26387(Direction.Axis axis) {
		switch (axis) {
			case X:
				return this.field_23291;
			case Y:
				return this.field_23293;
			case Z:
			default:
				return this.field_23294;
		}
	}

	public class_5000 method_26389(class_5000 arg) {
		return class_5000.method_26425(this.method_26388(arg.method_26426()), this.method_26388(arg.method_26428()));
	}
}
