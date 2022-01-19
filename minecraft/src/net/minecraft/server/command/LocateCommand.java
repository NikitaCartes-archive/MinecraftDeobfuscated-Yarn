package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.feature.StructureFeature;

public class LocateCommand {
	private static final DynamicCommandExceptionType FAILED_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.locate.failed", object)
	);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("locate")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.argument("structure", IdentifierArgumentType.identifier())
						.suggests(SuggestionProviders.AVAILABLE_STRUCTURES)
						.executes(commandContext -> execute(commandContext.getSource(), IdentifierArgumentType.getStructureFeatureEntry(commandContext, "structure")))
				)
		);
	}

	private static int execute(ServerCommandSource source, IdentifierArgumentType.RegistryEntry<StructureFeature<?>> structureFeatureEntry) throws CommandSyntaxException {
		StructureFeature<?> structureFeature = structureFeatureEntry.resource();
		BlockPos blockPos = new BlockPos(source.getPosition());
		BlockPos blockPos2 = source.getWorld().locateStructure(structureFeature, blockPos, 100, false);
		Identifier identifier = structureFeatureEntry.id();
		if (blockPos2 == null) {
			throw FAILED_EXCEPTION.create(identifier);
		} else {
			return sendCoordinates(source, identifier.toString(), blockPos, blockPos2, "commands.locate.success");
		}
	}

	public static int sendCoordinates(ServerCommandSource source, String structure, BlockPos sourcePos, BlockPos structurePos, String successMessage) {
		int i = MathHelper.floor(getDistance(sourcePos.getX(), sourcePos.getZ(), structurePos.getX(), structurePos.getZ()));
		Text text = Texts.bracketed(new TranslatableText("chat.coordinates", structurePos.getX(), "~", structurePos.getZ()))
			.styled(
				style -> style.withColor(Formatting.GREEN)
						.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + structurePos.getX() + " ~ " + structurePos.getZ()))
						.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("chat.coordinates.tooltip")))
			);
		source.sendFeedback(new TranslatableText(successMessage, structure, text, i), false);
		return i;
	}

	private static float getDistance(int x1, int y1, int x2, int y2) {
		int i = x2 - x1;
		int j = y2 - y1;
		return MathHelper.sqrt((float)(i * i + j * j));
	}
}
