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
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.DyeColor;
import net.minecraft.util.InventoryUtil;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShapes;

public class ShulkerBoxBlockEntity extends LootableContainerBlockEntity implements SidedInventory, Tickable {
	private static final int[] field_12059 = IntStream.range(0, 27).toArray();
	private DefaultedList<ItemStack> inv = DefaultedList.create(27, ItemStack.EMPTY);
	private int field_12053;
	private ShulkerBoxBlockEntity.AnimationStage animationStage = ShulkerBoxBlockEntity.AnimationStage.CLOSED;
	private float animationProgress;
	private float prevAnimationProgress;
	private DyeColor color;
	private boolean field_12058;

	public ShulkerBoxBlockEntity(@Nullable DyeColor dyeColor) {
		super(BlockEntityType.SHUlKER_BOX);
		this.color = dyeColor;
	}

	public ShulkerBoxBlockEntity() {
		this(null);
		this.field_12058 = true;
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
		return this.method_11311(blockState.get(ShulkerBoxBlock.field_11496));
	}

	public BoundingBox method_11311(Direction direction) {
		return VoxelShapes.fullCube()
			.getBoundingBox()
			.stretch(
				(double)(0.5F * this.getAnimationProgress(1.0F) * (float)direction.getOffsetX()),
				(double)(0.5F * this.getAnimationProgress(1.0F) * (float)direction.getOffsetY()),
				(double)(0.5F * this.getAnimationProgress(1.0F) * (float)direction.getOffsetZ())
			);
	}

	private BoundingBox method_11315(Direction direction) {
		Direction direction2 = direction.getOpposite();
		return this.method_11311(direction).method_1002((double)direction2.getOffsetX(), (double)direction2.getOffsetY(), (double)direction2.getOffsetZ());
	}

	private void method_11316() {
		BlockState blockState = this.world.getBlockState(this.getPos());
		if (blockState.getBlock() instanceof ShulkerBoxBlock) {
			Direction direction = blockState.get(ShulkerBoxBlock.field_11496);
			BoundingBox boundingBox = this.method_11315(direction).offset(this.pos);
			List<Entity> list = this.world.getVisibleEntities(null, boundingBox);
			if (!list.isEmpty()) {
				for (int i = 0; i < list.size(); i++) {
					Entity entity = (Entity)list.get(i);
					if (entity.getPistonBehavior() != PistonBehavior.field_15975) {
						double d = 0.0;
						double e = 0.0;
						double f = 0.0;
						BoundingBox boundingBox2 = entity.getBoundingBox();
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

						entity.move(MovementType.SHULKER_BOX, d * (double)direction.getOffsetX(), e * (double)direction.getOffsetY(), f * (double)direction.getOffsetZ());
					}
				}
			}
		}
	}

	@Override
	public int getInvSize() {
		return this.inv.size();
	}

	@Override
	public boolean onBlockAction(int i, int j) {
		if (i == 1) {
			this.field_12053 = j;
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
	public void onInvOpen(PlayerEntity playerEntity) {
		if (!playerEntity.isSpectator()) {
			if (this.field_12053 < 0) {
				this.field_12053 = 0;
			}

			this.field_12053++;
			this.world.addBlockAction(this.pos, this.getCachedState().getBlock(), 1, this.field_12053);
			if (this.field_12053 == 1) {
				this.world.playSound(null, this.pos, SoundEvents.field_14825, SoundCategory.field_15245, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
			}
		}
	}

	@Override
	public void onInvClose(PlayerEntity playerEntity) {
		if (!playerEntity.isSpectator()) {
			this.field_12053--;
			this.world.addBlockAction(this.pos, this.getCachedState().getBlock(), 1, this.field_12053);
			if (this.field_12053 <= 0) {
				this.world.playSound(null, this.pos, SoundEvents.field_14751, SoundCategory.field_15245, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
			}
		}
	}

	@Override
	protected TextComponent method_17823() {
		return new TranslatableTextComponent("container.shulkerBox");
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.method_11319(compoundTag);
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		return this.method_11317(compoundTag);
	}

	public void method_11319(CompoundTag compoundTag) {
		this.inv = DefaultedList.create(this.getInvSize(), ItemStack.EMPTY);
		if (!this.deserializeLootTable(compoundTag) && compoundTag.containsKey("Items", 9)) {
			InventoryUtil.deserialize(compoundTag, this.inv);
		}
	}

	public CompoundTag method_11317(CompoundTag compoundTag) {
		if (!this.serializeLootTable(compoundTag)) {
			InventoryUtil.serialize(compoundTag, this.inv, false);
		}

		return compoundTag;
	}

	@Override
	protected DefaultedList<ItemStack> getInvStackList() {
		return this.inv;
	}

	@Override
	protected void setInvStackList(DefaultedList<ItemStack> defaultedList) {
		this.inv = defaultedList;
	}

	@Override
	public boolean isInvEmpty() {
		for (ItemStack itemStack : this.inv) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int[] getInvAvailableSlots(Direction direction) {
		return field_12059;
	}

	@Override
	public boolean canInsertInvStack(int i, ItemStack itemStack, @Nullable Direction direction) {
		return !(Block.getBlockFromItem(itemStack.getItem()) instanceof ShulkerBoxBlock);
	}

	@Override
	public boolean canExtractInvStack(int i, ItemStack itemStack, Direction direction) {
		return true;
	}

	public float getAnimationProgress(float f) {
		return MathHelper.lerp(f, this.prevAnimationProgress, this.animationProgress);
	}

	@Environment(EnvType.CLIENT)
	public DyeColor getColor() {
		if (this.field_12058) {
			this.color = ShulkerBoxBlock.getColor(this.getCachedState().getBlock());
			this.field_12058 = false;
		}

		return this.color;
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
