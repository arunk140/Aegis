package com.beemdevelopment.aegis.db;

import com.beemdevelopment.aegis.encoding.Base64;
import com.beemdevelopment.aegis.encoding.Base64Exception;
import com.beemdevelopment.aegis.otp.GoogleAuthInfo;
import com.beemdevelopment.aegis.otp.OtpInfo;
import com.beemdevelopment.aegis.otp.OtpInfoException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class DatabaseEntry implements Serializable {
    private UUID _uuid;
    private String _name = "";
    private String _issuer = "";
    private String _group;
    private OtpInfo _info;
    private byte[] _icon;

    private DatabaseEntry(UUID uuid, OtpInfo info) {
        _uuid = uuid;
        _info = info;
    }

    public DatabaseEntry(OtpInfo info) {
        this(UUID.randomUUID(), info);
    }

    public DatabaseEntry(OtpInfo info, String name, String issuer) {
        this(info);
        setName(name);
        setIssuer(issuer);
    }

    public DatabaseEntry(GoogleAuthInfo info) {
        this(info.getOtpInfo(), info.getAccountName(), info.getIssuer());
    }

    public JSONObject toJson() {
        JSONObject obj = new JSONObject();

        try {
            obj.put("type", _info.getType());
            obj.put("uuid", _uuid.toString());
            obj.put("name", _name);
            obj.put("issuer", _issuer);
            obj.put("group", _group);
            obj.put("icon", _icon == null ? JSONObject.NULL : Base64.encode(_icon));
            obj.put("info", _info.toJson());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return obj;
    }

    public static DatabaseEntry fromJson(JSONObject obj) throws JSONException, OtpInfoException, Base64Exception {
        // if there is no uuid, generate a new one
        UUID uuid;
        if (!obj.has("uuid")) {
            uuid = UUID.randomUUID();
        } else {
            uuid = UUID.fromString(obj.getString("uuid"));
        }

        OtpInfo info = OtpInfo.fromJson(obj.getString("type"), obj.getJSONObject("info"));
        DatabaseEntry entry = new DatabaseEntry(uuid, info);
        entry.setName(obj.getString("name"));
        entry.setIssuer(obj.getString("issuer"));
        entry.setGroup(obj.optString("group", null));

        Object icon = obj.get("icon");
        if (icon != JSONObject.NULL) {
            entry.setIcon(Base64.decode((String) icon));
        }

        return entry;
    }

    public void resetUUID() {
        _uuid = UUID.randomUUID();
    }

    public UUID getUUID() {
        return _uuid;
    }

    public String getName() {
        return _name;
    }

    public String getIssuer() {
        return _issuer;
    }

    public String getGroup() {
        return _group;
    }

    public byte[] getIcon() {
        return _icon;
    }

    public OtpInfo getInfo() {
        return _info;
    }

    public void setName(String name) {
        _name = name;
    }

    public void setIssuer(String issuer) {
        _issuer = issuer;
    }

    public void setGroup(String group) {
        _group = group;
    }

    public void setInfo(OtpInfo info) {
        _info = info;
    }

    public void setIcon(byte[] icon) {
        _icon = icon;
    }

    public boolean hasIcon() {
        return _icon != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DatabaseEntry)) {
            return false;
        }

        DatabaseEntry entry = (DatabaseEntry) o;
        return getUUID().equals(entry.getUUID())
                && getName().equals(entry.getName())
                && getIssuer().equals(entry.getIssuer())
                && Objects.equals(getGroup(), entry.getGroup())
                && getInfo().equals(entry.getInfo())
                && Arrays.equals(getIcon(), entry.getIcon());
    }
}
