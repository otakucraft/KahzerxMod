package com.kahzerx.kahzerxmod;

import com.kahzerx.kahzerxmod.config.KSettings;
import com.kahzerx.kahzerxmod.database.ServerDatabase;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.utils.FileUtils;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.WorldSavePath;

import java.util.ArrayList;
import java.util.List;

public class KahzerxServer {
    public static MinecraftServer minecraftServer;
    public static List<Extensions> extensions = new ArrayList<>();
    public static ServerDatabase db = new ServerDatabase();
    public static CommandDispatcher<ServerCommandSource> dispatcher;
    // TODO !exadd !exremove !ban !pardon
    // TODO limpieza de whitelist con discord.

    // TODO comandos de discord para detener el bot y eso.
    // TODO member extension es demasiado complejo, comprobar si el member tiene un team, si no tiene nada darle MIEMBRO, no hace falta tabla.
    // TODO block info que lea cuando sacas items de containers.
    // TODO si te cambias de nombre no te va a dar lo de MIEMBRO.
    // TODO hacer que se puedan des-activar las extensiones sin reiniciar.
    // TODO comando para habilitar o deshabilitar extensiones desde la array de extensions.
    // TODO implementar onExtensionEnabled y onExtensionDisabled.
    // TODO comando de reload para recargar el archivo de configuración.
    // TODO grafana!!
    // TODO que el bot responda a agradecimientos XD.
    // TODO thread para los get del block info y aumentar el máximo de rows.
    // TODO hacer que perms autocomplete por uuid y no por weas de gente conectada.
    // TODO igual hacer que la lista de auto-completar de perms te de los valores o en string o en int para los niveles.
    // TODO comandos de admin.
    // TODO que sea el propio mod el que cree el team miembro.
    // TODO cosas fancy para la gente que se añade a la whitelist tipo un !info para ver tu perfil de mc o algo así idk.
    // TODO copias de seguridad de la base de datos.
    // TODO copias de seguridad del mundo.
    // TODO crear las extensions con decorators para añadir enable, nombre de la extensión y la descripción.
    // TODO spaghetti is everywhere!!
    // TODO spoof inventory hace cosas raras si sacas y metes items de los slots, puede hacer que pierdas el item.
    // TODO /spoof no va si el player está offline.
    // TODO /perms no va si el player está offline.
    // TODO Comentar XD

    public static void onRunServer(MinecraftServer minecraftServer) {
        KahzerxServer.minecraftServer = minecraftServer;
        ExtensionManager.manageExtensions(FileUtils.loadConfig(minecraftServer.getSavePath(WorldSavePath.ROOT).toString()));

        extensions.forEach(e -> e.onServerRun(minecraftServer));

        List<ExtensionSettings> settingsArray = new ArrayList<>();
        for (Extensions ex : extensions) {
            settingsArray.add(ex.extensionSettings());
        }
        KSettings settings = new KSettings(settingsArray);
        FileUtils.createConfig(minecraftServer.getSavePath(WorldSavePath.ROOT).toString(), settings);

        extensions.forEach(e -> e.onRegisterCommands(dispatcher));
    }

    public static void onCreateDatabase() {
        db = new ServerDatabase();
        db.initializeConnection(minecraftServer.getSavePath(WorldSavePath.ROOT).toString());
        db.createPlayerTable();
        extensions.forEach(e -> e.onCreateDatabase(db.getConnection()));
    }

    public static void onRegisterCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        KahzerxServer.dispatcher = dispatcher;
    }

    public static void onStopServer() {
        db.close();
        extensions.forEach(Extensions::onServerStop);
    }

    public static void onAutoSave() {
        extensions.forEach(Extensions::onAutoSave);
    }

    public static void onPlayerJoined(ServerPlayerEntity player) {
        db.getQuery().insertPlayerUUID(player.getUuidAsString(), player.getName().getString());
        extensions.forEach(e -> e.onPlayerJoined(player));
    }

    public static void onPlayerLeft(ServerPlayerEntity player) {
        extensions.forEach(e -> e.onPlayerLeft(player));
    }

    public static void onPlayerDied(ServerPlayerEntity player) {
        extensions.forEach(e -> e.onPlayerDied(player));
    }

    public static void onChatMessage(ServerPlayerEntity player, String chatMessage) {
        extensions.forEach(e -> e.onChatMessage(player, chatMessage));
    }

    public static void onAdvancement(String advancement) {
        extensions.forEach(e -> e.onAdvancement(advancement));
    }
}
