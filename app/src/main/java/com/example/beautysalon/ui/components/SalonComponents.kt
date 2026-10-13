package com.example.beautysalon.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.beautysalon.R
import com.example.beautysalon.ui.theme.Surface
import com.example.beautysalon.util.loadColor
import com.example.beautysalon.util.statusColor

@Composable
fun SalonCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            content()
        }
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled
    ) {
        Text(text = text)
    }
}

@Composable
fun StatusChip(status: String) {
    val label = when (status) {
        "PENDING" -> stringResource(R.string.status_pending)
        "DONE" -> stringResource(R.string.status_done)
        "CANCELLED" -> stringResource(R.string.status_cancelled)
        else -> status
    }

    Text(
        text = label,
        color = statusColor(status),
        style = MaterialTheme.typography.labelMedium
    )
}

@Composable
fun LoadBar(
    loaded: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    val percent = if (total > 0) (loaded.toFloat() / total).coerceIn(0f, 1f) else 0f
    val barColor = loadColor(percent)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        LinearProgressIndicator(
            progress = { percent },
            modifier = Modifier.fillMaxWidth(),
            color = barColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.load_count, loaded, total),
                color = Color.LightGray,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}