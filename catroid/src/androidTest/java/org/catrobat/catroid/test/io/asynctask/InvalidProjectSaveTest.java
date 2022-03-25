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

package org.catrobat.catroid.test.io.asynctask;

import org.catrobat.catroid.ProjectManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import kotlin.Lazy;

import static org.catrobat.catroid.io.asynctask.ProjectSaverKt.saveProjectSerial;
import static org.junit.Assert.assertFalse;
import static org.koin.java.KoinJavaComponent.inject;

@RunWith(AndroidJUnit4.class)
public class InvalidProjectSaveTest {
	@Before
	public void setUp() {
		final Lazy<ProjectManager> projectManager = inject(ProjectManager.class);
		projectManager.getValue().setCurrentProject(null);
	}

	@Test
	public void invalidProjectSaveTaskTest() {
		assertFalse(saveProjectSerial(null, ApplicationProvider.getApplicationContext()));
	}
}
