package com.emoniph.witchery.ritual.rites;

import com.emoniph.witchery.blocks.BlockCircle;
import com.emoniph.witchery.ritual.Rite;
import com.emoniph.witchery.ritual.RitualStep;
import com.emoniph.witchery.util.ChatUtil;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class RiteIdentify extends Rite {
    private final int radius;

    public RiteIdentify(int radius) {
        this.radius = radius;
    }

    @Override
    public void addSteps(ArrayList steps, int initialStage) {
        steps.add(new StepIdentify(this, initialStage));
    }

    private static class StepIdentify extends RitualStep {
        private final RiteIdentify rite;

        public StepIdentify(RiteIdentify rite, int initialStage) {
            super(false);
            this.rite = rite;
        }

        @Override
        public RitualStep.Result process(World world, int posX, int posY, int posZ, long ticks, BlockCircle.TileEntityCircle.ActivatedRitual ritual) {
            if (ticks % 20L != 0L) {
                return RitualStep.Result.STARTING;
            } else {
                if (!world.isRemote) {
                    AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox((double)(posX - this.rite.radius), (double)posY, (double)(posZ - this.rite.radius), (double)(posX + this.rite.radius + 1), (double)(posY + 1), (double)(posZ + this.rite.radius + 1));
                    List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, bounds);
                    boolean identified = false;
                    EntityPlayer player = ritual.getInitiatingPlayer(world);

                    if (player != null) {
                        for (EntityItem entityItem : items) {
                            ItemStack stack = entityItem.getEntityItem();
                            if (stack != null) {
                                String name = stack.getDisplayName();
                                ChatUtil.sendTranslated(EnumChatFormatting.LIGHT_PURPLE, player, "witchery.rite.identify.result", name);
                                if (stack.hasTagCompound()) {
                                    NBTTagCompound tag = stack.getTagCompound();
                                    if (tag.hasKey("ench", 9)) {
                                        ChatUtil.sendTranslated(EnumChatFormatting.AQUA, player, "witchery.rite.identify.enchanted");
                                    }
                                }
                                ParticleEffect.PORTAL.send(SoundEffect.RANDOM_ORB, world, entityItem.posX, entityItem.posY, entityItem.posZ, 0.5, 0.5, 16);
                                identified = true;
                            }
                        }
                    }

                    if (identified) {
                        return RitualStep.Result.COMPLETED;
                    } else {
                        return RitualStep.Result.ABORTED_REFUND;
                    }
                }
                return RitualStep.Result.COMPLETED;
            }
        }
    }
}
