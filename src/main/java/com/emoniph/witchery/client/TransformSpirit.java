package com.emoniph.witchery.client;

import com.emoniph.witchery.client.model.ModelSpectre;
import com.emoniph.witchery.common.SupremeWitchData;
import com.emoniph.witchery.entity.EntityBanshee;
import com.emoniph.witchery.infusion.Infusion;
import com.emoniph.witchery.util.RenderUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TransformSpirit {

    private EntityBanshee proxyEntity;
    private int currentColor = 0xFFFFFF;
    private boolean isSupreme = false;
    private int[] currentPalette = new int[]{ 0xFFFFFF };
    private float currentScale = 1.0F;

    // Cache to prevent recreating palette and arrays every single frame
    private int lastSyncedTick = -1;
    private int lastInfusionId = -1;
    private boolean lastSupremeState = false;
    private final float[] tempRgb = new float[3];

    private float renderR = 1.0F;
    private float renderG = 1.0F;
    private float renderB = 1.0F;

    private RenderLiving proxyRenderer = new RenderLiving(new ModelSpectre(false), 0.0F) {
        protected ResourceLocation getEntityTexture(Entity par1Entity) {
            return new ResourceLocation("witchery", "textures/entities/banshee.png");
        }

        @Override
        protected void preRenderCallback(EntityLivingBase entity, float partialTicks) {
            GL11.glScalef(currentScale, currentScale, currentScale);
            GL11.glColor4f(renderR, renderG, renderB, 0.75F);
        }

        @Override
        public void doRender(EntityLivingBase entity, double x, double y, double z, float yaw, float partialTicks) {
            if (currentPalette != null && currentPalette.length > 1) {
                float time = (entity.ticksExisted + partialTicks) * 0.01F;
                interpolateColors(currentPalette, time, tempRgb);
            } else {
                tempRgb[0] = (float)(currentColor >> 16 & 255) / 255.0F;
                tempRgb[1] = (float)(currentColor >> 8 & 255) / 255.0F;
                tempRgb[2] = (float)(currentColor & 255) / 255.0F;
            }

            // 1. Renderizado principal del espectro
            GL11.glPushMatrix();
            RenderUtil.blend(true);
            currentScale = 1.0F;
            renderR = tempRgb[0];
            renderG = tempRgb[1];
            renderB = tempRgb[2];
            GL11.glColor4f(renderR, renderG, renderB, 0.75F);
            super.doRender(entity, x, y, z, yaw, partialTicks);
            RenderUtil.blend(false);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPopMatrix();

            // 2. Efecto especial de la Bruja Suprema: Resplandor sutil celestial (optimizado)
            if (isSupreme) {
                GL11.glPushMatrix();
                GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
                RenderUtil.blend(true);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
                GL11.glDepthMask(false);
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

                float pulse = 1.03F + 0.02F * MathHelper.sin((entity.ticksExisted + partialTicks) * 0.08F);

                currentScale = pulse;
                renderR = tempRgb[0];
                renderG = tempRgb[1];
                renderB = tempRgb[2];
                GL11.glColor4f(renderR, renderG, renderB, 0.20F);
                super.doRender(entity, x, y, z, yaw, partialTicks);

                currentScale = 1.0F;
                GL11.glDepthMask(true);
                RenderUtil.blend(false);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glPopAttrib();
                GL11.glPopMatrix();
            }
        }
    };

   public static int getInfusionColor(int infusionID) {
       switch (infusionID) {
           case 1: // Infusión de la Luz
               return 0xFFDF00; // Oro solar radiante
           case 2: // Infusión de la Tierra / Overworld
               return 0x33FF55; // Verde esmeralda de la naturaleza
           case 3: // Infusión del Más Allá / Ender
               return 0xAA22FF; // Violeta místico del vacío
           case 4: // Infusión del Infierno / Nether
               return 0xFF3333; // Rojo carmesí infernal
           default:
               return 0xFFFFFF; // Blanco espectral por defecto
       }
   }

    public static void interpolateColors(int[] colors, float time, float[] out) {
        if (out == null || out.length < 3) return;
        if (colors == null || colors.length == 0) {
            out[0] = 1.0F; out[1] = 1.0F; out[2] = 1.0F;
            return;
        }
        if (colors.length == 1) {
            out[0] = (float)(colors[0] >> 16 & 255) / 255.0F;
            out[1] = (float)(colors[0] >> 8 & 255) / 255.0F;
            out[2] = (float)(colors[0] & 255) / 255.0F;
            return;
        }

        int n = colors.length;
        float scaled = (time % n + n) % n;
        int i1 = (int) scaled;
        int i2 = (i1 + 1) % n;
        float frac = scaled - i1;

        float smoothFrac = (1.0F - MathHelper.cos(frac * (float)Math.PI)) * 0.5F;

        float r1 = (float)(colors[i1] >> 16 & 255) / 255.0F;
        float g1 = (float)(colors[i1] >> 8 & 255) / 255.0F;
        float b1 = (float)(colors[i1] & 255) / 255.0F;

        float r2 = (float)(colors[i2] >> 16 & 255) / 255.0F;
        float g2 = (float)(colors[i2] >> 8 & 255) / 255.0F;
        float b2 = (float)(colors[i2] & 255) / 255.0F;

        out[0] = r1 + (r2 - r1) * smoothFrac;
        out[1] = g1 + (g2 - g1) * smoothFrac;
        out[2] = b1 + (b2 - b1) * smoothFrac;
    }

    public static float[] interpolateColors(int[] colors, float time) {
        float[] res = new float[3];
        interpolateColors(colors, time, res);
        return res;
    }

   private static int[] getPlayerInfusionIDs(EntityPlayer player) {
       if (player == null) return new int[0];
       NBTTagCompound nbt = Infusion.getNBT(player);
       if (nbt != null) {
           if (nbt.hasKey("witcheryInfusionIDs", 11)) {
               int[] arr = nbt.getIntArray("witcheryInfusionIDs");
               if (arr != null && arr.length > 0) return arr;
           } else if (nbt.hasKey("witcheryInfusionIDs", 8)) {
               String str = nbt.getString("witcheryInfusionIDs");
               String[] parts = str.split(",");
               ArrayList list = new ArrayList();
               for (int i = 0; i < parts.length; i++) {
                   try {
                       int id = Integer.parseInt(parts[i].trim());
                       if (id > 0) list.add(Integer.valueOf(id));
                   } catch (NumberFormatException ignored) {}
               }
               if (!list.isEmpty()) {
                   int[] arr = new int[list.size()];
                   for (int i = 0; i < list.size(); i++) arr[i] = ((Integer)list.get(i)).intValue();
                   return arr;
               }
           }
       }
       int single = Infusion.getInfusionID(player);
       return single > 0 ? new int[]{ single } : new int[0];
   }

   private static int[] buildPalette(int[] ids) {
       LinkedHashSet set = new LinkedHashSet();
       for (int i = 0; i < ids.length; i++) {
           set.add(Integer.valueOf(getInfusionColor(ids[i])));
       }
       int[] pal = new int[set.size()];
       int idx = 0;
       for (Iterator it = set.iterator(); it.hasNext(); ) {
           pal[idx++] = ((Integer)it.next()).intValue();
       }
       return pal;
   }

   public EntityLivingBase getModel() {
      return this.proxyEntity;
   }

   public void syncModelWith(EntityLivingBase entity, boolean frontface) {
      if(this.proxyEntity == null) {
         this.proxyEntity = new EntityBanshee(entity.worldObj);
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

      if (entity instanceof EntityPlayer) {
          EntityPlayer player = (EntityPlayer)entity;
          boolean supremeNow = SupremeWitchData.isSupremeAuraActive(player);
          int singleId = Infusion.getInfusionID(player);

          // Only recalculate palette if infusion, supreme state changes, or after 40 ticks (~2 seconds)
          if (this.lastSyncedTick == -1 || this.lastInfusionId != singleId || this.lastSupremeState != supremeNow || entity.ticksExisted - this.lastSyncedTick > 40) {
              this.isSupreme = supremeNow;
              this.lastSupremeState = supremeNow;
              this.lastInfusionId = singleId;
              this.lastSyncedTick = entity.ticksExisted;

              int[] ids = getPlayerInfusionIDs(player);
              if (ids.length > 1) {
                  this.currentPalette = buildPalette(ids);
                  this.currentColor = this.currentPalette[0];
              } else {
                  this.currentColor = getInfusionColor(singleId);
                  this.currentPalette = new int[]{ this.currentColor };
              }
          }
      } else {
          this.isSupreme = false;
          this.lastSupremeState = false;
          this.currentColor = 0xFFFFFF;
          this.currentPalette = new int[]{ 0xFFFFFF };
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
