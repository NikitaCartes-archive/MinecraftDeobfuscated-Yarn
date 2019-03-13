package net.minecraft.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.block.ChestAnimationProgress;
import net.minecraft.container.Container;
import net.minecraft.container.GenericContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

@EnvironmentInterfaces({@EnvironmentInterface(
		value = EnvType.CLIENT,
		itf = ChestAnimationProgress.class
	)})
public class ChestBlockEntity extends LootableContainerBlockEntity implements ChestAnimationProgress, Tickable {
	private DefaultedList<ItemStack> field_11927 = DefaultedList.create(27, ItemStack.EMPTY);
	protected float animationAngle;
	protected float lastAnimationAngle;
	protected int viewerCount;
	private int ticksOpen;

	protected ChestBlockEntity(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
	}

	public ChestBlockEntity() {
		this(BlockEntityType.CHEST);
	}

	@Override
	public int getInvSize() {
		return 27;
	}

	@Override
	public boolean isInvEmpty() {
		for (ItemStack itemStack : this.field_11927) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	protected TextComponent method_17823() {
		return new TranslatableTextComponent("container.chest");
	}

	@Override
	public void method_11014(CompoundTag compoundTag) {
		super.method_11014(compoundTag);
		this.field_11927 = DefaultedList.create(this.getInvSize(), ItemStack.EMPTY);
		if (!this.method_11283(compoundTag)) {
			Inventories.method_5429(compoundTag, this.field_11927);
		}
	}

	@Override
	public CompoundTag method_11007(CompoundTag compoundTag) {
		super.method_11007(compoundTag);
		if (!this.method_11286(compoundTag)) {
			Inventories.method_5426(compoundTag, this.field_11927);
		}

		return compoundTag;
	}

	@Override
	public void tick() {
		int i = this.field_11867.getX();
		int j = this.field_11867.getY();
		int k = this.field_11867.getZ();
		this.ticksOpen++;
		this.viewerCount = recalculateViewerCountIfNecessary(this.world, this, this.ticksOpen, i, j, k, this.viewerCount);
		this.lastAnimationAngle = this.animationAngle;
		float f = 0.1F;
		if (this.viewerCount > 0 && this.animationAngle == 0.0F) {
			this.method_11050(SoundEvents.field_14982);
		}

		if (this.viewerCount == 0 && this.animationAngle > 0.0F || this.viewerCount > 0 && this.animationAngle < 1.0F) {
			float g = this.animationAngle;
			if (this.viewerCount > 0) {
				this.animationAngle += 0.1F;
			} else {
				this.animationAngle -= 0.1F;
			}

			if (this.animationAngle > 1.0F) {
				this.animationAngle = 1.0F;
			}

			float h = 0.5F;
			if (this.animationAngle < 0.5F && g >= 0.5F) {
				this.method_11050(SoundEvents.field_14823);
			}

			if (this.animationAngle < 0.0F) {
				this.animationAngle = 0.0F;
			}
		}
	}

	public static int recalculateViewerCountIfNecessary(World world, LockableContainerBlockEntity lockableContainerBlockEntity, int i, int j, int k, int l, int m) {
		if (!world.isClient && m != 0 && (i + j + k + l) % 200 == 0) {
			m = 0;
			float f = 5.0F;

			for (PlayerEntity playerEntity : world.method_18467(
				PlayerEntity.class,
				new BoundingBox(
					(double)((float)j - 5.0F),
					(double)((float)k - 5.0F),
					(double)((float)l - 5.0F),
					(double)((float)(j + 1) + 5.0F),
					(double)((float)(k + 1) + 5.0F),
					(double)((float)(l + 1) + 5.0F)
				)
			)) {
				if (playerEntity.field_7512 instanceof GenericContainer) {
					Inventory inventory = ((GenericContainer)playerEntity.field_7512).getInventory();
					if (inventory == lockableContainerBlockEntity
						|| inventory instanceof DoubleInventory && ((DoubleInventory)inventory).method_5405(lockableContainerBlockEntity)) {
						m++;
					}
				}
			}
		}

		return m;
	}

	private void method_11050(SoundEvent soundEvent) {
		ChestType chestType = this.method_11010().method_11654(ChestBlock.field_10770);
		if (chestType != ChestType.field_12574) {
			double d = (double)this.field_11867.getX() + 0.5;
			double e = (double)this.field_11867.getY() + 0.5;
			double f = (double)this.field_11867.getZ() + 0.5;
			if (chestType == ChestType.field_12571) {
				Direction direction = ChestBlock.method_9758(this.method_11010());
				d += (double)direction.getOffsetX() * 0.5;
				f += (double)direction.getOffsetZ() * 0.5;
			}

			this.world.method_8465(null, d, e, f, soundEvent, SoundCategory.field_15245, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
		}
	}

	@Override
	public boolean onBlockAction(int i, int j) {
		if (i == 1) {
			this.viewerCount = j;
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
			this.onInvOpenOrClose();
		}
	}

	@Override
	public void method_5432(PlayerEntity playerEntity) {
		if (!playerEntity.isSpectator()) {
			this.viewerCount--;
			this.onInvOpenOrClose();
		}
	}

	protected void onInvOpenOrClose() {
		Block block = this.method_11010().getBlock();
		if (block instanceof ChestBlock) {
			this.world.method_8427(this.field_11867, block, 1, this.viewerCount);
			this.world.method_8452(this.field_11867, block);
		}
	}

	@Override
	protected DefaultedList<ItemStack> method_11282() {
		return this.field_11927;
	}

	@Override
	protected void method_11281(DefaultedList<ItemStack> defaultedList) {
		this.field_11927 = defaultedList;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public float getAnimationProgress(float f) {
		return MathHelper.lerp(f, this.lastAnimationAngle, this.animationAngle);
	}

	public static int method_11048(BlockView blockView, BlockPos blockPos) {
		BlockState blockState = blockView.method_8320(blockPos);
		if (blockState.getBlock().hasBlockEntity()) {
			BlockEntity blockEntity = blockView.method_8321(blockPos);
			if (blockEntity instanceof ChestBlockEntity) {
				return ((ChestBlockEntity)blockEntity).viewerCount;
			}
		}

		return 0;
	}

	public static void copyInventory(ChestBlockEntity chestBlockEntity, ChestBlockEntity chestBlockEntity2) {
		DefaultedList<ItemStack> defaultedList = chestBlockEntity.method_11282();
		chestBlockEntity.method_11281(chestBlockEntity2.method_11282());
		chestBlockEntity2.method_11281(defaultedList);
	}

	@Override
	protected Container createContainer(int i, PlayerInventory playerInventory) {
		return GenericContainer.method_19245(i, playerInventory, this);
	}
}
