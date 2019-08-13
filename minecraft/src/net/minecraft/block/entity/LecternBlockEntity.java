package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.block.LecternBlock;
import net.minecraft.container.Container;
import net.minecraft.container.LecternContainer;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.container.PropertyDelegate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class LecternBlockEntity extends BlockEntity implements Clearable, NameableContainerProvider {
	private final Inventory inventory = new Inventory() {
		@Override
		public int getInvSize() {
			return 1;
		}

		@Override
		public boolean isInvEmpty() {
			return LecternBlockEntity.this.book.isEmpty();
		}

		@Override
		public ItemStack getInvStack(int i) {
			return i == 0 ? LecternBlockEntity.this.book : ItemStack.EMPTY;
		}

		@Override
		public ItemStack takeInvStack(int i, int j) {
			if (i == 0) {
				ItemStack itemStack = LecternBlockEntity.this.book.split(j);
				if (LecternBlockEntity.this.book.isEmpty()) {
					LecternBlockEntity.this.onBookRemoved();
				}

				return itemStack;
			} else {
				return ItemStack.EMPTY;
			}
		}

		@Override
		public ItemStack removeInvStack(int i) {
			if (i == 0) {
				ItemStack itemStack = LecternBlockEntity.this.book;
				LecternBlockEntity.this.book = ItemStack.EMPTY;
				LecternBlockEntity.this.onBookRemoved();
				return itemStack;
			} else {
				return ItemStack.EMPTY;
			}
		}

		@Override
		public void setInvStack(int i, ItemStack itemStack) {
		}

		@Override
		public int getInvMaxStackAmount() {
			return 1;
		}

		@Override
		public void markDirty() {
			LecternBlockEntity.this.markDirty();
		}

		@Override
		public boolean canPlayerUseInv(PlayerEntity playerEntity) {
			if (LecternBlockEntity.this.world.getBlockEntity(LecternBlockEntity.this.pos) != LecternBlockEntity.this) {
				return false;
			} else {
				return playerEntity.squaredDistanceTo(
							(double)LecternBlockEntity.this.pos.getX() + 0.5, (double)LecternBlockEntity.this.pos.getY() + 0.5, (double)LecternBlockEntity.this.pos.getZ() + 0.5
						)
						> 64.0
					? false
					: LecternBlockEntity.this.hasBook();
			}
		}

		@Override
		public boolean isValidInvStack(int i, ItemStack itemStack) {
			return false;
		}

		@Override
		public void clear() {
		}
	};
	private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
		@Override
		public int get(int i) {
			return i == 0 ? LecternBlockEntity.this.currentPage : 0;
		}

		@Override
		public void set(int i, int j) {
			if (i == 0) {
				LecternBlockEntity.this.setCurrentPage(j);
			}
		}

		@Override
		public int size() {
			return 1;
		}
	};
	private ItemStack book = ItemStack.EMPTY;
	private int currentPage;
	private int pageCount;

	public LecternBlockEntity() {
		super(BlockEntityType.field_16412);
	}

	public ItemStack getBook() {
		return this.book;
	}

	public boolean hasBook() {
		Item item = this.book.getItem();
		return item == Items.field_8674 || item == Items.field_8360;
	}

	public void setBook(ItemStack itemStack) {
		this.setBook(itemStack, null);
	}

	private void onBookRemoved() {
		this.currentPage = 0;
		this.pageCount = 0;
		LecternBlock.setHasBook(this.getWorld(), this.getPos(), this.getCachedState(), false);
	}

	public void setBook(ItemStack itemStack, @Nullable PlayerEntity playerEntity) {
		this.book = this.resolveBook(itemStack, playerEntity);
		this.currentPage = 0;
		this.pageCount = WrittenBookItem.getPageCount(this.book);
		this.markDirty();
	}

	private void setCurrentPage(int i) {
		int j = MathHelper.clamp(i, 0, this.pageCount - 1);
		if (j != this.currentPage) {
			this.currentPage = j;
			this.markDirty();
			LecternBlock.setPowered(this.getWorld(), this.getPos(), this.getCachedState());
		}
	}

	public int getCurrentPage() {
		return this.currentPage;
	}

	public int getComparatorOutput() {
		float f = this.pageCount > 1 ? (float)this.getCurrentPage() / ((float)this.pageCount - 1.0F) : 1.0F;
		return MathHelper.floor(f * 14.0F) + (this.hasBook() ? 1 : 0);
	}

	private ItemStack resolveBook(ItemStack itemStack, @Nullable PlayerEntity playerEntity) {
		if (this.world instanceof ServerWorld && itemStack.getItem() == Items.field_8360) {
			WrittenBookItem.resolve(itemStack, this.getCommandSource(playerEntity), playerEntity);
		}

		return itemStack;
	}

	private ServerCommandSource getCommandSource(@Nullable PlayerEntity playerEntity) {
		String string;
		Text text;
		if (playerEntity == null) {
			string = "Lectern";
			text = new LiteralText("Lectern");
		} else {
			string = playerEntity.getName().getString();
			text = playerEntity.getDisplayName();
		}

		Vec3d vec3d = new Vec3d((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5);
		return new ServerCommandSource(CommandOutput.DUMMY, vec3d, Vec2f.ZERO, (ServerWorld)this.world, 2, string, text, this.world.getServer(), playerEntity);
	}

	@Override
	public boolean shouldNotCopyTagFromItem() {
		return true;
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		if (compoundTag.containsKey("Book", 10)) {
			this.book = this.resolveBook(ItemStack.fromTag(compoundTag.getCompound("Book")), null);
		} else {
			this.book = ItemStack.EMPTY;
		}

		this.pageCount = WrittenBookItem.getPageCount(this.book);
		this.currentPage = MathHelper.clamp(compoundTag.getInt("Page"), 0, this.pageCount - 1);
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		if (!this.getBook().isEmpty()) {
			compoundTag.put("Book", this.getBook().toTag(new CompoundTag()));
			compoundTag.putInt("Page", this.currentPage);
		}

		return compoundTag;
	}

	@Override
	public void clear() {
		this.setBook(ItemStack.EMPTY);
	}

	@Override
	public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return new LecternContainer(i, this.inventory, this.propertyDelegate);
	}

	@Override
	public Text getDisplayName() {
		return new TranslatableText("container.lectern");
	}
}
