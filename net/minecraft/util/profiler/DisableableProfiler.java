/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.profiler;

import java.time.Duration;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.DummyProfiler;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.ProfilerSystem;
import net.minecraft.util.profiler.ReadableProfiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DisableableProfiler
implements Profiler {
    private static final Logger field_19286 = LogManager.getLogger();
    private static final long field_16268 = Duration.ofMillis(300L).toNanos();
    private final IntSupplier tickSupplier;
    private final ProfilerControllerImpl controller = new ProfilerControllerImpl();
    private final ProfilerControllerImpl field_16271 = new ProfilerControllerImpl();

    public DisableableProfiler(IntSupplier intSupplier) {
        this.tickSupplier = intSupplier;
    }

    public ProfilerController getController() {
        return this.controller;
    }

    @Override
    public void startTick() {
        this.controller.profiler.startTick();
        this.field_16271.profiler.startTick();
    }

    @Override
    public void endTick() {
        this.controller.profiler.endTick();
        this.field_16271.profiler.endTick();
    }

    @Override
    public void push(String string) {
        this.controller.profiler.push(string);
        this.field_16271.profiler.push(string);
    }

    @Override
    public void push(Supplier<String> supplier) {
        this.controller.profiler.push(supplier);
        this.field_16271.profiler.push(supplier);
    }

    @Override
    public void pop() {
        this.controller.profiler.pop();
        this.field_16271.profiler.pop();
    }

    @Override
    public void swap(String string) {
        this.controller.profiler.swap(string);
        this.field_16271.profiler.swap(string);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void swap(Supplier<String> supplier) {
        this.controller.profiler.swap(supplier);
        this.field_16271.profiler.swap(supplier);
    }

    class ProfilerControllerImpl
    implements ProfilerController {
        protected ReadableProfiler profiler = DummyProfiler.INSTANCE;

        private ProfilerControllerImpl() {
        }

        @Override
        public boolean isEnabled() {
            return this.profiler != DummyProfiler.INSTANCE;
        }

        @Override
        public ProfileResult disable() {
            ProfileResult profileResult = this.profiler.getResult();
            this.profiler = DummyProfiler.INSTANCE;
            return profileResult;
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public ProfileResult getResults() {
            return this.profiler.getResult();
        }

        @Override
        public void enable() {
            if (this.profiler == DummyProfiler.INSTANCE) {
                this.profiler = new ProfilerSystem(Util.getMeasuringTimeNano(), DisableableProfiler.this.tickSupplier);
            }
        }
    }

    public static interface ProfilerController {
        public boolean isEnabled();

        public ProfileResult disable();

        @Environment(value=EnvType.CLIENT)
        public ProfileResult getResults();

        public void enable();
    }
}

