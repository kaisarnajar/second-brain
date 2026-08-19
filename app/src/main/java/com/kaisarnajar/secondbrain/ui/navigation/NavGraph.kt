package com.kaisarnajar.secondbrain.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kaisarnajar.secondbrain.ui.screens.NoteEditorScreen
import com.kaisarnajar.secondbrain.ui.screens.NotesScreen
import com.kaisarnajar.secondbrain.viewmodel.NotesViewModel

object Screen {
    const val NotesList = "notes_list"
    const val NoteEditor = "note_editor"
}

@Composable
fun SecondBrainNavGraph(
    navController: NavHostController = rememberNavController(),
    notesViewModel: NotesViewModel = hiltViewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.NotesList
    ) {
        composable(Screen.NotesList) {
            NotesScreen(
                viewModel = notesViewModel,
                onNavigateToEditor = { noteId, isEdit ->
                    if (noteId != null && noteId > 0) {
                        navController.navigate("${Screen.NoteEditor}?noteId=$noteId&isEdit=$isEdit")
                    } else {
                        navController.navigate("${Screen.NoteEditor}?isEdit=true")
                    }
                }
            )
        }

        composable(
            route = "${Screen.NoteEditor}?noteId={noteId}&isEdit={isEdit}",
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.LongType
                    defaultValue = -1L
                },
                navArgument("isEdit") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val noteIdArg = backStackEntry.arguments?.getLong("noteId")
            val isEditArg = backStackEntry.arguments?.getBoolean("isEdit") ?: false
            val noteId = if (noteIdArg != null && noteIdArg > 0) noteIdArg else null

            NoteEditorScreen(
                noteId = noteId,
                initialIsEditing = isEditArg,
                viewModel = notesViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

