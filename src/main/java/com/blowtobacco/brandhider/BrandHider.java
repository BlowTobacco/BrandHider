package com.blowtobacco.brandhider;

import java.util.List;
import java.util.ArrayList;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Plugin(
        id = "brandhider",
        name = "BrandHider",
        version = "3.0",
        description = "Hides and customizes server branding",
        authors = {"BlowTobacco"}
)
public class BrandHider {

    private static final MinecraftChannelIdentifier BRAND_CHANNEL =
            MinecraftChannelIdentifier.from("minecraft:brand");

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;

    private String customBrand;
    private String motdVersion;
    private String prefix;

    @Inject
    public BrandHider(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }


    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        loadConfig();
        server.getChannelRegistrar().register(BRAND_CHANNEL);
        registerCommands();
        printStartupBanner();
    }


    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getIdentifier().equals(BRAND_CHANNEL)) return;

        if (event.getSource() instanceof RegisteredServer && event.getTarget() instanceof Player) {
            event.setResult(PluginMessageEvent.ForwardResult.handled());
            sendCustomBrand((Player) event.getTarget());
        }
    }
// bag pula n karma bogdi ez
    @Subscribe
    public void onServerPostConnect(ServerPostConnectEvent event) {
        server.getScheduler()
                .buildTask(this, () -> sendCustomBrand(event.getPlayer()))
                .delay(Duration.ofMillis(500))
                .schedule();
    }

    private void sendCustomBrand(Player player) {
        try {
            byte[] brandBytes = translateColorCodes(customBrand)
                    .getBytes(StandardCharsets.UTF_8);

            var buffer = new java.io.ByteArrayOutputStream();
            var out = new java.io.DataOutputStream(buffer);

            writeVarInt(out, brandBytes.length);
            out.write(brandBytes);

            player.sendPluginMessage(BRAND_CHANNEL, buffer.toByteArray());
        } catch (Exception e) {
            logger.error("Failed to send brand to " + player.getUsername(), e);
        }
    }


    @Subscribe
    public void onProxyPing(ProxyPingEvent event) {
        ServerPing ping = event.getPing();

        ServerPing.Version version = new ServerPing.Version(
                ping.getVersion().getProtocol(),
                motdVersion
        );

        event.setPing(new ServerPing(
                version,
                ping.getPlayers().orElse(null),
                ping.getDescriptionComponent(),
                ping.getFavicon().orElse(null),
                ping.getModinfo().orElse(null)
        ));
    }


    private void registerCommands() {
        CommandManager cm = server.getCommandManager();

        CommandMeta meta = cm.metaBuilder("brandhider")
                .aliases("bh")
                .plugin(this)
                .build();

        cm.register(meta, new BrandHiderCommand());
    }
    
    private Component cc(String text) {
        return LegacyComponentSerializer.legacySection().deserialize(
                translateColorCodes(text)
        );
    }


    private class BrandHiderCommand implements SimpleCommand {

        @Override
        public void execute(Invocation inv) {
            String[] args = inv.arguments();

            if (args.length == 0) {
                inv.source().sendMessage(Component.empty());

                inv.source().sendMessage(cc(
                        "&7Running &#C62626&lB&#D13D3D&lr&#DD5454&la&#E86B6B&ln" +
                        "&#F48282&ld&#FF9999&lH&#F17C7C&li&#E36060&ld&#D44343&le&#C62626&lr &7v3.0"
                ));

                inv.source().sendMessage(cc(
                        "&7Developer: &#C62626B&#D13D3Dl&#DD5454o&#E86B6Bw&#F48282T" +
                        "&#FF9999o&#F48282b&#E86B6Ba&#DD5454c&#D13D3Dc&#C62626o"
                ));

                inv.source().sendMessage(cc("&7Use &b/brandhider help &7for command info."));
                inv.source().sendMessage(Component.empty());
                return;
            }

            if (args[0].equalsIgnoreCase("help")) {
                inv.source().sendMessage(Component.empty());
                inv.source().sendMessage(cc("&#C62626C&#D44343o&#E36060m&#F17C7Cm&#FF9999a&#EC7373n&#D94C4Cd&#C62626s"));
                inv.source().sendMessage(cc("&b/brandhider help &7- Shows this menu"));
                inv.source().sendMessage(cc("&b/brandhider version &7- Plugin version"));

                if (inv.source() instanceof ConsoleCommandSource) {
                    inv.source().sendMessage(cc("&b/brandhider reload &7- Reload config"));
                }

                inv.source().sendMessage(Component.empty());
                return;
            }

            if (args[0].equalsIgnoreCase("version")) {
                inv.source().sendMessage(Component.empty());
                inv.source().sendMessage(cc(
                        "&#C62626&lBrandHider &7version &#FF9999&lv3.0"
                ));
                inv.source().sendMessage(Component.empty());
                return;
            }

            if (args[0].equalsIgnoreCase("reload")) {
                if (!(inv.source() instanceof ConsoleCommandSource)) return;

                loadConfig();
                server.getAllPlayers().forEach(BrandHider.this::sendCustomBrand);
                logger.info("BrandHider config reloaded.");
            }
        }

        @Override
        public List<String> suggest(Invocation inv) {
            List<String> suggestions = new ArrayList<>();

            if (inv.arguments().length == 0) {
                suggestions.add("help");
                suggestions.add("version");
                if (inv.source() instanceof ConsoleCommandSource) {
                    suggestions.add("reload");
                }
            }

            return suggestions;
        }

        @Override
        public boolean hasPermission(Invocation invocation) {
            return true;
        }
    }


    private void loadConfig() {
        try {
            Files.createDirectories(dataDirectory);
            Path configPath = dataDirectory.resolve("config.yml");

            if (!Files.exists(configPath)) {
                try (var in = getClass().getResourceAsStream("/config.yml")) {
                    if (in == null) throw new IllegalStateException("config.yml missing from resources");
                    Files.copy(in, configPath);
                }
            }

            YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                    .path(configPath)
                    .build();

            CommentedConfigurationNode root = loader.load();

            customBrand = root.node("custom-brand").getString("BrandHider-3.0");
            motdVersion = root.node("motd-version").getString("BrandHider-3.0");
            prefix = root.node("messages", "prefix").getString("");

        } catch (Exception e) {
            logger.error("Failed to load config.yml", e);
            customBrand = "BrandHider-3.0";
            motdVersion = "BrandHider-3.0";
            prefix = "";
        }
    }


    private void writeVarInt(java.io.DataOutputStream out, int value) throws IOException {
        while ((value & ~0x7F) != 0) {
            out.writeByte((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        out.writeByte(value);
    }

    private String translateColorCodes(String text) {
        Pattern hex = Pattern.compile("&#([0-9a-fA-F]{6})");
        Matcher m = hex.matcher(text);
        StringBuffer sb = new StringBuffer();

        while (m.find()) {
            String h = m.group(1);
            StringBuilder r = new StringBuilder("§x");
            for (char c : h.toCharArray()) r.append('§').append(c);
            m.appendReplacement(sb, r.toString());
        }
        m.appendTail(sb);

        return sb.toString().replace("&", "§");
    }


    private void printStartupBanner() {
        logger.info("  _    _ _     _");
        logger.info(" | |  | (_)   | |");
        logger.info(" | |__| |_  __| | ___");
        logger.info(" |  __  | |/ _` |/ _ \\");
        logger.info(" | |  | | | (_| |  __/");
        logger.info(" |_|  |_|_|\\__,_|\\___|");
        logger.info("Author: BlowTobacco");
        logger.info("Version: v3.0");
    }
}
