package com.example.suicanfcreader.view.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.suicanfcreader.model.Card
import com.example.suicanfcreader.ui.theme.*
import com.example.suicanfcreader.viewModel.TopScreenViewModel

import androidx.compose.ui.res.stringResource
import com.example.suicanfcreader.R
import java.util.Locale

@Composable
fun TopScreen(
    topScreenViewModel: TopScreenViewModel
) {
    val nfcCards by topScreenViewModel.nfcCards.observeAsState(emptyList())
    val isDataRefreshed by topScreenViewModel.isDataRefreshed.observeAsState(false)
    val isDownloading by topScreenViewModel.isDownloading.observeAsState(false)
    val isTranslatorReady by topScreenViewModel.isTranslatorReady.observeAsState(false)
    
    val snackbarHostState = remember { SnackbarHostState() }
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    val context = androidx.compose.ui.platform.LocalContext.current
    val successMessage = stringResource(R.string.scan_success)

    LaunchedEffect(isDataRefreshed) {
        if (isDataRefreshed) {
            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
            snackbarHostState.showSnackbar(successMessage)
            topScreenViewModel.resetDataRefreshed()
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { 
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    containerColor = SuicaGreen,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    snackbarData = data
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(BackgroundGradientStart, BackgroundGradientEnd)
                    )
                )
                .padding(padding)
        ) {
            // Background Elements... (kept same)
            Box(
                modifier = Modifier
                    .offset(x = (-50).dp, y = (-50).dp)
                    .size(300.dp)
                    .blur(100.dp)
                    .background(SuicaGreen.copy(alpha = 0.2f), RoundedCornerShape(150.dp))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = 100.dp, y = (-100).dp)
                    .size(250.dp)
                    .blur(120.dp)
                    .background(JRBlue.copy(alpha = 0.15f), RoundedCornerShape(125.dp))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-80).dp, y = 80.dp)
                    .size(350.dp)
                    .blur(150.dp)
                    .background(Purple40.copy(alpha = 0.12f), RoundedCornerShape(175.dp))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 50.dp, y = 50.dp)
                    .size(300.dp)
                    .blur(100.dp)
                    .background(SuicaGreen.copy(alpha = 0.15f), RoundedCornerShape(150.dp))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(60.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.app_title),
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }
                
                Text(
                    text = if (nfcCards.isEmpty()) stringResource(R.string.scan_prompt) else stringResource(R.string.history_count, nfcCards.size),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )

                AnimatedVisibility(visible = !isTranslatorReady && Locale.getDefault().language != "ja") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(SuicaGreen.copy(alpha = 0.1f))
                            .border(1.dp, SuicaGreen.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                            .padding(20.dp)
                    ) {
                        Column {
                            Text(
                                text = "Setup AI Translation",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Enhance your experience with real-time station name translation in your language.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { topScreenViewModel.downloadTranslationModel(context) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = SuicaGreen),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Get Started", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (nfcCards.isEmpty()) {
                    EmptyStateView()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        itemsIndexed(nfcCards) { index, card ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(animationSpec = tween(500, delayMillis = index * 100)) +
                                        slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(500, delayMillis = index * 100))
                            ) {
                                HistoryCard(card)
                            }
                        }
                    }
                }
            }

            // Full Screen Loading Overlay
            if (isDownloading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f))
                        .blur(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = SuicaGreen, strokeWidth = 4.dp)
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Downloading AI Model...",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Please wait while we prepare your offline translation kit.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 50.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                // Subtle glow behind icon
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .blur(40.dp)
                        .background(SuicaGreen.copy(alpha = 0.15f), RoundedCornerShape(70.dp))
                )
                Icon(
                    imageVector = Icons.Default.Nfc,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = Color.White.copy(alpha = 0.8f)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Ready to Scan",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.scan_prompt),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.5f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 40.dp)
            )
        }
    }
}

@Composable
fun HistoryCard(card: Card) {
    val typeColor = when (card.kindResId) {
        R.string.kind_shopping -> ShoppingOrange
        R.string.kind_jr -> JRBlue
        else -> SuicaGreen
    }

    val icon = when (card.kindResId) {
        R.string.kind_shopping -> Icons.Default.ShoppingCart
        R.string.kind_bus -> Icons.Default.DirectionsBus
        else -> Icons.Default.Train
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(GlassWhite)
            .border(1.dp, GlassBorder, RoundedCornerShape(28.dp))
            .padding(24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(typeColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = typeColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = if (card.kindResId != 0) stringResource(card.kindResId) else stringResource(R.string.unknown),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.date_format, card.year, card.month, card.day),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = stringResource(R.string.balance_symbol),
                            style = MaterialTheme.typography.bodyMedium,
                            color = SuicaGreen,
                            modifier = Modifier.padding(bottom = 2.dp, end = 2.dp),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = card.balance ?: "0",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Text(
                        text = stringResource(R.string.balance_label),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
            }

            if (card.inStation != null && card.inStation != "-") {
                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StationInfo(
                        label = stringResource(R.string.boarding),
                        line = card.inLine ?: "-",
                        station = card.inStation ?: "-",
                        company = card.inCompany ?: "-",
                        alignEnd = false
                    )

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.size(20.dp)
                    )

                    StationInfo(
                        label = stringResource(R.string.alighting),
                        line = card.outLine ?: "-",
                        station = card.outStation ?: "-",
                        company = card.outCompany ?: "-",
                        alignEnd = true
                    )
                }
            } else if (card.kindResId == R.string.kind_shopping) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.payment_method, if (card.deviceResId != 0) stringResource(card.deviceResId) else stringResource(R.string.unknown)),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun StationInfo(label: String, line: String, station: String, company: String, alignEnd: Boolean) {
    Column(horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.5f)
        )
        Text(
            text = station,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = if (company.isNotEmpty()) "$company $line" else line,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.4f),
            maxLines = 1
        )
    }
}
