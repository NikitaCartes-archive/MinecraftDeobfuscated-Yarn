package net.minecraft.block;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.container.Container;
import net.minecraft.container.GenericContainer;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ChestBlock extends BlockWithEntity implements Waterloggable {
	public static final DirectionProperty field_10768 = HorizontalFacingBlock.field_11177;
	public static final EnumProperty<ChestType> field_10770 = Properties.field_12506;
	public static final BooleanProperty field_10772 = Properties.field_12508;
	protected static final VoxelShape field_10767 = Block.method_9541(1.0, 0.0, 0.0, 15.0, 14.0, 15.0);
	protected static final VoxelShape field_10771 = Block.method_9541(1.0, 0.0, 1.0, 15.0, 14.0, 16.0);
	protected static final VoxelShape field_10773 = Block.method_9541(0.0, 0.0, 1.0, 15.0, 14.0, 15.0);
	protected static final VoxelShape field_10769 = Block.method_9541(1.0, 0.0, 1.0, 16.0, 14.0, 15.0);
	protected static final VoxelShape field_10774 = Block.method_9541(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
	private static final ChestBlock.PropertyRetriever<Inventory> INVENTORY_RETRIEVER = new ChestBlock.PropertyRetriever<Inventory>() {
		public Inventory method_17461(ChestBlockEntity chestBlockEntity, ChestBlockEntity chestBlockEntity2) {
			return new DoubleInventory(chestBlockEntity, chestBlockEntity2);
		}

		public Inventory method_17460(ChestBlockEntity chestBlockEntity) {
			return chestBlockEntity;
		}
	};
	private static final ChestBlock.PropertyRetriever<NameableContainerProvider> NAME_RETRIEVER = new ChestBlock.PropertyRetriever<NameableContainerProvider>() {
		public NameableContainerProvider method_17463(ChestBlockEntity chestBlockEntity, ChestBlockEntity chestBlockEntity2) {
			final Inventory inventory = new DoubleInventory(chestBlockEntity, chestBlockEntity2);
			return new NameableContainerProvider() {
				@Nullable
				@Override
				public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
					if (chestBlockEntity.checkUnlocked(playerEntity) && chestBlockEntity2.checkUnlocked(playerEntity)) {
						chestBlockEntity.checkLootInteraction(playerInventory.player);
						chestBlockEntity2.checkLootInteraction(playerInventory.player);
						return GenericContainer.createGeneric9x6(i, playerInventory, inventory);
					} else {
						return null;
					}
				}

				@Override
				public Text getDisplayName() {
					if (chestBlockEntity.hasCustomName()) {
						return chestBlockEntity.getDisplayName();
					} else {
						return (Text)(chestBlockEntity2.hasCustomName() ? chestBlockEntity2.getDisplayName() : new TranslatableText("container.chestDouble"));
					}
				}
			};
		}

		public NameableContainerProvider method_17462(ChestBlockEntity chestBlockEntity) {
			return chestBlockEntity;
		}
	};

	protected ChestBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_10768, Direction.field_11043)
				.method_11657(field_10770, ChestType.field_12569)
				.method_11657(field_10772, Boolean.valueOf(false))
		);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_9589(BlockState blockState) {
		return true;
	}

	@Override
	public BlockRenderType method_9604(BlockState blockState) {
		return BlockRenderType.field_11456;
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if ((Boolean)blockState.method_11654(field_10772)) {
			iWorld.method_8405().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
		}

		if (blockState2.getBlock() == this && direction.getAxis().isHorizontal()) {
			ChestType chestType = blockState2.method_11654(field_10770);
			if (blockState.method_11654(field_10770) == ChestType.field_12569
				&& chestType != ChestType.field_12569
				&& blockState.method_11654(field_10768) == blockState2.method_11654(field_10768)
				&& method_9758(blockState2) == direction.getOpposite()) {
				return blockState.method_11657(field_10770, chestType.getOpposite());
			}
		} else if (method_9758(blockState) == direction) {
			return blockState.method_11657(field_10770, ChestType.field_12569);
		}

		return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		if (blockState.method_11654(field_10770) == ChestType.field_12569) {
			return field_10774;
		} else {
			switch (method_9758(blockState)) {
				case field_11043:
				default:
					return field_10767;
				case field_11035:
					return field_10771;
				case field_11039:
					return field_10773;
				case field_11034:
					return field_10769;
			}
		}
	}

	public static Direction method_9758(BlockState blockState) {
		Direction direction = blockState.method_11654(field_10768);
		return blockState.method_11654(field_10770) == ChestType.field_12574 ? direction.rotateYClockwise() : direction.rotateYCounterclockwise();
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		ChestType chestType = ChestType.field_12569;
		Direction direction = itemPlacementContext.getPlayerFacing().getOpposite();
		FluidState fluidState = itemPlacementContext.method_8045().method_8316(itemPlacementContext.getBlockPos());
		boolean bl = itemPlacementContext.isPlayerSneaking();
		Direction direction2 = itemPlacementContext.getSide();
		if (direction2.getAxis().isHorizontal() && bl) {
			Direction direction3 = this.getNeighborChestDirection(itemPlacementContext, direction2.getOpposite());
			if (direction3 != null && direction3.getAxis() != direction2.getAxis()) {
				direction = direction3;
				chestType = direction3.rotateYCounterclockwise() == direction2.getOpposite() ? ChestType.field_12571 : ChestType.field_12574;
			}
		}

		if (chestType == ChestType.field_12569 && !bl) {
			if (direction == this.getNeighborChestDirection(itemPlacementContext, direction.rotateYClockwise())) {
				chestType = ChestType.field_12574;
			} else if (direction == this.getNeighborChestDirection(itemPlacementContext, direction.rotateYCounterclockwise())) {
				chestType = ChestType.field_12571;
			}
		}

		return this.method_9564()
			.method_11657(field_10768, direction)
			.method_11657(field_10770, chestType)
			.method_11657(field_10772, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
	}

	@Override
	public FluidState method_9545(BlockState blockState) {
		return blockState.method_11654(field_10772) ? Fluids.WATER.method_15729(false) : super.method_9545(blockState);
	}

	@Nullable
	private Direction getNeighborChestDirection(ItemPlacementContext itemPlacementContext, Direction direction) {
		BlockState blockState = itemPlacementContext.method_8045().method_8320(itemPlacementContext.getBlockPos().offset(direction));
		return blockState.getBlock() == this && blockState.method_11654(field_10770) == ChestType.field_12569 ? blockState.method_11654(field_10768) : null;
	}

	@Override
	public void method_9567(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
		if (itemStack.hasCustomName()) {
			BlockEntity blockEntity = world.method_8321(blockPos);
			if (blockEntity instanceof ChestBlockEntity) {
				((ChestBlockEntity)blockEntity).setCustomName(itemStack.getName());
			}
		}
	}

	@Override
	public void method_9536(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			BlockEntity blockEntity = world.method_8321(blockPos);
			if (blockEntity instanceof Inventory) {
				ItemScatterer.method_5451(world, blockPos, (Inventory)blockEntity);
				world.method_8455(blockPos, this);
			}

			super.method_9536(blockState, world, blockPos, blockState2, bl);
		}
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (world.isClient) {
			return true;
		} else {
			NameableContainerProvider nameableContainerProvider = this.method_17454(blockState, world, blockPos);
			if (nameableContainerProvider != null) {
				playerEntity.openContainer(nameableContainerProvider);
				playerEntity.incrementStat(this.getOpenStat());
			}

			return true;
		}
	}

	protected Stat<Identifier> getOpenStat() {
		return Stats.field_15419.getOrCreateStat(Stats.field_15395);
	}

	@Nullable
	public static <T> T method_17459(BlockState blockState, IWorld iWorld, BlockPos blockPos, boolean bl, ChestBlock.PropertyRetriever<T> propertyRetriever) {
		BlockEntity blockEntity = iWorld.method_8321(blockPos);
		if (!(blockEntity instanceof ChestBlockEntity)) {
			return null;
		} else if (!bl && isChestBlocked(iWorld, blockPos)) {
			return null;
		} else {
			ChestBlockEntity chestBlockEntity = (ChestBlockEntity)blockEntity;
			ChestType chestType = blockState.method_11654(field_10770);
			if (chestType == ChestType.field_12569) {
				return propertyRetriever.method_17464(chestBlockEntity);
			} else {
				BlockPos blockPos2 = blockPos.offset(method_9758(blockState));
				BlockState blockState2 = iWorld.method_8320(blockPos2);
				if (blockState2.getBlock() == blockState.getBlock()) {
					ChestType chestType2 = blockState2.method_11654(field_10770);
					if (chestType2 != ChestType.field_12569 && chestType != chestType2 && blockState2.method_11654(field_10768) == blockState.method_11654(field_10768)) {
						if (!bl && isChestBlocked(iWorld, blockPos2)) {
							return null;
						}

						BlockEntity blockEntity2 = iWorld.method_8321(blockPos2);
						if (blockEntity2 instanceof ChestBlockEntity) {
							ChestBlockEntity chestBlockEntity2 = chestType == ChestType.field_12571 ? chestBlockEntity : (ChestBlockEntity)blockEntity2;
							ChestBlockEntity chestBlockEntity3 = chestType == ChestType.field_12571 ? (ChestBlockEntity)blockEntity2 : chestBlockEntity;
							return propertyRetriever.method_17465(chestBlockEntity2, chestBlockEntity3);
						}
					}
				}

				return propertyRetriever.method_17464(chestBlockEntity);
			}
		}
	}

	@Nullable
	public static Inventory method_17458(BlockState blockState, World world, BlockPos blockPos, boolean bl) {
		return method_17459(blockState, world, blockPos, bl, INVENTORY_RETRIEVER);
	}

	@Nullable
	@Override
	public NameableContainerProvider method_17454(BlockState blockState, World world, BlockPos blockPos) {
		return method_17459(blockState, world, blockPos, false, NAME_RETRIEVER);
	}

	@Override
	public BlockEntity method_10123(BlockView blockView) {
		return new ChestBlockEntity();
	}

	private static boolean isChestBlocked(IWorld iWorld, BlockPos blockPos) {
		return hasBlockOnTop(iWorld, blockPos) || hasOcelotOnTop(iWorld, blockPos);
	}

	private static boolean hasBlockOnTop(BlockView blockView, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.up();
		return blockView.method_8320(blockPos2).isSimpleFullBlock(blockView, blockPos2);
	}

	private static boolean hasOcelotOnTop(IWorld iWorld, BlockPos blockPos) {
		List<CatEntity> list = iWorld.method_18467(
			CatEntity.class,
			new Box(
				(double)blockPos.getX(),
				(double)(blockPos.getY() + 1),
				(double)blockPos.getZ(),
				(double)(blockPos.getX() + 1),
				(double)(blockPos.getY() + 2),
				(double)(blockPos.getZ() + 1)
			)
		);
		if (!list.isEmpty()) {
			for (CatEntity catEntity : list) {
				if (catEntity.isSitting()) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean method_9498(BlockState blockState) {
		return true;
	}

	@Override
	public int method_9572(BlockState blockState, World world, BlockPos blockPos) {
		return Container.calculateComparatorOutput(method_17458(blockState, world, blockPos, false));
	}

	@Override
	public BlockState method_9598(BlockState blockState, BlockRotation blockRotation) {
		return blockState.method_11657(field_10768, blockRotation.rotate(blockState.method_11654(field_10768)));
	}

	@Override
	public BlockState method_9569(BlockState blockState, BlockMirror blockMirror) {
		return blockState.rotate(blockMirror.method_10345(blockState.method_11654(field_10768)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_10768, field_10770, field_10772);
	}

	@Override
	public boolean method_9516(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}

	interface PropertyRetriever<T> {
		T method_17465(ChestBlockEntity chestBlockEntity, ChestBlockEntity chestBlockEntity2);

		T method_17464(ChestBlockEntity chestBlockEntity);
	}
}
