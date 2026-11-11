package com.emoniph.witchery.client.renderer;

import com.emoniph.witchery.client.model.ModelFlyingMortar;
import com.emoniph.witchery.entity.EntityFlyingMortar;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderFlyingMortar extends Render {

   private static final ResourceLocation TEXTURE = new ResourceLocation("witchery", "textures/entities/flyingmortar.png");
   protected ModelFlyingMortar model = new ModelFlyingMortar();

   public RenderFlyingMortar() {
      super.shadowSize = 0.6F;
   }

   @Override
   public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
      this.renderMortar((EntityFlyingMortar)entity, x, y, z, yaw, partialTicks);
   }

   public void renderMortar(EntityFlyingMortar mortar, double x, double y, double z, float yaw, float partialTicks) {
      GL11.glPushMatrix();
      GL11.glTranslatef((float)x, (float)y + 0.1F, (float)z);
      GL11.glRotatef(180.0F - yaw, 0.0F, 1.0F, 0.0F);

      float pitch = mortar.prevRotationPitch + (mortar.rotationPitch - mortar.prevRotationPitch) * partialTicks;
      GL11.glRotatef(pitch, 1.0F, 0.0F, 0.0F);

      // In 1.7.10 models are rendered upside down by default
      GL11.glScalef(-1.0F, -1.0F, 1.0F);
      GL11.glTranslatef(0.0F, -1.5F, 0.0F);

      this.bindEntityTexture(mortar);
      this.model.render(mortar, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

      GL11.glPopMatrix();
   }

   @Override
   protected ResourceLocation getEntityTexture(Entity entity) {
      return TEXTURE;
   }
}

