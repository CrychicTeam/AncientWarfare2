package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.block.BlockCoffin;
import net.shadowmage.ancientwarfare.structure.util.LootHelper;

import javax.annotation.Nullable;
import java.util.Optional;

public abstract class TileCoffin extends TileMulti implements ITickable, ISpecialLootContainer {
	protected BlockCoffin.CoffinDirection direction = BlockCoffin.CoffinDirection.NORTH;
	private boolean opening = false;
	private boolean open = false;
	private float prevLidAngle = 0;
	private float lidAngle = 0;
	private int openTime = 0;
	private LootSettings lootSettings = new LootSettings();
	private static final float OPEN_ANGLE = 15F;

	public BlockCoffin.IVariant getVariant() {
		Optional<BlockPos> mainPos = getMainBlockPos();
		if (!mainPos.isPresent() || mainPos.get().equals(pos)) {
			return variant;
		}
		return WorldTools.getTile(world, mainPos.get(), TileCoffin.class).map(TileCoffin::getVariant).orElse(getDefaultVariant());
	}

	public void setVariant(BlockCoffin.IVariant variant) {
		this.variant = variant;
	}

	private BlockCoffin.IVariant variant = getDefaultVariant();

	protected abstract BlockCoffin.IVariant getDefaultVariant();

	@Override
	public void setPlacementDirection(World world, BlockPos pos, IBlockState state, EnumFacing horizontalFacing, float rotationYaw) {
		setDirection(BlockCoffin.CoffinDirection.fromFacing(horizontalFacing));
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		readNBT(compound);
	}

	protected void readNBT(NBTTagCompound compound) {
		direction = BlockCoffin.CoffinDirection.fromName(compound.getString("direction"));
		variant = deserializeVariant(compound.getString("variant"));
		opening = compound.getBoolean("opening");
		open = compound.getBoolean("open");
		if (open) {
			lidAngle = prevLidAngle = OPEN_ANGLE;
		}
		lootSettings = LootSettings.deserializeNBT(compound.getCompoundTag("lootSettings"));
	}

	protected abstract BlockCoffin.IVariant deserializeVariant(String name);

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		writeNBT(compound);
		return compound;
	}

	protected void writeNBT(NBTTagCompound compound) {
		compound.setString("direction", direction.getName());
		compound.setString("variant", variant.getName());
		compound.setBoolean("opening", opening);
		compound.setBoolean("open", open);
		compound.setTag("lootSettings", lootSettings.serializeNBT());
	}

	@Override
	protected void writeUpdateNBT(NBTTagCompound tag) {
		super.writeUpdateNBT(tag);
		writeNBT(tag);
	}

	@Override
	protected void handleUpdateNBT(NBTTagCompound tag) {
		super.handleUpdateNBT(tag);
		readNBT(tag);
	}

	public void setDirection(BlockCoffin.CoffinDirection direction) {
		this.direction = direction;
	}

	public BlockCoffin.CoffinDirection getDirection() {
		return direction;
	}

	public void open() {
		Optional<BlockPos> mainPos = getMainBlockPos();
		if (!mainPos.isPresent() || mainPos.get().equals(pos)) {
			if (!open && !opening) {
				playSound(variant);
				opening = true;
				BlockTools.notifyBlockUpdate(this);
			}
			return;
		}
		WorldTools.getTile(world, mainPos.get(), TileCoffin.class).ifPresent(TileCoffin::open);
	}

	protected abstract void playSound(BlockCoffin.IVariant variant);

	private void dropLoot(@Nullable EntityPlayer player) {
		if (world.isRemote || isOpen()) {
			return;
		}
		Optional<BlockPos> mainPos = getMainBlockPos();
		if (!mainPos.isPresent() || mainPos.get().equals(pos)) {
			LootHelper.dropLoot(this, player);
			return;
		}
		WorldTools.getTile(world, mainPos.get(), TileCoffin.class).ifPresent(te -> te.dropLoot(player));
	}

	private boolean isOpen() {
		return getMainBlockPos().map(mp -> mp.equals(pos) ? open : WorldTools.getTile(world, mp, TileCoffin.class).map(te -> te.open).orElse(true)).orElse(open);
	}

	@Override
	public void onBlockBroken(IBlockState state) {
		dropLoot(EntityTools.findClosestPlayer(world, pos, 100));
		super.onBlockBroken(state);
	}

	@Override
	public void update() {
		if (opening && !open) {
			prevLidAngle = lidAngle;
			openTime++;

			float halfAngle = OPEN_ANGLE / 2;
			float halfTime = (float) getTotalOpenTime() / 2;
			if (openTime > halfTime) {
				float ratio = (getTotalOpenTime() - openTime) / halfTime;
				lidAngle = OPEN_ANGLE - (halfAngle * ratio * ratio);
			} else {
				float ratio = openTime / halfTime;
				lidAngle = halfAngle * ratio * ratio;
			}
			if (lidAngle >= OPEN_ANGLE) {
				dropLoot(EntityTools.findClosestPlayer(world, pos, 100));
				prevLidAngle = lidAngle;
				open = true;
			}
		}
	}

	protected abstract int getTotalOpenTime();

	public float getPrevLidAngle() {
		return prevLidAngle;
	}

	public float getLidAngle() {
		return lidAngle;
	}

	@Override
	public void setLootSettings(LootSettings settings) {
		this.lootSettings = settings;
	}

	@Override
	public LootSettings getLootSettings() {
		return lootSettings;
	}
}
