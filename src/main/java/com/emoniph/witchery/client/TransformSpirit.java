package com.emoniph.witchery.client;

import com.emoniph.witchery.client.renderer.RenderSpirit;
import com.emoniph.witchery.entity.EntitySpirit;
import com.emoniph.witchery.infusion.Infusion;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class TransformSpirit {

   private EntitySpirit proxyEntity;
   private RenderSpirit proxyRenderer = new RenderSpirit();

   public EntityLivingBase getModel() {
      return this.proxyEntity;
   }

   public void syncModelWith(EntityLivingBase entity, boolean frontface) {
      if(this.proxyEntity == null) {
         this.proxyEntity = new EntitySpirit(entity.worldObj);
      } else if(this.proxyEntity.worldObj != entity.worldObj) {
         this.proxyEntity.setWorld(entity.worldObj);
      }

      this.proxyEntity.setPosition(entity.posX, entity.posY, entity.posZ);
      this.proxyEntity.lastTickPosX = entity.lastTickPosX;
      this.proxyEntity.lastTickPosY = entity.lastTickPosY;
      this.proxyEntity.lastTickPosZ = entity.lastTickPosZ;
      this.proxyEntity.motionX = entity.motionX;
      this.proxyEntity.motionY = entity.motionY;
      this.proxyEntity.motionZ = entity.motionZ;
      this.proxyEntity.rotationPitch = entity.rotationPitch;
      this.proxyEntity.rotationYaw = entity.rotationYaw;
      this.proxyEntity.rotationYawHead = entity.rotationYawHead;
      this.proxyEntity.prevRotationPitch = entity.prevRotationPitch;
      this.proxyEntity.prevRotationYaw = entity.prevRotationYaw;
      this.proxyEntity.prevRotationYawHead = entity.prevRotationYawHead;
      this.proxyEntity.renderYawOffset = frontface ? 0.0F : entity.renderYawOffset;
      this.proxyEntity.prevRenderYawOffset = frontface ? 0.0F : entity.prevRenderYawOffset;
      this.proxyEntity.ticksExisted = entity.ticksExisted;
      this.proxyEntity.isDead = false;

      // Set color based on infusion
      if (entity instanceof EntityPlayer) {
          EntityPlayer player = (EntityPlayer)entity;
          int infusionID = Infusion.getInfusionID(player);
          int color = 0xFFFFFF; // White (Ghosting or default)
          if (infusionID == 4) {
              color = 0xFF0000; // Red (Infernal)
          } else if (infusionID == 2) {
              color = 0x00FF00; // Green (Overworld/Earth)
          } else if (infusionID == 3) {
              color = 0x800080; // Purple (Otherwhere)
          }
          this.proxyEntity.setFeatherColor(color);
      }
   }

   public void render(World worldObj, EntityLivingBase entity, double x, double y, double z, RendererLivingEntity renderer, float partialTicks, boolean frontface) {
      this.syncModelWith(entity, frontface);
      this.proxyRenderer.setRenderManager(RenderManager.instance);
      float f1 = this.proxyEntity.prevRotationYaw + (this.proxyEntity.rotationYaw - this.proxyEntity.prevRotationYaw) * partialTicks;
      double d3 = -((double)this.proxyEntity.yOffset);
      
      this.proxyRenderer.doRender(this.proxyEntity, x, y + d3 + 0.5D, z, frontface ? 0.0F : f1, partialTicks);
   }
}
