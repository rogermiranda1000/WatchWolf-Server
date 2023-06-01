package dev.watchwolf.server.timings;

import dev.watchwolf.server.ExtendedPetitionManager;
import dev.watchwolf.server.Server;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpigotTimingsManager extends ExtendedPetitionManager implements TimingsOperator {
    public SpigotTimingsManager(Server watchwolf, Plugin _watchwolf) {
        super(watchwolf, _watchwolf);
    }

    public SpigotTimingsManager(Server watchwolf) {
        this(watchwolf, watchwolf);
    }

    @Override
    public void startTimings() {
        try {
            this.getWatchWolf().runCommand("timings on");
        } catch (IOException ignore) {}
    }

    @Override
    public String stopTimings() throws TimeoutException, IllegalArgumentException {
        String result = null;
        try {
            result = this.getWatchWolf().runCommand("timings paste");

            Pattern p = Pattern.compile("https:\\/\\/www\\.spigotmc\\.org\\/go\\/timings\\?url=\\S+");
            Matcher m = p.matcher(result);
            if (!m.matches()) throw new IllegalArgumentException("The return of the timings doesn't contain an Spigot URL (got '" + result + "' instead)");
            result = m.group(); // get only the url; discard the rest

            this.getWatchWolf().runCommand("timings off");
        } catch (IOException ignore) {}
        return result;
    }
}
