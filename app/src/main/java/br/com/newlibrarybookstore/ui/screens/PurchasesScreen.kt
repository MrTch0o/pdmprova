package br.com.newlibrarybookstore.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.newlibrarybookstore.ui.viewmodel.CartViewModel

@Composable
fun PurchasesScreen(cartViewModel: CartViewModel = viewModel()) {
    val purchasedItems by cartViewModel.purchasedItems.collectAsState()

    if (purchasedItems.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Você ainda não fez nenhuma compra.")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(purchasedItems) { book ->
                // Apenas exibimos o item, sem botões de ação
                BookStoreItem(book = book, onAddToCart = {})
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}