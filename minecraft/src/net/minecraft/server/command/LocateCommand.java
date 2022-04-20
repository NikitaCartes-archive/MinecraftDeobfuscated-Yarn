package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import net.minecraft.command.argument.RegistryPredicateArgumentType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.gen.structure.StructureType;

public class LocateCommand {
	private static final DynamicCommandExceptionType FAILED_EXCEPTION = new DynamicCommandExceptionType(id -> Text.method_43469("commands.locate.failed", id));
	private static final DynamicCommandExceptionType INVALID_EXCEPTION = new DynamicCommandExceptionType(id -> Text.method_43469("commands.locate.invalid", id));

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("locate")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.argument("structure", RegistryPredicateArgumentType.registryPredicate(Registry.STRUCTURE_KEY))
						.executes(context -> execute(context.getSource(), RegistryPredicateArgumentType.getConfiguredStructureFeaturePredicate(context, "structure")))
				)
		);
	}

	private static int execute(ServerCommandSource source, RegistryPredicateArgumentType.RegistryPredicate<StructureType> structureFeature) throws CommandSyntaxException {
		Registry<StructureType> registry = source.getWorld().getRegistryManager().get(Registry.STRUCTURE_KEY);
		RegistryEntryList<StructureType> registryEntryList = (RegistryEntryList<StructureType>)structureFeature.getKey()
			.<Optional>map(key -> registry.getEntry(key).map(entry -> RegistryEntryList.of(entry)), registry::getEntryList)
			.orElseThrow(() -> INVALID_EXCEPTION.create(structureFeature.asString()));
		BlockPos blockPos = new BlockPos(source.getPosition());
		ServerWorld serverWorld = source.getWorld();
		Pair<BlockPos, RegistryEntry<StructureType>> pair = serverWorld.getChunkManager()
			.getChunkGenerator()
			.locateStructure(serverWorld, registryEntryList, blockPos, 100, false);
		if (pair == null) {
			throw FAILED_EXCEPTION.create(structureFeature.asString());
		} else {
			return sendCoordinates(source, structureFeature, blockPos, pair, "commands.locate.success", false);
		}
	}

	public static int sendCoordinates(
		ServerCommandSource source,
		RegistryPredicateArgumentType.RegistryPredicate<?> structureFeature,
		BlockPos currentPos,
		Pair<BlockPos, ? extends RegistryEntry<?>> structurePosAndEntry,
		String successMessage,
		boolean bl
	) {
		BlockPos blockPos = structurePosAndEntry.getFirst();
		String string = structureFeature.getKey()
			.map(
				key -> key.getValue().toString(),
				key -> "#" + key.id() + " (" + (String)structurePosAndEntry.getSecond().getKey().map(keyx -> keyx.getValue().toString()).orElse("[unregistered]") + ")"
			);
		int i = bl
			? MathHelper.floor(MathHelper.sqrt((float)currentPos.getSquaredDistance(blockPos)))
			: MathHelper.floor(getDistance(currentPos.getX(), currentPos.getZ(), blockPos.getX(), blockPos.getZ()));
		String string2 = bl ? String.valueOf(blockPos.getY()) : "~";
		Text text = Texts.bracketed(Text.method_43469("chat.coordinates", blockPos.getX(), string2, blockPos.getZ()))
			.styled(
				style -> style.withColor(Formatting.GREEN)
						.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + blockPos.getX() + " " + string2 + " " + blockPos.getZ()))
						.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.method_43471("chat.coordinates.tooltip")))
			);
		source.sendFeedback(Text.method_43469(successMessage, string, text, i), false);
		return i;
	}

	private static float getDistance(int x1, int y1, int x2, int y2) {
		int i = x2 - x1;
		int j = y2 - y1;
		return MathHelper.sqrt((float)(i * i + j * j));
	}
}
