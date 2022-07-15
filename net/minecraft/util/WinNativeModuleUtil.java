/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.Version;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import net.minecraft.util.crash.CrashReportSection;
import org.slf4j.Logger;

public class WinNativeModuleUtil {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int CODE_PAGE_MASK = 65535;
    private static final int EN_US_CODE_PAGE = 1033;
    private static final int LANGUAGE_ID_MASK = -65536;
    private static final int LANGUAGE_ID = 0x4B00000;

    public static List<NativeModule> collectNativeModules() {
        if (!Platform.isWindows()) {
            return ImmutableList.of();
        }
        int i = Kernel32.INSTANCE.GetCurrentProcessId();
        ImmutableList.Builder builder = ImmutableList.builder();
        List<Tlhelp32.MODULEENTRY32W> list = Kernel32Util.getModules(i);
        for (Tlhelp32.MODULEENTRY32W mODULEENTRY32W : list) {
            String string = mODULEENTRY32W.szModule();
            Optional<NativeModuleInfo> optional = WinNativeModuleUtil.createNativeModuleInfo(mODULEENTRY32W.szExePath());
            builder.add(new NativeModule(string, optional));
        }
        return builder.build();
    }

    private static Optional<NativeModuleInfo> createNativeModuleInfo(String path) {
        try {
            IntByReference intByReference = new IntByReference();
            int i = Version.INSTANCE.GetFileVersionInfoSize(path, intByReference);
            if (i == 0) {
                int j = Native.getLastError();
                if (j == 1813 || j == 1812) {
                    return Optional.empty();
                }
                throw new Win32Exception(j);
            }
            Memory pointer = new Memory(i);
            if (!Version.INSTANCE.GetFileVersionInfo(path, 0, i, pointer)) {
                throw new Win32Exception(Native.getLastError());
            }
            IntByReference intByReference2 = new IntByReference();
            Pointer pointer2 = WinNativeModuleUtil.query(pointer, "\\VarFileInfo\\Translation", intByReference2);
            int[] is = pointer2.getIntArray(0L, intByReference2.getValue() / 4);
            OptionalInt optionalInt = WinNativeModuleUtil.getEnglishTranslationIndex(is);
            if (!optionalInt.isPresent()) {
                return Optional.empty();
            }
            int k = optionalInt.getAsInt();
            int l = k & 0xFFFF;
            int m = (k & 0xFFFF0000) >> 16;
            String string = WinNativeModuleUtil.queryString(pointer, WinNativeModuleUtil.getStringFileInfoPath("FileDescription", l, m), intByReference2);
            String string2 = WinNativeModuleUtil.queryString(pointer, WinNativeModuleUtil.getStringFileInfoPath("CompanyName", l, m), intByReference2);
            String string3 = WinNativeModuleUtil.queryString(pointer, WinNativeModuleUtil.getStringFileInfoPath("FileVersion", l, m), intByReference2);
            return Optional.of(new NativeModuleInfo(string, string3, string2));
        } catch (Exception exception) {
            LOGGER.info("Failed to find module info for {}", (Object)path, (Object)exception);
            return Optional.empty();
        }
    }

    private static String getStringFileInfoPath(String key, int languageId, int codePage) {
        return String.format(Locale.ROOT, "\\StringFileInfo\\%04x%04x\\%s", languageId, codePage, key);
    }

    private static OptionalInt getEnglishTranslationIndex(int[] indices) {
        OptionalInt optionalInt = OptionalInt.empty();
        for (int i : indices) {
            if ((i & 0xFFFF0000) == 0x4B00000 && (i & 0xFFFF) == 1033) {
                return OptionalInt.of(i);
            }
            optionalInt = OptionalInt.of(i);
        }
        return optionalInt;
    }

    private static Pointer query(Pointer pointer, String path, IntByReference lengthPointer) {
        PointerByReference pointerByReference = new PointerByReference();
        if (!Version.INSTANCE.VerQueryValue(pointer, path, pointerByReference, lengthPointer)) {
            throw new UnsupportedOperationException("Can't get version value " + path);
        }
        return pointerByReference.getValue();
    }

    private static String queryString(Pointer pointer, String path, IntByReference lengthPointer) {
        try {
            Pointer pointer2 = WinNativeModuleUtil.query(pointer, path, lengthPointer);
            byte[] bs = pointer2.getByteArray(0L, (lengthPointer.getValue() - 1) * 2);
            return new String(bs, StandardCharsets.UTF_16LE);
        } catch (Exception exception) {
            return "";
        }
    }

    public static void addDetailTo(CrashReportSection section) {
        section.add("Modules", () -> WinNativeModuleUtil.collectNativeModules().stream().sorted(Comparator.comparing(module -> module.path)).map(moduleName -> "\n\t\t" + moduleName).collect(Collectors.joining()));
    }

    public static class NativeModule {
        public final String path;
        public final Optional<NativeModuleInfo> info;

        public NativeModule(String path, Optional<NativeModuleInfo> info) {
            this.path = path;
            this.info = info;
        }

        public String toString() {
            return this.info.map(info -> this.path + ":" + info).orElse(this.path);
        }
    }

    public static class NativeModuleInfo {
        public final String fileDescription;
        public final String fileVersion;
        public final String companyName;

        public NativeModuleInfo(String fileDescription, String fileVersion, String companyName) {
            this.fileDescription = fileDescription;
            this.fileVersion = fileVersion;
            this.companyName = companyName;
        }

        public String toString() {
            return this.fileDescription + ":" + this.fileVersion + ":" + this.companyName;
        }
    }
}

