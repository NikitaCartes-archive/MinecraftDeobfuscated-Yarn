/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Pair;
import net.minecraft.command.argument.RegistryPredicateArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;

public class LocateCommand {
    private static final DynamicCommandExceptionType FAILED_EXCEPTION = new DynamicCommandExceptionType(id -> new TranslatableText("commands.locate.failed", id));
    private static final DynamicCommandExceptionType INVALID_EXCEPTION = new DynamicCommandExceptionType(id -> new TranslatableText("commands.locate.invalid", id));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("locate").requires(source -> source.hasPermissionLevel(2))).then(CommandManager.argument("structure", RegistryPredicateArgumentType.registryPredicate(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY)).executes(context -> LocateCommand.execute((ServerCommandSource)context.getSource(), RegistryPredicateArgumentType.getConfiguredStructureFeaturePredicate(context, "structure")))));
    }

    private static int execute(ServerCommandSource source, RegistryPredicateArgumentType.RegistryPredicate<ConfiguredStructureFeature<?, ?>> structureFeature) throws CommandSyntaxException {
        Registry<ConfiguredStructureFeature<?, ?>> registry = source.getWorld().getRegistryManager().get(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY);
        RegistryEntryList registryEntryList = (RegistryEntryList)structureFeature.getKey().map(key -> registry.getEntry((RegistryKey<ConfiguredStructureFeature<?, ?>>)key).map(entry -> RegistryEntryList.of(entry)), registry::getEntryList).orElseThrow(() -> INVALID_EXCEPTION.create(structureFeature.asString()));
        BlockPos blockPos = new BlockPos(source.getPosition());
        ServerWorld serverWorld = source.getWorld();
        Pair<BlockPos, RegistryEntry<ConfiguredStructureFeature<?, ?>>> pair = serverWorld.getChunkManager().getChunkGenerator().locateStructure(serverWorld, registryEntryList, blockPos, 100, false);
        if (pair == null) {
            throw FAILED_EXCEPTION.create(structureFeature.asString());
        }
        return LocateCommand.sendCoordinates(source, structureFeature, blockPos, pair, "commands.locate.success");
    }

    public static int sendCoordinates(ServerCommandSource source, RegistryPredicateArgumentType.RegistryPredicate<?> structureFeature, BlockPos currentPos, Pair<BlockPos, ? extends RegistryEntry<?>> structurePosAndEntry, String successMessage) {
        BlockPos blockPos = structurePosAndEntry.getFirst();
        String string = structureFeature.getKey().map(key -> key.getValue().toString(), key2 -> "#" + key2.id() + " (" + ((RegistryEntry)structurePosAndEntry.getSecond()).getKey().map(key -> key.getValue().toString()).orElse("[unregistered]") + ")");
        int i = MathHelper.floor(LocateCommand.getDistance(currentPos.getX(), currentPos.getZ(), blockPos.getX(), blockPos.getZ()));
        MutableText text = Texts.bracketed(new TranslatableText("chat.coordinates", blockPos.getX(), "~", blockPos.getZ())).styled(style -> style.withColor(Formatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + blockPos.getX() + " ~ " + blockPos.getZ())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("chat.coordinates.tooltip"))));
        source.sendFeedback(new TranslatableText(successMessage, string, text, i), false);
        return i;
    }

    private static float getDistance(int x1, int y1, int x2, int y2) {
        int i = x2 - x1;
        int j = y2 - y1;
        return MathHelper.sqrt(i * i + j * j);
    }
}

