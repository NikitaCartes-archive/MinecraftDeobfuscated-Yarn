package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WritableBookContentComponent;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.LecternScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class LecternBlockEntity extends BlockEntity implements Clearable, NamedScreenHandlerFactory {
	public static final int field_31348 = 0;
	public static final int field_31349 = 1;
	public static final int field_31350 = 0;
	public static final int field_31351 = 1;
	private final Inventory inventory = new Inventory() {
		@Override
		public int size() {
			return 1;
		}

		@Override
		public boolean isEmpty() {
			return LecternBlockEntity.this.book.isEmpty();
		}

		@Override
		public ItemStack getStack(int slot) {
			return slot == 0 ? LecternBlockEntity.this.book : ItemStack.EMPTY;
		}

		@Override
		public ItemStack removeStack(int slot, int amount) {
			if (slot == 0) {
				ItemStack itemStack = LecternBlockEntity.this.book.split(amount);
				if (LecternBlockEntity.this.book.isEmpty()) {
					LecternBlockEntity.this.onBookRemoved();
				}

				return itemStack;
			} else {
				return ItemStack.EMPTY;
			}
		}

		@Override
		public ItemStack removeStack(int slot) {
			if (slot == 0) {
				ItemStack itemStack = LecternBlockEntity.this.book;
				LecternBlockEntity.this.book = ItemStack.EMPTY;
				LecternBlockEntity.this.onBookRemoved();
				return itemStack;
			} else {
				return ItemStack.EMPTY;
			}
		}

		@Override
		public void setStack(int slot, ItemStack stack) {
		}

		@Override
		public int getMaxCountPerStack() {
			return 1;
		}

		@Override
		public void markDirty() {
			LecternBlockEntity.this.markDirty();
		}

		@Override
		public boolean canPlayerUse(PlayerEntity player) {
			return Inventory.canPlayerUse(LecternBlockEntity.this, player) && LecternBlockEntity.this.hasBook();
		}

		@Override
		public boolean isValid(int slot, ItemStack stack) {
			return false;
		}

		@Override
		public void clear() {
		}
	};
	private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
		@Override
		public int get(int index) {
			return index == 0 ? LecternBlockEntity.this.currentPage : 0;
		}

		@Override
		public void set(int index, int value) {
			if (index == 0) {
				LecternBlockEntity.this.setCurrentPage(value);
			}
		}

		@Override
		public int size() {
			return 1;
		}
	};
	ItemStack book = ItemStack.EMPTY;
	int currentPage;
	private int pageCount;

	public LecternBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.LECTERN, pos, state);
	}

	public ItemStack getBook() {
		return this.book;
	}

	public boolean hasBook() {
		return this.book.isOf(Items.WRITABLE_BOOK) || this.book.isOf(Items.WRITTEN_BOOK);
	}

	public void setBook(ItemStack book) {
		this.setBook(book, null);
	}

	void onBookRemoved() {
		this.currentPage = 0;
		this.pageCount = 0;
		LecternBlock.setHasBook(null, this.getWorld(), this.getPos(), this.getCachedState(), false);
	}

	public void setBook(ItemStack book, @Nullable PlayerEntity player) {
		this.book = this.resolveBook(book, player);
		this.currentPage = 0;
		this.pageCount = getPageCount(this.book);
		this.markDirty();
	}

	void setCurrentPage(int currentPage) {
		int i = MathHelper.clamp(currentPage, 0, this.pageCount - 1);
		if (i != this.currentPage) {
			this.currentPage = i;
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

	private ItemStack resolveBook(ItemStack book, @Nullable PlayerEntity player) {
		if (this.world instanceof ServerWorld && book.isOf(Items.WRITTEN_BOOK)) {
			WrittenBookItem.resolve(book, this.getCommandSource(player), player);
		}

		return book;
	}

	private ServerCommandSource getCommandSource(@Nullable PlayerEntity player) {
		String string;
		Text text;
		if (player == null) {
			string = "Lectern";
			text = Text.literal("Lectern");
		} else {
			string = player.getName().getString();
			text = player.getDisplayName();
		}

		Vec3d vec3d = Vec3d.ofCenter(this.pos);
		return new ServerCommandSource(CommandOutput.DUMMY, vec3d, Vec2f.ZERO, (ServerWorld)this.world, 2, string, text, this.world.getServer(), player);
	}

	@Override
	public boolean copyItemDataRequiresOperator() {
		return true;
	}

	@Override
	public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(nbt, registryLookup);
		if (nbt.contains("Book", NbtElement.COMPOUND_TYPE)) {
			this.book = this.resolveBook((ItemStack)ItemStack.fromNbt(registryLookup, nbt.getCompound("Book")).orElse(ItemStack.EMPTY), null);
		} else {
			this.book = ItemStack.EMPTY;
		}

		this.pageCount = getPageCount(this.book);
		this.currentPage = MathHelper.clamp(nbt.getInt("Page"), 0, this.pageCount - 1);
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(nbt, registryLookup);
		if (!this.getBook().isEmpty()) {
			nbt.put("Book", this.getBook().encode(registryLookup));
			nbt.putInt("Page", this.currentPage);
		}
	}

	@Override
	public void clear() {
		this.setBook(ItemStack.EMPTY);
	}

	@Override
	public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return new LecternScreenHandler(i, this.inventory, this.propertyDelegate);
	}

	@Override
	public Text getDisplayName() {
		return Text.translatable("container.lectern");
	}

	private static int getPageCount(ItemStack stack) {
		WrittenBookContentComponent writtenBookContentComponent = stack.get(DataComponentTypes.WRITTEN_BOOK_CONTENT);
		if (writtenBookContentComponent != null) {
			return writtenBookContentComponent.pages().size();
		} else {
			WritableBookContentComponent writableBookContentComponent = stack.get(DataComponentTypes.WRITABLE_BOOK_CONTENT);
			return writableBookContentComponent != null ? writableBookContentComponent.pages().size() : 0;
		}
	}
}
