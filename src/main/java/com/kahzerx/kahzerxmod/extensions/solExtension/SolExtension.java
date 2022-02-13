package com.kahzerx.kahzerxmod.extensions.solExtension;

import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SolExtension extends GenericExtension implements Extensions {
    private final String SOL_UUID = "91f6c774-013c-4fd2-865d-745fbf1e5524";
    private int solJoined = 0;
    private final String[] B64 = new String[]{"dXd1", "dGUgYW1v", "b2xhIHF0IHV3dQ==", "YmVzdCB3YWlmdQ==", "wqo=", "dGUgcXVpZXJv", "cXQ=", "bWF2YWxlZWVlcmlv", "PDM="};

    private static final List<ItemStack> fws = new ArrayList<>();
    private static final ItemStack SAKURA_TREE;
    private static final ItemStack ADMIN;
    private static final ItemStack BLANK;
    private static final ItemStack RAINY;
    private static final ItemStack FIRE;
    private static final ItemStack IG;
    private static final ItemStack LATTE;
    private static final ItemStack NONAME1;
    private static final ItemStack NONAME2;
    private static final ItemStack MAINCRA;
    private static final ItemStack NONAME3;
    private static final ItemStack SUPER_NENAS;
    private static final ItemStack NATURE;
    private static final ItemStack OCEAN;
    private static final ItemStack NONAME4;
    private static final ItemStack PRINCESS;
    private static final ItemStack SUN;
    private static final ItemStack RAINBOW;
    private static final ItemStack SUNRISE;
    private static final ItemStack ARGENTINA;
    private static final ItemStack ZAPAHORIAS;
    private static final ItemStack SHADES;
    private static final ItemStack PINK_LEMONADE;
    private static final ItemStack TODOROKI;
    private static final ItemStack FIJI;
    public SolExtension(ExtensionSettings settings) {
        super(settings);
    }

    static {
        SAKURA_TREE = createFirework(
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{8405535, 10247480, 16758232, 16762084, 16768754}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{16759544, 8405535, 10247480, 16758232, 16762084, 16768754, 16759544}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{8405535, 10247480, 16758232, 16762084, 16768754, 16759544}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{8405535}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{10247480, 16758232, 16762084, 16768754, 16759544, 8405535, 10247480, 16758232, 16762084, 16768754, 16759544})
        );
        ADMIN = createFirework(
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), 1, new int[]{13057598, 14373715, 13057598, 14373715}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{13057598, 14373715, 13057598, 14373715}),
                new FireworkHelper(FireworkRocketItem.Type.STAR.getId(), 1, new int[]{16777215}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{13057598, 14373715, 13057598, 14373715}),
                new FireworkHelper(FireworkRocketItem.Type.STAR.getId(), 1, new int[]{16777215}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{13057598, 14373715, 13057598, 14373715}),
                new FireworkHelper(FireworkRocketItem.Type.STAR.getId(), 1, new int[]{16777215})
        );
        BLANK = createFirework(
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), 1, new int[]{16777215, 16777215, 16777215, 16777215}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{16777215, 16777215, 16777215, 16777215}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{16777215, 16777215, 16777215, 16777215})
        );
        RAINY = createFirework(
                new FireworkHelper(FireworkRocketItem.Type.SMALL_BALL.getId(), 1, 1, new int[]{6066343, 11846573, 15000794, 12829635}),
                new FireworkHelper(FireworkRocketItem.Type.SMALL_BALL.getId(), 1, new int[]{10724260, 4543597, 6066343, 11846573, 15000794, 12829635, 10724260, 4543597}, new int[]{2714316, 2714316})
        );
        FIRE = createFirework(
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), 1, 1, new int[]{14032425, 15809846, 15954764, 16037719}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), 1, new int[]{16177762, 14032425, 15809846, 15954764, 16037719, 16177762}, new int[]{14032425})
        );
        IG = createFirework(
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{2636472, 9254814, 16736349, 16689497, 16704122}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{15807620, 2636472, 9254814, 15807620, 16736349, 16689497, 16704122}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{2636472, 9254814, 15807620, 16736349, 16689497, 16704122}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{2636472}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{9254814, 15807620, 16736349, 16689497, 16704122, 2636472, 9254814, 15807620, 16736349, 16689497, 16704122})
        );
        LATTE = createFirework(
                new FireworkHelper(FireworkRocketItem.Type.BURST.getId(), 1, new int[]{10780518, 11703160, 13877160, 15127738}),
                new FireworkHelper(FireworkRocketItem.Type.BURST.getId(), 1, new int[]{9003077, 10780518, 11703160, 13877160, 15127738, 9003077}),
                new FireworkHelper(FireworkRocketItem.Type.BURST.getId(), 1, new int[]{9003077, 10780518, 11703160, 13877160, 15127738, 9003077}),
                new FireworkHelper(FireworkRocketItem.Type.BURST.getId(), 1, new int[]{9003077, 10780518, 11703160, 13877160, 15127738, 9003077}),
                new FireworkHelper(FireworkRocketItem.Type.BURST.getId(), 1, new int[]{9003077, 10780518, 11703160, 13877160, 15127738, 9003077})
        );
        NONAME1 = createFirework(
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{270627}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{40100}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{10083555}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{13977409}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{16702842, 16758897})
        );
        NONAME2 = createFirework(
                new FireworkHelper(FireworkRocketItem.Type.BURST.getId(), 1, new int[]{14794471, 13955312, 16762363})
        );
        MAINCRA = createFirework(
                new FireworkHelper(FireworkRocketItem.Type.STAR.getId(), new int[]{4684318}),
                new FireworkHelper(FireworkRocketItem.Type.STAR.getId(), new int[]{7385655}),
                new FireworkHelper(FireworkRocketItem.Type.STAR.getId(), new int[]{9423452}),
                new FireworkHelper(FireworkRocketItem.Type.STAR.getId(), new int[]{6371103}),
                new FireworkHelper(FireworkRocketItem.Type.STAR.getId(), new int[]{8736555, 12747584}),
                new FireworkHelper(FireworkRocketItem.Type.STAR.getId(), new int[]{4684318, 7385655, 9423452, 6371103}),
                new FireworkHelper(FireworkRocketItem.Type.STAR.getId(), new int[]{8736555}),
                new FireworkHelper(FireworkRocketItem.Type.STAR.getId(), new int[]{12747584})
        );
        NONAME3 = createFirework(
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{16735436}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{14439103}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{12142513}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{9911716}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{7615126})
        );
        SUPER_NENAS = createFirework(
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), 1, new int[]{3386595, 10017521, 15362404, 15174793}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), 1, new int[]{4896300, 8506225, 3386595, 10017521, 15362404, 15174793, 4896300, 8506225, 283}, new int[]{283, 283})
        );
        NATURE = createFirework(
                new FireworkHelper(FireworkRocketItem.Type.SMALL_BALL.getId(), 1, new int[]{1536580, 3901265, 6200414}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), 1, new int[]{1536580, 3901265, 6200414}),
                new FireworkHelper(FireworkRocketItem.Type.SMALL_BALL.getId(), new int[]{8565356, 10864505, 13229190, 8565356, 10864505, 13229190}),
                new FireworkHelper(FireworkRocketItem.Type.SMALL_BALL.getId(), new int[]{8565356, 10864505, 13229190, 8565356, 10864505, 13229190}),
                new FireworkHelper(FireworkRocketItem.Type.SMALL_BALL.getId(), new int[]{8565356, 10864505, 13229190, 8565356, 10864505, 13229190})
        );
        OCEAN = createFirework(
                new FireworkHelper(FireworkRocketItem.Type.BURST.getId(), new int[]{11789544, 11129320, 10204139, 6587605, 8429544}),
                new FireworkHelper(FireworkRocketItem.Type.BURST.getId(), new int[]{7849197, 11789544, 11129320, 10204139, 6587605, 8429544, 7849197}),
                new FireworkHelper(FireworkRocketItem.Type.BURST.getId(), new int[]{7849197, 11789544, 11129320, 10204139, 6587605, 8429544, 7849197})
        );
        NONAME4 = createFirework(
                new FireworkHelper(FireworkRocketItem.Type.BURST.getId(), new int[]{9563112, 15399673, 12709370, 11462138, 10540280})
        );
        PRINCESS = createFirework(
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), 1, new int[]{15502525, 16358849, 16710118, 10743523}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), 0, 1, new int[]{7981529, 15502525, 16358849, 16710118, 10743523, 7981529}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), 0, 1, new int[]{7981529, 15502525, 16358849, 16710118, 10743523, 7981529}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), 0, 1, new int[]{7981529, 15502525, 16358849, 16710118, 10743523, 7981529})
        );
        SUN = createFirework(
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), 1, new int[]{13408512, 15122176, 16769024, 16773471}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), 1, new int[]{16776062, 16777113, 13408512, 15122176, 16769024, 16773471, 16776062, 16777113})
        );
        RAINBOW = createFirework(
                new FireworkHelper(FireworkRocketItem.Type.STAR.getId(), 1, new int[]{13975927, 15698267, 15916648, 7456096}),
                new FireworkHelper(FireworkRocketItem.Type.STAR.getId(), 1, new int[]{5280975, 8734145, 13975927, 15698267, 15916648, 7456096, 5280975, 8734145}),
                new FireworkHelper(FireworkRocketItem.Type.STAR.getId(), 1, new int[]{5280975, 8734145, 13975927, 15698267, 15916648, 7456096, 5280975, 8734145})
        );
        SUNRISE = createFirework(
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{9754593, 13951971, 16708812, 16572842, 16631198}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{16689801, 9754593, 13951971, 16708812, 16572842, 16631198, 16689801}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{9754593, 13951971, 16708812, 16572842, 16631198, 16689801}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{9754593}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{13951971, 16708812, 16572842, 16631198, 16689801, 9754593, 13951971, 16708812, 16572842, 16631198, 16689801})
        );
        ARGENTINA = createFirework(
                new FireworkHelper(FireworkRocketItem.Type.STAR.getId(), new int[]{11789544, 11129320, 16711422, 15260072}),
                new FireworkHelper(FireworkRocketItem.Type.STAR.getId(), new int[]{7849197, 11789544, 11129320, 16711422, 15260072, 7849197}),
                new FireworkHelper(FireworkRocketItem.Type.STAR.getId(), new int[]{7849197, 11789544, 11129320, 16711422, 15260072, 7849197}),
                new FireworkHelper(FireworkRocketItem.Type.STAR.getId(), new int[]{7849197, 11789544, 11129320, 16711422, 15260072, 7849197})
        );
        ZAPAHORIAS = createFirework(
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), 1, new int[]{15296295, 16750208, 16764321, 6939273, 6344840}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{15296295, 16750208, 16764321, 6939273, 6344840})
        );
        SHADES = createFirework(
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{11184810}),
                new FireworkHelper(FireworkRocketItem.Type.SMALL_BALL.getId(), new int[]{12303291}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{12303291}),
                new FireworkHelper(FireworkRocketItem.Type.SMALL_BALL.getId(), new int[]{14540253}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{15658734, 14540253}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{11184810, 12303291, 13421772, 14540253}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{15658734}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), new int[]{16777215})
        );
        PINK_LEMONADE = createFirework(
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), 0, 1, new int[]{16777151, 16773062, 16768717, 16764628}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), 0, 1, new int[]{16760283, 16777151, 16773062, 16768717, 16764628, 16760283}, new int[]{16764628, 16773062}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), 0, 1, new int[]{16760283, 16777151, 16773062, 16768717, 16764628, 16760283}, new int[]{16764628, 16773062})
        );
        TODOROKI = createFirework(
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), 1, 1, new int[]{14221312, 16777215, 15809846, 13955576}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), 1, new int[]{10865137, 7711708, 14221312, 16777215, 15809846, 13955576, 10865137, 7711708}, new int[]{14096673, 14096673}),
                new FireworkHelper(FireworkRocketItem.Type.LARGE_BALL.getId(), 1, new int[]{10865137, 7711708, 14221312, 16777215, 15809846, 13955576, 10865137, 7711708}, new int[]{14096673, 14096673})
        );
        FIJI = createFirework(
                new FireworkHelper(FireworkRocketItem.Type.SMALL_BALL.getId(), 1, new int[]{32896, 2332821, 4698282}),
                new FireworkHelper(FireworkRocketItem.Type.STAR.getId(), 1, new int[]{32896, 2332821, 4698282}),
                new FireworkHelper(FireworkRocketItem.Type.SMALL_BALL.getId(), new int[]{6998462, 9363923, 11663848, 6998462, 9363923, 11663848}),
                new FireworkHelper(FireworkRocketItem.Type.SMALL_BALL.getId(), new int[]{6998462, 9363923, 11663848, 6998462, 9363923, 11663848}),
                new FireworkHelper(FireworkRocketItem.Type.SMALL_BALL.getId(), new int[]{6998462, 9363923, 11663848, 6998462, 9363923, 11663848})
        );

        fws.add(SAKURA_TREE);
        fws.add(ADMIN);
        fws.add(BLANK);
        fws.add(RAINY);
        fws.add(FIRE);
        fws.add(IG);
        fws.add(LATTE);
        fws.add(NONAME1);
        fws.add(NONAME2);
        fws.add(MAINCRA);
        fws.add(NONAME3);
        fws.add(SUPER_NENAS);
        fws.add(NATURE);
        fws.add(OCEAN);
        fws.add(NONAME4);
        fws.add(PRINCESS);
        fws.add(SUN);
        fws.add(RAINBOW);
        fws.add(SUNRISE);
        fws.add(ARGENTINA);
        fws.add(ZAPAHORIAS);
        fws.add(SHADES);
        fws.add(PINK_LEMONADE);
        fws.add(TODOROKI);
        fws.add(FIJI);
    }

    @Override
    public void onTick(MinecraftServer server) {
        if (solJoined == -100) {
            return;
        }
        String v = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM"));
        if (v.equals("14-02") || v.equals("02-06") || v.startsWith("30-")) {
            if (server.getTicks() == solJoined) {
                ServerPlayerEntity player = server.getPlayerManager().getPlayer(UUID.fromString(SOL_UUID));
                if (player == null) {
                    solJoined = -100;
                    return;
                }
                FireworkRocketEntity f1 = new FireworkRocketEntity(player.getWorld(), player, player.getX() + 10, player.getY() + 10, player.getZ() + 10, fws.get(new Random().nextInt(fws.size())));
                FireworkRocketEntity f2 = new FireworkRocketEntity(player.getWorld(), player, player.getX(), player.getY() + 10, player.getZ() + 6, fws.get(new Random().nextInt(fws.size())));
                f1.noClip = true;
                f2.noClip = true;
                player.getWorld().spawnEntity(f1);
                player.getWorld().spawnEntity(f2);
            }
            if (server.getTicks() == solJoined + 10) {
                ServerPlayerEntity player = server.getPlayerManager().getPlayer(UUID.fromString(SOL_UUID));
                if (player == null) {
                    solJoined = -100;
                    return;
                }
                FireworkRocketEntity f1 = new FireworkRocketEntity(player.getWorld(), player, player.getX() - 10, player.getY() + 10, player.getZ() - 10, fws.get(new Random().nextInt(fws.size())));
                FireworkRocketEntity f2 = new FireworkRocketEntity(player.getWorld(), player, player.getX() - 6, player.getY() + 10, player.getZ(), fws.get(new Random().nextInt(fws.size())));
                f1.noClip = true;
                f2.noClip = true;
                player.getWorld().spawnEntity(f1);
                player.getWorld().spawnEntity(f2);
            }
            if (server.getTicks() == solJoined + 20) {
                ServerPlayerEntity player = server.getPlayerManager().getPlayer(UUID.fromString(SOL_UUID));
                if (player == null) {
                    solJoined = -100;
                    return;
                }
                FireworkRocketEntity f1 = new FireworkRocketEntity(player.getWorld(), player, player.getX() - 10, player.getY() + 10, player.getZ() + 10, fws.get(new Random().nextInt(fws.size())));
                FireworkRocketEntity f2 = new FireworkRocketEntity(player.getWorld(), player, player.getX() + 6, player.getY() + 10, player.getZ(), fws.get(new Random().nextInt(fws.size())));
                f1.noClip = true;
                f2.noClip = true;
                player.getWorld().spawnEntity(f1);
                player.getWorld().spawnEntity(f2);
                player.networkHandler.sendPacket(new TitleS2CPacket(new LiteralText(new String(Base64.getDecoder().decode(B64[new Random().nextInt(B64.length)])))));
            }
            if (server.getTicks() == solJoined + 30) {
                ServerPlayerEntity player = server.getPlayerManager().getPlayer(UUID.fromString(SOL_UUID));
                if (player == null) {
                    solJoined = -100;
                    return;
                }
                FireworkRocketEntity f1 = new FireworkRocketEntity(player.getWorld(), player, player.getX() + 10, player.getY() + 10, player.getZ() - 10, fws.get(new Random().nextInt(fws.size())));
                FireworkRocketEntity f2 = new FireworkRocketEntity(player.getWorld(), player, player.getX(), player.getY() + 10, player.getZ() - 6, fws.get(new Random().nextInt(fws.size())));
                f1.noClip = true;
                f2.noClip = true;
                player.getWorld().spawnEntity(f1);
                player.getWorld().spawnEntity(f2);
                solJoined = -100;
            }
        } else {
            solJoined = -100;
        }
    }

    @Override
    public void onPlayerJoined(ServerPlayerEntity player) {
        if (!player.getUuidAsString().equals(SOL_UUID)) {
            return;
        }
        if (player.getServer() == null) {
            return;
        }
        solJoined = player.getServer().getTicks() + (20 * 20);
    }

    @Override
    public ExtensionSettings extensionSettings() {
        return this.getSettings();
    }

    @Override
    public void onRegisterCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        new SolCommand().register(dispatcher, this);
    }

    private static ItemStack createFirework(FireworkHelper... fireworksConfigs) {
        ItemStack firework = new ItemStack(Items.FIREWORK_ROCKET, 1);
        ItemStack star = new ItemStack(Items.FIREWORK_STAR);
        NbtList explosions = new NbtList();
        for (FireworkHelper config : fireworksConfigs) {
            NbtCompound explosion = star.getOrCreateSubNbt(FireworkRocketItem.EXPLOSION_KEY).copy();
            explosion.putIntArray(FireworkRocketItem.COLORS_KEY, config.getColors());
            explosion.putByte(FireworkRocketItem.TYPE_KEY, (byte) config.getType());
            explosion.putByte(FireworkRocketItem.FLICKER_KEY, (byte) config.getFlicker());
            explosion.putByte(FireworkRocketItem.TRAIL_KEY, (byte) config.getTrail());
            if (config.getFadeColors().length != 0) {
                explosion.putIntArray(FireworkRocketItem.FADE_COLORS_KEY, config.getFadeColors());
            }
            explosions.add(explosion);
        }
        NbtCompound fireworks = firework.getOrCreateSubNbt(FireworkRocketItem.FIREWORKS_KEY);
        fireworks.putByte(FireworkRocketItem.FLIGHT_KEY, (byte) 1);
        fireworks.put(FireworkRocketItem.EXPLOSIONS_KEY, explosions);
        firework.getOrCreateNbt().putBoolean("Sol", true);
        return firework;
    }

    static class FireworkHelper {
        private final int type;
        private final int trail;
        private final int flicker;
        private final int[] colors;
        private final int[] fadeColors;
        public FireworkHelper(int type, int[] colors) {
            this.type = type;
            this.trail = 0;
            this.flicker = 0;
            this.colors = colors;
            this.fadeColors = new int[]{};
        }

        public FireworkHelper(int type, int trail, int[] colors) {
            this.type = type;
            this.trail = trail;
            this.flicker = 0;
            this.colors = colors;
            this.fadeColors = new int[]{};
        }

        public FireworkHelper(int type, int trail, int flicker, int[] colors) {
            this.type = type;
            this.trail = trail;
            this.flicker = flicker;
            this.colors = colors;
            this.fadeColors = new int[]{};
        }

        public FireworkHelper(int type, int trail, int flicker, int[] colors, int[] fadeColors) {
            this.type = type;
            this.trail = trail;
            this.flicker = flicker;
            this.colors = colors;
            this.fadeColors = fadeColors;
        }

        public FireworkHelper(int type, int trail, int[] colors, int[] fadeColors) {
            this.type = type;
            this.trail = trail;
            this.flicker = 0;
            this.colors = colors;
            this.fadeColors = fadeColors;
        }

        public int getTrail() {
            return trail;
        }

        public int getType() {
            return type;
        }

        public int[] getColors() {
            return colors;
        }

        public int getFlicker() {
            return flicker;
        }

        public int[] getFadeColors() {
            return fadeColors;
        }
    }
}
