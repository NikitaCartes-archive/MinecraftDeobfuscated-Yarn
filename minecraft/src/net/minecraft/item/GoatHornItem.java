package net.minecraft.item;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.TagKey;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class GoatHornItem extends Item {
	private static final String INSTRUMENT_KEY = "instrument";
	private TagKey<Instrument> instrumentTag;

	public GoatHornItem(Item.Settings settings, TagKey<Instrument> instrumentTag) {
		super(settings);
		this.instrumentTag = instrumentTag;
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		MutableText mutableText = Text.translatable(Util.createTranslationKey("instrument", getInstrumentId(stack)));
		tooltip.add(mutableText.formatted(Formatting.GRAY));
	}

	public static ItemStack getStackForInstrument(Item item, RegistryEntry<Instrument> instrument) {
		ItemStack itemStack = new ItemStack(item);
		setInstrument(itemStack, instrument);
		return itemStack;
	}

	public static void setRandomInstrumentFromTag(ItemStack stack, TagKey<Instrument> instrumentTag, AbstractRandom random) {
		Optional<RegistryEntry<Instrument>> optional = Registry.INSTRUMENT.getEntryList(instrumentTag).flatMap(entryList -> entryList.getRandom(random));
		if (optional.isPresent()) {
			setInstrument(stack, (RegistryEntry<Instrument>)optional.get());
		}
	}

	private static void setInstrument(ItemStack stack, RegistryEntry<Instrument> instrument) {
		NbtCompound nbtCompound = stack.getOrCreateNbt();
		nbtCompound.putString(
			"instrument", ((RegistryKey)instrument.getKey().orElseThrow(() -> new IllegalStateException("Invalid instrument"))).getValue().toString()
		);
	}

	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
		if (this.isIn(group)) {
			for (RegistryEntry<Instrument> registryEntry : Registry.INSTRUMENT.iterateEntries(this.instrumentTag)) {
				stacks.add(getStackForInstrument(Items.GOAT_HORN, registryEntry));
			}
		}
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		Instrument instrument = getInstrument(itemStack);
		if (instrument != null) {
			user.setCurrentHand(hand);
			playSound(world, user, instrument);
			user.getItemCooldownManager().set(this, instrument.useDuration());
			return TypedActionResult.consume(itemStack);
		} else {
			return TypedActionResult.fail(itemStack);
		}
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		Instrument instrument = getInstrument(stack);
		return instrument != null ? instrument.useDuration() : 0;
	}

	@Nullable
	private static Instrument getInstrument(ItemStack stack) {
		return Registry.INSTRUMENT.get(getInstrumentId(stack));
	}

	@Nullable
	private static Identifier getInstrumentId(ItemStack stack) {
		NbtCompound nbtCompound = stack.getNbt();
		return nbtCompound != null ? Identifier.tryParse(nbtCompound.getString("instrument")) : null;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.TOOT_HORN;
	}

	private static void playSound(World world, PlayerEntity player, Instrument instrument) {
		SoundEvent soundEvent = instrument.soundEvent();
		float f = instrument.range() / 16.0F;
		world.playSoundFromEntity(player, player, soundEvent, SoundCategory.RECORDS, f, 1.0F);
	}
}
