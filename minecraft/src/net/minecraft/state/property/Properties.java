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
	public static final BooleanProperty field_12493 = BooleanProperty.create("attached");
	public static final BooleanProperty field_16562 = BooleanProperty.create("bottom");
	public static final BooleanProperty field_12486 = BooleanProperty.create("conditional");
	public static final BooleanProperty field_12553 = BooleanProperty.create("disarmed");
	public static final BooleanProperty field_12526 = BooleanProperty.create("drag");
	public static final BooleanProperty field_12515 = BooleanProperty.create("enabled");
	public static final BooleanProperty field_12552 = BooleanProperty.create("extended");
	public static final BooleanProperty field_12488 = BooleanProperty.create("eye");
	public static final BooleanProperty field_12480 = BooleanProperty.create("falling");
	public static final BooleanProperty field_16561 = BooleanProperty.create("hanging");
	public static final BooleanProperty field_12554 = BooleanProperty.create("has_bottle_0");
	public static final BooleanProperty field_12500 = BooleanProperty.create("has_bottle_1");
	public static final BooleanProperty field_12531 = BooleanProperty.create("has_bottle_2");
	public static final BooleanProperty field_12544 = BooleanProperty.create("has_record");
	public static final BooleanProperty field_17393 = BooleanProperty.create("has_book");
	public static final BooleanProperty field_12501 = BooleanProperty.create("inverted");
	public static final BooleanProperty field_12491 = BooleanProperty.create("in_wall");
	public static final BooleanProperty field_12548 = BooleanProperty.create("lit");
	public static final BooleanProperty field_12502 = BooleanProperty.create("locked");
	public static final BooleanProperty field_12528 = BooleanProperty.create("occupied");
	public static final BooleanProperty field_12537 = BooleanProperty.create("open");
	public static final BooleanProperty field_12514 = BooleanProperty.create("persistent");
	public static final BooleanProperty field_12484 = BooleanProperty.create("powered");
	public static final BooleanProperty field_12535 = BooleanProperty.create("short");
	public static final BooleanProperty field_17394 = BooleanProperty.create("signal_fire");
	public static final BooleanProperty field_12512 = BooleanProperty.create("snowy");
	public static final BooleanProperty field_12522 = BooleanProperty.create("triggered");
	public static final BooleanProperty field_12539 = BooleanProperty.create("unstable");
	public static final BooleanProperty field_12508 = BooleanProperty.create("waterlogged");
	public static final EnumProperty<Direction.Axis> field_12529 = EnumProperty.create("axis", Direction.Axis.class, Direction.Axis.X, Direction.Axis.Z);
	public static final EnumProperty<Direction.Axis> field_12496 = EnumProperty.create("axis", Direction.Axis.class);
	public static final BooleanProperty field_12519 = BooleanProperty.create("up");
	public static final BooleanProperty field_12546 = BooleanProperty.create("down");
	public static final BooleanProperty field_12489 = BooleanProperty.create("north");
	public static final BooleanProperty field_12487 = BooleanProperty.create("east");
	public static final BooleanProperty field_12540 = BooleanProperty.create("south");
	public static final BooleanProperty field_12527 = BooleanProperty.create("west");
	public static final DirectionProperty field_12525 = DirectionProperty.method_11845(
		"facing", Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN
	);
	public static final DirectionProperty field_12545 = DirectionProperty.create("facing", (Predicate<Direction>)(direction -> direction != Direction.UP));
	public static final DirectionProperty field_12481 = DirectionProperty.create("facing", Direction.Type.HORIZONTAL);
	public static final EnumProperty<WallMountLocation> field_12555 = EnumProperty.create("face", WallMountLocation.class);
	public static final EnumProperty<Attachment> field_17104 = EnumProperty.create("attachment", Attachment.class);
	public static final EnumProperty<WireConnection> field_12523 = EnumProperty.create("east", WireConnection.class);
	public static final EnumProperty<WireConnection> field_12495 = EnumProperty.create("north", WireConnection.class);
	public static final EnumProperty<WireConnection> field_12551 = EnumProperty.create("south", WireConnection.class);
	public static final EnumProperty<WireConnection> field_12504 = EnumProperty.create("west", WireConnection.class);
	public static final EnumProperty<DoubleBlockHalf> field_12533 = EnumProperty.create("half", DoubleBlockHalf.class);
	public static final EnumProperty<BlockHalf> field_12518 = EnumProperty.create("half", BlockHalf.class);
	public static final EnumProperty<RailShape> field_12507 = EnumProperty.create("shape", RailShape.class);
	public static final EnumProperty<RailShape> field_12542 = EnumProperty.create(
		"shape",
		RailShape.class,
		(Predicate)(railShape -> railShape != RailShape.field_12663
				&& railShape != RailShape.field_12672
				&& railShape != RailShape.field_12664
				&& railShape != RailShape.field_12671)
	);
	public static final IntegerProperty field_12521 = IntegerProperty.create("age", 0, 1);
	public static final IntegerProperty field_12556 = IntegerProperty.create("age", 0, 2);
	public static final IntegerProperty field_12497 = IntegerProperty.create("age", 0, 3);
	public static final IntegerProperty field_12482 = IntegerProperty.create("age", 0, 5);
	public static final IntegerProperty field_12550 = IntegerProperty.create("age", 0, 7);
	public static final IntegerProperty field_12498 = IntegerProperty.create("age", 0, 15);
	public static final IntegerProperty field_12517 = IntegerProperty.create("age", 0, 25);
	public static final IntegerProperty field_12505 = IntegerProperty.create("bites", 0, 6);
	public static final IntegerProperty field_12494 = IntegerProperty.create("delay", 1, 4);
	public static final IntegerProperty field_12541 = IntegerProperty.create("distance", 1, 7);
	public static final IntegerProperty field_12509 = IntegerProperty.create("eggs", 1, 4);
	public static final IntegerProperty field_12530 = IntegerProperty.create("hatch", 0, 2);
	public static final IntegerProperty field_12536 = IntegerProperty.create("layers", 1, 8);
	public static final IntegerProperty field_12513 = IntegerProperty.create("level", 0, 3);
	public static final IntegerProperty field_17586 = IntegerProperty.create("level", 0, 8);
	public static final IntegerProperty field_12490 = IntegerProperty.create("level", 1, 8);
	public static final IntegerProperty field_12538 = IntegerProperty.create("level", 0, 15);
	public static final IntegerProperty field_12510 = IntegerProperty.create("moisture", 0, 7);
	public static final IntegerProperty field_12524 = IntegerProperty.create("note", 0, 24);
	public static final IntegerProperty field_12543 = IntegerProperty.create("pickles", 1, 4);
	public static final IntegerProperty field_12511 = IntegerProperty.create("power", 0, 15);
	public static final IntegerProperty field_12549 = IntegerProperty.create("stage", 0, 1);
	public static final IntegerProperty field_16503 = IntegerProperty.create("distance", 0, 7);
	public static final IntegerProperty field_12532 = IntegerProperty.create("rotation", 0, 15);
	public static final EnumProperty<BedPart> field_12483 = EnumProperty.create("part", BedPart.class);
	public static final EnumProperty<ChestType> field_12506 = EnumProperty.create("type", ChestType.class);
	public static final EnumProperty<ComparatorMode> field_12534 = EnumProperty.create("mode", ComparatorMode.class);
	public static final EnumProperty<DoorHinge> field_12520 = EnumProperty.create("hinge", DoorHinge.class);
	public static final EnumProperty<Instrument> field_12499 = EnumProperty.create("instrument", Instrument.class);
	public static final EnumProperty<PistonType> field_12492 = EnumProperty.create("type", PistonType.class);
	public static final EnumProperty<SlabType> field_12485 = EnumProperty.create("type", SlabType.class);
	public static final EnumProperty<StairShape> field_12503 = EnumProperty.create("shape", StairShape.class);
	public static final EnumProperty<StructureBlockMode> field_12547 = EnumProperty.create("mode", StructureBlockMode.class);
	public static final EnumProperty<BambooLeaves> field_12516 = EnumProperty.create("leaves", BambooLeaves.class);
}
