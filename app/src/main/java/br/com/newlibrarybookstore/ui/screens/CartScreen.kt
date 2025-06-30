package br.com.newlibrarybookstore.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
fun CartScreen(
    cartViewModel: CartViewModel = viewModel(),
    onCheckout: () -> Unit
) {
    val cartItems by cartViewModel.cartItems.collectAsState()

    if (cartItems.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Seu carrinho está vazio!")
        }
    } else {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(cartItems) { book ->
                    // Reutilizamos o BookStoreItem, mas com um botão de remover
                    BookStoreItem(
                        book = book,
                        onAddToCart = { cartViewModel.removeFromCart(book) },
                        onClick = {}
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            Button(
                onClick = {
                    cartViewModel.checkout()
                    onCheckout() // Avisa que a compra foi finalizada
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Finalizar Compra (Simulado)")
            }
        }
    }
}