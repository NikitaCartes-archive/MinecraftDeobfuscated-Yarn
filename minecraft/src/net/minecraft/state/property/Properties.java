package net.minecraft.state.property;

import java.util.function.Predicate;
import net.minecraft.block.enums.Attachment;
import net.minecraft.block.enums.BambooLeaves;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.ChestType;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.Instrument;
import net.minecraft.block.enums.PistonType;
import net.minecraft.block.enums.RailShape;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.enums.StairShape;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.util.math.Direction;

public class Properties {
	public static final BooleanProperty ATTACHED = BooleanProperty.create("attached");
	public static final BooleanProperty BOTTOM = BooleanProperty.create("bottom");
	public static final BooleanProperty CONDITIONAL = BooleanProperty.create("conditional");
	public static final BooleanProperty DISARMED = BooleanProperty.create("disarmed");
	public static final BooleanProperty DRAG = BooleanProperty.create("drag");
	public static final BooleanProperty ENABLED = BooleanProperty.create("enabled");
	public static final BooleanProperty EXTENDED = BooleanProperty.create("extended");
	public static final BooleanProperty EYE = BooleanProperty.create("eye");
	public static final BooleanProperty FALLING = BooleanProperty.create("falling");
	public static final BooleanProperty HANGING = BooleanProperty.create("hanging");
	public static final BooleanProperty HAS_BOTTLE_0 = BooleanProperty.create("has_bottle_0");
	public static final BooleanProperty HAS_BOTTLE_1 = BooleanProperty.create("has_bottle_1");
	public static final BooleanProperty HAS_BOTTLE_2 = BooleanProperty.create("has_bottle_2");
	public static final BooleanProperty HAS_RECORD = BooleanProperty.create("has_record");
	public static final BooleanProperty HAS_BOOK = BooleanProperty.create("has_book");
	public static final BooleanProperty INVERTED = BooleanProperty.create("inverted");
	public static final BooleanProperty IN_WALL = BooleanProperty.create("in_wall");
	public static final BooleanProperty LIT = BooleanProperty.create("lit");
	public static final BooleanProperty LOCKED = BooleanProperty.create("locked");
	public static final BooleanProperty OCCUPIED = BooleanProperty.create("occupied");
	public static final BooleanProperty OPEN = BooleanProperty.create("open");
	public static final BooleanProperty PERSISTENT = BooleanProperty.create("persistent");
	public static final BooleanProperty POWERED = BooleanProperty.create("powered");
	public static final BooleanProperty SHORT = BooleanProperty.create("short");
	public static final BooleanProperty SIGNAL_FIRE = BooleanProperty.create("signal_fire");
	public static final BooleanProperty SNOWY = BooleanProperty.create("snowy");
	public static final BooleanProperty TRIGGERED = BooleanProperty.create("triggered");
	public static final BooleanProperty UNSTABLE = BooleanProperty.create("unstable");
	public static final BooleanProperty WATERLOGGED = BooleanProperty.create("waterlogged");
	public static final EnumProperty<Direction.Axis> AXIS_XZ = EnumProperty.create("axis", Direction.Axis.class, Direction.Axis.X, Direction.Axis.Z);
	public static final EnumProperty<Direction.Axis> AXIS_XYZ = EnumProperty.create("axis", Direction.Axis.class);
	public static final BooleanProperty UP_BOOL = BooleanProperty.create("up");
	public static final BooleanProperty DOWN_BOOL = BooleanProperty.create("down");
	public static final BooleanProperty NORTH_BOOL = BooleanProperty.create("north");
	public static final BooleanProperty EAST_BOOL = BooleanProperty.create("east");
	public static final BooleanProperty SOUTH_BOOL = BooleanProperty.create("south");
	public static final BooleanProperty WEST_BOOL = BooleanProperty.create("west");
	public static final DirectionProperty FACING = DirectionProperty.create(
		"facing", Direction.field_11043, Direction.field_11034, Direction.field_11035, Direction.field_11039, Direction.field_11036, Direction.field_11033
	);
	public static final DirectionProperty HOPPER_FACING = DirectionProperty.create(
		"facing", (Predicate<Direction>)(direction -> direction != Direction.field_11036)
	);
	public static final DirectionProperty FACING_HORIZONTAL = DirectionProperty.create("facing", Direction.Type.field_11062);
	public static final EnumProperty<WallMountLocation> WALL_MOUNT_LOCATION = EnumProperty.create("face", WallMountLocation.class);
	public static final EnumProperty<Attachment> ATTACHMENT = EnumProperty.create("attachment", Attachment.class);
	public static final EnumProperty<WireConnection> WIRE_CONNECTION_EAST = EnumProperty.create("east", WireConnection.class);
	public static final EnumProperty<WireConnection> WIRE_CONNECTION_NORTH = EnumProperty.create("north", WireConnection.class);
	public static final EnumProperty<WireConnection> WIRE_CONNECTION_SOUTH = EnumProperty.create("south", WireConnection.class);
	public static final EnumProperty<WireConnection> WIRE_CONNECTION_WEST = EnumProperty.create("west", WireConnection.class);
	public static final EnumProperty<DoubleBlockHalf> DOUBLE_BLOCK_HALF = EnumProperty.create("half", DoubleBlockHalf.class);
	public static final EnumProperty<BlockHalf> BLOCK_HALF = EnumProperty.create("half", BlockHalf.class);
	public static final EnumProperty<RailShape> RAIL_SHAPE = EnumProperty.create("shape", RailShape.class);
	public static final EnumProperty<RailShape> STRAIGHT_RAIL_SHAPE = EnumProperty.create(
		"shape",
		RailShape.class,
		(Predicate)(railShape -> railShape != RailShape.field_12663
				&& railShape != RailShape.field_12672
				&& railShape != RailShape.field_12664
				&& railShape != RailShape.field_12671)
	);
	public static final IntegerProperty AGE_1 = IntegerProperty.create("age", 0, 1);
	public static final IntegerProperty AGE_2 = IntegerProperty.create("age", 0, 2);
	public static final IntegerProperty AGE_3 = IntegerProperty.create("age", 0, 3);
	public static final IntegerProperty AGE_5 = IntegerProperty.create("age", 0, 5);
	public static final IntegerProperty AGE_7 = IntegerProperty.create("age", 0, 7);
	public static final IntegerProperty AGE_15 = IntegerProperty.create("age", 0, 15);
	public static final IntegerProperty AGE_25 = IntegerProperty.create("age", 0, 25);
	public static final IntegerProperty BITES = IntegerProperty.create("bites", 0, 6);
	public static final IntegerProperty DELAY = IntegerProperty.create("delay", 1, 4);
	public static final IntegerProperty DISTANCE_1_7 = IntegerProperty.create("distance", 1, 7);
	public static final IntegerProperty EGGS = IntegerProperty.create("eggs", 1, 4);
	public static final IntegerProperty HATCH = IntegerProperty.create("hatch", 0, 2);
	public static final IntegerProperty LAYERS = IntegerProperty.create("layers", 1, 8);
	public static final IntegerProperty CAULDRON_LEVEL = IntegerProperty.create("level", 0, 3);
	public static final IntegerProperty COMPOSTER_LEVEL = IntegerProperty.create("level", 0, 8);
	public static final IntegerProperty FLUID_LEVEL = IntegerProperty.create("level", 1, 8);
	public static final IntegerProperty FLUID_BLOCK_LEVEL = IntegerProperty.create("level", 0, 15);
	public static final IntegerProperty MOISTURE = IntegerProperty.create("moisture", 0, 7);
	public static final IntegerProperty NOTE = IntegerProperty.create("note", 0, 24);
	public static final IntegerProperty PICKLES = IntegerProperty.create("pickles", 1, 4);
	public static final IntegerProperty POWER = IntegerProperty.create("power", 0, 15);
	public static final IntegerProperty SAPLING_STAGE = IntegerProperty.create("stage", 0, 1);
	public static final IntegerProperty DISTANCE_0_7 = IntegerProperty.create("distance", 0, 7);
	public static final IntegerProperty ROTATION_16 = IntegerProperty.create("rotation", 0, 15);
	public static final EnumProperty<BedPart> BED_PART = EnumProperty.create("part", BedPart.class);
	public static final EnumProperty<ChestType> CHEST_TYPE = EnumProperty.create("type", ChestType.class);
	public static final EnumProperty<ComparatorMode> COMPARATOR_MODE = EnumProperty.create("mode", ComparatorMode.class);
	public static final EnumProperty<DoorHinge> DOOR_HINGE = EnumProperty.create("hinge", DoorHinge.class);
	public static final EnumProperty<Instrument> INSTRUMENT = EnumProperty.create("instrument", Instrument.class);
	public static final EnumProperty<PistonType> PISTON_TYPE = EnumProperty.create("type", PistonType.class);
	public static final EnumProperty<SlabType> SLAB_TYPE = EnumProperty.create("type", SlabType.class);
	public static final EnumProperty<StairShape> STAIR_SHAPE = EnumProperty.create("shape", StairShape.class);
	public static final EnumProperty<StructureBlockMode> STRUCTURE_BLOCK_MODE = EnumProperty.create("mode", StructureBlockMode.class);
	public static final EnumProperty<BambooLeaves> BAMBOO_LEAVES = EnumProperty.create("leaves", BambooLeaves.class);
}
