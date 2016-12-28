package com.bilbosoft.chekers.adapter;

import com.bilbosoft.chekers.R;
import com.bilbosoft.chekers.domain.Slot;
import com.bilbosoft.chekers.enums.PlayerTeam;

public class ImageHandler {

	public void loadImages(ViewHolder holder, Slot[] line) {
		holder.ivColumn0.imageView.setImageResource(getImage(line[0]));
		holder.ivColumn1.imageView.setImageResource(getImage(line[1]));
		holder.ivColumn2.imageView.setImageResource(getImage(line[2]));
		holder.ivColumn3.imageView.setImageResource(getImage(line[3]));
		holder.ivColumn4.imageView.setImageResource(getImage(line[4]));
		holder.ivColumn5.imageView.setImageResource(getImage(line[5]));
		holder.ivColumn6.imageView.setImageResource(getImage(line[6]));
		holder.ivColumn7.imageView.setImageResource(getImage(line[7]));
	}

	private int getImage(Slot slot) {

		int imageId;

		if (slot.getPiece() == null) {

			if (slot.isSelectable()) {

				imageId = R.drawable.valid;

			} else if (slot.isActive()) {

				imageId = R.drawable.black;

			} else {

				imageId = R.drawable.white;
			}

		} else {

			if (slot.getPiece().isSelected()) {

				if (slot.getPiece().getPlayerTeam().equals(PlayerTeam.RED)) {

					if (slot.getPiece().isKing()) {

						imageId = R.drawable.red_king_selected;

					} else {

						imageId = R.drawable.red_selected;

					}

				} else {

					if (slot.getPiece().isKing()) {

						imageId = R.drawable.white_king_selected;

					} else {

						imageId = R.drawable.white_selected;
					}
				}

			} else if (slot.getPiece().getPlayerTeam().equals(PlayerTeam.RED)) {

				if (slot.getPiece().isKing()) {

					imageId = R.drawable.red_king;

				} else {

					imageId = R.drawable.red_piece;
				}

			} else {

				if (slot.getPiece().isKing()) {

					imageId = R.drawable.white_king;

				} else {

					imageId = R.drawable.white_piece;
				}
			}
		}

		return imageId;
	}

}
