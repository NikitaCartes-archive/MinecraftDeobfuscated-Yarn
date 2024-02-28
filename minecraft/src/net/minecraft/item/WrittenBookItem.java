package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.StringHelper;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class WrittenBookItem extends Item {
	public WrittenBookItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public Text getName(ItemStack stack) {
		WrittenBookContentComponent writtenBookContentComponent = stack.get(DataComponentTypes.WRITTEN_BOOK_CONTENT);
		if (writtenBookContentComponent != null) {
			String string = writtenBookContentComponent.title().raw();
			if (!StringHelper.isBlank(string)) {
				return Text.literal(string);
			}
		}

		return super.getName(stack);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		WrittenBookContentComponent writtenBookContentComponent = stack.get(DataComponentTypes.WRITTEN_BOOK_CONTENT);
		if (writtenBookContentComponent != null) {
			if (!StringHelper.isBlank(writtenBookContentComponent.author())) {
				tooltip.add(Text.translatable("book.byAuthor", writtenBookContentComponent.author()).formatted(Formatting.GRAY));
			}

			tooltip.add(Text.translatable("book.generation." + writtenBookContentComponent.generation()).formatted(Formatting.GRAY));
		}
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		user.useBook(itemStack, hand);
		user.incrementStat(Stats.USED.getOrCreateStat(this));
		return TypedActionResult.success(itemStack, world.isClient());
	}

	public static boolean resolve(ItemStack book, ServerCommandSource commandSource, @Nullable PlayerEntity player) {
		WrittenBookContentComponent writtenBookContentComponent = book.get(DataComponentTypes.WRITTEN_BOOK_CONTENT);
		if (writtenBookContentComponent != null && !writtenBookContentComponent.resolved()) {
			WrittenBookContentComponent writtenBookContentComponent2 = writtenBookContentComponent.resolve(commandSource, player);
			if (writtenBookContentComponent2 != null) {
				book.set(DataComponentTypes.WRITTEN_BOOK_CONTENT, writtenBookContentComponent2);
				return true;
			}

			book.set(DataComponentTypes.WRITTEN_BOOK_CONTENT, writtenBookContentComponent.asResolved());
		}

		return false;
	}
}
