package com.emoniph.witchery.client.renderer;

import com.emoniph.witchery.blocks.BlockGrimoireLectern;
import com.emoniph.witchery.client.model.ModelGrimoireLectern;
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
public class RenderGrimoireLectern extends TileEntitySpecialRenderer {

    private final ModelGrimoireLectern model = new ModelGrimoireLectern();
    private static final ResourceLocation TEXTURE = new ResourceLocation("witchery", "textures/blocks/grimoire_lectern.png");
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

    public RenderGrimoireLectern() {
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

        if (tileEntity instanceof BlockGrimoireLectern.TileEntityGrimoireLectern && tileEntity.getWorldObj() != null) {
            BlockGrimoireLectern.TileEntityGrimoireLectern lectern = (BlockGrimoireLectern.TileEntityGrimoireLectern) tileEntity;
            ItemStack book = lectern.getBook();
            if (book != null) {
                EntityItem ei = new EntityItem(tileEntity.getWorldObj());
                ei.hoverStart = 0.0F;
                ItemStack copy = book.copy();
                copy.stackSize = 1;
                ei.setEntityItemStack(copy);
                GL11.glPushMatrix();
                GL11.glTranslatef(0.0F, 0.72F, 0.0F);
                GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScalef(0.75F, 0.75F, 0.75F);
                this.renderItems.doRender(ei, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
                GL11.glPopMatrix();
            }
        }

        GL11.glPopMatrix();
    }
}

