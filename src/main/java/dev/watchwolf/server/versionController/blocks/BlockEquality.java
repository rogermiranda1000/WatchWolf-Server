package dev.watchwolf.server.versionController.blocks;

import com.google.gson.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class BlockEquality {
    public final String blockData;
    public final short blockId;
    public final byte blockSubId;

    public BlockEquality(String blockData, short blockID, byte blockSubId) {
        this.blockData = blockData;
        this.blockId = blockID;
        this.blockSubId = blockSubId;
    }

    public String getBlockData() {
        return blockData;
    }

    public short getBlockId() {
        return blockId;
    }

    public byte getBlockSubId() {
        return blockSubId;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof String) return ((String)o).equals(this.blockData); // object equals blockData?
        if (!(o instanceof BlockEquality)) return false;
        return ((BlockEquality)o).blockData.equals(this.blockData);
    }

    /**
     * Given a JSON with all the equalities between <1.13 blocks will get all equalities
     * @param blockJson Link to the downloaded <a href="https://gist.github.com/rogermiranda1000/6f6cafeacbf9c1da0043a583d15a313a">JSON</a>
     * @return List of all the elements inside the JSON, representing all the possible combinations of block equalities
     */
    public static List<BlockEquality> getAllBlockEqualities(InputStream blockJson) throws IOException {
        List<BlockEquality> r = new ArrayList<>();

        JsonArray objectArray = getJSON(blockJson);
        for (JsonElement e : objectArray) {
            JsonObject element = e.getAsJsonObject();

            String blockData = element.get("base").getAsString();
            String []id = element.get("default").getAsString().split(":");

            r.add(new BlockEquality( blockData, Short.parseShort(id[0]), Byte.parseByte(id[1]) ));
        }
        System.out.println(r.toString());

        return r;
    }

    private static JsonArray getJSON(InputStream blockJson) throws IOException {
        JsonParser parser = new JsonParser();
        Reader reader = new InputStreamReader(blockJson);

        JsonObject rootObj = parser.parse(reader).getAsJsonObject();
        JsonArray locObj = rootObj.getAsJsonArray("blocks");

        reader.close();
        return locObj;
    }
}
