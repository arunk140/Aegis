package com.beemdevelopment.aegis.importers;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.beemdevelopment.aegis.db.DatabaseEntry;
import com.beemdevelopment.aegis.util.ByteInputStream;

public abstract class DatabaseImporter {
    private static Map<String, Class<? extends DatabaseImporter>> _importers;
    static {
        // note: keep this list sorted alphabetically
        LinkedHashMap<String, Class<? extends DatabaseImporter>> importers = new LinkedHashMap<>();
        importers.put("Aegis", AegisImporter.class);
        importers.put("andOTP", AndOtpImporter.class);
        importers.put("FreeOTP", FreeOtpImporter.class);
        _importers = Collections.unmodifiableMap(importers);
    }

    protected ByteInputStream _stream;

    protected DatabaseImporter(ByteInputStream stream) {
        _stream = stream;
    }

    public abstract void parse() throws DatabaseImporterException;

    public abstract List<DatabaseEntry> convert() throws DatabaseImporterException;

    public abstract boolean isEncrypted();

    public static DatabaseImporter create(ByteInputStream stream, Class<? extends DatabaseImporter> type) {
        try {
            return type.getConstructor(ByteInputStream.class).newInstance(stream);
        } catch (IllegalAccessException | InstantiationException
                | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Class<? extends DatabaseImporter>> getImporters() {
        return _importers;
    }
}
