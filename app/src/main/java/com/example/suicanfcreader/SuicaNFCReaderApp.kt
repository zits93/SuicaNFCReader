package com.example.suicanfcreader

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.suicanfcreader.navigation.SuicaNFCReaderNavigation
import com.example.suicanfcreader.ui.theme.SuicaNFCReaderTheme
import com.example.suicanfcreader.viewModel.TopScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuicaNFCReaderApp(context: Context, viewModel: TopScreenViewModel) {
    SuicaNFCReaderTheme {
        val navController = rememberNavController()

        SuicaNFCReaderNavigation(
            navController = navController,
            context = context,
            modifier = Modifier,
            viewModel
        )
    }
}
