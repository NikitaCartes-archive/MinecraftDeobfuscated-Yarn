package net.minecraft.item;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.consume.UseAction;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class GoatHornItem extends Item {
	private final TagKey<Instrument> instrumentTag;

	public GoatHornItem(Item.Settings settings, TagKey<Instrument> instrumentTag) {
		super(settings);
		this.instrumentTag = instrumentTag;
	}

	@Override
	public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
		super.appendTooltip(stack, context, tooltip, type);
		RegistryWrapper.WrapperLookup wrapperLookup = context.getRegistryLookup();
		if (wrapperLookup != null) {
			Optional<RegistryEntry<Instrument>> optional = this.getInstrument(stack, wrapperLookup);
			if (optional.isPresent()) {
				MutableText mutableText = ((Instrument)((RegistryEntry)optional.get()).value()).description().copy();
				Texts.setStyleIfAbsent(mutableText, Style.EMPTY.withColor(Formatting.GRAY));
				tooltip.add(mutableText);
			}
		}
	}

	public static ItemStack getStackForInstrument(Item item, RegistryEntry<Instrument> instrument) {
		ItemStack itemStack = new ItemStack(item);
		itemStack.set(DataComponentTypes.INSTRUMENT, instrument);
		return itemStack;
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		Optional<? extends RegistryEntry<Instrument>> optional = this.getInstrument(itemStack, user.getRegistryManager());
		if (optional.isPresent()) {
			Instrument instrument = (Instrument)((RegistryEntry)optional.get()).value();
			user.setCurrentHand(hand);
			playSound(world, user, instrument);
			user.getItemCooldownManager().set(itemStack, MathHelper.floor(instrument.useDuration() * 20.0F));
			user.incrementStat(Stats.USED.getOrCreateStat(this));
			return ActionResult.CONSUME;
		} else {
			return ActionResult.FAIL;
		}
	}

	@Override
	public int getMaxUseTime(ItemStack stack, LivingEntity user) {
		Optional<RegistryEntry<Instrument>> optional = this.getInstrument(stack, user.getRegistryManager());
		return (Integer)optional.map(instrument -> MathHelper.floor(((Instrument)instrument.value()).useDuration() * 20.0F)).orElse(0);
	}

	private Optional<RegistryEntry<Instrument>> getInstrument(ItemStack stack, RegistryWrapper.WrapperLookup registries) {
		RegistryEntry<Instrument> registryEntry = stack.get(DataComponentTypes.INSTRUMENT);
		if (registryEntry != null) {
			return Optional.of(registryEntry);
		} else {
			Optional<RegistryEntryList.Named<Instrument>> optional = registries.getOrThrow(RegistryKeys.INSTRUMENT).getOptional(this.instrumentTag);
			if (optional.isPresent()) {
				Iterator<RegistryEntry<Instrument>> iterator = ((RegistryEntryList.Named)optional.get()).iterator();
				if (iterator.hasNext()) {
					return Optional.of((RegistryEntry)iterator.next());
				}
			}

			return Optional.empty();
		}
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
