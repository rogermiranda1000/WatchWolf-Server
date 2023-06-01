package dev.watchwolf.server;

import dev.watchwolf.entities.files.ConfigFile;
import dev.watchwolf.server.timings.TimingsManagerFactory;
import dev.watchwolf.server.timings.TimingsOperator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class EnhancedInformationProvider extends ExtendedPetitionManager implements EnhancedInformationServerPetition {
    private TimingsOperator timingsOperator;

    public EnhancedInformationProvider(Server watchwolf, Plugin _watchwolf) {
        super(watchwolf, _watchwolf);

        this.timingsOperator = TimingsManagerFactory.build(watchwolf);
    }

    @Override
    public void startTimings() throws IOException {
        this.timingsOperator.startTimings();
    }

    @Override
    public ConfigFile stopTimings() throws IOException {
        String timingsPath = null;
        boolean timeoutRaised;
        do {
            try {
                timingsPath = this.timingsOperator.stopTimings();
                timeoutRaised = false;
            } catch (TimeoutException ignore) {
                timeoutRaised = true;
                try { Thread.sleep(30_000); } catch (InterruptedException ignored) {}
            }
        } while (timeoutRaised);

        // TODO generate a redirect file to timingsPath's url
        return new ConfigFile("index.html", timingsPath.getBytes(StandardCharsets.UTF_8), "");
    }
}
