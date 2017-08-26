package net.darkhax.gamestages;

import net.darkhax.bookshelf.BookshelfRegistry;
import net.darkhax.bookshelf.command.CommandTree;
import net.darkhax.bookshelf.lib.LoggingHelper;
import net.darkhax.bookshelf.network.NetworkHandler;
import net.darkhax.bookshelf.world.gamerule.GameRule;
import net.darkhax.gamestages.capabilities.PlayerDataHandler;
import net.darkhax.gamestages.capabilities.PlayerDataHandler.DefaultStageData;
import net.darkhax.gamestages.capabilities.PlayerDataHandler.IStageData;
import net.darkhax.gamestages.capabilities.PlayerDataHandler.Storage;
import net.darkhax.gamestages.commands.CommandStageTree;
import net.darkhax.gamestages.packet.PacketRequestClientSync;
import net.darkhax.gamestages.packet.PacketStage;
import net.darkhax.gamestages.packet.PacketStageAll;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = "gamestages", name = "Game Stages", version = "@VERSION@", dependencies = "required-after:bookshelf@[2.1.427,);", acceptedMinecraftVersions = "[1.12,1.12.2)")
public class GameStages {
    
    public static final LoggingHelper LOG = new LoggingHelper("gamestages");
    public static final NetworkHandler NETWORK = new NetworkHandler("gamestages");
    public static final CommandTree COMMAND = new CommandStageTree();
    public static final GameRule GAME_RULE_SHARE_STAGES = new GameRule("shareGameStages", false);
    
    @EventHandler
    public void preInit (FMLPreInitializationEvent event) {
        
        // Packets
        NETWORK.register(PacketStage.class, Side.CLIENT);
        NETWORK.register(PacketStageAll.class, Side.CLIENT);
        NETWORK.register(PacketRequestClientSync.class, Side.SERVER);
        
        CapabilityManager.INSTANCE.register(IStageData.class, new Storage(), DefaultStageData.class);
        MinecraftForge.EVENT_BUS.register(new PlayerDataHandler());
        BookshelfRegistry.addCommand(COMMAND);
        BookshelfRegistry.addGameRule(GAME_RULE_SHARE_STAGES);
    }
}