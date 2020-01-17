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
    private static final Logger LOGGER = LogManager.getLogger();
    private static final long field_16268 = Duration.ofMillis(300L).toNanos();
    private final IntSupplier tickSupplier;
    private final ControllerImpl controller = new ControllerImpl();
    private final ControllerImpl field_16271 = new ControllerImpl();

    public DisableableProfiler(IntSupplier tickSupplier) {
        this.tickSupplier = tickSupplier;
    }

    public Controller getController() {
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
    public void push(String location) {
        this.controller.profiler.push(location);
        this.field_16271.profiler.push(location);
    }

    @Override
    public void push(Supplier<String> locationGetter) {
        this.controller.profiler.push(locationGetter);
        this.field_16271.profiler.push(locationGetter);
    }

    @Override
    public void pop() {
        this.controller.profiler.pop();
        this.field_16271.profiler.pop();
    }

    @Override
    public void swap(String location) {
        this.controller.profiler.swap(location);
        this.field_16271.profiler.swap(location);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void swap(Supplier<String> locationGetter) {
        this.controller.profiler.swap(locationGetter);
        this.field_16271.profiler.swap(locationGetter);
    }

    @Override
    public void visit(String marker) {
        this.controller.profiler.visit(marker);
        this.field_16271.profiler.visit(marker);
    }

    @Override
    public void visit(Supplier<String> markerGetter) {
        this.controller.profiler.visit(markerGetter);
        this.field_16271.profiler.visit(markerGetter);
    }

    class ControllerImpl
    implements Controller {
        protected ReadableProfiler profiler = DummyProfiler.INSTANCE;

        private ControllerImpl() {
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
                this.profiler = new ProfilerSystem(Util.getMeasuringTimeNano(), DisableableProfiler.this.tickSupplier, true);
            }
        }
    }

    public static interface Controller {
        public boolean isEnabled();

        public ProfileResult disable();

        @Environment(value=EnvType.CLIENT)
        public ProfileResult getResults();

        public void enable();
    }
}

