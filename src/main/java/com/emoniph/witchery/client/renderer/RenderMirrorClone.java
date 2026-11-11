package com.emoniph.witchery.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderMirrorClone extends RenderBiped {

   private static final ResourceLocation TEXTURE = new ResourceLocation("witchery", "textures/entities/reflection.png");

   public RenderMirrorClone() {
      super(new ModelBiped(0.0F), 0.5F);
   }

   @Override
   protected ResourceLocation getEntityTexture(Entity entity) {
      return TEXTURE;
   }

   @Override
   protected void preRenderCallback(EntityLivingBase entity, float partialTicks) {
      super.preRenderCallback(entity, partialTicks);
      GL11.glEnable(GL11.GL_BLEND);
      GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      GL11.glColor4f(0.85F, 0.95F, 1.0F, 0.85F);
   }
}

