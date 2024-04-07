/*
 * This file is part of Neo Launcher
 * Copyright (c) 2022   Neo Launcher Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.saggitt.omega.compose.pages.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import com.android.launcher3.R
import com.android.launcher3.Utilities
import com.saggitt.omega.compose.components.BaseDialog
import com.saggitt.omega.compose.components.ViewWithActionBar
import com.saggitt.omega.compose.components.preferences.IntSelectionPrefDialogUI
import com.saggitt.omega.compose.components.preferences.PreferenceGroup
import com.saggitt.omega.compose.navigation.Routes
import com.saggitt.omega.compose.navigation.preferenceGraph
import com.saggitt.omega.compose.pages.AppCategoriesPage
import com.saggitt.omega.preferences.IntSelectionPref
import com.saggitt.omega.preferences.LAYOUT_CUSTOM_CATEGORIES

@Composable
fun DevPrefsPage() {
    val context = LocalContext.current
    val prefs = Utilities.getNeoPrefs(context)
    val openDialog = remember { mutableStateOf(false) }
    var dialogPref by remember { mutableStateOf<Any?>(null) }
    val onPrefDialog = { pref: Any ->
        dialogPref = pref
        openDialog.value = true
    }

    val developerPrefs = remember(prefs.changePoker.collectAsState(initial = 1).value) {
        mutableStateListOf(
            *listOfNotNull(
                prefs.restartLauncher,
                prefs.developerOptionsEnabled,
                prefs.showDebugInfo,
                prefs.desktopFreeScrolling,
                prefs.drawerLayout,
                if (prefs.drawerLayout.getValue() == LAYOUT_CUSTOM_CATEGORIES) {
                    prefs.drawerAppGroups
                } else {
                    null
                }
            ).toTypedArray()
        )
    }

    ViewWithActionBar(
            title = stringResource(R.string.developer_options_title)
    ) { paddingValues ->
        LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                contentPadding = paddingValues,
                verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                PreferenceGroup(
                    heading = null,
                    prefs = developerPrefs,
                    onPrefDialog = onPrefDialog
                )
            }
        }

        if (openDialog.value) {
            BaseDialog(openDialogCustom = openDialog) {
                when (dialogPref) {
                    is IntSelectionPref -> IntSelectionPrefDialogUI(
                        pref = dialogPref as IntSelectionPref,
                        openDialogCustom = openDialog
                    )
                }
            }
        }
    }
}

fun NavGraphBuilder.devPrefsGraph(route: String) {
    preferenceGraph(route, { DevPrefsPage() }) { subRoute ->
        preferenceGraph(route = subRoute(Routes.CATEGORIZE_APPS), { AppCategoriesPage() })
    }
}