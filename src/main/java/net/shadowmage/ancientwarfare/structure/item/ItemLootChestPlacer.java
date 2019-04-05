package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.render.property.CoreProperties;
import net.shadowmage.ancientwarfare.core.util.NBTBuilder;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.gui.GuiLootChestPlacer;
import net.shadowmage.ancientwarfare.structure.init.AWStructureBlocks;
import net.shadowmage.ancientwarfare.structure.tile.IUpdatableLootContainer;

import java.util.Optional;
import java.util.Random;

public class ItemLootChestPlacer extends ItemBaseStructure {
	private static final String LOOT_TABLE_NAME_TAG = "lootTableName";
	private static final String LOOT_ROLLS_TAG = "lootRolls";
	private static final String BASKET_TAG = "basket";

	public ItemLootChestPlacer() {
		super("loot_chest_placer");
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (!player.world.isRemote) {
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_LOOT_CHEST_PLACER, 0, 0, 0);
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (player.isSneaking()) {
			return EnumActionResult.PASS;
		}
		if (world.isRemote) {
			return EnumActionResult.SUCCESS;
		}

		ItemStack placer = player.getHeldItem(hand);
		Optional<Tuple<ResourceLocation, Byte>> lt = getLootParameters(placer);
		if (!lt.isPresent() || !lootTableExists(world, lt.get().getFirst())) {
			return EnumActionResult.PASS;
		}

		BlockPos placePos = pos.offset(facing);
		Block block = getPlaceBasket(placer) ? AWStructureBlocks.LOOT_BASKET : AWStructureBlocks.ADVANCED_LOOT_CHEST;
		if (block.canPlaceBlockAt(world, placePos)) {
			world.setBlockState(placePos, block.getDefaultState().withProperty(CoreProperties.FACING, player.getHorizontalFacing().getOpposite()));
			WorldTools.getTile(world, placePos, IUpdatableLootContainer.class)
					.ifPresent(t -> {
						t.setLootTable(lt.get().getFirst(), new Random(placePos.toLong()).nextLong());
						t.setLootRolls(lt.get().getSecond());
					});

			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;
	}

	private boolean lootTableExists(World world, ResourceLocation lootTableName) {
		return world.getLootTableManager().getLootTableFromLocation(lootTableName) != null;
	}

	public static Optional<Tuple<ResourceLocation, Byte>> getLootParameters(ItemStack placer) {
		//noinspection ConstantConditions
		return placer.hasTagCompound() && placer.getTagCompound().hasKey(LOOT_TABLE_NAME_TAG) ?
				Optional.of(new Tuple<>(new ResourceLocation(placer.getTagCompound().getString(LOOT_TABLE_NAME_TAG)), placer.getTagCompound().getByte(LOOT_ROLLS_TAG))) : Optional.empty();
	}

	public static boolean getPlaceBasket(ItemStack placer) {
		return placer.hasTagCompound() && placer.getTagCompound().getBoolean(BASKET_TAG);
	}

	public static void setLootParameters(ItemStack placer, String lootTableName, byte rolls, boolean basket) {
		placer.setTagCompound(new NBTBuilder().setString(LOOT_TABLE_NAME_TAG, lootTableName).setByte(LOOT_ROLLS_TAG, rolls).setBoolean(BASKET_TAG, basket).build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		super.registerClient();

		NetworkHandler.registerGui(NetworkHandler.GUI_LOOT_CHEST_PLACER, GuiLootChestPlacer.class);
	}
}
