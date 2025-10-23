package com.aam.viper4android.ui.effect

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aam.viper4android.R
import com.aam.viper4android.ui.component.Effect
import com.aam.viper4android.ui.component.eq.EqualizerBottomSheet
import com.aam.viper4android.ui.component.eq.EqualizerPreview
import com.aam.viper4android.vm.FIREqualizerViewModel

private data class FirEqualizerPreset(
    val id: String,
    val label: String,
    val gains: List<Float>
)

private val firEqualizerPresetOptions = listOf(
    FirEqualizerPreset(
        id = "bass_reduce",
        label = "Bass reduce",
        gains = listOf(-8f, -6f, -4f, -2f, -1f, 0f, 1f, 2f, 3f, 4f)
    ),
    FirEqualizerPreset(
        id = "flat",
        label = "Flat",
        gains = List(10) { 0f }
    ),
    FirEqualizerPreset(
        id = "classic",
        label = "Classic",
        gains = listOf(4f, 2f, 0f, -2f, -3f, -1f, 1f, 3f, 4f, 5f)
    ),
    FirEqualizerPreset(
        id = "jazz",
        label = "Jazz",
        gains = listOf(5f, 3f, 1f, -1f, -2f, 0f, 2f, 4f, 5f, 5f)
    ),
    FirEqualizerPreset(
        id = "pop",
        label = "Pop",
        gains = listOf(4f, 2f, 0f, -1f, -2f, 1f, 3f, 4f, 4f, 3f)
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FIREqualizerEffect(
    viewModel: FIREqualizerViewModel = hiltViewModel()
) {
    val enabled by viewModel.enabled.collectAsStateWithLifecycle()
    val gains by viewModel.gains.collectAsStateWithLifecycle()

    var showEditorDialog by rememberSaveable { mutableStateOf(false) }
    val selectedPresetId = firEqualizerPresetOptions.firstOrNull { it.gains == gains }?.id

    Effect(
        icon = painterResource(R.drawable.ic_equalizer),
        title = stringResource(R.string.fir_equalizer),
        checked = enabled,
        onCheckedChange = viewModel::setEnabled
    ) {
        Column {
            /*
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val selectedIcon: @Composable () -> Unit = {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "Done icon",
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }

                val isCustomSelected = selectedPresetId == null
                FilterChip(
                    selected = isCustomSelected,
                    onClick = { showEditorDialog = true },
                    label = { Text("Custom") },
                    leadingIcon = if (isCustomSelected) selectedIcon else null
                )

                firEqualizerPresetOptions.forEach { preset ->
                    val selected = preset.id == selectedPresetId
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.setGains(preset.gains.toList()) },
                        label = { Text(preset.label) },
                        leadingIcon = if (selected) selectedIcon else null
                    )
                }
            }
            Spacer(Modifier.height(18.dp))
             */
            EqualizerPreview(
                modifier = Modifier
                    .height(300.dp)
                    .fillMaxWidth()
                    .clickable { showEditorDialog = true },
                gains = gains
            )
        }
    }

    if (showEditorDialog) {
        val density = LocalDensity.current
        val sheetState = remember {
            SheetState(
                skipPartiallyExpanded = true,
                density = density,
                initialValue = SheetValue.Expanded,
                { false },
                skipHiddenState = true
            )
        }
        EqualizerBottomSheet(
            sheetState = sheetState,
            gains = gains,
            onGainsChanged = viewModel::setGains,
            onBandCountChange = viewModel::setBandCount,
            onReset = viewModel::resetGains,
            onDismissRequest = { showEditorDialog = false }
        )
    }
}