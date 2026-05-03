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
    var showInfoSheet by remember { mutableStateOf(false) }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    val context = androidx.compose.ui.platform.LocalContext.current
    val successMessage = stringResource(R.string.scan_success)

    val showDownloadPrompt = remember { mutableStateOf(false) }

    // Prompt for download on start if not ready
    LaunchedEffect(isTranslatorReady) {
        if (!isTranslatorReady && Locale.getDefault().language != "ja" && !isDownloading) {
            showDownloadPrompt.value = true
        }
    }

    LaunchedEffect(showDownloadPrompt.value) {
        if (showDownloadPrompt.value) {
            val result = snackbarHostState.showSnackbar(
                message = context.getString(R.string.download_translation_prompt),
                actionLabel = context.getString(R.string.download),
                duration = SnackbarDuration.Indefinite
            )
            if (result == SnackbarResult.ActionPerformed) {
                topScreenViewModel.downloadTranslationModel(context)
            }
            showDownloadPrompt.value = false
        }
    }

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
            // Optimized Background Elements (Canvas for better performance instead of overlapping Boxes)
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(SuicaGreen.copy(alpha = 0.15f), Color.Transparent),
                        center = androidx.compose.ui.geometry.Offset(150.dp.toPx(), 150.dp.toPx()),
                        radius = 150.dp.toPx()
                    ),
                    center = androidx.compose.ui.geometry.Offset(100.dp.toPx(), 100.dp.toPx()),
                    radius = 150.dp.toPx()
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(JRBlue.copy(alpha = 0.12f), Color.Transparent),
                        center = androidx.compose.ui.geometry.Offset(size.width - 25.dp.toPx(), 25.dp.toPx()),
                        radius = 125.dp.toPx()
                    ),
                    center = androidx.compose.ui.geometry.Offset(size.width - 25.dp.toPx(), 25.dp.toPx()),
                    radius = 125.dp.toPx()
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Purple40.copy(alpha = 0.1f), Color.Transparent),
                        center = androidx.compose.ui.geometry.Offset(95.dp.toPx(), size.height - 95.dp.toPx()),
                        radius = 175.dp.toPx()
                    ),
                    center = androidx.compose.ui.geometry.Offset(95.dp.toPx(), size.height - 95.dp.toPx()),
                    radius = 175.dp.toPx()
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(SuicaGreen.copy(alpha = 0.12f), Color.Transparent),
                        center = androidx.compose.ui.geometry.Offset(size.width - 100.dp.toPx(), size.height - 100.dp.toPx()),
                        radius = 150.dp.toPx()
                    ),
                    center = androidx.compose.ui.geometry.Offset(size.width - 100.dp.toPx(), size.height - 100.dp.toPx()),
                    radius = 150.dp.toPx()
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(60.dp))
                
                if (isDownloading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        color = SuicaGreen,
                        trackColor = Color.White.copy(alpha = 0.2f)
                    )
                }

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
                    
                    IconButton(
                        onClick = { showInfoSheet = true },
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = stringResource(R.string.supported_cards_title),
                            tint = Color.White
                        )
                    }
                }
                
                Text(
                    text = if (nfcCards.isEmpty()) stringResource(R.string.scan_prompt) else stringResource(R.string.history_count, nfcCards.size),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (nfcCards.isEmpty()) {
                    EmptyStateView()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        items(
                            items = nfcCards,
                            key = { it.number ?: it.hashCode() }
                        ) { card ->
                            HistoryCard(card)
                        }
                    }
                }
            }

            // Full Screen Loading Overlay Removed for UX
        }
    }

    if (showInfoSheet) {
        ModalBottomSheet(
            onDismissRequest = { showInfoSheet = false },
            containerColor = Color(0xFF1A1C1E),
            contentColor = Color.White,
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.2f)) },
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 40.dp)
            ) {
                Text(
                    text = stringResource(R.string.supported_cards_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.supported_cards_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                val cards = listOf(
                    "Suica" to SuicaGreen,
                    "PASMO" to ShoppingOrange,
                    "ICOCA" to JRBlue,
                    "TOICA" to Color(0xFFFDD835),
                    "SUGOCA" to Color(0xFFE53935),
                    "Kitaca" to Color(0xFF8BC34A),
                    "manaca" to Color(0xFFFB8C00),
                    "nimoca" to Color(0xFF03A9F4),
                    "hayakaken" to Color(0xFF00ACC1),
                    "PiTaPa" to Color(0xFF9575CD)
                )
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(cards) { (name, color) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.05f))
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(color)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
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
                        .background(
                            Brush.radialGradient(
                                colors = listOf(SuicaGreen.copy(alpha = 0.3f), Color.Transparent)
                            ),
                            shape = RoundedCornerShape(70.dp)
                        )
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
                text = stringResource(R.string.ready_to_scan),
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.scan_prompt),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
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
                            color = Color.White.copy(alpha = 0.7f)
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
                        color = Color.White.copy(alpha = 0.7f)
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
                    color = Color.White.copy(alpha = 0.7f)
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
            color = Color.White.copy(alpha = 0.7f)
        )
        Text(
            text = station,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
        Text(
            text = if (company.isNotEmpty()) "$company $line" else line,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.7f),
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}
