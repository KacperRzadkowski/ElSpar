package com.team12.ElSpar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.team12.ElSpar.ui.ElSparApp
import com.team12.ElSpar.ui.viewmodel.ElSparViewModel
import com.team12.ElSpar.ui.theme.ElSparTheme

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: ElSparViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ElSparTheme {
                viewModel = viewModel(factory = ElSparViewModel.Factory)
                ElSparApp(viewModel)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ElSparTheme {
    }
}