/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.PhysicsLook;

import static org.koin.java.KoinJavaComponent.inject;

public class PointToAction extends TemporalAction {

	private Sprite sprite;
	private Sprite pointedSprite;

	@Override
	protected void update(float percent) {
		if (pointedSprite == null
				|| !inject(ProjectManager.class).getValue().getCurrentlyPlayingScene().getSpriteList().contains(pointedSprite)) {
			return;
		}

		float spriteXPosition = sprite.look.getXInUserInterfaceDimensionUnit();
		float spriteYPosition = sprite.look.getYInUserInterfaceDimensionUnit();
		float pointedSpriteXPosition = pointedSprite.look.getXInUserInterfaceDimensionUnit();
		float pointedSpriteYPosition = pointedSprite.look.getYInUserInterfaceDimensionUnit();

		double rotationDegrees;
		if (spriteXPosition == pointedSpriteXPosition && spriteYPosition == pointedSpriteYPosition) {
			rotationDegrees = 90;
		} else if (spriteXPosition == pointedSpriteXPosition) {
			if (spriteYPosition < pointedSpriteYPosition) {
				rotationDegrees = 0;
			} else {
				rotationDegrees = 180;
			}
		} else if (spriteYPosition == pointedSpriteYPosition) {
			if (spriteXPosition < pointedSpriteXPosition) {
				rotationDegrees = 90;
			} else {
				rotationDegrees = -90;
			}
		} else {
			rotationDegrees = 90f - Math.toDegrees(Math.atan2(pointedSpriteYPosition - spriteYPosition,
					pointedSpriteXPosition - spriteXPosition));
		}
		if (sprite.look instanceof PhysicsLook) {
			((PhysicsLook) sprite.look).setFlippedByDirection((float) rotationDegrees);
		}
		sprite.look.setMotionDirectionInUserInterfaceDimensionUnit((float) rotationDegrees);
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setPointedSprite(Sprite pointedSprite) {
		this.pointedSprite = pointedSprite;
	}
}
