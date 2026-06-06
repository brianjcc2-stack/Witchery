package com.emoniph.witchery.ritual.rites;

import com.emoniph.witchery.blocks.BlockCircle;
import com.emoniph.witchery.ritual.Rite;
import com.emoniph.witchery.ritual.RitualStep;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class RiteReparo extends Rite {

    private final int radius;

    public RiteReparo(int radius) {
        this.radius = radius;
    }

    @Override
    public void addSteps(ArrayList steps, int initialStage) {
        steps.add(new StepReparo(this, initialStage));
    }

    private static class StepReparo extends RitualStep {

        private final RiteReparo rite;

        public StepReparo(RiteReparo rite, int initialStage) {
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
                    boolean repaired = false;
                    for (EntityItem entityItem : items) {
                        ItemStack stack = entityItem.getEntityItem();
                        if (stack != null && stack.isItemStackDamageable() && stack.isItemDamaged()) {
                            stack.setItemDamage(0);
                            ParticleEffect.INSTANT_SPELL.send(SoundEffect.RANDOM_FIZZ, world, entityItem.posX, entityItem.posY, entityItem.posZ, 1.0, 1.0, 16);
                            repaired = true;
                        }
                    }
                    if (repaired) {
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
