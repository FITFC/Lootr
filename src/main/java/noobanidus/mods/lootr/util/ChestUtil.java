package noobanidus.mods.lootr.util;

import net.minecraft.block.BarrelBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.monster.piglin.PiglinTasks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.data.NewChestData;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.init.ModStats;
import noobanidus.mods.lootr.tiles.ILootTile;
import noobanidus.mods.lootr.tiles.SpecialLootInventoryTile;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@SuppressWarnings("unused")
public class ChestUtil {
  public static Random random = new Random();
  public static Set<Class<?>> tileClasses = new HashSet<>();

  public static boolean handleLootChest(Block block, World world, BlockPos pos, PlayerEntity player) {
    if (world.isRemote()) {
      return false;
    }
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof ILootTile) {
      if (block instanceof BarrelBlock) {
        Lootr.BARREL_PREDICATE.trigger((ServerPlayerEntity) player, null);
      } else if (block instanceof ChestBlock) {
        Lootr.CHEST_PREDICATE.trigger((ServerPlayerEntity) player, null);
      }
      INamedContainerProvider provider = NewChestData.getInventory(world, ((ILootTile) te).getTileId(), pos, (ServerPlayerEntity) player, (LockableLootTileEntity) te, ((ILootTile) te)::fillWithLoot);
      if (!((ILootTile)te).getOpeners().contains(player.getUniqueID())) {
        player.addStat(ModStats.LOOTED_STAT);
      }
      player.openContainer(provider);
      PiglinTasks.func_234478_a_(player, true);
      return true;
    } else {
      return false;
    }
  }

  public static void handleLootCart(World world, LootrChestMinecartEntity cart, PlayerEntity player) {
    if (!world.isRemote()) {
      Lootr.CART_PREDICATE.trigger((ServerPlayerEntity) player, null);
      if (!cart.getOpeners().contains(player.getUniqueID())) {
        cart.addOpener(player);
        player.addStat(ModStats.LOOTED_STAT);
      }
      INamedContainerProvider provider = NewChestData.getInventory(world, cart, (ServerPlayerEntity) player, cart::addLoot);
      player.openContainer(provider);
    }
  }

  public static boolean handleLootInventory(Block block, World world, BlockPos pos, PlayerEntity player) {
    if (world.isRemote()) {
      return false;
    }
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof SpecialLootInventoryTile) {
      Lootr.CHEST_PREDICATE.trigger((ServerPlayerEntity) player, null);
      SpecialLootInventoryTile tile = (SpecialLootInventoryTile) te;
      INamedContainerProvider provider = NewChestData.getInventory(world, tile.getTileId(), tile.getCustomInventory(), (ServerPlayerEntity) player, pos, tile);
      if (!((ILootTile)te).getOpeners().contains(player.getUniqueID())) {
        player.addStat(ModStats.LOOTED_STAT);
      }
      player.openContainer(provider);
      PiglinTasks.func_234478_a_(player, true);
      return true;
    } else {
      return false;
    }
  }
}
