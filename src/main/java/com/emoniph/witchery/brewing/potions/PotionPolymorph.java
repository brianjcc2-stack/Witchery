package com.emoniph.witchery.brewing.potions;

import com.emoniph.witchery.Witchery;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderLivingEvent.Pre;
import net.minecraftforge.client.event.RenderLivingEvent.Post;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.lwjgl.opengl.GL11;

public class PotionPolymorph extends PotionBase implements IHandlePreRenderLiving, IHandleRenderLiving, IHandleLivingAttack {

    @SideOnly(Side.CLIENT)
    private static EntityPig dummyPig;

    public PotionPolymorph(int id, int color) {
        super(id, true, color);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onLivingRender(World world, EntityLivingBase entity, Pre event, int amplifier) {
        if (dummyPig == null || dummyPig.worldObj != world) {
            dummyPig = new EntityPig(world);
        }
        
        event.setCanceled(true);
        
        dummyPig.copyDataFrom(entity, true);
        dummyPig.renderYawOffset = entity.renderYawOffset;
        dummyPig.rotationYawHead = entity.rotationYawHead;
        dummyPig.prevRenderYawOffset = entity.prevRenderYawOffset;
        dummyPig.prevRotationYawHead = entity.prevRotationYawHead;

        GL11.glPushMatrix();
        RenderManager.instance.renderEntityWithPosYaw(dummyPig, event.x, event.y, event.z, 0, 0.0f);
        GL11.glPopMatrix();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onLivingRender(World world, EntityLivingBase entity, Post event, int amplifier) {
    }

    public boolean handleAllHurtEvents() {
        return false;
    }

    @Override
    public void onLivingAttack(World world, EntityLivingBase entity, LivingAttackEvent event, int amplifier) {
        if (!world.isRemote && event.source.getEntity() != null) {
            if (event.source.getEntity() instanceof EntityLivingBase) {
                EntityLivingBase attacker = (EntityLivingBase) event.source.getEntity();
                if (attacker.isPotionActive(this)) {
                    event.setCanceled(true);
                }
            }
        }
    }
}
