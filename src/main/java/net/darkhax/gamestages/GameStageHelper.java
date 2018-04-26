package net.darkhax.gamestages;

import java.util.Arrays;

import net.darkhax.gamestages.data.GameStageSaveHandler;
import net.darkhax.gamestages.data.IStageData;
import net.darkhax.gamestages.event.GameStageEvent;
import net.darkhax.gamestages.packet.PacketSyncClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;

public class GameStageHelper {

    /**
     * Checks if a player has a stage. This will also fire the check event which can be used to
     * change the result.
     *
     * @param player The player to check the stages of.
     * @param stage The stage to look for.
     * @return Whether or not the player has access to this stage.
     */
    public static boolean hasStage (EntityPlayer player, String stage) {

        return hasStage(player, getPlayerData(player), stage);
    }

    /**
     * Internal helper method to fire a stage check. It's used to reduce the total amount of
     * code needed for the event firing, and improve performance for multiple stage lookups.
     *
     * @param player The player to check the stages of.
     * @param data The player's stage data. This is to allow multiple stage checks to look this
     *        up only once.
     * @param stage The stage to check for.
     * @return Whether or not the player has the stage.
     */
    private static boolean hasStage (EntityPlayer player, IStageData data, String stage) {

        final GameStageEvent.Check event = new GameStageEvent.Check(player, stage, data.hasStage(stage));
        MinecraftForge.EVENT_BUS.post(event);
        return event.hasStage();
    }

    /**
     * Adds a stage to a player. This will fire the add event which can be canceled, and the
     * added event if the stage is added successfully.
     *
     * @param player The player to add the stage too.
     * @param stage The stage to add.
     */
    public static void addStage (EntityPlayer player, String stage) {

        if (!MinecraftForge.EVENT_BUS.post(new GameStageEvent.Add(player, stage))) {

            getPlayerData(player).addStage(stage);
            MinecraftForge.EVENT_BUS.post(new GameStageEvent.Added(player, stage));
        }
    }

    /**
     * Removes a stages from a player. This will fire the remove event which can be canceled,
     * and the removed event if the stage is removed successfully.
     *
     * @param player The player to remove the stage from.
     * @param stage The stage to remove.
     */
    public static void removeStage (EntityPlayer player, String stage) {

        if (!MinecraftForge.EVENT_BUS.post(new GameStageEvent.Remove(player, stage))) {

            getPlayerData(player).removeStage(stage);
            MinecraftForge.EVENT_BUS.post(new GameStageEvent.Removed(player, stage));
        }
    }

    /**
     * Checks if a player has any of the passed stages. This will also fire the check event for
     * each stage.
     *
     * @param player The player to check the stages of.
     * @param stages The stages to look for.
     * @return Whether or not the player has at least one of the stages.
     */
    public static boolean hasAnyOf (EntityPlayer player, String... stages) {

        final IStageData playerData = getPlayerData(player);
        return Arrays.stream(stages).anyMatch(stage -> hasStage(player, playerData, stage));
    }

    /**
     * Checks if a player has all of the passed stages. This will also fire the check event for
     * each stage.
     *
     * @param player The player to check the stages of.
     * @param stages The stages to look for.
     * @return Whether or not the player has all of the passed stages.
     */
    public static boolean hasAllOf (EntityPlayer player, String... stages) {

        final IStageData playerData = getPlayerData(player);
        return Arrays.stream(stages).allMatch(stage -> hasStage(player, playerData, stage));
    }

    /**
     * Gets the player data for an EntityPlayer. This will resolve fake players to their
     * special data as well.
     *
     * @param player The player to get the data of.
     * @return The stage data for the player.
     */
    public static IStageData getPlayerData (EntityPlayer player) {

        if (player instanceof FakePlayer) {

            // TODO
        }

        return GameStageSaveHandler.getPlayerData(player.getPersistentID().toString());
    }

    /**
     * Syncs a client's data with the data that is on the server. This can only be called
     * server side.
     *
     * @param player The player to sync.
     */
    public static void syncPlayer (EntityPlayerMP player) {

        final IStageData info = GameStageHelper.getPlayerData(player);
        GameStages.LOG.info("Syncing data for {}.", player.getName());
        GameStages.NETWORK.sendTo(new PacketSyncClient(info.getStages()), player);
    }
}