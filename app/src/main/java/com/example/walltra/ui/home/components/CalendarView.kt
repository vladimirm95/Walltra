package com.example.walltra.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val anchorMonth = YearMonth.of(1990, 1)
private const val PAGE_COUNT = 2400

private fun monthToPage(month: LocalDate): Int {
    val ym = YearMonth.of(month.year, month.month)
    return (ym.year - anchorMonth.year) * 12 + (ym.monthValue - anchorMonth.monthValue)
}

private fun pageToMonth(page: Int): LocalDate {
    return anchorMonth.plusMonths(page.toLong()).atDay(1)
}

@Composable
fun CalendarView(
    currentMonth: LocalDate,
    selectedDate: LocalDate,
    periodStartDate: LocalDate?,
    totalAmountByDate: Map<String, Double>,
    onDayClick: (LocalDate) -> Unit,
    onMonthChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val dayNames = listOf("Ne", "Po", "Ut", "Sr", "Če", "Pe", "Su")
    val maxAmount = totalAmountByDate.values.maxOrNull() ?: 1.0
    val coroutineScope = rememberCoroutineScope()

    val pagerState = rememberPagerState(
        initialPage = monthToPage(currentMonth),
        pageCount = { PAGE_COUNT }
    )

    // Mesec promenjen spolja (npr. inicijalno postavljanje) -> pomeri pager
    LaunchedEffect(currentMonth) {
        val targetPage = monthToPage(currentMonth)
        if (pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    // Korisnik prevukao pager -> javi ViewModel-u novi mesec
    LaunchedEffect(pagerState.currentPage) {
        val newMonth = pageToMonth(pagerState.currentPage)
        if (newMonth != currentMonth) {
            onMonthChange(newMonth)
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Header — naziv meseca i navigacija
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Prethodni mesec")
            }
            Text(
                text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale("sr"))} ${currentMonth.year}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Sledeći mesec")
            }
        }

        // Nazivi dana
        Row(modifier = Modifier.fillMaxWidth()) {
            dayNames.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Mreža dana - swipe između meseci
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            CalendarGrid(
                month = pageToMonth(page),
                selectedDate = selectedDate,
                periodStartDate = periodStartDate,
                totalAmountByDate = totalAmountByDate,
                maxAmount = maxAmount,
                onDayClick = onDayClick
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    month: LocalDate,
    selectedDate: LocalDate,
    periodStartDate: LocalDate?,
    totalAmountByDate: Map<String, Double>,
    maxAmount: Double,
    onDayClick: (LocalDate) -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val yearMonth = YearMonth.of(month.year, month.month)
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfWeek = yearMonth.atDay(1).dayOfWeek.value % 7

    val totalCells = firstDayOfWeek + daysInMonth
    val rows = (totalCells + 6) / 7

    Column(modifier = Modifier.fillMaxWidth()) {
        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    val dayNumber = cellIndex - firstDayOfWeek + 1

                    if (dayNumber < 1 || dayNumber > daysInMonth) {
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                    } else {
                        val date = yearMonth.atDay(dayNumber)
                        val dateStr = date.format(formatter)
                        val amount = totalAmountByDate[dateStr] ?: 0.0
                        val isSelected = date == selectedDate
                        val isInPeriod = periodStartDate != null &&
                                !date.isBefore(periodStartDate) &&
                                !date.isAfter(LocalDate.now())

                        val backgroundColor = when {
                            isSelected -> MaterialTheme.colorScheme.primary
                            isInPeriod && amount > 0 -> {
                                val ratio = (amount / maxAmount).toFloat().coerceIn(0.1f, 1f)
                                lerp(Color(0xFFBBDEFB), Color(0xFF1565C0), ratio)
                            }
                            isInPeriod -> Color(0xFFE3F2FD)
                            else -> Color.Transparent
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(backgroundColor)
                                .clickable {
                                    if (isInPeriod) onDayClick(date)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayNumber.toString(),
                                fontSize = 13.sp,
                                color = when {
                                    isSelected -> Color.White
                                    isInPeriod && amount > 0 -> Color.White
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}