package com.emoniph.witchery.ritual.rites;

import com.emoniph.witchery.blocks.BlockCircle;
import com.emoniph.witchery.ritual.Rite;
import com.emoniph.witchery.ritual.RitualStep;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

import java.util.ArrayList;

public class RiteHerbivicus extends Rite {
    private final int radius;

    public RiteHerbivicus(int radius) {
        this.radius = radius;
    }

    @Override
    public void addSteps(ArrayList steps, int initialStage) {
        steps.add(new StepHerbivicus(this, initialStage));
    }

    private static class StepHerbivicus extends RitualStep {
        private final RiteHerbivicus rite;

        public StepHerbivicus(RiteHerbivicus rite, int initialStage) {
            super(false);
            this.rite = rite;
        }

        @Override
        public RitualStep.Result process(World world, int posX, int posY, int posZ, long ticks, BlockCircle.TileEntityCircle.ActivatedRitual ritual) {
            if (ticks % 20L != 0L) {
                return RitualStep.Result.STARTING;
            } else {
                if (!world.isRemote) {
                    int r = this.rite.radius;
                    for (int i = 0; i < 5; i++) {
                        int dx = posX - r + world.rand.nextInt(r * 2 + 1);
                        int dz = posZ - r + world.rand.nextInt(r * 2 + 1);
                        int dy = posY - 2 + world.rand.nextInt(5);

                        Block block = world.getBlock(dx, dy, dz);
                        if (block instanceof IGrowable || block instanceof IPlantable) {
                            block.updateTick(world, dx, dy, dz, world.rand);
                            ParticleEffect.TOWN_AURA.send(SoundEffect.NONE, world, dx + 0.5, dy + 0.5, dz + 0.5, 0.5, 0.5, 16);
                        }
                    }
                }
                return RitualStep.Result.UPKEEP;
            }
        }
    }
}
