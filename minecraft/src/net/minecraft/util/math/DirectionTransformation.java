package net.minecraft.util.math;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.enums.JigsawOrientation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;

public enum DirectionTransformation implements StringIdentifiable {
	field_23292("identity", AxisTransformation.field_23362, false, false, false),
	field_23299("rot_180_face_xy", AxisTransformation.field_23362, true, true, false),
	field_23300("rot_180_face_xz", AxisTransformation.field_23362, true, false, true),
	field_23301("rot_180_face_yz", AxisTransformation.field_23362, false, true, true),
	field_23302("rot_120_nnn", AxisTransformation.field_23365, false, false, false),
	field_23303("rot_120_nnp", AxisTransformation.field_23366, true, false, true),
	field_23304("rot_120_npn", AxisTransformation.field_23366, false, true, true),
	field_23305("rot_120_npp", AxisTransformation.field_23365, true, false, true),
	field_23306("rot_120_pnn", AxisTransformation.field_23366, true, true, false),
	field_23307("rot_120_pnp", AxisTransformation.field_23365, true, true, false),
	field_23308("rot_120_ppn", AxisTransformation.field_23365, false, true, true),
	field_23309("rot_120_ppp", AxisTransformation.field_23366, false, false, false),
	field_23310("rot_180_edge_xy_neg", AxisTransformation.field_23363, true, true, true),
	field_23311("rot_180_edge_xy_pos", AxisTransformation.field_23363, false, false, true),
	field_23312("rot_180_edge_xz_neg", AxisTransformation.field_23367, true, true, true),
	field_23313("rot_180_edge_xz_pos", AxisTransformation.field_23367, false, true, false),
	field_23314("rot_180_edge_yz_neg", AxisTransformation.field_23364, true, true, true),
	field_23315("rot_180_edge_yz_pos", AxisTransformation.field_23364, true, false, false),
	field_23316("rot_90_x_neg", AxisTransformation.field_23364, false, false, true),
	field_23317("rot_90_x_pos", AxisTransformation.field_23364, false, true, false),
	field_23318("rot_90_y_neg", AxisTransformation.field_23367, true, false, false),
	field_23319("rot_90_y_pos", AxisTransformation.field_23367, false, false, true),
	field_23320("rot_90_z_neg", AxisTransformation.field_23363, false, true, false),
	field_23321("rot_90_z_pos", AxisTransformation.field_23363, true, false, false),
	field_23322("inversion", AxisTransformation.field_23362, true, true, true),
	field_23323("invert_x", AxisTransformation.field_23362, true, false, false),
	field_23266("invert_y", AxisTransformation.field_23362, false, true, false),
	field_23267("invert_z", AxisTransformation.field_23362, false, false, true),
	field_23268("rot_60_ref_nnn", AxisTransformation.field_23366, true, true, true),
	field_23269("rot_60_ref_nnp", AxisTransformation.field_23365, true, false, false),
	field_23270("rot_60_ref_npn", AxisTransformation.field_23365, false, false, true),
	field_23271("rot_60_ref_npp", AxisTransformation.field_23366, false, false, true),
	field_23272("rot_60_ref_pnn", AxisTransformation.field_23365, false, true, false),
	field_23273("rot_60_ref_pnp", AxisTransformation.field_23366, true, false, false),
	field_23274("rot_60_ref_ppn", AxisTransformation.field_23366, false, true, false),
	field_23275("rot_60_ref_ppp", AxisTransformation.field_23365, true, true, true),
	field_23276("swap_xy", AxisTransformation.field_23363, false, false, false),
	field_23277("swap_yz", AxisTransformation.field_23364, false, false, false),
	field_23278("swap_xz", AxisTransformation.field_23367, false, false, false),
	field_23279("swap_neg_xy", AxisTransformation.field_23363, true, true, false),
	field_23280("swap_neg_yz", AxisTransformation.field_23364, false, true, true),
	field_23281("swap_neg_xz", AxisTransformation.field_23367, true, false, true),
	field_23282("rot_90_ref_x_neg", AxisTransformation.field_23364, true, false, true),
	field_23283("rot_90_ref_x_pos", AxisTransformation.field_23364, true, true, false),
	field_23284("rot_90_ref_y_neg", AxisTransformation.field_23367, true, true, false),
	field_23285("rot_90_ref_y_pos", AxisTransformation.field_23367, false, true, true),
	field_23286("rot_90_ref_z_neg", AxisTransformation.field_23363, false, true, true),
	field_23287("rot_90_ref_z_pos", AxisTransformation.field_23363, true, false, true);

	private final Matrix3f matrix;
	private final String name;
	@Nullable
	private Map<Direction, Direction> mappings;
	private final boolean flipX;
	private final boolean flipY;
	private final boolean flipZ;
	private final AxisTransformation axisTransformation;
	private static final DirectionTransformation[][] COMBINATIONS = Util.make(
		new DirectionTransformation[values().length][values().length],
		directionTransformations -> {
			Map<Pair<AxisTransformation, BooleanList>, DirectionTransformation> map = (Map<Pair<AxisTransformation, BooleanList>, DirectionTransformation>)Arrays.stream(
					values()
				)
				.collect(
					Collectors.toMap(
						directionTransformationx -> Pair.of(directionTransformationx.axisTransformation, directionTransformationx.getAxisFlips()),
						directionTransformationx -> directionTransformationx
					)
				);

			for (DirectionTransformation directionTransformation : values()) {
				for (DirectionTransformation directionTransformation2 : values()) {
					BooleanList booleanList = directionTransformation.getAxisFlips();
					BooleanList booleanList2 = directionTransformation2.getAxisFlips();
					AxisTransformation axisTransformation = directionTransformation2.axisTransformation.prepend(directionTransformation.axisTransformation);
					BooleanArrayList booleanArrayList = new BooleanArrayList(3);

					for (int i = 0; i < 3; i++) {
						booleanArrayList.add(booleanList.getBoolean(i) ^ booleanList2.getBoolean(directionTransformation.axisTransformation.map(i)));
					}

					directionTransformations[directionTransformation.ordinal()][directionTransformation2.ordinal()] = (DirectionTransformation)map.get(
						Pair.of(axisTransformation, booleanArrayList)
					);
				}
			}
		}
	);
	private static final DirectionTransformation[] INVERSES = (DirectionTransformation[])Arrays.stream(values())
		.map(
			directionTransformation -> (DirectionTransformation)Arrays.stream(values())
					.filter(directionTransformation2 -> directionTransformation.prepend(directionTransformation2) == field_23292)
					.findAny()
					.get()
		)
		.toArray(DirectionTransformation[]::new);

	private DirectionTransformation(String name, AxisTransformation axisTransformation, boolean flipX, boolean flipY, boolean flipZ) {
		this.name = name;
		this.flipX = flipX;
		this.flipY = flipY;
		this.flipZ = flipZ;
		this.axisTransformation = axisTransformation;
		this.matrix = new Matrix3f();
		this.matrix.a00 = flipX ? -1.0F : 1.0F;
		this.matrix.a11 = flipY ? -1.0F : 1.0F;
		this.matrix.a22 = flipZ ? -1.0F : 1.0F;
		this.matrix.multiply(axisTransformation.getMatrix());
	}

	private BooleanList getAxisFlips() {
		return new BooleanArrayList(new boolean[]{this.flipX, this.flipY, this.flipZ});
	}

	public DirectionTransformation prepend(DirectionTransformation transformation) {
		return COMBINATIONS[this.ordinal()][transformation.ordinal()];
	}

	public String toString() {
		return this.name;
	}

	@Override
	public String asString() {
		return this.name;
	}

	public Direction map(Direction direction) {
		if (this.mappings == null) {
			this.mappings = Maps.newEnumMap(Direction.class);

			for (Direction direction2 : Direction.values()) {
				Direction.Axis axis = direction2.getAxis();
				Direction.AxisDirection axisDirection = direction2.getDirection();
				Direction.Axis axis2 = Direction.Axis.values()[this.axisTransformation.map(axis.ordinal())];
				Direction.AxisDirection axisDirection2 = this.shouldFlipDirection(axis2) ? axisDirection.getOpposite() : axisDirection;
				Direction direction3 = Direction.from(axis2, axisDirection2);
				this.mappings.put(direction2, direction3);
			}
		}

		return (Direction)this.mappings.get(direction);
	}

	public boolean shouldFlipDirection(Direction.Axis axis) {
		switch (axis) {
			case field_11048:
				return this.flipX;
			case field_11052:
				return this.flipY;
			case field_11051:
			default:
				return this.flipZ;
		}
	}

	public JigsawOrientation mapJigsawOrientation(JigsawOrientation orientation) {
		return JigsawOrientation.byDirections(this.map(orientation.getFacing()), this.map(orientation.getRotation()));
	}
}
