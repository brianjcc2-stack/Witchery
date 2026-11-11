package com.emoniph.witchery.client;

import com.emoniph.witchery.common.ExtendedPlayer;
import com.emoniph.witchery.common.SupremeWitchData;
import com.emoniph.witchery.infusion.Infusion;
import com.emoniph.witchery.util.RenderUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TransformSubduedSpirit {

    public void render(World worldObj, EntityLivingBase entity, double x, double y, double z, RendererLivingEntity renderer, float partialTicks, boolean frontface) {
        if (!(entity instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) entity;
        ExtendedPlayer playerEx = ExtendedPlayer.get(player);
        boolean isDementor = PlayerRender.isPlayerDementor(player, playerEx);

        // Render pure aerodynamic Harry Potter smoke flight - fluid billowing black smoke
        renderSmokeFlight(player, x, y, z, partialTicks, isDementor);
    }

    private void renderSmokeFlight(EntityPlayer player, double x, double y, double z, float partialTicks, boolean dementor) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_ENABLE_BIT);

        // Center on the player's torso
        GL11.glTranslated(x, y + 0.9D, z);

        // Align flight rotation with player view direction
        float yaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks;
        float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;

        GL11.glRotatef(180.0F - yaw, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-pitch * 0.85F, 1.0F, 0.0F, 0.0F);

        RenderUtil.blend(true);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(false);

        // Smooth alpha blending for both modes so it never blows out into a solid white orb
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

        float animTime = (player.ticksExisted + partialTicks) * 0.08F;
        Tessellator tessellator = Tessellator.instance;

        // Base colors
        float rCol = dementor ? 0.04F : 0.88F;
        float gCol = dementor ? 0.04F : 0.92F;
        float bCol = dementor ? 0.05F : 0.98F;
        float baseAlpha = dementor ? 0.60F : 0.40F;

        // 1. Aerodynamic Helical Smoke Vortex Funnels (Stretching along flight axis Z from +0.5F down to -2.4F)
        int numHelices = 6;
        for (int h = 0; h < numHelices; h++) {
            float phaseOffset = h * ((float)Math.PI * 2.0F / (float)numHelices);
            float spinSpeed = (h % 2 == 0 ? 4.5F : -4.0F);

            tessellator.startDrawing(GL11.GL_QUAD_STRIP);
            int steps = 18;
            for (int s = 0; s <= steps; s++) {
                float progress = (float)s / (float)steps;
                float zPos = 0.5F - progress * 2.7F; // Front to trailing tail

                // Radius starts medium at front, expands at torso, tapers at tail
                float envelope = MathHelper.sin(progress * (float)Math.PI);
                float radius = (0.28F + envelope * 0.35F) * (1.0F + 0.15F * MathHelper.sin(animTime * 2.0F + progress * 6.0F + h));

                float angle = animTime * spinSpeed + phaseOffset + progress * 8.0F;
                float cos = MathHelper.cos(angle);
                float sin = MathHelper.sin(angle);

                float ribbonWidth = (0.12F + envelope * 0.10F);
                float alpha = baseAlpha * envelope;

                // Subtle shadow shading: outer edges darker
                float shade = 1.0F - progress * 0.25F;
                float r = rCol * shade;
                float g = gCol * shade;
                float b = bCol * shade;
                GL11.glColor4f(r, g, b, alpha);
                tessellator.setColorRGBA_F(r, g, b, alpha);
                tessellator.addVertex(cos * (radius - ribbonWidth), sin * (radius - ribbonWidth), zPos);
                tessellator.setColorRGBA_F(r, g, b, alpha);
                tessellator.addVertex(cos * (radius + ribbonWidth), sin * (radius + ribbonWidth), zPos);
            }
            tessellator.draw();
        }

        // 2. Swirling Tendril Billows (Outer turbulent smoke wisps)
        int numBillows = 4;
        for (int b = 0; b < numBillows; b++) {
            GL11.glPushMatrix();

            float billowZ = -0.3F - (float)b * 0.45F;
            GL11.glTranslatef(0.0F, 0.0F, billowZ);

            float billowRot = animTime * (b % 2 == 0 ? 80.0F : -70.0F) + b * 55.0F;
            GL11.glRotatef(billowRot, 0.0F, 0.0F, 1.0F);

            float bRadius = 0.48F + 0.08F * MathHelper.sin(animTime * 2.2F + b);
            float bAlpha = dementor ? 0.40F : 0.30F;

            GL11.glColor4f(rCol, gCol, bCol, bAlpha);

            tessellator.startDrawing(GL11.GL_QUAD_STRIP);
            int segs = 14;
            for (int i = 0; i <= segs; i++) {
                double a = (2.0D * Math.PI * (double)i) / (double)segs;
                double cos = Math.cos(a);
                double sin = Math.sin(a);
                tessellator.setColorRGBA_F(rCol, gCol, bCol, bAlpha);
                tessellator.addVertex(cos * (bRadius - 0.10D), sin * (bRadius - 0.10D), -0.08D);
                tessellator.setColorRGBA_F(rCol, gCol, bCol, bAlpha);
                tessellator.addVertex(cos * (bRadius + 0.10D), sin * (bRadius + 0.10D), 0.08D);
            }
            tessellator.draw();

            GL11.glPopMatrix();
        }

        // 3. Dense Inner Core Corkscrew (Tightly wrapped central plume)
        tessellator.startDrawing(GL11.GL_QUAD_STRIP);
        int coreSteps = 16;
        for (int cs = 0; cs <= coreSteps; cs++) {
            float cProgress = (float)cs / (float)coreSteps;
            float cZ = 0.4F - cProgress * 2.2F;
            float cEnv = MathHelper.sin(cProgress * (float)Math.PI);
            float cRad = 0.18F * cEnv;
            float cAngle = animTime * -6.0F + cProgress * 10.0F;

            float cAlpha = (dementor ? 0.70F : 0.50F) * cEnv;
            float cr = rCol * 0.85F;
            float cg = gCol * 0.85F;
            float cb = bCol * 0.9F;
            GL11.glColor4f(cr, cg, cb, cAlpha);
            tessellator.setColorRGBA_F(cr, cg, cb, cAlpha);
            float cWidth = 0.08F;
            tessellator.addVertex(MathHelper.cos(cAngle) * (cRad - cWidth), MathHelper.sin(cAngle) * (cRad - cWidth), cZ);
            tessellator.setColorRGBA_F(cr, cg, cb, cAlpha);
            tessellator.addVertex(MathHelper.cos(cAngle) * (cRad + cWidth), MathHelper.sin(cAngle) * (cRad + cWidth), cZ);
        }
        tessellator.draw();

        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_CULL_FACE);
        RenderUtil.blend(false);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}
