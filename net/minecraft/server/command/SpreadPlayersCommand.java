/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic4CommandExceptionType;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.Vec2ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.BlockView;

public class SpreadPlayersCommand {
    private static final Dynamic4CommandExceptionType FAILED_TEAMS_EXCEPTION = new Dynamic4CommandExceptionType((object, object2, object3, object4) -> new TranslatableComponent("commands.spreadplayers.failed.teams", object, object2, object3, object4));
    private static final Dynamic4CommandExceptionType FAILED_ENTITIES_EXCEPTION = new Dynamic4CommandExceptionType((object, object2, object3, object4) -> new TranslatableComponent("commands.spreadplayers.failed.entities", object, object2, object3, object4));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("spreadplayers").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.argument("center", Vec2ArgumentType.create()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("spreadDistance", FloatArgumentType.floatArg(0.0f)).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("maxRange", FloatArgumentType.floatArg(1.0f)).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("respectTeams", BoolArgumentType.bool()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", EntityArgumentType.entities()).executes(commandContext -> SpreadPlayersCommand.execute((ServerCommandSource)commandContext.getSource(), Vec2ArgumentType.getVec2(commandContext, "center"), FloatArgumentType.getFloat(commandContext, "spreadDistance"), FloatArgumentType.getFloat(commandContext, "maxRange"), BoolArgumentType.getBool(commandContext, "respectTeams"), EntityArgumentType.getEntities(commandContext, "targets")))))))));
    }

    private static int execute(ServerCommandSource serverCommandSource, Vec2f vec2f, float f, float g, boolean bl, Collection<? extends Entity> collection) throws CommandSyntaxException {
        Random random = new Random();
        double d = vec2f.x - g;
        double e = vec2f.y - g;
        double h = vec2f.x + g;
        double i = vec2f.y + g;
        Pile[] piles = SpreadPlayersCommand.makePiles(random, bl ? SpreadPlayersCommand.getPileCountRespectingTeams(collection) : collection.size(), d, e, h, i);
        SpreadPlayersCommand.spread(vec2f, f, serverCommandSource.getWorld(), random, d, e, h, i, piles, bl);
        double j = SpreadPlayersCommand.getMinimumDistance(collection, serverCommandSource.getWorld(), piles, bl);
        serverCommandSource.sendFeedback(new TranslatableComponent("commands.spreadplayers.success." + (bl ? "teams" : "entities"), piles.length, Float.valueOf(vec2f.x), Float.valueOf(vec2f.y), String.format(Locale.ROOT, "%.2f", j)), true);
        return piles.length;
    }

    private static int getPileCountRespectingTeams(Collection<? extends Entity> collection) {
        HashSet<AbstractTeam> set = Sets.newHashSet();
        for (Entity entity : collection) {
            if (entity instanceof PlayerEntity) {
                set.add(entity.getScoreboardTeam());
                continue;
            }
            set.add(null);
        }
        return set.size();
    }

    private static void spread(Vec2f vec2f, double d, ServerWorld serverWorld, Random random, double e, double f, double g, double h, Pile[] piles, boolean bl) throws CommandSyntaxException {
        int j;
        boolean bl2 = true;
        double i = 3.4028234663852886E38;
        for (j = 0; j < 10000 && bl2; ++j) {
            bl2 = false;
            i = 3.4028234663852886E38;
            for (int k = 0; k < piles.length; ++k) {
                Pile pile = piles[k];
                int l = 0;
                Pile pile2 = new Pile();
                for (int m = 0; m < piles.length; ++m) {
                    if (k == m) continue;
                    Pile pile3 = piles[m];
                    double n = pile.getDistance(pile3);
                    i = Math.min(n, i);
                    if (!(n < d)) continue;
                    ++l;
                    pile2.x = pile2.x + (pile3.x - pile.x);
                    pile2.z = pile2.z + (pile3.z - pile.z);
                }
                if (l > 0) {
                    pile2.x = pile2.x / (double)l;
                    pile2.z = pile2.z / (double)l;
                    double o = pile2.absolute();
                    if (o > 0.0) {
                        pile2.normalize();
                        pile.subtract(pile2);
                    } else {
                        pile.setPileLocation(random, e, f, g, h);
                    }
                    bl2 = true;
                }
                if (!pile.clamp(e, f, g, h)) continue;
                bl2 = true;
            }
            if (bl2) continue;
            for (Pile pile2 : piles) {
                if (pile2.isSafe(serverWorld)) continue;
                pile2.setPileLocation(random, e, f, g, h);
                bl2 = true;
            }
        }
        if (i == 3.4028234663852886E38) {
            i = 0.0;
        }
        if (j >= 10000) {
            if (bl) {
                throw FAILED_TEAMS_EXCEPTION.create(piles.length, Float.valueOf(vec2f.x), Float.valueOf(vec2f.y), String.format(Locale.ROOT, "%.2f", i));
            }
            throw FAILED_ENTITIES_EXCEPTION.create(piles.length, Float.valueOf(vec2f.x), Float.valueOf(vec2f.y), String.format(Locale.ROOT, "%.2f", i));
        }
    }

    private static double getMinimumDistance(Collection<? extends Entity> collection, ServerWorld serverWorld, Pile[] piles, boolean bl) {
        double d = 0.0;
        int i = 0;
        HashMap<AbstractTeam, Pile> map = Maps.newHashMap();
        for (Entity entity : collection) {
            Pile pile;
            if (bl) {
                AbstractTeam abstractTeam;
                AbstractTeam abstractTeam2 = abstractTeam = entity instanceof PlayerEntity ? entity.getScoreboardTeam() : null;
                if (!map.containsKey(abstractTeam)) {
                    map.put(abstractTeam, piles[i++]);
                }
                pile = (Pile)map.get(abstractTeam);
            } else {
                pile = piles[i++];
            }
            entity.requestTeleport((float)MathHelper.floor(pile.x) + 0.5f, pile.getY(serverWorld), (double)MathHelper.floor(pile.z) + 0.5);
            double e = Double.MAX_VALUE;
            for (Pile pile2 : piles) {
                if (pile == pile2) continue;
                double f = pile.getDistance(pile2);
                e = Math.min(f, e);
            }
            d += e;
        }
        if (collection.size() < 2) {
            return 0.0;
        }
        return d /= (double)collection.size();
    }

    private static Pile[] makePiles(Random random, int i, double d, double e, double f, double g) {
        Pile[] piles = new Pile[i];
        for (int j = 0; j < piles.length; ++j) {
            Pile pile = new Pile();
            pile.setPileLocation(random, d, e, f, g);
            piles[j] = pile;
        }
        return piles;
    }

    static class Pile {
        private double x;
        private double z;

        Pile() {
        }

        double getDistance(Pile pile) {
            double d = this.x - pile.x;
            double e = this.z - pile.z;
            return Math.sqrt(d * d + e * e);
        }

        void normalize() {
            double d = this.absolute();
            this.x /= d;
            this.z /= d;
        }

        float absolute() {
            return MathHelper.sqrt(this.x * this.x + this.z * this.z);
        }

        public void subtract(Pile pile) {
            this.x -= pile.x;
            this.z -= pile.z;
        }

        public boolean clamp(double d, double e, double f, double g) {
            boolean bl = false;
            if (this.x < d) {
                this.x = d;
                bl = true;
            } else if (this.x > f) {
                this.x = f;
                bl = true;
            }
            if (this.z < e) {
                this.z = e;
                bl = true;
            } else if (this.z > g) {
                this.z = g;
                bl = true;
            }
            return bl;
        }

        public int getY(BlockView blockView) {
            BlockPos blockPos = new BlockPos(this.x, 256.0, this.z);
            while (blockPos.getY() > 0) {
                if (blockView.getBlockState(blockPos = blockPos.down()).isAir()) continue;
                return blockPos.getY() + 1;
            }
            return 257;
        }

        public boolean isSafe(BlockView blockView) {
            BlockPos blockPos = new BlockPos(this.x, 256.0, this.z);
            while (blockPos.getY() > 0) {
                BlockState blockState = blockView.getBlockState(blockPos = blockPos.down());
                if (blockState.isAir()) continue;
                Material material = blockState.getMaterial();
                return !material.isLiquid() && material != Material.FIRE;
            }
            return false;
        }

        public void setPileLocation(Random random, double d, double e, double f, double g) {
            this.x = MathHelper.nextDouble(random, d, f);
            this.z = MathHelper.nextDouble(random, e, g);
        }
    }
}

