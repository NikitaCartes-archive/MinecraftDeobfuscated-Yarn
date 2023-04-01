package net.minecraft.recipe;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.class_8293;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.NbtItem;
import net.minecraft.item.NbtNameItem;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtShort;
import net.minecraft.nbt.NbtString;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class NbtCraftingRecipe extends SpecialCraftingRecipe {
	public NbtCraftingRecipe(Identifier identifier, CraftingRecipeCategory craftingRecipeCategory) {
		super(identifier, craftingRecipeCategory);
	}

	private static List<ItemStack> getInputs(CraftingInventory craftingInventory) {
		List<ItemStack> list = new ArrayList();

		for (int i = 0; i < craftingInventory.size(); i++) {
			ItemStack itemStack = craftingInventory.getStack(i);
			if (!itemStack.isEmpty()) {
				list.add(itemStack);
			}
		}

		return list;
	}

	public boolean matches(CraftingInventory craftingInventory, World world) {
		return !class_8293.field_43666.method_50116() ? false : !getOutput(getInputs(craftingInventory)).isEmpty();
	}

	public ItemStack craft(CraftingInventory craftingInventory, DynamicRegistryManager dynamicRegistryManager) {
		return !class_8293.field_43666.method_50116() ? ItemStack.EMPTY : getOutput(getInputs(craftingInventory));
	}

	private static int countMatching(List<ItemStack> inputs, Predicate<ItemStack> predicate, boolean sizeCounted) {
		int i = 0;
		Iterator<ItemStack> iterator = inputs.iterator();

		while (iterator.hasNext()) {
			ItemStack itemStack = (ItemStack)iterator.next();
			if (predicate.test(itemStack)) {
				iterator.remove();
				i += sizeCounted ? itemStack.getCount() : 1;
			}
		}

		return i;
	}

	@Nullable
	private static <T> T method_50817(List<ItemStack> list, Function<ItemStack, T> function) {
		T object = null;
		Iterator<ItemStack> iterator = list.iterator();

		while (iterator.hasNext()) {
			ItemStack itemStack = (ItemStack)iterator.next();
			T object2 = (T)function.apply(itemStack);
			if (object2 != null) {
				if (object != null) {
					return null;
				}

				iterator.remove();
				object = object2;
			}
		}

		return object;
	}

	private static <T extends NbtElement> ItemStack createNbtItemStack(Item item, T nbtElement) {
		ItemStack itemStack = new ItemStack(item);
		((NbtItem)item).setValue(itemStack, nbtElement);
		return itemStack;
	}

	private static ItemStack createSyntaxError(String error) {
		ItemStack itemStack = new ItemStack(Items.SYNTAX_ERROR);
		NbtCompound nbtCompound = itemStack.getOrCreateSubNbt("display");
		NbtList nbtList = new NbtList();
		String string = Text.Serializer.toJson(Text.literal(error));
		nbtList.add(NbtString.of(string));
		nbtCompound.put("Lore", nbtList);
		return itemStack;
	}

	public static ItemStack getOutput(List<ItemStack> inputs) {
		if (inputs.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			int i = countMatching(inputs, stack -> stack.isOf(Items.TAG), false);
			if (i > 0) {
				return getTagOutput(inputs, i);
			} else {
				int j = countMatching(inputs, stack -> stack.isIn(ItemTags.BOATS), false);
				if (j > 0) {
					return method_50823(inputs, j);
				} else {
					ItemStack itemStack = (ItemStack)inputs.get(0);
					if (itemStack.isOf(Items.LEFT_CURLY) && inputs.size() > 1) {
						return method_50825(inputs);
					} else if (itemStack.isOf(Items.LEFT_SQUARE) && inputs.size() > 1) {
						return method_50822(inputs);
					} else if (!itemStack.isOf(Items.RIGHT_CURLY) && !itemStack.isOf(Items.RIGHT_SQUARE) && !itemStack.isOf(Items.NAME)) {
						byte[] bs = stacksToByteArray(inputs);
						return bs != null ? createStackFromBytes(bs) : concatenateStackValues(inputs);
					} else {
						return createSyntaxError("Expected { or [");
					}
				}
			}
		}
	}

	private static ItemStack getTagOutput(List<ItemStack> inputs, int tagCount) {
		if (tagCount != 1) {
			return ItemStack.EMPTY;
		} else {
			int i = countMatching(inputs, stack -> stack.isOf(Items.STRING), false);
			if (i > 0) {
				return i == 1 && inputs.isEmpty() ? createNbtItemStack(Items.STRING_TAG, NbtString.of("")) : ItemStack.EMPTY;
			} else {
				int j = countMatching(inputs, stack -> stack.isOf(Items.STICK), true);
				return !inputs.isEmpty() ? ItemStack.EMPTY : createNbtItemStack(Items.BYTE_TAG, NbtByte.of((byte)j));
			}
		}
	}

	private static ItemStack method_50823(List<ItemStack> list, int i) {
		AbstractNbtNumber abstractNbtNumber = method_50817(list, NbtCraftingRecipe::getNbtNumberFromStack);
		if (abstractNbtNumber == null) {
			return ItemStack.EMPTY;
		} else {
			boolean bl = i == 1;
			boolean bl2 = i == 2;
			if (!bl && !bl2) {
				return createSyntaxError("Expected either single or double");
			} else {
				boolean bl3 = countMatching(list, itemStack -> itemStack.isOf(Items.BIT), false) > 0;
				if (!list.isEmpty()) {
					return createSyntaxError("Unexpected entries in when casting to float");
				} else if (bl) {
					float f = bl3 ? Float.intBitsToFloat(abstractNbtNumber.intValue()) : abstractNbtNumber.floatValue();
					return createNbtItemStack(Items.FLOAT_TAG, NbtFloat.of(f));
				} else {
					double d = bl3 ? Double.longBitsToDouble(abstractNbtNumber.longValue()) : abstractNbtNumber.doubleValue();
					return createNbtItemStack(Items.DOUBLE_TAG, NbtDouble.of(d));
				}
			}
		}
	}

	private static ItemStack method_50822(List<ItemStack> list) {
		NbtList nbtList = new NbtList();
		boolean bl = false;

		for (int i = 1; i < list.size(); i++) {
			if (bl) {
				return createSyntaxError("Unexpected value after closing bracket");
			}

			ItemStack itemStack = (ItemStack)list.get(i);
			if (itemStack.isOf(Items.RIGHT_SQUARE)) {
				bl = true;
			} else {
				if (!(itemStack.getItem() instanceof NbtItem<?> nbtItem)) {
					return createSyntaxError("Unexpected value in list: expected either tag or closing bracket");
				}

				NbtElement nbtElement = nbtItem.getValue(itemStack);
				if (nbtElement == null) {
					return createSyntaxError("OH NO INTERNAL ERROR");
				}

				if (!nbtList.addElement(nbtList.size(), nbtElement)) {
					return createSyntaxError("Can't add element of type " + nbtElement.asString() + " to list " + nbtList.asString());
				}
			}
		}

		return !bl ? createSyntaxError("Expected closing bracket") : createNbtItemStack(Items.LIST_TAG, nbtList);
	}

	private static ItemStack method_50825(List<ItemStack> list) {
		NbtCompound nbtCompound = new NbtCompound();
		boolean bl = false;
		String string = null;

		for (int i = 1; i < list.size(); i++) {
			if (bl) {
				return createSyntaxError("Unexpected value after closing bracket");
			}

			ItemStack itemStack = (ItemStack)list.get(i);
			if (itemStack.isOf(Items.RIGHT_CURLY)) {
				bl = true;
			} else if (itemStack.isOf(Items.NAME)) {
				if (string != null) {
					return createSyntaxError("Expected tag after name");
				}

				string = NbtNameItem.getValue(itemStack);
			} else {
				if (!(itemStack.getItem() instanceof NbtItem<?> nbtItem)) {
					return createSyntaxError("Unexpected value in compound tag: expected either name, tag or closing bracket");
				}

				if (string == null) {
					return createSyntaxError("Expected name");
				}

				NbtElement nbtElement = nbtItem.getValue(itemStack);
				if (nbtElement == null) {
					return createSyntaxError("INTERNAL ERROR OH NO");
				}

				nbtCompound.put(string, nbtElement);
				string = null;
			}
		}

		if (string != null) {
			return createSyntaxError("Expected tag after name");
		} else {
			return !bl ? createSyntaxError("Expected closing bracket") : createNbtItemStack(Items.COMPOUND_TAG, nbtCompound);
		}
	}

	private static ItemStack createStackFromBytes(byte[] bytes) {
		ByteArrayDataInput byteArrayDataInput = ByteStreams.newDataInput(bytes);

		return switch (bytes.length) {
			case 0 -> createNbtItemStack(Items.BYTE_TAG, NbtByte.ZERO);
			case 1 -> createNbtItemStack(Items.BYTE_TAG, NbtByte.of(byteArrayDataInput.readByte()));
			case 2 -> createNbtItemStack(Items.SHORT_TAG, NbtShort.of(byteArrayDataInput.readShort()));
			case 3, 5, 6, 7 -> createSyntaxError("Number of bytes (" + bytes.length + ") is not power of 2");
			case 4 -> createNbtItemStack(Items.INT_TAG, NbtInt.of(byteArrayDataInput.readInt()));
			case 8 -> createNbtItemStack(Items.LONG_TAG, NbtLong.of(byteArrayDataInput.readLong()));
			default -> createSyntaxError("Total number of bytes (" + bytes.length + " exceeds 8");
		};
	}

	@Nullable
	private static AbstractNbtNumber getNbtNumberFromStack(ItemStack stack) {
		if (stack.getItem() instanceof NbtItem<?> nbtItem) {
			NbtElement nbtElement = nbtItem.getValue(stack);
			if (nbtElement instanceof AbstractNbtNumber) {
				return (AbstractNbtNumber)nbtElement;
			}
		}

		return null;
	}

	@Nullable
	private static byte[] stacksToByteArray(List<ItemStack> stacks) {
		ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();

		for (ItemStack itemStack : stacks) {
			if (!(itemStack.getItem() instanceof NbtItem<?> nbtItem)) {
				return null;
			}

			NbtElement nbtElement = nbtItem.getValue(itemStack);
			if (nbtElement instanceof NbtByte nbtByte) {
				byteArrayDataOutput.writeByte(nbtByte.byteValue());
			} else if (nbtElement instanceof NbtShort nbtShort) {
				byteArrayDataOutput.writeShort(nbtShort.shortValue());
			} else if (nbtElement instanceof NbtInt nbtInt) {
				byteArrayDataOutput.writeInt(nbtInt.intValue());
			} else {
				if (!(nbtElement instanceof NbtLong nbtLong)) {
					return null;
				}

				byteArrayDataOutput.writeLong(nbtLong.longValue());
			}
		}

		return byteArrayDataOutput.toByteArray();
	}

	private static ItemStack concatenateStackValues(List<ItemStack> stacks) {
		List<NbtElement> list = new ArrayList();

		for (ItemStack itemStack : stacks) {
			if (!(itemStack.getItem() instanceof NbtItem<?> nbtItem)) {
				return ItemStack.EMPTY;
			}

			list.add(nbtItem.getValue(itemStack));
		}

		if (list.size() < 2) {
			return ItemStack.EMPTY;
		} else {
			NbtElement nbtElement = (NbtElement)list.get(0);

			for (int i = 1; i < list.size(); i++) {
				NbtElement nbtElement2 = (NbtElement)list.get(i);
				NbtElement nbtElement3 = concatenateNbt(nbtElement, nbtElement2);
				if (nbtElement3 == null) {
					return createSyntaxError("Can't concatenate " + nbtElement.asString() + " with " + nbtElement2.asString());
				}

				nbtElement = nbtElement3;
			}

			if (nbtElement instanceof NbtCompound nbtCompound) {
				return createNbtItemStack(Items.COMPOUND_TAG, nbtCompound);
			} else if (nbtElement instanceof NbtList nbtList) {
				return createNbtItemStack(Items.LIST_TAG, nbtList);
			} else {
				return nbtElement instanceof NbtString nbtString ? createNbtItemStack(Items.STRING_TAG, nbtString) : ItemStack.EMPTY;
			}
		}
	}

	@Nullable
	private static NbtElement concatenateNbt(NbtElement first, NbtElement second) {
		if (first instanceof NbtCompound nbtCompound && second instanceof NbtCompound nbtCompound2) {
			return nbtCompound.copy().copyFrom(nbtCompound2);
		}

		if (first instanceof NbtList nbtList && second instanceof NbtList nbtList2) {
			NbtList nbtList3 = nbtList.copy();

			for (NbtElement nbtElement : nbtList2) {
				if (!nbtList3.addElement(nbtList3.size(), nbtElement)) {
					return null;
				}
			}

			return nbtList3;
		}

		if (first instanceof NbtString nbtString && second instanceof NbtString nbtString2) {
			return NbtString.of(nbtString.asString() + nbtString2.asString());
		}

		return null;
	}

	@Override
	public boolean fits(int width, int height) {
		return true;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.NBT_CRAFTING_RECIPE;
	}
}
