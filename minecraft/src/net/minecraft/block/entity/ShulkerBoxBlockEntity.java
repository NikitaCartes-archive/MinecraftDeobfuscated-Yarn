package net.minecraft.block.entity;

import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.container.Container;
import net.minecraft.container.ShulkerBoxContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;

public class ShulkerBoxBlockEntity extends LootableContainerBlockEntity implements SidedInventory, Tickable {
	private static final int[] AVAILABLE_SLOTS = IntStream.range(0, 27).toArray();
	private DefaultedList<ItemStack> field_12054 = DefaultedList.create(27, ItemStack.EMPTY);
	private int viewerCount;
	private ShulkerBoxBlockEntity.AnimationStage animationStage = ShulkerBoxBlockEntity.AnimationStage.CLOSED;
	private float animationProgress;
	private float prevAnimationProgress;
	private DyeColor cachedColor;
	private boolean cachedColorUpdateNeeded;

	public ShulkerBoxBlockEntity(@Nullable DyeColor dyeColor) {
		super(BlockEntityType.SHUlKER_BOX);
		this.cachedColor = dyeColor;
	}

	public ShulkerBoxBlockEntity() {
		this(null);
		this.cachedColorUpdateNeeded = true;
	}

	@Override
	public void tick() {
		this.updateAnimation();
		if (this.animationStage == ShulkerBoxBlockEntity.AnimationStage.field_12066 || this.animationStage == ShulkerBoxBlockEntity.AnimationStage.field_12064) {
			this.method_11316();
		}
	}

	protected void updateAnimation() {
		this.prevAnimationProgress = this.animationProgress;
		switch (this.animationStage) {
			case CLOSED:
				this.animationProgress = 0.0F;
				break;
			case field_12066:
				this.animationProgress += 0.1F;
				if (this.animationProgress >= 1.0F) {
					this.method_11316();
					this.animationStage = ShulkerBoxBlockEntity.AnimationStage.OPENED;
					this.animationProgress = 1.0F;
				}
				break;
			case field_12064:
				this.animationProgress -= 0.1F;
				if (this.animationProgress <= 0.0F) {
					this.animationStage = ShulkerBoxBlockEntity.AnimationStage.CLOSED;
					this.animationProgress = 0.0F;
				}
				break;
			case OPENED:
				this.animationProgress = 1.0F;
		}
	}

	public ShulkerBoxBlockEntity.AnimationStage getAnimationStage() {
		return this.animationStage;
	}

	public BoundingBox method_11314(BlockState blockState) {
		return this.method_11311(blockState.method_11654(ShulkerBoxBlock.field_11496));
	}

	public BoundingBox method_11311(Direction direction) {
		return VoxelShapes.method_1077()
			.getBoundingBox()
			.stretch(
				(double)(0.5F * this.getAnimationProgress(1.0F) * (float)direction.getOffsetX()),
				(double)(0.5F * this.getAnimationProgress(1.0F) * (float)direction.getOffsetY()),
				(double)(0.5F * this.getAnimationProgress(1.0F) * (float)direction.getOffsetZ())
			);
	}

	private BoundingBox method_11315(Direction direction) {
		Direction direction2 = direction.getOpposite();
		return this.method_11311(direction).shrink((double)direction2.getOffsetX(), (double)direction2.getOffsetY(), (double)direction2.getOffsetZ());
	}

	private void method_11316() {
		BlockState blockState = this.world.method_8320(this.method_11016());
		if (blockState.getBlock() instanceof ShulkerBoxBlock) {
			Direction direction = blockState.method_11654(ShulkerBoxBlock.field_11496);
			BoundingBox boundingBox = this.method_11315(direction).method_996(this.field_11867);
			List<Entity> list = this.world.method_8335(null, boundingBox);
			if (!list.isEmpty()) {
				for (int i = 0; i < list.size(); i++) {
					Entity entity = (Entity)list.get(i);
					if (entity.method_5657() != PistonBehavior.field_15975) {
						double d = 0.0;
						double e = 0.0;
						double f = 0.0;
						BoundingBox boundingBox2 = entity.method_5829();
						switch (direction.getAxis()) {
							case X:
								if (direction.getDirection() == Direction.AxisDirection.POSITIVE) {
									d = boundingBox.maxX - boundingBox2.minX;
								} else {
									d = boundingBox2.maxX - boundingBox.minX;
								}

								d += 0.01;
								break;
							case Y:
								if (direction.getDirection() == Direction.AxisDirection.POSITIVE) {
									e = boundingBox.maxY - boundingBox2.minY;
								} else {
									e = boundingBox2.maxY - boundingBox.minY;
								}

								e += 0.01;
								break;
							case Z:
								if (direction.getDirection() == Direction.AxisDirection.POSITIVE) {
									f = boundingBox.maxZ - boundingBox2.minZ;
								} else {
									f = boundingBox2.maxZ - boundingBox.minZ;
								}

								f += 0.01;
						}

						entity.method_5784(
							MovementType.field_6306, new Vec3d(d * (double)direction.getOffsetX(), e * (double)direction.getOffsetY(), f * (double)direction.getOffsetZ())
						);
					}
				}
			}
		}
	}

	@Override
	public int getInvSize() {
		return this.field_12054.size();
	}

	@Override
	public boolean onBlockAction(int i, int j) {
		if (i == 1) {
			this.viewerCount = j;
			if (j == 0) {
				this.animationStage = ShulkerBoxBlockEntity.AnimationStage.field_12064;
			}

			if (j == 1) {
				this.animationStage = ShulkerBoxBlockEntity.AnimationStage.field_12066;
			}

			return true;
		} else {
			return super.onBlockAction(i, j);
		}
	}

	@Override
	public void method_5435(PlayerEntity playerEntity) {
		if (!playerEntity.isSpectator()) {
			if (this.viewerCount < 0) {
				this.viewerCount = 0;
			}

			this.viewerCount++;
			this.world.method_8427(this.field_11867, this.method_11010().getBlock(), 1, this.viewerCount);
			if (this.viewerCount == 1) {
				this.world.method_8396(null, this.field_11867, SoundEvents.field_14825, SoundCategory.field_15245, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
			}
		}
	}

	@Override
	public void method_5432(PlayerEntity playerEntity) {
		if (!playerEntity.isSpectator()) {
			this.viewerCount--;
			this.world.method_8427(this.field_11867, this.method_11010().getBlock(), 1, this.viewerCount);
			if (this.viewerCount <= 0) {
				this.world.method_8396(null, this.field_11867, SoundEvents.field_14751, SoundCategory.field_15245, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
			}
		}
	}

	@Override
	protected TextComponent method_17823() {
		return new TranslatableTextComponent("container.shulkerBox");
	}

	@Override
	public void method_11014(CompoundTag compoundTag) {
		super.method_11014(compoundTag);
		this.method_11319(compoundTag);
	}

	@Override
	public CompoundTag method_11007(CompoundTag compoundTag) {
		super.method_11007(compoundTag);
		return this.method_11317(compoundTag);
	}

	public void method_11319(CompoundTag compoundTag) {
		this.field_12054 = DefaultedList.create(this.getInvSize(), ItemStack.EMPTY);
		if (!this.method_11283(compoundTag) && compoundTag.containsKey("Items", 9)) {
			Inventories.method_5429(compoundTag, this.field_12054);
		}
	}

	public CompoundTag method_11317(CompoundTag compoundTag) {
		if (!this.method_11286(compoundTag)) {
			Inventories.method_5427(compoundTag, this.field_12054, false);
		}

		return compoundTag;
	}

	@Override
	protected DefaultedList<ItemStack> method_11282() {
		return this.field_12054;
	}

	@Override
	protected void method_11281(DefaultedList<ItemStack> defaultedList) {
		this.field_12054 = defaultedList;
	}

	@Override
	public boolean isInvEmpty() {
		for (ItemStack itemStack : this.field_12054) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int[] method_5494(Direction direction) {
		return AVAILABLE_SLOTS;
	}

	@Override
	public boolean method_5492(int i, ItemStack itemStack, @Nullable Direction direction) {
		return !(Block.getBlockFromItem(itemStack.getItem()) instanceof ShulkerBoxBlock);
	}

	@Override
	public boolean method_5493(int i, ItemStack itemStack, Direction direction) {
		return true;
	}

	public float getAnimationProgress(float f) {
		return MathHelper.lerp(f, this.prevAnimationProgress, this.animationProgress);
	}

	@Environment(EnvType.CLIENT)
	public DyeColor getColor() {
		if (this.cachedColorUpdateNeeded) {
			this.cachedColor = ShulkerBoxBlock.getColor(this.method_11010().getBlock());
			this.cachedColorUpdateNeeded = false;
		}

		return this.cachedColor;
	}

	@Override
	protected Container createContainer(int i, PlayerInventory playerInventory) {
		return new ShulkerBoxContainer(i, playerInventory, this);
	}

	public static enum AnimationStage {
		CLOSED,
		field_12066,
		OPENED,
		field_12064;
	}
}
