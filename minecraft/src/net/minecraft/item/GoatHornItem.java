package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.World;

public class GoatHornItem extends Item {
	private static final int field_39053 = 256;
	public static final String SOUND_VARIANT_KEY = "SoundVariant";
	private static final int field_39054 = 4;
	private static final int field_39055 = 4;
	private static final int field_39056 = 140;

	public GoatHornItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		NbtCompound nbtCompound = stack.getOrCreateNbt();
		MutableText mutableText = Text.translatable("item.minecraft.goat_horn.sound." + nbtCompound.getInt("SoundVariant"));
		tooltip.add(mutableText.formatted(Formatting.GRAY));
	}

	private static ItemStack getStackForSoundVariant(int soundVariant) {
		ItemStack itemStack = new ItemStack(Items.GOAT_HORN);
		setSoundVariant(itemStack, soundVariant);
		return itemStack;
	}

	public static ItemStack getStackForGoat(GoatEntity goat) {
		AbstractRandom abstractRandom = AbstractRandom.createAtomic((long)goat.getUuid().hashCode());
		return getStackForSoundVariant(getRandomSoundVariant(abstractRandom, goat.isScreaming()));
	}

	public static void setRandomSoundVariant(ItemStack stack, AbstractRandom random) {
		setSoundVariant(stack, random.nextInt(4));
	}

	private static void setSoundVariant(ItemStack stack, int soundVariant) {
		NbtCompound nbtCompound = stack.getOrCreateNbt();
		nbtCompound.put("SoundVariant", NbtInt.of(soundVariant));
	}

	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
		for (int i = 0; i < 8; i++) {
			stacks.add(getStackForSoundVariant(i));
		}
	}

	protected static int getRandomSoundVariant(AbstractRandom random, boolean screaming) {
		int i;
		int j;
		if (screaming) {
			i = 4;
			j = 7;
		} else {
			i = 0;
			j = 3;
		}

		return MathHelper.nextBetween(random, i, j);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		user.setCurrentHand(hand);
		SoundEvent soundEvent = (SoundEvent)SoundEvents.GOAT_HORN_SOUNDS.get(this.getSoundVariant(itemStack));
		playSound(world, user, soundEvent);
		user.getItemCooldownManager().set(Items.GOAT_HORN, 140);
		return TypedActionResult.consume(itemStack);
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 140;
	}

	private int getSoundVariant(ItemStack stack) {
		NbtCompound nbtCompound = stack.getNbt();
		return nbtCompound == null ? 0 : nbtCompound.getInt("SoundVariant");
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.TOOT_HORN;
	}

	private static void playSound(World world, PlayerEntity player, SoundEvent sound) {
		int i = 16;
		world.playSoundFromEntity(player, player, sound, SoundCategory.RECORDS, 16.0F, 1.0F);
	}
}
