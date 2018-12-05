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
import net.minecraft.container.DoubleLockableContainer;
import net.minecraft.container.GenericContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.InventoryUtil;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;

@EnvironmentInterfaces({@EnvironmentInterface(
		value = EnvType.CLIENT,
		itf = ChestAnimationProgress.class
	)})
public class ChestBlockEntity extends LootableContainerBlockEntity implements ChestAnimationProgress, Tickable {
	private DefaultedList<ItemStack> inventory = DefaultedList.create(27, ItemStack.EMPTY);
	protected float field_11929;
	protected float field_11926;
	protected int field_11928;
	private int field_11930;

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
		for (ItemStack itemStack : this.inventory) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public TextComponent getName() {
		TextComponent textComponent = this.getCustomName();
		return (TextComponent)(textComponent != null ? textComponent : new TranslatableTextComponent("container.chest"));
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.inventory = DefaultedList.create(this.getInvSize(), ItemStack.EMPTY);
		if (!this.deserializeLootTable(compoundTag)) {
			InventoryUtil.deserialize(compoundTag, this.inventory);
		}

		if (compoundTag.containsKey("CustomName", 8)) {
			this.customName = TextComponent.Serializer.fromJsonString(compoundTag.getString("CustomName"));
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		if (!this.serializeLootTable(compoundTag)) {
			InventoryUtil.serialize(compoundTag, this.inventory);
		}

		TextComponent textComponent = this.getCustomName();
		if (textComponent != null) {
			compoundTag.putString("CustomName", TextComponent.Serializer.toJsonString(textComponent));
		}

		return compoundTag;
	}

	@Override
	public int getInvMaxStackAmount() {
		return 64;
	}

	@Override
	public void tick() {
		int i = this.pos.getX();
		int j = this.pos.getY();
		int k = this.pos.getZ();
		this.field_11930++;
		if (!this.world.isRemote && this.field_11928 != 0 && (this.field_11930 + i + j + k) % 200 == 0) {
			this.field_11928 = 0;
			float f = 5.0F;

			for (PlayerEntity playerEntity : this.world
				.getVisibleEntities(
					PlayerEntity.class,
					new BoundingBox(
						(double)((float)i - 5.0F),
						(double)((float)j - 5.0F),
						(double)((float)k - 5.0F),
						(double)((float)(i + 1) + 5.0F),
						(double)((float)(j + 1) + 5.0F),
						(double)((float)(k + 1) + 5.0F)
					)
				)) {
				if (playerEntity.container instanceof GenericContainer) {
					Inventory inventory = ((GenericContainer)playerEntity.container).getInventory();
					if (inventory == this || inventory instanceof DoubleLockableContainer && ((DoubleLockableContainer)inventory).isPart(this)) {
						this.field_11928++;
					}
				}
			}
		}

		this.field_11926 = this.field_11929;
		float f = 0.1F;
		if (this.field_11928 > 0 && this.field_11929 == 0.0F) {
			this.method_11050(SoundEvents.field_14982);
		}

		if (this.field_11928 == 0 && this.field_11929 > 0.0F || this.field_11928 > 0 && this.field_11929 < 1.0F) {
			float g = this.field_11929;
			if (this.field_11928 > 0) {
				this.field_11929 += 0.1F;
			} else {
				this.field_11929 -= 0.1F;
			}

			if (this.field_11929 > 1.0F) {
				this.field_11929 = 1.0F;
			}

			float h = 0.5F;
			if (this.field_11929 < 0.5F && g >= 0.5F) {
				this.method_11050(SoundEvents.field_14823);
			}

			if (this.field_11929 < 0.0F) {
				this.field_11929 = 0.0F;
			}
		}
	}

	private void method_11050(SoundEvent soundEvent) {
		ChestType chestType = this.getCachedState().get(ChestBlock.field_10770);
		if (chestType != ChestType.field_12574) {
			double d = (double)this.pos.getX() + 0.5;
			double e = (double)this.pos.getY() + 0.5;
			double f = (double)this.pos.getZ() + 0.5;
			if (chestType == ChestType.field_12571) {
				Direction direction = ChestBlock.method_9758(this.getCachedState());
				d += (double)direction.getOffsetX() * 0.5;
				f += (double)direction.getOffsetZ() * 0.5;
			}

			this.world.playSound(null, d, e, f, soundEvent, SoundCategory.field_15245, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
		}
	}

	@Override
	public boolean method_11004(int i, int j) {
		if (i == 1) {
			this.field_11928 = j;
			return true;
		} else {
			return super.method_11004(i, j);
		}
	}

	@Override
	public void onInvOpen(PlayerEntity playerEntity) {
		if (!playerEntity.isSpectator()) {
			if (this.field_11928 < 0) {
				this.field_11928 = 0;
			}

			this.field_11928++;
			this.method_11049();
		}
	}

	@Override
	public void onInvClose(PlayerEntity playerEntity) {
		if (!playerEntity.isSpectator()) {
			this.field_11928--;
			this.method_11049();
		}
	}

	protected void method_11049() {
		Block block = this.getCachedState().getBlock();
		if (block instanceof ChestBlock) {
			this.world.addBlockAction(this.pos, block, 1, this.field_11928);
			this.world.updateNeighborsAlways(this.pos, block);
		}
	}

	@Override
	public String getContainerId() {
		return "minecraft:chest";
	}

	@Override
	public Container createContainer(PlayerInventory playerInventory, PlayerEntity playerEntity) {
		this.checkLootInteraction(playerEntity);
		return new GenericContainer(playerInventory, this, playerEntity);
	}

	@Override
	protected DefaultedList<ItemStack> getInvStackList() {
		return this.inventory;
	}

	@Override
	protected void method_11281(DefaultedList<ItemStack> defaultedList) {
		this.inventory = defaultedList;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public float getAnimationProgress(float f) {
		return MathHelper.lerp(f, this.field_11926, this.field_11929);
	}

	public static int method_11048(BlockView blockView, BlockPos blockPos) {
		BlockState blockState = blockView.getBlockState(blockPos);
		if (blockState.getBlock().hasBlockEntity()) {
			BlockEntity blockEntity = blockView.getBlockEntity(blockPos);
			if (blockEntity instanceof ChestBlockEntity) {
				return ((ChestBlockEntity)blockEntity).field_11928;
			}
		}

		return 0;
	}

	public static void method_11047(ChestBlockEntity chestBlockEntity, ChestBlockEntity chestBlockEntity2) {
		DefaultedList<ItemStack> defaultedList = chestBlockEntity.getInvStackList();
		chestBlockEntity.method_11281(chestBlockEntity2.getInvStackList());
		chestBlockEntity2.method_11281(defaultedList);
	}
}
