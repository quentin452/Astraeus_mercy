package com.bambam01.astraeusmercy;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class CustomTeleport extends Teleporter {
    private final WorldServer worldServerInstance;

    public CustomTeleport(WorldServer worldServer)
    {
        super(worldServer);
        this.worldServerInstance = worldServer;
    }

    public void placeInPortal(Entity entity, double param2, double param3, double param4, float param5)
    {
        // dont do ANY portal junk, just grab a dummy block then SHOVE the player setPosition() at height
        int i = MathHelper.floor_double(entity.posX);
        int j = MathHelper.floor_double(entity.posY);
        int k = MathHelper.floor_double(entity.posZ);
        this.worldServerInstance.getBlock(i, j, k); //dummy load to maybe gen chunk
        int height = this.worldServerInstance.getHeightValue(i, k);
        entity.setPosition( i, height, k );
    }
}
