package com.emoniph.witchery.client.renderer;

import com.emoniph.witchery.blocks.BlockBroomStand;
import com.emoniph.witchery.client.model.ModelBroom;
import com.emoniph.witchery.client.model.ModelBroomStand;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderBroomStand extends TileEntitySpecialRenderer {

    private final ModelBroomStand model = new ModelBroomStand();
    private final ModelBroom modelBroom = new ModelBroom();
    private static final ResourceLocation TEXTURE_STAND = new ResourceLocation("witchery", "textures/blocks/broom_stand.png");
    private static final ResourceLocation TEXTURE_BROOM = new ResourceLocation("witchery", "textures/entities/Broom.png");

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
        GL11.glTranslatef(0.0F, -1.0F, 0.0F);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.bindTexture(TEXTURE_STAND);
        this.model.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);

        if (tileEntity instanceof BlockBroomStand.TileEntityBroomStand) {
            BlockBroomStand.TileEntityBroomStand stand = (BlockBroomStand.TileEntityBroomStand) tileEntity;
            ItemStack broomStack = stand.getBroom();
            if (broomStack != null) {
                GL11.glPushMatrix();
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.bindTexture(TEXTURE_BROOM);
                GL11.glTranslatef(0.0F, -0.18F, 0.12F);
                GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-15.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScalef(0.8F, 0.8F, 0.8F);
                this.modelBroom.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
                GL11.glPopMatrix();
            }
        }

        GL11.glPopMatrix();
    }
}

