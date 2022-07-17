package com.bambam01.astraeusmercy;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod(modid = AstraeusMercy.MODID, version = AstraeusMercy.VERSION)
public class AstraeusMercy
{
    public static final String MODID = "AstraeusMercy";
    public static final String VERSION = "1.0";
    public static Config config;
    public static Logger logger = LogManager.getLogger("AstraeussMercy");

    static class PlayerRespawnDeaths{
        long lastDeath = 0;
        int numberOfDeaths = 0;
    }

    public HashMap<UUID, PlayerRespawnDeaths> deathList = new HashMap<UUID, PlayerRespawnDeaths>();


     @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(LivingDeathEvent event)
    {
        if(event.entityLiving.worldObj.isRemote)
            return;
        if(!(event.entityLiving instanceof EntityPlayer))
            return;
        long current = Instant.now().getEpochSecond();
        if(ArrayUtils.contains(config.deathNames, event.source.damageType)){
            PlayerRespawnDeaths playerDeath = deathList.getOrDefault(event.entityLiving.getUniqueID(), new PlayerRespawnDeaths());
            if(current - playerDeath.lastDeath > config.respawnGracePeriod){
                playerDeath.lastDeath = current;
                playerDeath.numberOfDeaths = 1;
                deathList.put(event.entityLiving.getUniqueID(), playerDeath);
            }else{
                if(playerDeath.numberOfDeaths < config.deathBeforeForceRespawn){
                    playerDeath.lastDeath = current;
                    playerDeath.numberOfDeaths += 1;
                    deathList.put(event.entityLiving.getUniqueID(),playerDeath);
                }else {

                    event.setCanceled(true);
                    event.setResult(Event.Result.DENY);
                    event.entityLiving.setHealth(event.entityLiving.getMaxHealth());
                    event.entityLiving.addPotionEffect(new PotionEffect(Potion.resistance.getId(), 20*10, 3));
                    ChunkCoordinates info = DimensionManager.getWorld(config.respawnDimensionId).provider.getRandomizedSpawnPoint();
                    event.entityLiving.mountEntity((Entity) null);
                    if (event.entityLiving.worldObj.provider.dimensionId == config.respawnDimensionId)
                    {
                        event.entityLiving.setPositionAndUpdate(info.posX, info.posY, info.posZ);
                    }
                    else
                    {

                        WorldServer targetWorldServer = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(config.respawnDimensionId);
                        CustomTeleport teleporter = new CustomTeleport(targetWorldServer);
                        FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().transferPlayerToDimension((EntityPlayerMP) event.entityLiving, config.respawnDimensionId, teleporter);
                        event.entityLiving.setPositionAndUpdate(info.posX, info.posY, info.posZ);
                        ((EntityPlayerMP) event.entityLiving).addChatMessage(new ChatComponentText("Astraeus spares your live and returns you to a save place"));
                    }
                }
            }
        }
        cleanup();
    }




    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
        config = new Config(new Configuration(event.getSuggestedConfigurationFile()));
        MinecraftForge.EVENT_BUS.register(this);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {

    }

    @EventHandler
    public void postIinit(FMLPostInitializationEvent event){
        if(!DimensionManager.isDimensionRegistered(config.respawnDimensionId)){
            logger.error("no dimension found with id " + config.respawnDimensionId + ". setting id to 1");
            config.respawnDimensionId = 0;
        }else{
            logger.info("testinfo");
            logger.info("testDebug");
            logger.warn("set respawn id: " + config.respawnDimensionId);
        }
    }

    public void cleanup() {
        long current = Instant.now().getEpochSecond();
        deathList.entrySet().removeIf(entry -> current - entry.getValue().lastDeath > config.respawnGracePeriod);
    }
}
