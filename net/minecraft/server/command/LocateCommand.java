/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Pair;
import net.minecraft.class_7066;
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
    private static final DynamicCommandExceptionType FAILED_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.locate.failed", object));
    private static final DynamicCommandExceptionType field_37038 = new DynamicCommandExceptionType(object -> new TranslatableText("commands.locate.invalid", object));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("locate").requires(source -> source.hasPermissionLevel(2))).then(CommandManager.argument("structure", class_7066.method_41170(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY)).executes(commandContext -> LocateCommand.execute((ServerCommandSource)commandContext.getSource(), class_7066.method_41171(commandContext, "structure")))));
    }

    private static int execute(ServerCommandSource source, class_7066.class_7068<ConfiguredStructureFeature<?, ?>> arg) throws CommandSyntaxException {
        Registry<ConfiguredStructureFeature<?, ?>> registry = source.getWorld().getRegistryManager().get(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY);
        RegistryEntryList registryEntryList = (RegistryEntryList)arg.method_41173().map(registryKey -> registry.getEntry((RegistryKey<ConfiguredStructureFeature<?, ?>>)registryKey).map(registryEntry -> RegistryEntryList.of(registryEntry)), registry::getEntryList).orElseThrow(() -> field_37038.create(arg.method_41176()));
        BlockPos blockPos = new BlockPos(source.getPosition());
        ServerWorld serverWorld = source.getWorld();
        Pair<BlockPos, RegistryEntry<ConfiguredStructureFeature<?, ?>>> pair = serverWorld.getChunkManager().getChunkGenerator().locateStructure(serverWorld, registryEntryList, blockPos, 100, false);
        if (pair == null) {
            throw FAILED_EXCEPTION.create(arg.method_41176());
        }
        return LocateCommand.sendCoordinates(source, arg, blockPos, pair, "commands.locate.success");
    }

    public static int sendCoordinates(ServerCommandSource source, class_7066.class_7068<?> arg, BlockPos blockPos, Pair<BlockPos, ? extends RegistryEntry<?>> pair, String successMessage) {
        BlockPos blockPos2 = pair.getFirst();
        String string = arg.method_41173().map(registryKey -> registryKey.getValue().toString(), tagKey -> "#" + tagKey.id() + " (" + ((RegistryEntry)pair.getSecond()).getKey().map(registryKey -> registryKey.getValue().toString()).orElse("[unregistered]") + ")");
        int i = MathHelper.floor(LocateCommand.getDistance(blockPos.getX(), blockPos.getZ(), blockPos2.getX(), blockPos2.getZ()));
        MutableText text = Texts.bracketed(new TranslatableText("chat.coordinates", blockPos2.getX(), "~", blockPos2.getZ())).styled(style -> style.withColor(Formatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + blockPos2.getX() + " ~ " + blockPos2.getZ())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("chat.coordinates.tooltip"))));
        source.sendFeedback(new TranslatableText(successMessage, string, text, i), false);
        return i;
    }

    private static float getDistance(int x1, int y1, int x2, int y2) {
        int i = x2 - x1;
        int j = y2 - y1;
        return MathHelper.sqrt(i * i + j * j);
    }
}

