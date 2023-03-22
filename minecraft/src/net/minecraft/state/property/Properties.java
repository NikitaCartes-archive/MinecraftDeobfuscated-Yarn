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
import net.minecraft.block.enums.JigsawOrientation;
import net.minecraft.block.enums.PistonType;
import net.minecraft.block.enums.RailShape;
import net.minecraft.block.enums.SculkSensorPhase;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.enums.StairShape;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.block.enums.Thickness;
import net.minecraft.block.enums.Tilt;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.block.enums.WallShape;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationPropertyHelper;

/**
 * Contains all block and fluid state properties that Minecraft uses.
 */
public class Properties {
	/**
	 * A property that specifies if a tripwire is attached to a tripwire hook.
	 */
	public static final BooleanProperty ATTACHED = BooleanProperty.of("attached");
	/**
	 * A property that specifies if a scaffolding block is bottom of a floating segment.
	 */
	public static final BooleanProperty BOTTOM = BooleanProperty.of("bottom");
	/**
	 * A property that specifies if a command block is conditional.
	 */
	public static final BooleanProperty CONDITIONAL = BooleanProperty.of("conditional");
	/**
	 * A property that specifies if a tripwire has been disarmed.
	 */
	public static final BooleanProperty DISARMED = BooleanProperty.of("disarmed");
	/**
	 * A property that specifies if a bubble column should drag entities downwards.
	 */
	public static final BooleanProperty DRAG = BooleanProperty.of("drag");
	/**
	 * A property that specifies whether a hopper is enabled.
	 */
	public static final BooleanProperty ENABLED = BooleanProperty.of("enabled");
	/**
	 * A property that specifies if a piston is extended.
	 */
	public static final BooleanProperty EXTENDED = BooleanProperty.of("extended");
	/**
	 * A property that specifies if an end portal frame contains an eye of ender.
	 */
	public static final BooleanProperty EYE = BooleanProperty.of("eye");
	/**
	 * A property that specifies if a fluid is falling.
	 */
	public static final BooleanProperty FALLING = BooleanProperty.of("falling");
	/**
	 * A property that specifies if a lantern is hanging.
	 */
	public static final BooleanProperty HANGING = BooleanProperty.of("hanging");
	/**
	 * A property that specifies if a brewing stand has a bottle in slot 0.
	 */
	public static final BooleanProperty HAS_BOTTLE_0 = BooleanProperty.of("has_bottle_0");
	/**
	 * A property that specifies if a brewing stand has a bottle in slot 1.
	 */
	public static final BooleanProperty HAS_BOTTLE_1 = BooleanProperty.of("has_bottle_1");
	/**
	 * A property that specifies if a brewing stand has a bottle in slot 2.
	 */
	public static final BooleanProperty HAS_BOTTLE_2 = BooleanProperty.of("has_bottle_2");
	/**
	 * A property that specifies if a jukebox has a record.
	 */
	public static final BooleanProperty HAS_RECORD = BooleanProperty.of("has_record");
	/**
	 * A property that specifies if a lectern has a book.
	 */
	public static final BooleanProperty HAS_BOOK = BooleanProperty.of("has_book");
	/**
	 * A property that specifies if a daylight sensor's output is inverted.
	 */
	public static final BooleanProperty INVERTED = BooleanProperty.of("inverted");
	/**
	 * A property that specifies if a fence gate is attached to a wall.
	 * 
	 * <p>This lowers the fence gate by 3 pixels to attach more cleanly to a wall.
	 */
	public static final BooleanProperty IN_WALL = BooleanProperty.of("in_wall");
	/**
	 * A property that specifies if a block is lit.
	 */
	public static final BooleanProperty LIT = BooleanProperty.of("lit");
	/**
	 * A property that specifies if a repeater is locked.
	 */
	public static final BooleanProperty LOCKED = BooleanProperty.of("locked");
	/**
	 * A property that specifies if a bed is occupied.
	 */
	public static final BooleanProperty OCCUPIED = BooleanProperty.of("occupied");
	/**
	 * A property that specifies if a block is open.
	 * 
	 * <p>This property is normally used for doors, trapdoors and fence gates but is also used by barrels.
	 */
	public static final BooleanProperty OPEN = BooleanProperty.of("open");
	/**
	 * A property that specifies if a block is persistent.
	 * 
	 * <p>In vanilla, this is used to specify whether leaves should disappear when the logs are removed.
	 */
	public static final BooleanProperty PERSISTENT = BooleanProperty.of("persistent");
	/**
	 * A property that specifies if a block is being powered to produce or emit redstone signal.
	 */
	public static final BooleanProperty POWERED = BooleanProperty.of("powered");
	/**
	 * A property that specifies if a piston head is shorter than normal.
	 */
	public static final BooleanProperty SHORT = BooleanProperty.of("short");
	/**
	 * A property that specifies if a campfire's smoke should be taller.
	 * 
	 * <p>This occurs when a hay bale is placed under the campfire.
	 */
	public static final BooleanProperty SIGNAL_FIRE = BooleanProperty.of("signal_fire");
	/**
	 * A property that specifies if a block is covered in snow.
	 */
	public static final BooleanProperty SNOWY = BooleanProperty.of("snowy");
	/**
	 * A property that specifies if a dispenser is activated.
	 */
	public static final BooleanProperty TRIGGERED = BooleanProperty.of("triggered");
	/**
	 *  A property that specifies if TNT is unstable.
	 * 
	 *  <p>In vanilla, if TNT is unstable, it will ignite when broken.
	 */
	public static final BooleanProperty UNSTABLE = BooleanProperty.of("unstable");
	/**
	 * A property that specifies if a block is waterlogged.
	 */
	public static final BooleanProperty WATERLOGGED = BooleanProperty.of("waterlogged");
	public static final BooleanProperty BERRIES = BooleanProperty.of("berries");
	public static final BooleanProperty BLOOM = BooleanProperty.of("bloom");
	public static final BooleanProperty SHRIEKING = BooleanProperty.of("shrieking");
	public static final BooleanProperty CAN_SUMMON = BooleanProperty.of("can_summon");
	/**
	 *  A property that specifies the axis a block is oriented to.
	 * 
	 * <p>This property only allows a block to be oriented to the X and Z axes.
	 */
	public static final EnumProperty<Direction.Axis> HORIZONTAL_AXIS = EnumProperty.of("axis", Direction.Axis.class, Direction.Axis.X, Direction.Axis.Z);
	/**
	 * A property that specifies the axis a block is oriented to.
	 */
	public static final EnumProperty<Direction.Axis> AXIS = EnumProperty.of("axis", Direction.Axis.class);
	/**
	 * A property that specifies if this block is connected to another block from the top.
	 */
	public static final BooleanProperty UP = BooleanProperty.of("up");
	/**
	 * A property that specifies if this block is connected to another block from the below.
	 */
	public static final BooleanProperty DOWN = BooleanProperty.of("down");
	/**
	 * A property that specifies if this block is connected to another block from the north.
	 */
	public static final BooleanProperty NORTH = BooleanProperty.of("north");
	/**
	 * A property that specifies if this block is connected to another block from the east.
	 */
	public static final BooleanProperty EAST = BooleanProperty.of("east");
	/**
	 * A property that specifies if this block is connected to another block from the south.
	 */
	public static final BooleanProperty SOUTH = BooleanProperty.of("south");
	/**
	 * A property that specifies if this block is connected to another block from the west.
	 */
	public static final BooleanProperty WEST = BooleanProperty.of("west");
	/**
	 * A property that specifies the direction a block is facing.
	 */
	public static final DirectionProperty FACING = DirectionProperty.of(
		"facing", Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN
	);
	/**
	 * A property that specifies the direction a hopper's output faces.
	 * 
	 * <p>This property does not allow the hopper's output to face upwards.
	 */
	public static final DirectionProperty HOPPER_FACING = DirectionProperty.of("facing", (Predicate<Direction>)(facing -> facing != Direction.UP));
	/**
	 * A property that specifies the direction a block is facing.
	 * 
	 * <p>This property only allows a block to face in one of the cardinal directions (north, south, east and west).
	 */
	public static final DirectionProperty HORIZONTAL_FACING = DirectionProperty.of("facing", Direction.Type.HORIZONTAL);
	public static final IntProperty FLOWER_AMOUNT = IntProperty.of("flower_amount", 1, 4);
	/**
	 * A property that specifies the orientation of a jigsaw.
	 */
	public static final EnumProperty<JigsawOrientation> ORIENTATION = EnumProperty.of("orientation", JigsawOrientation.class);
	/**
	 * A property that specifies the type of wall a block is attached to.
	 */
	public static final EnumProperty<WallMountLocation> WALL_MOUNT_LOCATION = EnumProperty.of("face", WallMountLocation.class);
	/**
	 * A property that specifies how a bell is attached to a block.
	 */
	public static final EnumProperty<Attachment> ATTACHMENT = EnumProperty.of("attachment", Attachment.class);
	/**
	 * A property that specifies how a wall extends from the center post to the east.
	 */
	public static final EnumProperty<WallShape> EAST_WALL_SHAPE = EnumProperty.of("east", WallShape.class);
	/**
	 * A property that specifies how a wall extends from the center post to the north.
	 */
	public static final EnumProperty<WallShape> NORTH_WALL_SHAPE = EnumProperty.of("north", WallShape.class);
	/**
	 * A property that specifies how a wall extends from the center post to the south.
	 */
	public static final EnumProperty<WallShape> SOUTH_WALL_SHAPE = EnumProperty.of("south", WallShape.class);
	/**
	 * A property that specifies how a wall extends from the center post to the west.
	 */
	public static final EnumProperty<WallShape> WEST_WALL_SHAPE = EnumProperty.of("west", WallShape.class);
	/**
	 * A property that specifies how redstone wire attaches to the east.
	 */
	public static final EnumProperty<WireConnection> EAST_WIRE_CONNECTION = EnumProperty.of("east", WireConnection.class);
	/**
	 * A property that specifies how redstone wire attaches to the north.
	 */
	public static final EnumProperty<WireConnection> NORTH_WIRE_CONNECTION = EnumProperty.of("north", WireConnection.class);
	/**
	 * A property that specifies how redstone wire attaches to the south.
	 */
	public static final EnumProperty<WireConnection> SOUTH_WIRE_CONNECTION = EnumProperty.of("south", WireConnection.class);
	/**
	 * A property that specifies how redstone wire attaches to the west.
	 */
	public static final EnumProperty<WireConnection> WEST_WIRE_CONNECTION = EnumProperty.of("west", WireConnection.class);
	/**
	 * A property that specifies whether a double height block is the upper or lower half.
	 */
	public static final EnumProperty<DoubleBlockHalf> DOUBLE_BLOCK_HALF = EnumProperty.of("half", DoubleBlockHalf.class);
	/**
	 * A property that specifies if a block is the upper or lower half.
	 */
	public static final EnumProperty<BlockHalf> BLOCK_HALF = EnumProperty.of("half", BlockHalf.class);
	/**
	 * A property that specifies the two directions a rail connects to.
	 */
	public static final EnumProperty<RailShape> RAIL_SHAPE = EnumProperty.of("shape", RailShape.class);
	/**
	 * A property that specifies the two directions a rail connects to.
	 * 
	 * <p>This property does not allow for a rail to turn.
	 */
	public static final EnumProperty<RailShape> STRAIGHT_RAIL_SHAPE = EnumProperty.of(
		"shape",
		RailShape.class,
		(Predicate)(shape -> shape != RailShape.NORTH_EAST && shape != RailShape.NORTH_WEST && shape != RailShape.SOUTH_EAST && shape != RailShape.SOUTH_WEST)
	);
	public static final int AGE_1_MAX = 1;
	public static final int AGE_2_MAX = 2;
	public static final int AGE_3_MAX = 3;
	public static final int AGE_4_MAX = 4;
	public static final int AGE_5_MAX = 5;
	public static final int AGE_7_MAX = 7;
	public static final int AGE_15_MAX = 15;
	public static final int AGE_25_MAX = 25;
	/**
	 * A property that specifies the age of a block on a scale of 0 to 1.
	 */
	public static final IntProperty AGE_1 = IntProperty.of("age", 0, 1);
	/**
	 * A property that specifies the age of a block on a scale of 0 to 2.
	 */
	public static final IntProperty AGE_2 = IntProperty.of("age", 0, 2);
	/**
	 * A property that specifies the age of a block on a scale of 0 to 3.
	 */
	public static final IntProperty AGE_3 = IntProperty.of("age", 0, 3);
	public static final IntProperty AGE_4 = IntProperty.of("age", 0, 4);
	/**
	 * A property that specifies the age of a block on a scale of 0 to 5.
	 */
	public static final IntProperty AGE_5 = IntProperty.of("age", 0, 5);
	/**
	 * A property that specifies the age of a block on a scale of 0 to 7.
	 */
	public static final IntProperty AGE_7 = IntProperty.of("age", 0, 7);
	/**
	 * A property that specifies the age of a block on a scale of 0 to 15.
	 */
	public static final IntProperty AGE_15 = IntProperty.of("age", 0, 15);
	/**
	 * A property that specifies the age of a block on a scale of 0 to 25.
	 */
	public static final IntProperty AGE_25 = IntProperty.of("age", 0, 25);
	/**
	 * A property that specifies the bites taken out of a cake.
	 */
	public static final IntProperty BITES = IntProperty.of("bites", 0, 6);
	/**
	 * A property that specifies the amount of candles in a candle block.
	 */
	public static final IntProperty CANDLES = IntProperty.of("candles", 1, 4);
	/**
	 * A property that specifies the delay a repeater will apply.
	 */
	public static final IntProperty DELAY = IntProperty.of("delay", 1, 4);
	public static final int DISTANCE_1_7_MAX = 7;
	/**
	 * A property that specifies the overhang distance of a block on a scale of 1-7.
	 */
	public static final IntProperty DISTANCE_1_7 = IntProperty.of("distance", 1, 7);
	/**
	 * A property that specifies the amount of eggs in a turtle egg block.
	 */
	public static final IntProperty EGGS = IntProperty.of("eggs", 1, 4);
	/**
	 * A property that specifies how close an egg is hatching.
	 */
	public static final IntProperty HATCH = IntProperty.of("hatch", 0, 2);
	/**
	 * A property that specifies how many layers of snow are in a snow block.
	 */
	public static final IntProperty LAYERS = IntProperty.of("layers", 1, 8);
	public static final int LEVEL_3_MIN = 0;
	public static final int LEVEL_1_8_MIN = 1;
	public static final int LEVEL_3_MAX = 3;
	public static final int LEVEL_1_8_MAX = 8;
	/**
	 * A property that specifies how many levels of water there are in a cauldron.
	 */
	public static final IntProperty LEVEL_3 = IntProperty.of("level", 1, 3);
	/**
	 * A property that specifies the level of a composter.
	 */
	public static final IntProperty LEVEL_8 = IntProperty.of("level", 0, 8);
	/**
	 * A property that specifies the height of a fluid on a scale of 1 to 8.
	 */
	public static final IntProperty LEVEL_1_8 = IntProperty.of("level", 1, 8);
	/**
	 * A property that specifies the honey level of a beehive.
	 */
	public static final IntProperty HONEY_LEVEL = IntProperty.of("honey_level", 0, 5);
	public static final int LEVEL_15_MAX = 15;
	public static final IntProperty LEVEL_15 = IntProperty.of("level", 0, 15);
	/**
	 * A property that specifies the moisture of farmland.
	 */
	public static final IntProperty MOISTURE = IntProperty.of("moisture", 0, 7);
	/**
	 * A property that specifies the pitch of a note block.
	 */
	public static final IntProperty NOTE = IntProperty.of("note", 0, 24);
	/**
	 * A property that specifies how many pickles are in a sea pickle.
	 */
	public static final IntProperty PICKLES = IntProperty.of("pickles", 1, 4);
	/**
	 * A property that specifies the redstone power of a block.
	 */
	public static final IntProperty POWER = IntProperty.of("power", 0, 15);
	/**
	 * A property that specifies a growth stage on a scale of 0 to 1.
	 */
	public static final IntProperty STAGE = IntProperty.of("stage", 0, 1);
	public static final int DISTANCE_0_7_MAX = 7;
	/**
	 * A property that specifies the overhang distance of a scaffolding.
	 */
	public static final IntProperty DISTANCE_0_7 = IntProperty.of("distance", 0, 7);
	public static final int CHARGES_MIN = 0;
	public static final int CHARGES_MAX = 4;
	/**
	 * A property that specifies the amount of charges a respawn anchor has.
	 */
	public static final IntProperty CHARGES = IntProperty.of("charges", 0, 4);
	/**
	 * A property that specifies the rotation of a block on a 0 to 15 scale.
	 * 
	 * <p>Each rotation is 22.5 degrees.
	 */
	public static final IntProperty ROTATION = IntProperty.of("rotation", 0, RotationPropertyHelper.getMax());
	/**
	 * A property that specifies what part of a bed a block is.
	 */
	public static final EnumProperty<BedPart> BED_PART = EnumProperty.of("part", BedPart.class);
	/**
	 * A property that specifies what type of chest a block is.
	 */
	public static final EnumProperty<ChestType> CHEST_TYPE = EnumProperty.of("type", ChestType.class);
	/**
	 * A property that specifies the mode a comparator is set to.
	 */
	public static final EnumProperty<ComparatorMode> COMPARATOR_MODE = EnumProperty.of("mode", ComparatorMode.class);
	/**
	 * A property that specifies whether a door's hinge is to the right or left.
	 */
	public static final EnumProperty<DoorHinge> DOOR_HINGE = EnumProperty.of("hinge", DoorHinge.class);
	/**
	 * A property that specifies what instrument a note block will play.
	 */
	public static final EnumProperty<Instrument> INSTRUMENT = EnumProperty.of("instrument", Instrument.class);
	/**
	 * A property that specifies the type of a piston.
	 */
	public static final EnumProperty<PistonType> PISTON_TYPE = EnumProperty.of("type", PistonType.class);
	/**
	 * A property that specifies the type of slab.
	 */
	public static final EnumProperty<SlabType> SLAB_TYPE = EnumProperty.of("type", SlabType.class);
	/**
	 * A property that specifies the shape of a stair block.
	 */
	public static final EnumProperty<StairShape> STAIR_SHAPE = EnumProperty.of("shape", StairShape.class);
	/**
	 * A property that specifies the mode of a structure block.
	 */
	public static final EnumProperty<StructureBlockMode> STRUCTURE_BLOCK_MODE = EnumProperty.of("mode", StructureBlockMode.class);
	/**
	 * A property that specifies the size of bamboo leaves.
	 */
	public static final EnumProperty<BambooLeaves> BAMBOO_LEAVES = EnumProperty.of("leaves", BambooLeaves.class);
	public static final EnumProperty<Tilt> TILT = EnumProperty.of("tilt", Tilt.class);
	public static final DirectionProperty VERTICAL_DIRECTION = DirectionProperty.of("vertical_direction", Direction.UP, Direction.DOWN);
	public static final EnumProperty<Thickness> THICKNESS = EnumProperty.of("thickness", Thickness.class);
	public static final EnumProperty<SculkSensorPhase> SCULK_SENSOR_PHASE = EnumProperty.of("sculk_sensor_phase", SculkSensorPhase.class);
	public static final BooleanProperty SLOT_0_OCCUPIED = BooleanProperty.of("slot_0_occupied");
	public static final BooleanProperty SLOT_1_OCCUPIED = BooleanProperty.of("slot_1_occupied");
	public static final BooleanProperty SLOT_2_OCCUPIED = BooleanProperty.of("slot_2_occupied");
	public static final BooleanProperty SLOT_3_OCCUPIED = BooleanProperty.of("slot_3_occupied");
	public static final BooleanProperty SLOT_4_OCCUPIED = BooleanProperty.of("slot_4_occupied");
	public static final BooleanProperty SLOT_5_OCCUPIED = BooleanProperty.of("slot_5_occupied");
	public static final IntProperty DUSTED = IntProperty.of("dusted", 0, 3);
	public static final BooleanProperty CRACKED = BooleanProperty.of("cracked");
}
