package net.minecraft.item;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class GoatHornItem extends Item {
	private static final String INSTRUMENT_KEY = "instrument";
	private final TagKey<Instrument> instrumentTag;

	public GoatHornItem(Item.Settings settings, TagKey<Instrument> instrumentTag) {
		super(settings);
		this.instrumentTag = instrumentTag;
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		Optional<RegistryKey<Instrument>> optional = this.getInstrument(stack).flatMap(RegistryEntry::getKey);
		if (optional.isPresent()) {
			MutableText mutableText = Text.translatable(Util.createTranslationKey("instrument", ((RegistryKey)optional.get()).getValue()));
			tooltip.add(mutableText.formatted(Formatting.GRAY));
		}
	}

	public static ItemStack getStackForInstrument(Item item, RegistryEntry<Instrument> instrument) {
		ItemStack itemStack = new ItemStack(item);
		setInstrument(itemStack, instrument);
		return itemStack;
	}

	public static void setRandomInstrumentFromTag(ItemStack stack, TagKey<Instrument> instrumentTag, Random random) {
		Optional<RegistryEntry<Instrument>> optional = Registries.INSTRUMENT.getEntryList(instrumentTag).flatMap(entryList -> entryList.getRandom(random));
		optional.ifPresent(instrument -> setInstrument(stack, instrument));
	}

	private static void setInstrument(ItemStack stack, RegistryEntry<Instrument> instrument) {
		NbtCompound nbtCompound = stack.getOrCreateNbt();
		nbtCompound.putString(
			"instrument", ((RegistryKey)instrument.getKey().orElseThrow(() -> new IllegalStateException("Invalid instrument"))).getValue().toString()
		);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		Optional<? extends RegistryEntry<Instrument>> optional = this.getInstrument(itemStack);
		if (optional.isPresent()) {
			Instrument instrument = (Instrument)((RegistryEntry)optional.get()).value();
			user.setCurrentHand(hand);
			playSound(world, user, instrument);
			user.getItemCooldownManager().set(this, instrument.useDuration());
			user.incrementStat(Stats.USED.getOrCreateStat(this));
			return TypedActionResult.consume(itemStack);
		} else {
			return TypedActionResult.fail(itemStack);
		}
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		Optional<? extends RegistryEntry<Instrument>> optional = this.getInstrument(stack);
		return (Integer)optional.map(instrument -> ((Instrument)instrument.value()).useDuration()).orElse(0);
	}

	private Optional<? extends RegistryEntry<Instrument>> getInstrument(ItemStack stack) {
		NbtCompound nbtCompound = stack.getNbt();
		if (nbtCompound != null && nbtCompound.contains("instrument", 28)) {
			Identifier identifier = Identifier.tryParse(nbtCompound.getString("instrument"));
			if (identifier != null) {
				return Registries.INSTRUMENT.getEntry(RegistryKey.of(RegistryKeys.INSTRUMENT, identifier));
			}
		}

		Iterator<RegistryEntry<Instrument>> iterator = Registries.INSTRUMENT.iterateEntries(this.instrumentTag).iterator();
		return iterator.hasNext() ? Optional.of((RegistryEntry)iterator.next()) : Optional.empty();
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.TOOT_HORN;
	}

	private static void playSound(World world, PlayerEntity player, Instrument instrument) {
		SoundEvent soundEvent = instrument.soundEvent().value();
		float f = instrument.range() / 16.0F;
		world.playSoundFromEntity(player, player, soundEvent, SoundCategory.RECORDS, f, 1.0F);
		world.emitGameEvent(GameEvent.INSTRUMENT_PLAY, player.getPos(), GameEvent.Emitter.of(player));
	}
}
