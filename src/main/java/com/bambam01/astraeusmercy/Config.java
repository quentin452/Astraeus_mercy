package com.bambam01.astraeusmercy;


import net.minecraftforge.common.config.Configuration;

public class Config {

    public int deathBeforeForceRespawn;
    public int respawnDimensionId;
    public String[] deathNames;
    public int respawnGracePeriod;




    private final String CATEGORY_GENERAL = "general";

    public Configuration config;


    public Config(Configuration config) {
        this.config = config;
        this.load();
    }

    public void load() {
        config.load();
        this.deathBeforeForceRespawn = config.get(CATEGORY_GENERAL, "NumberOfDeaths", 2, "Number of deaths before we save you. So 2 means that when you are about to die for the 3th time it saves you").getInt();
        this.respawnDimensionId = config.get(CATEGORY_GENERAL, "RespawnDimensionID", 0, "What is the id of the dimension to teleport the player to").getInt();
        this.respawnGracePeriod = config.get(CATEGORY_GENERAL, "respawnGracePeriod", 120, "How long between deaths in seconds for the death counter to reset.").getInt();
        this.deathNames = config.get(CATEGORY_GENERAL, "listOfRespawnDeathNames", new String[]{"warpdrive.asphyxia"}, " A list of damage sources that count for saving. Damage source depend on how you die. Vanilla damage sources are:\n" +
                "                                \"inFire\"\n" +
                "                                \"onFire\"\n" +
                "                                \"lava\"\n" +
                "                                \"inWall\"\n" +
                "                                \"drown\"\n" +
                "                                \"starve\"\n" +
                "                                \"cactus\"\n" +
                "                                \"fall\"\n" +
                "                                \"outOfWorld\"\n" +
                "                                \"generic\"\n" +
                "                                \"magic\"\n" +
                "                                \"wither\"\n" +
                "                                \"anvil\"\n" +
                "                                \"fallingBlock\"\n" +
                "                        mods can add custom damage type like \"warpdrive.asphyxia\" from the WarpDrive mod for when you die in space").getStringList();

        if (config.hasChanged()) {
            config.save();
        }

    }
}
