/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.mojang.realmsclient.dto.RealmsServerAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4394;
import net.minecraft.class_4398;
import net.minecraft.class_4434;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class class_4414
extends RealmsScreen {
    private static final Logger field_20045 = LogManager.getLogger();
    private final RealmsScreen field_20046;
    private final RealmsServerAddress field_20047;
    private final ReentrantLock field_20048;

    public class_4414(RealmsScreen realmsScreen, RealmsServerAddress realmsServerAddress, ReentrantLock reentrantLock) {
        this.field_20046 = realmsScreen;
        this.field_20047 = realmsServerAddress;
        this.field_20048 = reentrantLock;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void confirmResult(boolean bl, int i) {
        try {
            if (!bl) {
                Realms.setScreen(this.field_20046);
            } else {
                try {
                    ((CompletableFuture)Realms.downloadResourcePack(this.field_20047.resourcePackUrl, this.field_20047.resourcePackHash).thenRun(() -> {
                        class_4398 lv = new class_4398(this.field_20046, new class_4434.class_4438(this.field_20046, this.field_20047));
                        lv.method_21288();
                        Realms.setScreen(lv);
                    })).exceptionally(throwable -> {
                        Realms.clearResourcePack();
                        field_20045.error(throwable);
                        Realms.setScreen(new class_4394("Failed to download resource pack!", this.field_20046));
                        return null;
                    });
                } catch (Exception exception) {
                    Realms.clearResourcePack();
                    field_20045.error(exception);
                    Realms.setScreen(new class_4394("Failed to download resource pack!", this.field_20046));
                }
            }
        } finally {
            if (this.field_20048 != null && this.field_20048.isHeldByCurrentThread()) {
                this.field_20048.unlock();
            }
        }
    }
}

