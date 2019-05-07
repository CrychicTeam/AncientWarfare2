package net.shadowmage.ancientwarfare.automation.tile.worksite;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

public final class WorkSiteQuarry extends TileWorksiteBoundedInventory {
	private boolean finished;
	private boolean hasDoneInit = false;
	public int height;

	/*
	 * Current position within work bounds.
	 * Incremented when work is processed.
	 */
	private BlockPos current = BlockPos.ORIGIN;//position within bounds that is the 'active' position
	private BlockPos validate = BlockPos.ORIGIN;

	public WorkSiteQuarry() {
		super();
	}

	@Override
	public boolean userAdjustableBlocks() {
		return false;
	}
	@Override
	protected void onBoundsSet() {
		super.onBoundsSet();
		height = pos.getY();
		offsetBounds();
	}

	private void offsetBounds() {
		BlockPos boundsMax = getWorkBoundsMax();
		setWorkBoundsMax(boundsMax.up(pos.getY() - 1 - boundsMax.getY()));
		boundsMax = getWorkBoundsMin();
		setWorkBoundsMin(boundsMax.up(pos.getY() - height - boundsMax.getY()));
		BlockTools.notifyBlockUpdate(this);
	}
	@Override
	public void onBoundsAdjusted() {
		offsetBounds();
		current = new BlockPos(getWorkBoundsMin().getX(), getWorkBoundsMax().getY(), getWorkBoundsMin().getZ());
		validate = current;
	}

	@Override
	public Set<WorksiteUpgrade> getValidUpgrades() {
		return EnumSet.of(WorksiteUpgrade.ENCHANTED_TOOLS_1, WorksiteUpgrade.ENCHANTED_TOOLS_2, WorksiteUpgrade.QUARRY_MEDIUM, WorksiteUpgrade.QUARRY_LARGE, WorksiteUpgrade.TOOL_QUALITY_1, WorksiteUpgrade.TOOL_QUALITY_2, WorksiteUpgrade.TOOL_QUALITY_3, WorksiteUpgrade.QUARRY_CHUNK_LOADER);
	}

	@Override
	public int getBoundsMaxWidth() {
		if (getUpgrades().contains(WorksiteUpgrade.QUARRY_LARGE)) {
			return 64;
		}
		return getUpgrades().contains(WorksiteUpgrade.QUARRY_MEDIUM) ? 32 : 16;
	}

	@Override
	protected void updateWorksite() {
		if (!hasDoneInit) {
			initWorkSite();
			hasDoneInit = true;
		}
		world.profiler.startSection("Incremental Scan");
		if (canHarvest(validate)) {
			current = validate;
			finished = false;
		} else {
			incrementValidationPosition();
		}
		world.profiler.endSection();
	}

	@Override
	public void addUpgrade(WorksiteUpgrade upgrade) {
		super.addUpgrade(upgrade);
		current = new BlockPos(getWorkBoundsMin().getX(), getWorkBoundsMax().getY(), getWorkBoundsMin().getZ());
		validate = current;
		this.finished = false;
	}

	private static final IWorksiteAction DIG_ACTION = WorksiteImplementation::getEnergyPerActivation;
	@Override
	protected Optional<IWorksiteAction> getNextAction() {
		return !finished ? Optional.of(DIG_ACTION) : Optional.empty();
	}

	@Override
	protected boolean processAction(IWorksiteAction action) {
		if (!hasDoneInit) {
			initWorkSite();
			hasDoneInit = true;
		}
		/*
		 * while the current position is invalid, increment to a valid one. generally the incremental scan
         * should have take care of this prior to processWork being called, but just in case...
         */
		while (!canHarvest(current)) {
			if (!incrementPosition()) {
				/*
				 * if no valid position was found, set finished, exit
                 */
				finished = true;
				return false;
			}
		}
		/*
		 * if made it this far, a valid position was found, break it and add blocks to inventory
         */
		return harvestBlock(current);
	}

	private boolean harvestBlock(BlockPos current) {
		IBlockState state = world.getBlockState(current);
		Block block = state.getBlock();
		NonNullList<ItemStack> stacks = NonNullList.create();

		block.getDrops(stacks, world, current, state, getFortune());

		if (!InventoryTools.insertItems(mainInventory, stacks, true).isEmpty()) {
			return false;
		}

		if (!BlockTools.breakBlockNoDrops(world, current, state)) {
			return false;
		}

		InventoryTools.insertOrDropItems(mainInventory, stacks, world, current);

		return true;
	}

	private boolean incrementPosition() {
		if (finished) {
			return false;
		}
		current = current.east();
		if (current.getX() > getWorkBoundsMax().getX()) {
			current = new BlockPos(getWorkBoundsMin().getX(), current.getY(), current.getZ());
			current = current.south();
			if (current.getZ() > getWorkBoundsMax().getZ()) {
				current = new BlockPos(current.getX(), current.getY(), getWorkBoundsMin().getZ());
				current = current.down();
				if (current.getY() <= (pos.getY() - (height + 1))) {
					return false;
				}
			}
		}
		return true;
	}

	private void incrementValidationPosition() {
		validate = validate.east();
		if (validate.getY() >= current.getY() && validate.getZ() >= current.getZ() && validate.getX() >= current.getX()) {//dont let validation pass current position
			validate = new BlockPos(getWorkBoundsMin().getX(), getWorkBoundsMax().getY(), getWorkBoundsMin().getZ());
		} else if (validate.getX() > getWorkBoundsMax().getX()) {
			validate = new BlockPos(getWorkBoundsMin().getX(), validate.getY(), validate.getZ());
			validate = validate.south();
			if (validate.getZ() > getWorkBoundsMax().getZ()) {
				validate = new BlockPos(validate.getX(), validate.getY(), getWorkBoundsMin().getZ());
				validate = validate.down();
				if (validate.getY() <= 0) {
					validate = new BlockPos(getWorkBoundsMin().getX(), getWorkBoundsMax().getY(), getWorkBoundsMin().getZ());
				}
			}
		}
	}

	private boolean canHarvest(BlockPos harvestPos) {
		//TODO add block-breaking exclusion list to config
		IBlockState state = world.getBlockState(harvestPos);
		Block block = state.getBlock();
		if (world.isAirBlock(harvestPos) || state.getMaterial().isLiquid()) {
			return false;
		}
		int harvestLevel = block.getHarvestLevel(state);
		if (harvestLevel >= 2) {
			int toolLevel = 1;
			if (getUpgrades().contains(WorksiteUpgrade.TOOL_QUALITY_3)) {
				toolLevel = Integer.MAX_VALUE;
			} else if (getUpgrades().contains(WorksiteUpgrade.TOOL_QUALITY_2)) {
				toolLevel = 3;
			} else if (getUpgrades().contains(WorksiteUpgrade.TOOL_QUALITY_1)) {
				toolLevel = 2;
			}
			if (toolLevel < harvestLevel) {
				return false;
			}//else is harvestable, check the rest of the checks
		}
		return state.getBlockHardness(world, harvestPos) >= 0;
	}

	private void initWorkSite() {
		BlockPos boundsMin = getWorkBoundsMin();
		setWorkBoundsMin(boundsMin.up(pos.getY() - height - boundsMin.getY()));
		current = new BlockPos(getWorkBoundsMin().getX(), getWorkBoundsMax().getY(), getWorkBoundsMin().getZ());
		validate = current;
		BlockTools.notifyBlockUpdate(this);//resend work-bounds change
	}

	@Override
	public WorkType getWorkType() {
		return WorkType.MINING;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		current = BlockPos.fromLong(tag.getLong("current"));
		validate = BlockPos.fromLong(tag.getLong("validate"));
		finished = tag.getBoolean("finished");
		hasDoneInit = tag.getBoolean("init");
		height = tag.getInteger("height");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setLong("current", current.toLong());
		tag.setLong("validate", validate.toLong());
		tag.setBoolean("finished", finished);
		tag.setBoolean("init", hasDoneInit);
		tag.setInteger("height", height);
		return tag;
	}

	@Override
	public boolean onBlockClicked(EntityPlayer player, @Nullable EnumHand hand) {
		if (!player.world.isRemote) {
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_QUARRY, pos);
		}
		return true;
	}
}
