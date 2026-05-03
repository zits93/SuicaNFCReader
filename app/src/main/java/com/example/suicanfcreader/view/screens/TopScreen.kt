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

@Composable
fun TopScreen(
    topScreenViewModel: TopScreenViewModel
) {
    val nfcCards by topScreenViewModel.nfcCards.observeAsState(emptyList())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BackgroundGradientStart, BackgroundGradientEnd)
                )
            )
    ) {
        // Decorative Blurry Circles for Liquid Effect
        Box(
            modifier = Modifier
                .offset(x = (-50).dp, y = (-50).dp)
                .size(300.dp)
                .blur(100.dp)
                .background(SuicaGreen.copy(alpha = 0.2f), RoundedCornerShape(150.dp))
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 50.dp, y = 50.dp)
                .size(300.dp)
                .blur(100.dp)
                .background(JRBlue.copy(alpha = 0.2f), RoundedCornerShape(150.dp))
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

                var expanded by remember { mutableStateOf(false) }
                val context = androidx.compose.ui.platform.LocalContext.current
                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Change Language",
                            tint = Color.White
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("English") },
                            onClick = { 
                                expanded = false
                                context.getSystemService(android.app.LocaleManager::class.java).applicationLocales = 
                                    android.os.LocaleList.forLanguageTags("en")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("한국어") },
                            onClick = { 
                                expanded = false
                                context.getSystemService(android.app.LocaleManager::class.java).applicationLocales = 
                                    android.os.LocaleList.forLanguageTags("ko")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("日本語") },
                            onClick = { 
                                expanded = false
                                context.getSystemService(android.app.LocaleManager::class.java).applicationLocales = 
                                    android.os.LocaleList.forLanguageTags("ja")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("中文") },
                            onClick = { 
                                expanded = false
                                context.getSystemService(android.app.LocaleManager::class.java).applicationLocales = 
                                    android.os.LocaleList.forLanguageTags("zh")
                            }
                        )
                    }
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
    }
}

@Composable
fun EmptyStateView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 100.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Nfc,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color.White.copy(alpha = 0.3f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.nfc_off_prompt),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.5f)
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
            .clip(RoundedCornerShape(24.dp))
            .background(GlassWhite)
            .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
            .padding(20.dp)
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
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(typeColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = typeColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (card.kindResId != 0) stringResource(card.kindResId) else stringResource(R.string.unknown),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.date_format, card.year, card.month, card.day),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${stringResource(R.string.balance_symbol)}${card.balance}",
                        style = MaterialTheme.typography.titleLarge,
                        color = BalanceGold,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = stringResource(R.string.balance_label),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
            }

            if (card.kindResId != R.string.kind_shopping) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.boarding),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                        Text(
                            text = card.inStation ?: "-",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = card.inCompany ?: "",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                    
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                        Text(
                            text = stringResource(R.string.alighting),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                        Text(
                            text = card.outStation ?: "-",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = card.outCompany ?: "",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.payment_method, if (card.deviceResId != 0) stringResource(card.deviceResId) else stringResource(R.string.unknown)),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}
