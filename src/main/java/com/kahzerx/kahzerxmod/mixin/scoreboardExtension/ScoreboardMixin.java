package com.kahzerx.kahzerxmod.mixin.scoreboardExtension;

import com.kahzerx.kahzerxmod.extensions.scoreboardExtension.ScoreboardExtension;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.ScoreboardPlayerUpdateS2CPacket;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ServerScoreboard.class)
public class ScoreboardMixin extends Scoreboard {
    @Shadow @Final private MinecraftServer server;

    @Inject(method = "createChangePackets", at = @At(value = "RETURN"))
    private void onCreate(ScoreboardObjective objective, CallbackInfoReturnable<List<Packet<?>>> cir) {
        if (ScoreboardExtension.isExtensionEnabled) {
            int i = 0;
            for (ScoreboardPlayerScore score : getAllPlayerScores(objective)) {
                i += score.getScore();
            }
            cir.getReturnValue().add(new ScoreboardPlayerUpdateS2CPacket(ServerScoreboard.UpdateMode.CHANGE, objective.getName(), Formatting.BOLD + "TOTAL", i));
        }
    }

    @Inject(method = "updateScore", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendToAll(Lnet/minecraft/network/Packet;)V"))
    private void onUpdate(ScoreboardPlayerScore score, CallbackInfo ci) {
        if (ScoreboardExtension.isExtensionEnabled) {
            ScoreboardObjective objective = score.getObjective();
            if (objective == null) {
                return;
            }
            int i = 0;
            for (ScoreboardPlayerScore sc : getAllPlayerScores(objective)) {
                i += sc.getScore();
            }
            server.getPlayerManager().sendToAll(new ScoreboardPlayerUpdateS2CPacket(ServerScoreboard.UpdateMode.CHANGE, objective.getName(), Formatting.BOLD + "TOTAL", i));
        }
    }
}
