package dev.watchwolf.server.versionController.blocks;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
     * @param blockJsonPath Path to the downloaded <a href="https://gist.github.com/rogermiranda1000/6f6cafeacbf9c1da0043a583d15a313a">JSON</a>. Keep in mind this is from the root of the .jar
     * @return List of all the elements inside the JSON, representing all the possible combinations of block equalities
     */
    public static List<BlockEquality> getAllBlockEqualities(String jarPath, String blockJsonPath) throws IOException {
        List<BlockEquality> r = new ArrayList<>();
        // TODO
        getJSON(jarPath, blockJsonPath);
        return r;
    }

    private static void getJSON(String jarPath, String jsonPath) throws IOException {
        final File jarFile = new File(jarPath);
        File jsonFile = null;

        if(jarFile.isFile()) {  // Run with JAR file
            final JarFile jar = new JarFile(jarFile);
            final Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
            while(entries.hasMoreElements() && jsonFile == null) {
                final String name = entries.nextElement().getName();
                //if (name.startsWith(jsonPath + "/")) { //filter according to the path
                    System.out.println(name);
                //}
            }
            jar.close();
        }

        if (jsonFile == null) throw new IOException("JSON file '" + jsonPath + "' not found");
        Gson gson = new Gson();
        //gson.fromJson(new FileReader(jsonFile))
        // TODO get array from '.blocks'
    }
}
