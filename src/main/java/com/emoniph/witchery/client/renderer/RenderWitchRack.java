package com.emoniph.witchery.client.renderer;

import com.emoniph.witchery.blocks.BlockWitchRack;
import com.emoniph.witchery.client.model.ModelWitchRack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderWitchRack extends TileEntitySpecialRenderer {

    private final ModelWitchRack model = new ModelWitchRack();
    private static final ResourceLocation TEXTURE = new ResourceLocation("witchery", "textures/blocks/witch_rack.png");
    private final RenderItem renderItems = new RenderItem() {
        public byte getMiniItemCountForItemStack(ItemStack stack) {
            return (byte)1;
        }
        public byte getMiniBlockCountForItemStack(ItemStack stack) {
            return (byte)1;
        }
        @Override
        public boolean shouldBob() {
            return false;
        }
        @Override
        public boolean shouldSpreadItems() {
            return false;
        }
    };

    public RenderWitchRack() {
        this.renderItems.setRenderManager(RenderManager.instance);
    }

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

        GL11.glPushMatrix();
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef(0.0F, -1.0F, 0.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.bindTexture(TEXTURE);
        this.model.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
        GL11.glPopMatrix();

        if (tileEntity instanceof BlockWitchRack.TileEntityWitchRack && tileEntity.getWorldObj() != null) {
            BlockWitchRack.TileEntityWitchRack rack = (BlockWitchRack.TileEntityWitchRack) tileEntity;
            EntityItem ei = new EntityItem(tileEntity.getWorldObj());
            ei.hoverStart = 0.0F;

            float[] hookX = new float[]{0.0F, 0.0F, -0.22F, 0.22F};
            float[] hookZ = new float[]{0.22F, -0.22F, 0.0F, 0.0F};
            float[] hookRot = new float[]{0.0F, 180.0F, 90.0F, 270.0F};

            for (int i = 0; i < BlockWitchRack.TileEntityWitchRack.MAX_SLOTS; ++i) {
                ItemStack stack = rack.getItem(i);
                if (stack != null) {
                    ItemStack copy = stack.copy();
                    copy.stackSize = 1;
                    ei.setEntityItemStack(copy);
                    GL11.glPushMatrix();
                    GL11.glTranslatef(hookX[i], 0.88F, hookZ[i]);
                    GL11.glRotatef(hookRot[i], 0.0F, 1.0F, 0.0F);
                    GL11.glScalef(0.65F, 0.65F, 0.65F);
                    this.renderItems.doRender(ei, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
                    GL11.glPopMatrix();
                }
            }
        }

        GL11.glPopMatrix();
    }
}

