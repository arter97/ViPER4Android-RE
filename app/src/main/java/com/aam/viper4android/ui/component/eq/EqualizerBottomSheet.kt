package com.aam.viper4android.ui.component.eq

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.aam.viper4android.R
import com.aam.viper4android.ui.component.BottomSheet
import com.aam.viper4android.ui.component.EqualizerEditor

@Suppress("UNUSED_PARAMETER")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EqualizerBottomSheet(
    sheetState: SheetState,
    gains: List<Float>,
    onGainsChanged: (List<Float>) -> Unit,
    onBandCountChange: (Int) -> Unit,
    onReset: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val view = LocalView.current
    val systemBarsInsets = remember(view) {
        ViewCompat.getRootWindowInsets(view)
            ?.getInsets(WindowInsetsCompat.Type.systemBars())
            ?.let {
                WindowInsets(
                    left = it.left,
                    top = it.top,
                    right = it.right,
                    bottom = it.bottom,
                ).only(WindowInsetsSides.Vertical)
            }
    }

    val bandOptions = listOf(10, 15, 31)
    val selectedBandCount = gains.size
    val selectedIndex = bandOptions.indexOf(selectedBandCount).takeIf { it >= 0 } ?: 0

    BottomSheet(
        onDismissRequest = onDismissRequest,
        windowInsets = systemBarsInsets ?: BottomSheetDefaults.windowInsets,
    ) {
        Column(
            modifier = Modifier.padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            CenterAlignedTopAppBar(
                title = {
                    Text("FIR Equalizer")
                },
                modifier = Modifier.padding(horizontal = 8.dp),
                actions = {
                    IconButton(onClick = onReset) {
                        Icon(
                            painter = painterResource(R.drawable.ic_restart),
                            contentDescription = "Reset to default"
                        )
                    }
                }
            )

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
            ) {
                bandOptions.forEachIndexed { index, bandCount ->
                    SegmentedButton(
                        selected = index == selectedIndex,
                        onClick = {
                            if (bandCount != selectedBandCount) {
                                onBandCountChange(bandCount)
                            }
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = bandOptions.size)
                    ) {
                        Text(bandCount.toString())
                    }
                }
            }

            EqualizerEditor(
                modifier = Modifier
                    .height(380.dp)
                    .fillMaxWidth(),
                gains = gains,
                onGainsChanged = onGainsChanged
            )

            Button(
                onClick = onDismissRequest,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text("Close")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun EqualizerBottomSheetPreview() {
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
        gains = List(10) { 0f },
        onGainsChanged = {},
        onBandCountChange = {},
        onReset = {},
        onDismissRequest = { }
    )
}
