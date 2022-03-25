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

package org.catrobat.catroid.test.content.bricks;

import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.CompositeBrick;
import org.catrobat.catroid.content.bricks.ConcurrentFormulaHashMap;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.PhiroIfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.RaspiIfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.koin.CatroidKoinHelperKt;
import org.catrobat.catroid.ui.recyclerview.fragment.DataListFragment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.koin.core.module.Module;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import kotlin.Lazy;

import static org.catrobat.catroid.test.StaticSingletonInitializer.initializeStaticSingletonMethods;
import static org.junit.Assert.assertEquals;
import static org.koin.java.KoinJavaComponent.inject;

@RunWith(Parameterized.class)
public class CompositeBrickWithSecondaryListUpdateTest {

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{IfLogicBeginBrick.class.getSimpleName(), IfLogicBeginBrick.class},
				{PhiroIfLogicBeginBrick.class.getSimpleName(), PhiroIfLogicBeginBrick.class},
				{RaspiIfLogicBeginBrick.class.getSimpleName(), RaspiIfLogicBeginBrick.class},
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public Class<CompositeBrick> compositeBrickClass;

	private static final String VARIABLE_NAME = "Test";
	private static final String VARIABLE_NAME_USERVARIABLE_FORMAT = "\"Test\" ";
	private static final String VARIABLE_NAME_USERLIST_FORMAT = "*Test* ";
	private static final String INVALID_NAME = "Abcd";
	private static final String NEW_VARIABLE_NAME = "NewName";
	private static final String NEW_VARIABLE_USERVARIABLE_FORMAT = "\"NewName\" ";
	private static final String NEW_VARIABLE_USERLIST_FORMAT = "*NewName* ";

	private UserList userList;
	private UserVariable userVariable;
	private FormulaBrick primaryListFormulaBrick;
	private FormulaBrick secondaryListFormulaBrick;

	private final Lazy<ProjectManager> projectManager = inject(ProjectManager.class);
	private final List<Module> dependencyModules =
			Collections.singletonList(CatroidKoinHelperKt.getProjectManagerModule());

	@Before
	public void setUp() throws IllegalAccessException, InstantiationException {
		initializeStaticSingletonMethods(dependencyModules);
		Project project = new Project();
		userVariable = new UserVariable();
		userList = new UserList();
		Scene scene = new Scene();
		Sprite sprite = new Sprite();
		Script script = new WhenScript();
		CompositeBrick compositeBrick = compositeBrickClass.newInstance();
		primaryListFormulaBrick = new SetXBrick();
		secondaryListFormulaBrick = new SetXBrick();

		userVariable.setName(VARIABLE_NAME);
		userList.setName(VARIABLE_NAME);

		project.addScene(scene);
		scene.addSprite(sprite);
		sprite.addScript(script);
		script.addBrick(compositeBrick);
		compositeBrick.getNestedBricks().add(primaryListFormulaBrick);
		compositeBrick.getSecondaryNestedBricks().add(secondaryListFormulaBrick);

		project.addUserVariable(userVariable);
		project.addUserList(userList);
		projectManager.getValue().setCurrentProject(project);
	}

	@After
	public void tearDown() throws Exception {
		CatroidKoinHelperKt.stop(dependencyModules);
	}

	@Test
	public void testRenamingVariable() {
		FormulaElement element = new FormulaElement(FormulaElement.ElementType.USER_VARIABLE,
				VARIABLE_NAME, null);
		Formula newFormula = new Formula(element);
		ConcurrentFormulaHashMap primaryMap = primaryListFormulaBrick.getFormulaMap();
		ConcurrentFormulaHashMap secondaryMap = secondaryListFormulaBrick.getFormulaMap();

		primaryMap.forEach((k, v) -> {
			primaryListFormulaBrick.setFormulaWithBrickField(k, newFormula);
		});
		secondaryMap.forEach((k, v) -> {
			secondaryListFormulaBrick.setFormulaWithBrickField(k, newFormula);
		});

		DataListFragment.updateUserDataReferences(VARIABLE_NAME, NEW_VARIABLE_NAME, userVariable);

		primaryMap.forEach((k, v) -> {
			assertEquals(v.getTrimmedFormulaString(CatroidApplication.getAppContext()),
					NEW_VARIABLE_USERVARIABLE_FORMAT);
		});
		secondaryMap.forEach((k, v) -> {
			assertEquals(v.getTrimmedFormulaString(CatroidApplication.getAppContext()),
					NEW_VARIABLE_USERVARIABLE_FORMAT);
		});
	}

	@Test
	public void testRenamingVariableNoChanges() {
		FormulaElement element = new FormulaElement(FormulaElement.ElementType.USER_VARIABLE,
				VARIABLE_NAME, null);
		Formula newFormula = new Formula(element);
		ConcurrentFormulaHashMap primaryMap = primaryListFormulaBrick.getFormulaMap();
		ConcurrentFormulaHashMap secondaryMap = secondaryListFormulaBrick.getFormulaMap();

		primaryMap.forEach((k, v) -> {
			primaryListFormulaBrick.setFormulaWithBrickField(k, newFormula);
		});
		secondaryMap.forEach((k, v) -> {
			secondaryListFormulaBrick.setFormulaWithBrickField(k, newFormula);
		});

		DataListFragment.updateUserDataReferences(INVALID_NAME, NEW_VARIABLE_NAME, userVariable);

		primaryMap.forEach((k, v) -> {
			assertEquals(v.getTrimmedFormulaString(CatroidApplication.getAppContext()),
					VARIABLE_NAME_USERVARIABLE_FORMAT);
		});
		secondaryMap.forEach((k, v) -> {
			assertEquals(v.getTrimmedFormulaString(CatroidApplication.getAppContext()),
					VARIABLE_NAME_USERVARIABLE_FORMAT);
		});
	}

	@Test
	public void testRenamingList() {
		FormulaElement element = new FormulaElement(FormulaElement.ElementType.USER_LIST,
				VARIABLE_NAME, null);
		Formula newFormula = new Formula(element);
		ConcurrentFormulaHashMap primaryMap = primaryListFormulaBrick.getFormulaMap();
		ConcurrentFormulaHashMap secondaryMap = secondaryListFormulaBrick.getFormulaMap();

		primaryMap.forEach((k, v) -> {
			primaryListFormulaBrick.setFormulaWithBrickField(k, newFormula);
		});
		secondaryMap.forEach((k, v) -> {
			secondaryListFormulaBrick.setFormulaWithBrickField(k, newFormula);
		});

		DataListFragment.updateUserDataReferences(VARIABLE_NAME, NEW_VARIABLE_NAME, userList);

		primaryMap.forEach((k, v) -> {
			assertEquals(v.getTrimmedFormulaString(CatroidApplication.getAppContext()),
					NEW_VARIABLE_USERLIST_FORMAT);
		});
		secondaryMap.forEach((k, v) -> {
			assertEquals(v.getTrimmedFormulaString(CatroidApplication.getAppContext()),
					NEW_VARIABLE_USERLIST_FORMAT);
		});
	}

	@Test
	public void testRenamingListNoChanges() {
		FormulaElement element = new FormulaElement(FormulaElement.ElementType.USER_LIST,
				VARIABLE_NAME, null);
		Formula newFormula = new Formula(element);
		ConcurrentFormulaHashMap primaryMap = primaryListFormulaBrick.getFormulaMap();
		ConcurrentFormulaHashMap secondaryMap = secondaryListFormulaBrick.getFormulaMap();

		primaryMap.forEach((k, v) -> {
			primaryListFormulaBrick.setFormulaWithBrickField(k, newFormula);
		});
		secondaryMap.forEach((k, v) -> {
			secondaryListFormulaBrick.setFormulaWithBrickField(k, newFormula);
		});

		DataListFragment.updateUserDataReferences(INVALID_NAME, NEW_VARIABLE_NAME, userList);

		primaryMap.forEach((k, v) -> {
			assertEquals(v.getTrimmedFormulaString(CatroidApplication.getAppContext()),
					VARIABLE_NAME_USERLIST_FORMAT);
		});
		secondaryMap.forEach((k, v) -> {
			assertEquals(v.getTrimmedFormulaString(CatroidApplication.getAppContext()),
					VARIABLE_NAME_USERLIST_FORMAT);
		});
	}
}
