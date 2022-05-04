/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt;

import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtEnd;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.NbtTagSizeTracker;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.NbtTypes;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.util.FixedBufferInputStream;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.jetbrains.annotations.Nullable;

/**
 * A set of utility functions for reading, writing, and scanning NBT files.
 */
public class NbtIo {
    /**
     * Reads an NBT compound from Gzip-compressed {@code file}.
     * 
     * @return the NBT compound from the file
     * @throws IOException if the IO operation fails or if the root NBT element is
     * not a compound
     * @see #readCompressed(InputStream)
     */
    public static NbtCompound readCompressed(File file) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(file);){
            NbtCompound nbtCompound = NbtIo.readCompressed(inputStream);
            return nbtCompound;
        }
    }

    /**
     * {@return a new input stream that decompresses the input {@code stream}}
     */
    private static DataInputStream decompress(InputStream stream) throws IOException {
        return new DataInputStream(new FixedBufferInputStream(new GZIPInputStream(stream)));
    }

    /**
     * Reads an NBT compound from Gzip-compressed {@code stream}.
     * 
     * @return the NBT compound from the stream
     * @throws IOException if the IO operation fails or if the root NBT element is
     * not a compound
     * @see #readCompressed(File)
     */
    public static NbtCompound readCompressed(InputStream stream) throws IOException {
        try (DataInputStream dataInputStream = NbtIo.decompress(stream);){
            NbtCompound nbtCompound = NbtIo.read(dataInputStream, NbtTagSizeTracker.EMPTY);
            return nbtCompound;
        }
    }

    /**
     * Scans the compressed NBT file using {@code scanner}.
     * 
     * @apiNote This method does not return the scan result; the user is expected
     * to call the appropriate method of the {@link NbtScanner} subclasses, such as
     * {@link net.minecraft.nbt.scanner.NbtCollector#getRoot()}.
     * 
     * @throws IOException if the IO operation fails
     * @see #scanCompressed(InputStream, NbtScanner)
     */
    public static void scanCompressed(File file, NbtScanner scanner) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(file);){
            NbtIo.scanCompressed(inputStream, scanner);
        }
    }

    /**
     * Scans the compressed NBT stream using {@code scanner}.
     * 
     * @apiNote This method does not return the scan result; the user is expected
     * to call the appropriate method of the {@link NbtScanner} subclasses, such as
     * {@link net.minecraft.nbt.scanner.NbtCollector#getRoot()}.
     * 
     * @throws IOException if the IO operation fails
     * @see #scanCompressed(File, NbtScanner)
     */
    public static void scanCompressed(InputStream stream, NbtScanner scanner) throws IOException {
        try (DataInputStream dataInputStream = NbtIo.decompress(stream);){
            NbtIo.scan(dataInputStream, scanner);
        }
    }

    /**
     * Writes the Gzip-compressed {@code nbt} to {@code file}.
     * 
     * @throws IOException if the IO operation fails
     * @see #writeCompressed(NbtCompound, OutputStream)
     */
    public static void writeCompressed(NbtCompound nbt, File file) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(file);){
            NbtIo.writeCompressed(nbt, outputStream);
        }
    }

    /**
     * Writes the Gzip-compressed {@code nbt} to {@code stream}.
     * 
     * @throws IOException if the IO operation fails
     * @see #writeCompressed(NbtCompound, File)
     */
    public static void writeCompressed(NbtCompound nbt, OutputStream stream) throws IOException {
        try (DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(stream)));){
            NbtIo.write(nbt, (DataOutput)dataOutputStream);
        }
    }

    /**
     * Writes the {@code nbt} to {@code file}.
     * 
     * @throws IOException if the IO operation fails
     * @see #write(NbtCompound, DataOutput)
     */
    public static void write(NbtCompound nbt, File file) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);){
            NbtIo.write(nbt, (DataOutput)dataOutputStream);
        }
    }

    /**
     * Reads an NBT compound from {@code file}.
     * 
     * @return the NBT compound from the file, or {@code null} if the file does not exist
     * @throws IOException if the IO operation fails or if the root NBT element is
     * not a compound
     */
    @Nullable
    public static NbtCompound read(File file) throws IOException {
        if (!file.exists()) {
            return null;
        }
        try (FileInputStream fileInputStream = new FileInputStream(file);){
            NbtCompound nbtCompound;
            try (DataInputStream dataInputStream = new DataInputStream(fileInputStream);){
                nbtCompound = NbtIo.read(dataInputStream, NbtTagSizeTracker.EMPTY);
            }
            return nbtCompound;
        }
    }

    /**
     * Reads an NBT compound from {@code input}.
     * 
     * @return the NBT compound from the input
     * @throws IOException if the IO operation fails or if the root NBT element is
     * not a compound
     */
    public static NbtCompound read(DataInput input) throws IOException {
        return NbtIo.read(input, NbtTagSizeTracker.EMPTY);
    }

    /**
     * Reads an NBT compound from {@code input}.
     * 
     * @return the NBT compound from the input
     * @throws IOException if the IO operation fails or if the root NBT element is
     * not a compound
     */
    public static NbtCompound read(DataInput input, NbtTagSizeTracker tracker) throws IOException {
        NbtElement nbtElement = NbtIo.read(input, 0, tracker);
        if (nbtElement instanceof NbtCompound) {
            return (NbtCompound)nbtElement;
        }
        throw new IOException("Root tag must be a named compound tag");
    }

    /**
     * Writes the {@code nbt} to {@code file}.
     * 
     * @throws IOException if the IO operation fails
     * @see #write(NbtCompound, File)
     */
    public static void write(NbtCompound nbt, DataOutput output) throws IOException {
        NbtIo.write((NbtElement)nbt, output);
    }

    /**
     * Scans the NBT input using {@code scanner}.
     * 
     * @apiNote This method does not return the scan result; the user is expected
     * to call the appropriate method of the {@link NbtScanner} subclasses, such as
     * {@link net.minecraft.nbt.scanner.NbtCollector#getRoot()}.
     * 
     * @throws IOException if the IO operation fails
     */
    public static void scan(DataInput input, NbtScanner scanner) throws IOException {
        NbtType<?> nbtType = NbtTypes.byId(input.readByte());
        if (nbtType == NbtEnd.TYPE) {
            if (scanner.start(NbtEnd.TYPE) == NbtScanner.Result.CONTINUE) {
                scanner.visitEnd();
            }
            return;
        }
        switch (scanner.start(nbtType)) {
            case HALT: {
                break;
            }
            case BREAK: {
                NbtString.skip(input);
                nbtType.skip(input);
                break;
            }
            case CONTINUE: {
                NbtString.skip(input);
                nbtType.doAccept(input, scanner);
            }
        }
    }

    public static void write(NbtElement nbt, DataOutput output) throws IOException {
        output.writeByte(nbt.getType());
        if (nbt.getType() == 0) {
            return;
        }
        output.writeUTF("");
        nbt.write(output);
    }

    private static NbtElement read(DataInput input, int depth, NbtTagSizeTracker tracker) throws IOException {
        byte b = input.readByte();
        if (b == 0) {
            return NbtEnd.INSTANCE;
        }
        NbtString.skip(input);
        try {
            return NbtTypes.byId(b).read(input, depth, tracker);
        } catch (IOException iOException) {
            CrashReport crashReport = CrashReport.create(iOException, "Loading NBT data");
            CrashReportSection crashReportSection = crashReport.addElement("NBT Tag");
            crashReportSection.add("Tag type", b);
            throw new CrashException(crashReport);
        }
    }
}

