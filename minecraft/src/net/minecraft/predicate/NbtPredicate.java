package net.minecraft.predicate;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringNbtReader;

public record NbtPredicate(NbtCompound nbt) {
	public static final Codec<NbtPredicate> CODEC = StringNbtReader.STRINGIFIED_CODEC.xmap(NbtPredicate::new, NbtPredicate::nbt);

	public boolean test(ItemStack stack) {
		return this.test(stack.getNbt());
	}

	public boolean test(Entity entity) {
		return this.test(entityToNbt(entity));
	}

	public boolean test(@Nullable NbtElement element) {
		return element != null && NbtHelper.matches(this.nbt, element, true);
	}

	public static NbtCompound entityToNbt(Entity entity) {
		NbtCompound nbtCompound = entity.writeNbt(new NbtCompound());
		if (entity instanceof PlayerEntity) {
			ItemStack itemStack = ((PlayerEntity)entity).getInventory().getMainHandStack();
			if (!itemStack.isEmpty()) {
				nbtCompound.put("SelectedItem", itemStack.writeNbt(new NbtCompound()));
			}
		}

		return nbtCompound;
	}
}
