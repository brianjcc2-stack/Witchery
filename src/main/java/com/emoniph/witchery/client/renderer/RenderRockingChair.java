package com.emoniph.witchery.client.renderer;

import com.emoniph.witchery.client.model.ModelRockingChair;
import com.emoniph.witchery.entity.EntitySeat;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.List;

@SideOnly(Side.CLIENT)
public class RenderRockingChair extends TileEntitySpecialRenderer {

    private final ModelRockingChair model = new ModelRockingChair();
    private static final ResourceLocation TEXTURE = new ResourceLocation("witchery", "textures/blocks/poppetShelf.png");

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);

        int meta = tileEntity != null && tileEntity.getWorldObj() != null ? tileEntity.getBlockMetadata() : 0;
        float angle = 0.0F;
        if (meta == 0) angle = 0.0F;
        else if (meta == 1) angle = 270.0F;
        else if (meta == 2) angle = 180.0F;
        else if (meta == 3) angle = 90.0F;

        GL11.glRotatef(angle, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);

        boolean occupied = false;
        if (tileEntity != null && tileEntity.getWorldObj() != null) {
            List seats = tileEntity.getWorldObj().getEntitiesWithinAABB(EntitySeat.class,
                    AxisAlignedBB.getBoundingBox(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord,
                            tileEntity.xCoord + 1, tileEntity.yCoord + 1, tileEntity.zCoord + 1));
            for (int i = 0; i < seats.size(); ++i) {
                EntitySeat s = (EntitySeat) seats.get(i);
                if (s.riddenByEntity != null) {
                    occupied = true;
                    break;
                }
            }
        }

        if (occupied) {
            float time = ((float)(Minecraft.getSystemTime() % 4000L) / 4000.0F) * (float)Math.PI * 2.0F;
            float rockAngle = MathHelper.sin(time * 2.0F) * 6.5F;
            GL11.glTranslatef(0.0F, 0.45F, 0.0F);
            GL11.glRotatef(rockAngle, 1.0F, 0.0F, 0.0F);
            GL11.glTranslatef(0.0F, -1.45F, 0.0F);
        } else {
            GL11.glTranslatef(0.0F, -1.0F, 0.0F);
        }

        this.bindTexture(TEXTURE);
        this.model.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);

        GL11.glPopMatrix();
    }
}

