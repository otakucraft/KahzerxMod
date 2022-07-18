package com.kahzerx.kahzerxmod.extensions.shopExtension;

import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.kahzerx.kahzerxmod.extensions.permsExtension.PermsExtension;
import com.kahzerx.kahzerxmod.extensions.shopExtension.bank.BankCommand;
import com.kahzerx.kahzerxmod.extensions.shopExtension.database.ShopDatabase;
import com.kahzerx.kahzerxmod.extensions.shopExtension.exchange.ExchangeCommand;
import com.kahzerx.kahzerxmod.extensions.shopExtension.parcel.ParcelCommand;
import com.kahzerx.kahzerxmod.extensions.shopExtension.parcel.ParcelsCommand;
import com.kahzerx.kahzerxmod.extensions.shopExtension.parcel.Parcels;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.UUID;

public class ShopExtension extends GenericExtension implements Extensions {
    private final HashMap<ServerPlayerEntity, BankInstance> accounts = new HashMap<>();
    private final Parcels parcels = new Parcels();
    private ShopDatabase db = new ShopDatabase();
    private MinecraftServer server;
    private final PermsExtension permsExtension;

    public ShopExtension(ExtensionSettings settings, PermsExtension permsExtension) {
        super(settings);
        this.permsExtension = permsExtension;
    }

    @Override
    public ExtensionSettings extensionSettings() {
        return this.getSettings();
    }

    @Override
    public void onRegisterCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        new ExchangeCommand().register(dispatcher, this);
        new BankCommand().register(dispatcher, this);
        new ParcelsCommand().register(dispatcher, this);
        new ParcelCommand().register(dispatcher, this);
    }

    @Override
    public void onServerRun(MinecraftServer minecraftServer) {
        this.server = minecraftServer;
    }

    @Override
    public void onServerStarted(MinecraftServer minecraftServer) {
        this.initBankInstance();
    }

    @Override
    public void onPlayerJoined(ServerPlayerEntity player) {
        accounts.remove(player);
        db.getQuery().insertPlayerUUID(player.getUuidAsString(), player.getName().getString());
        BankInstance.Exchanges ex = new BankInstance.Exchanges();
        ex.setDiamond(db.getQuery().getAlreadyExchangedItem(player, Items.DIAMOND));
        ex.setDiamondBlock(db.getQuery().getAlreadyExchangedItem(player, Items.DIAMOND_BLOCK));
        ex.setNetheriteIngot(db.getQuery().getAlreadyExchangedItem(player, Items.NETHERITE_INGOT));
        ex.setNetheriteBlock(db.getQuery().getAlreadyExchangedItem(player, Items.NETHERITE_BLOCK));
        ex.setNetheriteScrap(db.getQuery().getAlreadyExchangedItem(player, Items.NETHERITE_SCRAP));
        ex.setDebris(db.getQuery().getAlreadyExchangedItem(player, Items.ANCIENT_DEBRIS));
        accounts.put(player, new BankInstance(db.getQuery().getBalance(player), ex, db.getQuery().getTransfers(player, 1)));
    }

    @Override
    public void onPlayerLeft(ServerPlayerEntity player) {
        accounts.remove(player);
    }

    @Override
    public void onAutoSave(MinecraftServer server) {
        int tick = server.getTicks();
    }

    @Override
    public void onExtensionDisabled() {
        Extensions.super.onExtensionDisabled();
        accounts.clear();
    }

    @Override
    public void onExtensionEnabled() {
        Extensions.super.onExtensionEnabled();
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            this.onPlayerJoined(player);
        }
        this.initBankInstance();
    }

    @Override
    public void onCreateDatabase(String worldPath) {
        db = new ShopDatabase();
        db.initializeConnection(worldPath);
        db.getQuery().onCreateDatabase();
        parcels.createParcels(this.getDB().getQuery().loadParcels());
    }

    public HashMap<ServerPlayerEntity, BankInstance> getAccounts() {
        return accounts;
    }

    public BankInstance getBankAccount() {
        for (ServerPlayerEntity p : this.accounts.keySet()) {
            if (p.getUuidAsString().equals("00000000-0000-0000-0000-000000000000")) {
                return accounts.get(p);
            }
        }
        return null;
    }

    public ShopDatabase getDB() {
        return db;
    }

    private void initBankInstance() {
        String UUID_ = "00000000-0000-0000-0000-000000000000";
        String NAME = "Bank";
        ServerPlayerEntity bankPlayer = new ServerPlayerEntity(this.server, this.server.getOverworld(), new GameProfile(UUID.fromString(UUID_), NAME), null);
        this.onPlayerJoined(bankPlayer);
    }

    public PermsExtension getPermsExtension() {
        return permsExtension;
    }

    public Parcels getParcels() {
        return parcels;
    }
}
